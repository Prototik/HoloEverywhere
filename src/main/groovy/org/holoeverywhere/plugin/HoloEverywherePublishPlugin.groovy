package org.holoeverywhere.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.Module
import org.gradle.api.internal.artifacts.configurations.DependencyMetaDataProvider
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.internal.ProjectDependencyPublicationResolver
import org.gradle.api.publish.maven.MavenArtifact
import org.gradle.api.publish.maven.internal.artifact.MavenArtifactNotationParserFactory
import org.gradle.api.publish.maven.internal.publication.DefaultMavenProjectIdentity
import org.gradle.api.publish.maven.internal.publisher.MavenProjectIdentity
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.typeconversion.NotationParser
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.internal.DefaultHoloEverywherePublication

import javax.inject.Inject

class HoloEverywherePublishPlugin extends HoloEverywhereAbstractPlugin {
    private static final String DEFAULT_PUBLCATION_NAME = "holoeverywhere";

    private final DependencyMetaDataProvider dependencyMetaDataProvider
    private final FileResolver fileResolver
    private final ProjectDependencyPublicationResolver projectDependencyResolver;

    @Inject
    HoloEverywherePublishPlugin(Instantiator instantiator, DependencyMetaDataProvider dependencyMetaDataProvider, FileResolver fileResolver, ProjectDependencyPublicationResolver projectDependencyResolver) {
        super(instantiator)
        this.dependencyMetaDataProvider = dependencyMetaDataProvider
        this.fileResolver = fileResolver
        this.projectDependencyResolver = projectDependencyResolver
    }
    def HoloEverywhereExtension extension

    @Override
    void apply(final Project project) {
        project.plugins.apply(MavenPublishPlugin)
        extension = extension(project)

        project.extensions.configure(PublishingExtension, new Action<PublishingExtension>() {
            @Override
            void execute(PublishingExtension publishingExtension) {
                publishingExtension.publications.registerFactory(HoloEverywherePublication,
                        new HoloEverywherePublicationFactory(dependencyMetaDataProvider, instantiator, fileResolver, projectDependencyResolver))
                extension.publish.repository.addToHandler(publishingExtension.repositories)
                publishingExtension.publications.create(DEFAULT_PUBLCATION_NAME, HoloEverywherePublication)
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
}
