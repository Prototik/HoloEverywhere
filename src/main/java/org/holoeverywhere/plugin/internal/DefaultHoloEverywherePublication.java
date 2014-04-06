package org.holoeverywhere.plugin.internal;

import org.gradle.api.publish.internal.ProjectDependencyPublicationResolver;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.publish.maven.internal.publisher.MavenProjectIdentity;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.typeconversion.NotationParser;
import org.holoeverywhere.plugin.HoloEverywherePublication;

public class DefaultHoloEverywherePublication extends DefaultMavenPublication implements HoloEverywherePublication {
    public DefaultHoloEverywherePublication(String name, MavenProjectIdentity projectIdentity, NotationParser<Object, MavenArtifact> mavenArtifactParser, Instantiator instantiator, ProjectDependencyPublicationResolver projectDependencyResolver) {
        super(name, projectIdentity, mavenArtifactParser, instantiator, projectDependencyResolver);
    }
}
