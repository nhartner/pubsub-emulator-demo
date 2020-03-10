package nhartner.demo.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Testing
 */
@Service
class DemoSubscriber {

    private final PubSubTemplate template;
    private final String subscriptionName;

    private final AtomicInteger receiveCount = new AtomicInteger();

    public DemoSubscriber(PubSubTemplate template, DemoConfig config) {
        this.template = template;
        this.subscriptionName = config.getSubscriptionName();
    }

    @PostConstruct
    public void startSubscriber() {
        template.subscribe(subscriptionName, (message) -> {
            receiveCount.incrementAndGet();
            message.ack();
        });
    }

    public int getReceiveCount() {
        return receiveCount.get();
    }
}
