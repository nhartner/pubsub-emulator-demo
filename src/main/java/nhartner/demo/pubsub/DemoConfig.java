package nhartner.demo.pubsub;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Testing
 */
@Configuration
@ConfigurationProperties(prefix = "nhartner.demo")
public class DemoConfig {

  private String topicName;
  private String subscriptionName;

  public String getSubscriptionName() {
    return subscriptionName;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setSubscriptionName(String subscriptionName) {
    this.subscriptionName = subscriptionName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

}
