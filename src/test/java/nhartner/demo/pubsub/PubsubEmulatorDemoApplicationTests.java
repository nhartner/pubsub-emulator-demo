package nhartner.demo.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { PubsubEmulatorDemoApplication.class })
@TestPropertySource(locations="classpath:test.properties")
public class PubsubEmulatorDemoApplicationTests {

    @Autowired
    private DemoSubscriber subscriber;

    @Autowired
    private Publisher testPublisher;

    @Test
    public void testSubscriber() throws InterruptedException {
        Assert.assertEquals(0, subscriber.getReceiveCount());
        testPublisher.publish(toMessage("hello"));
        Thread.sleep(1000);
        Assert.assertEquals(1, subscriber.getReceiveCount());
    }

    private static PubsubMessage toMessage(String message) {
        ByteString data = ByteString.copyFromUtf8(message);
        return PubsubMessage.newBuilder().setData(data).build();
    }

}
