# Graylog GELF message forwarder

Simple command-line application to send messages to [Graylog](https://www.graylog.org/):

- uses Graylog HTTP API to send [GELF](https://docs.graylog.org/docs/gelf) messages;
- input data is provided in a file;
- each line of the input file is represented by JSON formatted text;

## Usage
The application accepts a single mandatory parameter: path to the input file. Example:
 ```
 $ java -jar graylog-forwarder.jar /var/log/input.txt
 ```
Please note, specific jar file name might be different right after compilation and could be found in `target/` directory. For example `target/graylog-forwarder-1.0-SNAPSHOT.jar`

## Setup and compilation
Project is using Java 11, Maven and is built with Spring Boot framework. Graylog destination URL should be configured in `application.yml`. Default settings are:
 ```
graylog:
  client:
    url: http://192.168.99.100:12201/gelf
 ```
so please, change it to your actual running Graylog instance URL.

#### Overriding default settings in the runtime
Alternatively you can also provide your Graylog instance URL as an additional command line parameter without the need to recompile the project:
```
$ java -jar graylog-forwarder.jar /var/log/input.txt --graylog.client.url="http://localhost:9000/gelf"
```
#### Compiling
In order to obtain executable JAR after checking out the project please run:
 ```
 $ mvn clean package
 ```
Result (jar file) could be found in `target/` directory of the project and is typically named like `graylog-forwarder-1.0-SNAPSHOT.jar`

## Development mode
In order to test the application without having your production Graylog instance running you could use the docker compose file provided inside the project. It will allow you to have a simple Graylog instance running in Docker containers.

1. Update the `GRAYLOG_HTTP_EXTERNAL_URI` variable inside `docker/docker-compose.yml` to point to your docker machine external URL. For example `http://localhost:9000`
2. Execute `$ docker-compose -f docker/docker-compose.yml up -d` in your project root.
3. After it start you should be able to access Graylog web interface `http://localhost:9000` using default login/password credentials `admin / admin`.
4. Don't forget to add [GELF HTTP input](https://docs.graylog.org/docs/sending-data#gelf-via-http) in Graylog settings.