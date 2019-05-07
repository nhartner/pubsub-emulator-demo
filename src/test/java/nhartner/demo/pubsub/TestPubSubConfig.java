package nhartner.demo.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gcp.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import org.springframework.cloud.gcp.autoconfigure.pubsub.GcpPubSubProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Creates the topic and subscription in GCP emulator. If emulator is not running, this will hang.
 */
@Component
@AutoConfigureAfter({DemoSubscriber.class})
public class TestPubSubConfig {

    private final String topicName;
    private final String subscriptionName;
    private final PubSubResourceGenerator pubSubResourceGenerator;
    private final GcpPubSubProperties gcpPubSubProperties;
    private GenericContainer pubsubContainer;

    TestPubSubConfig(PubSubResourceGenerator pubSubResourceGenerator, GcpPubSubProperties gcpPubSubProperties, DemoConfig config) {
        this.pubSubResourceGenerator = pubSubResourceGenerator;
        this.topicName = config.getTopicName();
        this.subscriptionName = config.getSubscriptionName();
        this.gcpPubSubProperties = gcpPubSubProperties;
    }

    @PostConstruct
    void createResources() {
        int pubsubPort = 8085;
        pubsubContainer =
            new FixedHostPortGenericContainer("google/cloud-sdk:latest")
                .withFixedExposedPort(pubsubPort, pubsubPort)
                .withExposedPorts(pubsubPort)
                .withCommand(
                    "/bin/sh",
                    "-c",
                    String.format(
                        "gcloud beta emulators pubsub start --project %s --host-port=0.0.0.0:%d",
                        gcpPubSubProperties.getProjectId(), pubsubPort)
                )
                .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*started.*$"));

        pubsubContainer.start();
        pubSubResourceGenerator.createTopic(topicName);
        pubSubResourceGenerator.createSubscription(topicName, subscriptionName);
    }

    @PreDestroy
    void destroyResources() {
        pubsubContainer.stop();
    }

    @Bean
    public Publisher testPublisher() throws IOException {
        return pubSubResourceGenerator.createPublisher(topicName);
    }

}
