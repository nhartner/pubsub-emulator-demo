# pubsub-emulator-demo
Example Spring project showing how to use launch GCP emulator and test spring GCP pubsub in a test.

Key points:
- test-containers library is used to launch google-sdk docker container and start pubsub emulator inside it
- Tests are overridden to hit emulator via `src/test/resources/test.properties` by setting `spring.cloud.gcp.pubsub-emulator-host: localhost:8085` (default emulator port)
- `TestPubSubConfig` is used to bring up pubsub emulator inside a docker container, create a topic and subscription in the emulator. This is using GCP SDK directly, not Spring pubsub, to create the resources. 

test change
