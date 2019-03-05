package nhartner.demo.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;


@Service
class DemoSubscriber {

    private PubSubTemplate template;

    private final AtomicInteger receiveCount = new AtomicInteger();

    public DemoSubscriber(@Autowired PubSubTemplate template) {
        this.template = template;
    }

    @PostConstruct
    public void startSubscriber() {
        template.subscribe("test-subscription", (message) -> {
            receiveCount.incrementAndGet();
            message.ack();
        });
    }

    public int getReceiveCount() {
        return receiveCount.get();
    }
}
