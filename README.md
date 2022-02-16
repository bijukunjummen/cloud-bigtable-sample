# Live Notification

## Run locally

### Start Redis

```shell
redis-server
```

### Start Cloud Firestore emulator

```shell
gcloud components install cloud-firestore-emulator
gcloud beta emulators firestore start --host-port=:8662
```

OR

### Start Bigtable Emulator

```shell
gcloud components install bigtable
gcloud beta emulators bigtable start --host-port=8086
```

OR

### Start Spanner Emulator

```shell
gcloud components install spanner
gcloud emulators spanner start

# In another window
gcloud config configurations create emulator
gcloud config set auth/disable_credentials true
gcloud config set project sample-project
gcloud config set api_endpoint_overrides/spanner http://localhost:9020/
gcloud spanner instances create test-instance \
   --config=emulator-config --description="Test Instance" --nodes=1
```

### Start application

```shell
# With Firestore
SPRING_PROFILES_ACTIVE="local,firestore" ./gradlew bootRun
# With Bigtable
SPRING_PROFILES_ACTIVE="local,bigtable" ./gradlew bootRun
# With Spanner
SPRING_PROFILES_ACTIVE="local,spanner" ./gradlew bootRun
```
# Create chat rooms

```sh
curl -v -X POST  \
  -H "Content-type: application/json" \
  -H "Accept: application/json" \
   http://localhost:8080/chatrooms \
   -d '{
   "id": "some-room",
   "name": "some-room"
}'
```

# Get Chat Room

```sh
curl -v http://localhost:8080/chatrooms/some-room
```

# Stream Messages from room

```sh
curl -v http://localhost:8080/messages/some-room
```

# Add a messsage to a room

```sh
curl -v -X POST \
  -H "Content-type: application/json" \
  -H "Accept: application/json" \
   http://localhost:8080/messages/some-room \
   -d '{
   "payload": "hello world"
}'
```

It should show up on the previous GET endpoint

