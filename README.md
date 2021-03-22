# gct-trace-file-replay-server

A server for replaying recorded GCT trace files, containing mainframe/host screen sessions.
There are other software/tools out there which are able to record/produce such GCT trace files
for host screen sessions.

# Known and missing features

This is a simple service implementation, able to handle one ore more parallel connections.
When a client connection is established, it replays each screen, until the connection is closed.
This is a great approach/tool, when debugging tn5250j connection issues.

##### Existing features
* port to serve is configurable
* the GCT trace file is served
* simple logging exists

##### Missing features 
* no checking of incoming bytes (purely send data back)
* no fancy other stuff
* no encryption/decryption support
* no recording available

### How to run

Compile the server code first (just once)
```shell
mvn package
```

Run the server second
```shell

java -jar target/gct-trace-file-replay-server-1.0-SNAPSHOT-jar-with-dependencies.jar --help
```

### Command line parameters

```
Usage: java -jar server.jar [-p=<port>] <file>
      <file>          The GCT trace file to use.
  -p, --port=<port>   the TCP port to listen to.
                        Default: 23235
```

### GCT file format in a nutshell

The GCT trace file format is ASCII text based and can easily be send around via email.
It is separated in numbered sections and identifies 'Recv' as well as 'Send' data.
This is an excerpt of a session start ...

```
[0]
NumberScreens=000002
Version=3.0.3.0029
NumberSections=00003
Started=03/18/21 10:05:07
Ended=03/18/21 10:08:22
Auto Reconnect=N
Language=0
Model=0
Device Type=
Translation Table=
Host Address=127.0.0.1
Port=23
Flicker=0

[1]
Time=10:05:07.117
RecType=Recv
Length=6
000000= FF FD 27 FF | FD 18 

[2]
Time=10:05:07.120
RecType=Send
Length=6
000000= FF FB 27 FF | FB 18 

```
