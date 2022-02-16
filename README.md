# Sample Java code with Cloud Bigtable

## Run locally

### Start Bigtable Emulator
```shell
gcloud components install bigtable
gcloud beta emulators bigtable start --host-port=0.0.0.0:8086

export BIGTABLE_EMULATOR_HOST=localhost:8086
cbt -project "project-id" -instance "bus-instance" createtable chat_messages
cbt -project "project-id" -instance "bus-instance" createfamily chat_messages chatRoomDetails
cbt -project "project-id" -instance "bus-instance" createfamily chat_messages chatMessageDetails
cbt -project "project-id" -instance "bus-instance" read chat_messages
```

### Start application

```shell
./gradlew bootRun
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

# Get Messages from room

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
