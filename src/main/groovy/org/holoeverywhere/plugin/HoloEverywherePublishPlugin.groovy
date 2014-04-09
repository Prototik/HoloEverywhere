package org.holoeverywhere.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.Module
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.configurations.DependencyMetaDataProvider
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.internal.ProjectDependencyPublicationResolver
import org.gradle.api.publish.maven.MavenArtifact
import org.gradle.api.publish.maven.internal.artifact.MavenArtifactNotationParserFactory
import org.gradle.api.publish.maven.internal.publication.DefaultMavenProjectIdentity
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.api.publish.maven.internal.publisher.MavenProjectIdentity
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskDependency
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.typeconversion.NotationParser
import org.gradle.model.ModelRule
import org.gradle.model.ModelRules
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.Signature
import org.gradle.plugins.signing.SigningPlugin
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.internal.DefaultHoloEverywherePublication
import org.holoeverywhere.plugin.internal.HoloEverywherePublishToMavenRepository
import org.holoeverywhere.plugin.internal.MainArtifactHelper

import javax.inject.Inject

public class HoloEverywherePublishPlugin extends HoloEverywhereAbstractPlugin {
    public static interface PublishInjector {
        public void prepareArtifactsForPublication()
    }

    private static final String DEFAULT_PUBLCATION_NAME = "holoeverywhere"

    private final DependencyMetaDataProvider dependencyMetaDataProvider
    private final FileResolver fileResolver
    private final ProjectDependencyPublicationResolver projectDependencyResolver
    private final ModelRules modelRules

