package nhartner.demo.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.cloud.gcp.autoconfigure.pubsub.GcpPubSubProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PubSubResourceGenerator {

    private final TransportChannelProvider channelProvider;
    private final CredentialsProvider credentialsProvider;
    private final TopicAdminClient topicAdminClient;
    private final SubscriptionAdminClient subscriptionAdminClient;

    private String projectId;

    PubSubResourceGenerator(GcpPubSubProperties gcpPubSubProperties) throws IOException {
        this.projectId = gcpPubSubProperties.getProjectId();
        ManagedChannel channel = ManagedChannelBuilder.forTarget(gcpPubSubProperties.getEmulatorHost()).usePlaintext().build();
        channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        credentialsProvider = NoCredentialsProvider.create();
        topicAdminClient = topicAdminClient();
        subscriptionAdminClient = subscriptionAdminClient();
    }

    public Subscription createSubscription(String topicName, String subscriptionName) {
        ProjectTopicName topic = ProjectTopicName.of(projectId, topicName);
        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionName);

        try {
            return subscriptionAdminClient
                    .createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 100);
        }
        catch (AlreadyExistsException e) {
            // this is fine, already created
            return subscriptionAdminClient.getSubscription(subscription);
        }
    }

    public Topic createTopic(String topicName) {
        ProjectTopicName topic = ProjectTopicName.of(projectId, topicName);
        try {
            return topicAdminClient.createTopic(topic);
        }
        catch (AlreadyExistsException e) {
            // this is fine, already created
            return topicAdminClient.getTopic(topic);
        }
    }

    public Publisher createPublisher(String topicName) throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(projectId, topicName))
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
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
