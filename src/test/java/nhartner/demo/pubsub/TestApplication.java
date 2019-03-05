package nhartner.demo.pubsub;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * This class exists primarily to force the IT PubSub topic to be recreated before Spring PubSub gets bootstrapped
 */
@DependsOn("testPublisher")
@Configuration
@Import(PubsubEmulatorDemoApplication.class)
public class TestApplication {

}
