# pubsub-emulator-demo
Example Spring project showing how to use GCP emulator in a test

Key points:
- Tests are overridden to hit emulator via `src/test/resources/application.properties` by setting `spring.cloud.gcp.pubsub-emulator-host: localhost:8085` (default emulator port)
- `TestPubSubConfig` is used to create a topic and subscription in the emulator. This is using GCP SDK directly, not Spring pubsub, to create the resources. Alternatively, the topic and subscription could be created manually using the Python GCP CLI (see https://cloud.google.com/pubsub/docs/emulator)
- `TestApplication` is used as a hack to force Spring to run TestPubSubConfig initialization before loading the Spring Boot app. This is necessary so that the topic and subscription get created before the Spring Boot application loads and tries to initialize PubSubTemplate and connect to the subscription. This hack isn't necessary if the topic and subscription are created ahead of time manually using the above mentioned Python GCP CLI.
