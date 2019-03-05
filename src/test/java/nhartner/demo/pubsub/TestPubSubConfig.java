package nhartner.demo.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Creates the topic and subscription in GCP emulator. If emulator is not running, this will hang.
 */
@Component
public class TestPubSubConfig {

    private final TransportChannelProvider channelProvider;
    private final CredentialsProvider credentialsProvider;

    private String projectId;
    private String topicName = "test-topic-5";

    TestPubSubConfig(@Value("${spring.cloud.gcp.pubsub.emulator-host}") String emulatorHost,
                     @Value("${spring.cloud.gcp.project-id}") String projectId,
                     @Value("${nhartner.demo.subscription.name}") String subscriptionName) throws IOException {
        this.projectId = projectId;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHost).usePlaintext().build();
        channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        credentialsProvider = NoCredentialsProvider.create();
        createTopic(topicName);
        createSubscription(topicName, subscriptionName);
    }

    @Bean
    public Publisher testPublisher() throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(projectId, topicName))
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }

    private void createSubscription(String topicName, String subscriptionName) throws IOException {
        ProjectTopicName topic = ProjectTopicName.of(projectId, topicName);
        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionName);

        try {
            subscriptionAdminClient()
                    .createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 100);
        }
        catch (AlreadyExistsException e) {
            // this is fine, already created
        }
    }

    private void createTopic(String topicName) throws IOException {
        ProjectTopicName topic = ProjectTopicName.of(projectId, topicName);
        try {
            topicAdminClient().createTopic(topic);
        }
        catch (AlreadyExistsException e) {
            // this is fine, already created
        }
    }

    private TopicAdminClient topicAdminClient() throws IOException {
        return TopicAdminClient.create(
                TopicAdminSettings.newBuilder()
                        .setTransportChannelProvider(channelProvider)
                        .setCredentialsProvider(credentialsProvider).build());
    }


    private SubscriptionAdminClient subscriptionAdminClient() throws IOException {
        return SubscriptionAdminClient.create(SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build());

    }

}
