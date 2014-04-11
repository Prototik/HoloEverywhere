package org.holoeverywhere.plugin.internal;

import org.gradle.api.publish.maven.InvalidMavenPublicationException;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.specs.Spec;
import org.gradle.util.CollectionUtils;

import java.util.Set;

public class MainArtifactHelper {
    public static MavenArtifact determineMainArtifact(String publicationName, final String possibleExtension, Set<MavenArtifact> mavenArtifacts) {
        Set<MavenArtifact> candidateMainArtifacts = CollectionUtils.filter(mavenArtifacts, new Spec<MavenArtifact>() {
            public boolean isSatisfiedBy(MavenArtifact element) {
                return element.getClassifier() == null || element.getClassifier().length() == 0;
            }
        });
        if (possibleExtension != null && possibleExtension.length() > 0) {
            final MavenArtifact artifact = CollectionUtils.findFirst(candidateMainArtifacts, new Spec<MavenArtifact>() {
                @Override
                public boolean isSatisfiedBy(MavenArtifact element) {
                    return possibleExtension.equals(element.getExtension());
                }
            });
            if (artifact != null) {
                return artifact;
            }
        }
        if (candidateMainArtifacts.isEmpty()) {
            return null;
        }
        if (candidateMainArtifacts.size() > 1) {
            throw new InvalidMavenPublicationException(publicationName, "Cannot determine main artifact - multiple artifacts found with empty classifier.");
        }
        return candidateMainArtifacts.iterator().next();
    }
}
