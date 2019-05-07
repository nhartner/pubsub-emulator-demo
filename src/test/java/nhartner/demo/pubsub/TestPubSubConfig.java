package nhartner.demo.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gcp.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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

    TestPubSubConfig(PubSubResourceGenerator pubSubResourceGenerator, DemoConfig config) {
        this.pubSubResourceGenerator = pubSubResourceGenerator;
        this.topicName = config.getTopicName();
        this.subscriptionName = config.getSubscriptionName();
    }

    @PostConstruct
    void createTopicAndSub() {
        pubSubResourceGenerator.createTopic(topicName);
        pubSubResourceGenerator.createSubscription(topicName, subscriptionName);
    }

    @Bean
    public Publisher testPublisher() throws IOException {
        return pubSubResourceGenerator.createPublisher(topicName);
    }

}
