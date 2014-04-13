package org.holoeverywhere.plugin.internal;

import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.internal.PublishOperation;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal;
import org.gradle.api.publish.maven.internal.publisher.MavenNormalizedPublication;
import org.gradle.api.publish.maven.internal.publisher.MavenPublisher;
import org.gradle.api.publish.maven.internal.publisher.StaticLockingMavenPublisher;
import org.gradle.api.publish.maven.internal.publisher.ValidatingMavenPublisher;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.specs.Spec;
import org.gradle.internal.Factory;
import org.gradle.logging.LoggingManagerInternal;
import org.gradle.util.CollectionUtils;

import javax.inject.Inject;
import java.util.Set;

public class HoloEverywherePublishToMavenRepository extends PublishToMavenRepository {
    public static final String SIGN_CLASSIFIER = "sign";
    public static final String POST_SIGN_CLASSIFIER = ".sign";

    private final Factory<LoggingManagerInternal> loggingManagerFactory;
    public MavenArtifact mainArtifact;
    public MavenPublisher publisher;

    @Inject
    public HoloEverywherePublishToMavenRepository(Factory<LoggingManagerInternal> loggingManagerFactory) {
        super(loggingManagerFactory);
        this.loggingManagerFactory = loggingManagerFactory;
        this.publisher = new HoloEverywhereMavenPublisher(this);
    }

    @Override
    public Factory<LoggingManagerInternal> getLoggingManagerFactory() {
        return loggingManagerFactory;
    }

    @Override
    protected void doPublish(final MavenPublicationInternal publication, final MavenArtifactRepository repository) {
        new PublishOperation(publication, repository) {
            @Override
            protected void publish() throws Exception {
                MavenPublisher staticLockingPublisher = new StaticLockingMavenPublisher(publisher);
                MavenPublisher validatingPublisher = new ValidatingMavenPublisher(staticLockingPublisher);
                validatingPublisher.publish(normalize(publication.asNormalisedPublication()), repository);
            }
        }.run();
    }

    private MavenNormalizedPublication normalize(MavenNormalizedPublication publication) {
        final Set<MavenArtifact> artifacts = publication.getArtifacts();
        artifacts.removeAll(CollectionUtils.filter(artifacts, new Spec<MavenArtifact>() {
            @Override
            public boolean isSatisfiedBy(MavenArtifact element) {
                final String classifier = element.getClassifier();
                return (classifier != null && (classifier.equals(SIGN_CLASSIFIER) || classifier.endsWith(POST_SIGN_CLASSIFIER)))
                        && !element.getFile().exists();
            }
        }));
        for (MavenArtifact artifact : artifacts) {
            final String classifier = artifact.getClassifier();
            if (classifier == null) {
                continue;
            }
            if (classifier.equals(SIGN_CLASSIFIER)) {
                artifact.setClassifier(null);
            }
            if (classifier.endsWith(POST_SIGN_CLASSIFIER)) {
                artifact.setClassifier(classifier.substring(0, classifier.length() - POST_SIGN_CLASSIFIER.length()));
            }
        }
        return publication;
    }
}
