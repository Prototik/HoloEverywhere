package org.holoeverywhere.plugin.util

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.resolution.VersionRangeResult
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.version.Version
import org.gradle.api.Project
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

public class VersionHelper {
    private static final Map<Project, VersionHelper> sInstanceMap = new WeakHashMap<>()

    public static VersionHelper get(Project project) {
        VersionHelper helper = sInstanceMap.get(project)
        if (helper == null) {
            sInstanceMap.put(project, helper = new VersionHelper(project))
        }
        return helper
    }

    private final RepositorySystem sRepositorySystem
    private final RepositorySystemSession sSession
    private final RemoteRepository sReleaseRepo, sSnapshotRepo
    private final List<RemoteRepository> sRepositories

    public VersionHelper(Project project) {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator()
        locator.addService(RepositoryConnectorFactory, BasicRepositoryConnectorFactory)
        locator.addService(TransporterFactory, HttpTransporterFactory)
        sRepositorySystem = locator.getService(RepositorySystem)

        sSession = MavenRepositorySystemUtils.newSession()

        sReleaseRepo = new RemoteRepository.Builder('holoeverywhere', 'default', HoloEverywhereExtension.HOLO_EVERYWHERE_REPO).build()
        sSnapshotRepo = new RemoteRepository.Builder('holoeverywhere-snapshot', 'default', HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO).build()

        sRepositories = [sReleaseRepo, sSnapshotRepo] as List<RemoteRepository>

        File localRepositoryPath = new File(project.gradle.gradleUserHomeDir, '.holoeverywhere-repo')
        if (!localRepositoryPath.exists()) {
            localRepositoryPath.mkdirs()
        }

        sSession.setLocalRepositoryManager(
                sRepositorySystem.newLocalRepositoryManager(sSession, new LocalRepository(localRepositoryPath)))
    }

    public enum VersionType {
        Stable, Snapshot
    }

    public String resolveVersion(String artifact, VersionType type) {
        VersionRangeRequest request = new VersionRangeRequest()
        request.artifact = new DefaultArtifact("${artifact}:[0,)")
        request.repositories = sRepositories
        VersionRangeResult result = sRepositorySystem.resolveVersionRange(sSession, request)
        return result.versions.collect { Version version -> version.toString() }.findAll {
            def boolean snapshot = it.endsWith("-SNAPSHOT")
            (snapshot && type == VersionType.Snapshot) || (!snapshot && type == VersionType.Stable)
        }.max { String s1, String s2 -> s1.compareTo(s2) }
    }
}
