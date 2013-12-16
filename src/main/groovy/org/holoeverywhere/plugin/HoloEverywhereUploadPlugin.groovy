package org.holoeverywhere.plugin

import org.apache.maven.artifact.ant.Authentication
import org.apache.maven.artifact.ant.RemoteRepository
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.publication.maven.internal.ant.BaseMavenDeployer
import org.gradle.api.tasks.Upload
import org.gradle.internal.reflect.Instantiator
import org.gradle.plugins.signing.SigningExtension
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.extension.UploadContainer

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

        project.configurations.create(DEPLOYER_JARS_CONFIGURATION_NAME)
        DEGAULT_WAGON_PROVIDERS.each { String protocol ->
            project.dependencies.add(DEPLOYER_JARS_CONFIGURATION_NAME, "org.apache.maven.wagon:wagon-${protocol}:${WAGON_VERSION}")
        }

        project.afterEvaluate { afterEvaluate(project) }
    }

    def void afterEvaluate(Project project) {
        UploadContainer upload = extension.upload

        Upload uploadTask = project.tasks.getByName('uploadArchives') as Upload
        BaseMavenDeployer mavenDeployer = uploadTask.repositories.getByName('mavenDeployer') as BaseMavenDeployer

        mavenDeployer.configuration = project.configurations.getByName(DEPLOYER_JARS_CONFIGURATION_NAME)

        RemoteRepository repository = new RemoteRepository()
        repository.url = upload.repository.url
        repository.addAuthentication(upload.repository as Authentication)
        mavenDeployer.repository = repository

        if (upload.repository.snapshotUrl != null) {
            repository = new RemoteRepository()
            repository.url = upload.repository.snapshotUrl
            repository.addAuthentication(upload.repository as Authentication)
            mavenDeployer.snapshotRepository = repository
        }

        mavenDeployer.pom.project {
            groupId = upload.group ?: project.group ?: project.rootProject.group ?: 'default'
            artifactId = upload.artifact ?: project.name
            version = upload.version ?: project.version
            if (upload.description != null) description upload.description

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

        final SigningExtension signingExtension = project.extensions.getByType(SigningExtension)
        mavenDeployer.beforeDeployment { MavenDeployment md -> signingExtension.signPom(md) }
    }
}