    @Inject
    HoloEverywherePublishPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory, DependencyMetaDataProvider dependencyMetaDataProvider, FileResolver fileResolver, ProjectDependencyPublicationResolver projectDependencyResolver, ModelRules modelRules) {
        super(instantiator, repositoryFactory)
        this.dependencyMetaDataProvider = dependencyMetaDataProvider
        this.fileResolver = fileResolver
        this.projectDependencyResolver = projectDependencyResolver
        this.modelRules = modelRules
    }
    def HoloEverywhereExtension extension

    @Override
    void apply(final Project project) {
        project.plugins.apply(SigningPlugin)
        project.plugins.apply(MavenPublishPlugin)
        extension = extension(project)

        (project.plugins.findAll {
            PublishInjector.isAssignableFrom(it.class)
        } as Set<PublishInjector>)*.prepareArtifactsForPublication()

        project.extensions.configure(PublishingExtension, new Action<PublishingExtension>() {
            @Override
            void execute(PublishingExtension publishingExtension) {
                publishingExtension.publications.registerFactory(HoloEverywherePublication,
                        new HoloEverywherePublicationFactory(dependencyMetaDataProvider, instantiator, fileResolver, projectDependencyResolver))
                extension.publish.repository.addToHandler(publishingExtension.repositories)
                publishingExtension.publications.create(DEFAULT_PUBLCATION_NAME, HoloEverywherePublication)
            }
        });

        modelRules.rule(new ModelRule() {
            @SuppressWarnings("UnusedDeclaration")
            public void realizePublishingTasks(TaskContainer tasks, PublishingExtension extension) {
                extension.publications.withType(MavenPublicationInternal).each { publication ->
                    createSignTaskForEachMavenRepo(tasks, extension, publication, publication.name)
                }
            }

            private void createSignTaskForEachMavenRepo(TaskContainer tasks, PublishingExtension extension, MavenPublicationInternal mavenPublicationInternal, String publicationName) {
                extension.repositories.withType(MavenArtifactRepository).each { repository ->
                    String repositoryName = repository.name

                    String signTaskName = "sign${publicationName.capitalize()}PublicationTo${repositoryName.capitalize()}Repository"
                    String publishTaskName = "publish${publicationName.capitalize()}PublicationTo${repositoryName.capitalize()}Repository"
                    String generatePomTaskName = "generatePomFileFor${publicationName.capitalize()}Publication"

                    GenerateMavenPom generatePomTask = tasks.getByName(generatePomTaskName) as GenerateMavenPom
                    PublishToMavenRepository publishTask = tasks.getByName(publishTaskName) as PublishToMavenRepository
                    overrideTask(tasks, publishTask);
                    MavenPublicationInternal publication = publishTask.publication as MavenPublicationInternal

                    Collection<File> files = publication.publishableFiles.files
                    File pomFile = files.find { it == generatePomTask.destination }

                    Sign signTask = tasks.create(signTaskName, Sign)
                    publication.artifacts.each { MavenArtifact mavenArtifact ->
                        signTask.sign new MavenizedPublishArtifact(mavenArtifact)
                        Signature signature = signTask.signatures.find { it.toSign == mavenArtifact.file };
                        signature.extension = "${mavenArtifact.extension}.${signature.extension}"
                    }

                    publishTask.inputs.files(signTask.signatureFiles)

                    publishTask.dependsOn(signTask)

                    signTask.signatures.each {
                        publication.artifact(it)
                    }

                    publication.artifact(signPom(signTask, pomFile))
                }
            }


            private void overrideTask(TaskContainer tasks, PublishToMavenRepository task) {
                println(task.publication.artifacts)
                if (task instanceof HoloEverywherePublishToMavenRepository) {
                    return;
                }
                // Yeah bitch. Replace.
                def newTask = tasks.replace(task.name, HoloEverywherePublishToMavenRepository)
                newTask.group = task.group
                newTask.description = task.description
                newTask.publication = task.publication
                newTask.inputs.files(task.inputs.files)
                newTask.repository = task.repository
                newTask.mainArtifact = MainArtifactHelper.determineMainArtifact(task.publication.name, extension.publish.packaging, task.publication.artifacts)
            }

            private Signature signPom(Sign signTask, File pomFile) {
                signTask.sign(pomFile)
                def Signature signature = signTask.signatures.find { it.toSign == pomFile }
                signature.classifier = null
                signature.type = signature.extension = "pom.${signature.extension}"
                return signature
            }
        });
    }

    private class HoloEverywherePublicationFactory implements NamedDomainObjectFactory<HoloEverywherePublication> {
        private final Instantiator instantiator;
        private final DependencyMetaDataProvider dependencyMetaDataProvider;
        private final FileResolver fileResolver;
        private final ProjectDependencyPublicationResolver projectDependencyResolver;

        private HoloEverywherePublicationFactory(DependencyMetaDataProvider dependencyMetaDataProvider, Instantiator instantiator, FileResolver fileResolver, ProjectDependencyPublicationResolver projectDependencyResolver) {
            this.dependencyMetaDataProvider = dependencyMetaDataProvider;
            this.instantiator = instantiator;
            this.fileResolver = fileResolver;
            this.projectDependencyResolver = projectDependencyResolver;
        }

        @Override
        public HoloEverywherePublication create(final String name) {
            Module module = dependencyMetaDataProvider.getModule();
            MavenProjectIdentity projectIdentity = new DefaultMavenProjectIdentity(module.getGroup(), module.getName(), module.getVersion());
            NotationParser<Object, MavenArtifact> artifactNotationParser = new MavenArtifactNotationParserFactory(instantiator, fileResolver).create();
            DefaultHoloEverywherePublication publication = instantiator.newInstance(
                    DefaultHoloEverywherePublication.class,
                    name, projectIdentity, artifactNotationParser, instantiator, projectDependencyResolver
            )

            publication.artifacts = extension.publish.artifacts
            publication.pom.packaging = extension.publish.packaging

            return publication
        }
    }

    private static class MavenizedPublishArtifact implements PublishArtifact {
        MavenArtifact artifact

        public MavenizedPublishArtifact(MavenArtifact artifact) {
            this.artifact = artifact
        }

        @Override
        String getName() {
            return "${extension}:${classifier}:${file.name}"
        }

        @Override
        String getExtension() {
            return artifact.extension
        }

        @Override
        String getType() {
            return artifact.extension
        }

        @Override
        String getClassifier() {
            return artifact.classifier
        }

        @Override
        File getFile() {
            return artifact.file
        }

        @Override
        Date getDate() {
            return new Date(artifact.file.lastModified())
        }

        @Override
        TaskDependency getBuildDependencies() {
            return artifact.buildDependencies
        }
    }
}
