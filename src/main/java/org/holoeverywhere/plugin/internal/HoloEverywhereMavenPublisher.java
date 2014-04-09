/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.holoeverywhere.plugin.internal;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.ant.*;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.file.TemporaryFileProvider;
import org.gradle.api.internal.file.TmpDirTemporaryFileProvider;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.publication.maven.internal.ant.CustomInstallDeployTaskSupport;
import org.gradle.api.publication.maven.internal.ant.MavenSettingsSupplier;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.internal.publisher.MavenNormalizedPublication;
import org.gradle.api.publish.maven.internal.publisher.MavenPublisher;
import org.gradle.internal.Factory;
import org.gradle.logging.LoggingManagerInternal;
import org.gradle.util.AntUtil;
import org.gradle.util.GUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class HoloEverywhereMavenPublisher implements MavenPublisher {
    private final Factory<LoggingManagerInternal> loggingManagerFactory;
    private static Logger logger = LoggerFactory.getLogger(HoloEverywhereMavenPublisher.class);
    protected final Factory<File> temporaryDirFactory;
    private final HoloEverywherePublishToMavenRepository task;

    public HoloEverywhereMavenPublisher(HoloEverywherePublishToMavenRepository task) {
        this.loggingManagerFactory = task.getLoggingManagerFactory();
        this.temporaryDirFactory = task.getTemporaryDirFactory();
        this.task = task;
    }

    @Override
    public void publish(MavenNormalizedPublication publication, MavenArtifactRepository artifactRepository) {
        logger.info("Publishing to repository {}", artifactRepository);
        HoloEverywhereDeployTask deployTask = new HoloEverywhereDeployTask(temporaryDirFactory);
        deployTask.setUniqueVersion(true);
        deployTask.setProject(AntUtil.createProject());

        MavenSettingsSupplier mavenSettingsSupplier = new EmptyMavenSettingsSupplier();
        mavenSettingsSupplier.supply(deployTask);

        postConfigure(deployTask, artifactRepository);
        addPomAndArtifacts(deployTask, publication, task.mainArtifact);
        execute(deployTask);

        mavenSettingsSupplier.done();
    }

    protected void postConfigure(HoloEverywhereDeployTask task, MavenArtifactRepository artifactRepository) {
        addRepository(task, artifactRepository);
    }

    private void addRepository(HoloEverywhereDeployTask deployTask, MavenArtifactRepository artifactRepository) {
        RemoteRepository mavenRepository = new MavenRemoteRepositoryFactory(artifactRepository).create();
        deployTask.addRemoteRepository(mavenRepository);
    }

    private static class HoloEverywhereDeployTask extends DeployTask implements CustomInstallDeployTaskSupport {
        @Override
        public synchronized Settings getSettings() {
            return super.getSettings();
        }

        @Override
        public synchronized PlexusContainer getContainer() {
            return super.getContainer();
        }

        @Override
        public void doExecute() {
            super.doExecute();
        }

        public void clearAttachedArtifactsList() {
            attachedArtifacts.clear();
        }

        private final Factory<File> tmpDirFactory;

        public HoloEverywhereDeployTask(Factory<File> tmpDirFactory) {
            this.tmpDirFactory = tmpDirFactory;
        }

        @Override
        protected ArtifactRepository createLocalArtifactRepository() {
            ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) lookup(ArtifactRepositoryLayout.ROLE, getLocalRepository().getLayout());
            return new DefaultArtifactRepository("local", tmpDirFactory.create().toURI().toString(), repositoryLayout);
        }

        @Override
        protected void updateRepositoryWithSettings(RemoteRepository repository) {
            // Do nothing
        }
    }

    private void addPomAndArtifacts(InstallDeployTaskSupport installOrDeployTask, MavenNormalizedPublication publication, MavenArtifact mainArtifact) {
        Pom pom = new Pom();
        pom.setProject(installOrDeployTask.getProject());
        pom.setFile(publication.getPomFile());
        installOrDeployTask.addPom(pom);

        if (mainArtifact == null) {
            mainArtifact = MainArtifactHelper.determineMainArtifact(publication.getName(), null, publication.getArtifacts());
        }
        installOrDeployTask.setFile(mainArtifact == null ? publication.getPomFile() : mainArtifact.getFile());

        for (MavenArtifact mavenArtifact : publication.getArtifacts()) {
            if (mavenArtifact == mainArtifact) {
                continue;
            }
            AttachedArtifact attachedArtifact = installOrDeployTask.createAttach();
            attachedArtifact.setClassifier(GUtil.elvis(mavenArtifact.getClassifier(), ""));
            attachedArtifact.setType(GUtil.elvis(mavenArtifact.getExtension(), ""));
            attachedArtifact.setFile(mavenArtifact.getFile());
        }
    }

    private void execute(InstallDeployTaskSupport deployTask) {
        LoggingManagerInternal loggingManager = loggingManagerFactory.create();
        loggingManager.captureStandardOutput(LogLevel.INFO).start();
        try {
            deployTask.execute();
        } finally {
            loggingManager.stop();
        }
    }

    class MavenRemoteRepositoryFactory implements Factory<RemoteRepository> {

        private final MavenArtifactRepository artifactRepository;

        public MavenRemoteRepositoryFactory(MavenArtifactRepository artifactRepository) {
            this.artifactRepository = artifactRepository;
        }

        public RemoteRepository create() {
            RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setUrl(artifactRepository.getUrl().toString());

            PasswordCredentials credentials = artifactRepository.getCredentials();
            String username = credentials.getUsername();
            String password = credentials.getPassword();

            if (username != null || password != null) {
                Authentication authentication = new Authentication();
                authentication.setUserName(username);
                authentication.setPassword(password);
                remoteRepository.addAuthentication(authentication);
            }

            return remoteRepository;
        }
    }

    public class EmptyMavenSettingsSupplier implements MavenSettingsSupplier {
        private final TemporaryFileProvider temporaryFileProvider = new TmpDirTemporaryFileProvider();
        private File settingsXml;

        public void supply(InstallDeployTaskSupport installDeployTaskSupport) {
            try {
                settingsXml = temporaryFileProvider.createTemporaryFile("gradle_empty_settings", ".xml");
                FileUtils.writeStringToFile(settingsXml, "<settings/>");
                settingsXml.deleteOnExit();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            installDeployTaskSupport.setSettingsFile(settingsXml);
        }

        public void done() {
            if (settingsXml != null) {
                settingsXml.delete();
            }
        }
    }
}
