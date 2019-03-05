package nhartner.demo.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Creates the topic and subscription in GCP emulator. If emulator is not running, this will hang.
 */
@Component
public class TestPubSubConfig {

    private final String topicName;
    private final String subscriptionName;
    private final PubSubResourceGenerator pubSubResourceGenerator;

    TestPubSubConfig(@Value("${nhartner.demo.topic.name}") String topicName,
                     @Value("${nhartner.demo.subscription.name}") String subscriptionName,
                     PubSubResourceGenerator pubSubResourceGenerator) {
        this.topicName = topicName;
        this.subscriptionName = subscriptionName;
        this.pubSubResourceGenerator = pubSubResourceGenerator;
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
