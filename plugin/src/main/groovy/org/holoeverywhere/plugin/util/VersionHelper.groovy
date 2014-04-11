package org.holoeverywhere.plugin.util

import org.apache.maven.artifact.versioning.ArtifactVersion
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.gradle.api.invocation.Gradle
import org.gradle.util.GUtil
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

public class VersionHelper {
    public static String resolveVersion(Gradle gradle, String group, String artifact, VersionType versionType) {
        File versionsCache = new File(gradle.gradleUserHomeDir, ".holoeverywhere_versions_cache")
        Properties properties = versionsCache.exists() ? GUtil.loadProperties(versionsCache) : new Properties()
        final String cacheId = "${group}:${artifact}:${versionType.name()}"

        try {
            URL metadataUrl = new URL(new URL(versionType == VersionType.Snapshot ? HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO : HoloEverywhereExtension.HOLO_EVERYWHERE_REPO),
                    "${group.replace('.', '/')}/${artifact.replace('.', '/')}/maven-metadata.xml")
            def metadata = new XmlParser().parse(metadataUrl.openStream())
            def List<String> versions = new ArrayList<>()
            def Node versionsNode = (metadata.find { it.name() == 'versioning' } as Node).find {
                it.name() == 'versions'
            } as Node
            versionsNode.each {
                if (it.name() == 'version') {
                    versions.add(it.text())
                }
            }
            def String version = maxVersion(versions)
            properties.put(cacheId, version)
            GUtil.saveProperties(properties, versionsCache)
            return version
        } catch (Exception e) {
            if (properties.hasProperty(cacheId)) {
                return properties.getProperty(cacheId)
            }
            throw new RuntimeException('Couldn\'t determine a final version')
        }
    }

    private static String maxVersion(Collection<String> versions) {
        ArtifactVersion maxVersion = null
        versions.each {
            if (maxVersion == null) {
                maxVersion = new DefaultArtifactVersion(it)
            } else {
                ArtifactVersion version = new DefaultArtifactVersion(it)
                if (version.compareTo(maxVersion) > 0) {
                    maxVersion = version
                }
            }
        }
        return maxVersion?.toString()
    }

    public static enum VersionType {
        Snapshot, Stable
    }
}
