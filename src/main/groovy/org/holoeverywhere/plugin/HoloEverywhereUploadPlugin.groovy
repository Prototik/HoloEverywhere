package org.holoeverywhere.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.plugins.MavenRepositoryHandlerConvention
import org.gradle.api.publication.maven.internal.ant.BaseMavenDeployer
import org.gradle.api.publication.maven.internal.ant.RepositoryBuilder
import org.gradle.api.tasks.Upload
import org.gradle.internal.reflect.Instantiator
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.extension.UploadContainer
import org.holoeverywhere.plugin.extension.upload.RepositoryContainer

import javax.inject.Inject

class HoloEverywhereUploadPlugin extends HoloEverywhereBasePlugin {
    private static final String WAGON_VERSION = '2.2'
    private static final String[] DEGAULT_WAGON_PROVIDERS = ['ssh', 'file', 'http', 'ftp']
    public static final DEPLOYER_JARS_CONFIGURATION_NAME = 'deployerJars'
    private HoloEverywhereExtension extension

    @Inject
    HoloEverywhereUploadPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        super(instantiator, registry)
    }

    @Override
    void apply(Project project) {
        extension = extension(project)

        project.repositories.mavenCentral()
        project.configurations.create(DEPLOYER_JARS_CONFIGURATION_NAME)
        DEGAULT_WAGON_PROVIDERS.each { String protocol ->
            project.dependencies.add(DEPLOYER_JARS_CONFIGURATION_NAME, "org.apache.maven.wagon:wagon-${protocol}:${WAGON_VERSION}")
        }

        project.plugins.apply(MavenPlugin)
        project.plugins.apply(SigningPlugin)

        UploadContainer upload = extension.upload

        Upload uploadTask = project.tasks.getByName('uploadArchives') as Upload

        BaseMavenDeployer mavenDeployer = (new DslObject(uploadTask.repositories).convention.plugins.get('maven') as MavenRepositoryHandlerConvention).mavenDeployer() as BaseMavenDeployer

        mavenDeployer.configuration = project.configurations.getByName(DEPLOYER_JARS_CONFIGURATION_NAME)

        // I'd already tell you about fucked conventions?
        mavenDeployer.repository = buildRepository(upload.repository.url, upload.repository)

        if (upload.repository.snapshotUrl != null) {
            // Well... You know.
            mavenDeployer.snapshotRepository = buildRepository(upload.repository.snapshotUrl, upload.repository)
        }

        mavenDeployer.pom.project {
            groupId = upload.group ?: project.group ?: project.rootProject.group ?: 'default'
            artifactId = upload.artifact ?: project.name
            version = upload.version ?: project.version
            if (upload.description != null) description = upload.description
            if (upload.packaging != null) packaging = upload.packaging

            scm {
                url upload.scm.url
                connection upload.scm.connection
                developerConnection upload.scm.developerConnection
            }

            licenses {
                license {
                    name upload.license.name
                    url upload.license.url
                    distribution upload.license.distribution
                    comments upload.license.comments
                }
            }
        }

        final SigningExtension signingExtension = project.extensions.findByType(SigningExtension)
        if (signingExtension != null) {
            mavenDeployer.beforeDeployment { MavenDeployment md -> signingExtension.signPom(md) }
        }
    }

    def Object buildRepository(String repositoryUrl, RepositoryContainer repositoryContainer) {
        return ConfigureUtil.configure({ url = repositoryUrl }, new RepositoryBuilder().repository {
            authentication(
                    userName: repositoryContainer.userName,
                    password: repositoryContainer.password,
                    passphrase: repositoryContainer.passphrase,
                    privateKey: repositoryContainer.privateKey
            )
        }, false);
    }
}
