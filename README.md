Wasteless

# The Wasteless Backen
#### Table of contents
* :information_source: [About](#information_source-about)
* :construction_worker: [IDE](#information_source-ide)
* :hammer_and_pick: [Building](#hammer_and_pick-building)
* :building_construction: [Development](#building_construction-development)
* :page_with_curl: [Guidelines](#page_with_curl-guidelines)
* :key: [Execution](#key-execution)
* :ship: [Releasing](#ship-releasing)

## :information_source: About

This project contains the backend that power the Wasteless app. 
The Wasteless backend is a Java 11 Spring Boot web applications.

## :construction_worker: IDE

The project use the library [Immutables][immutables] to generate all POJOs.
The following [Immutables Instructions][Instructions] will help you to setup you IDE accordingly. 

## :hammer_and_pick: Building

The project is built using [Apache Maven][maven]. Please use the latest version
whenever possible and do not rely on the version shipped with your IDE, if any.

Build command:
```
$ docker-compose up mongodb 
$ mvn clean install
```

**Before pushing any commits, always run all unit and integration tests locally (i.e. by using the second command from 
above).**

## :page_with_curl: Guidelines

TBD

## :key: Execution

This service is deployed using [Docker][docker] containers.
To run this service locally, [Docker Compose][docker-compose] is recommended.
After [installing][docker-install-ubuntu] Docker, also have a look at the
[post-installation steps][docker-postinstall-linux].

### Local execution using Docker Compose

Assuming Docker Compose has been installed, it can be used to run the Wastelss Backend. 
The Docker Compose configuration can be found in `docker-compose.yml`. 
It shows the available services, their names, their dependencies, the ports on which they're exposed.

#### Running

The basic steps to bring up and tear down services using Docker Compose are:

1. Run Maven to assemble the WAR files that contain the services you wish to
   deploy.
2. Run `docker-compose up --buil wasteless-app`.

It is also possible (and perhaps preferred) to run the services in "detached mode". In that case:

1. Start the services using `docker-compose up -d [<service>...]`.
2. Inspect the logs using `docker-compose logs -f [<service>...]`.
3. Stop services using `docker-compose stop [<service>...]`.

In order to remove all resources created by `docker-compose up`, such as
containers, volumes and images, run `docker-compose down`.

## :ship: Releasing

TBD
 
#### Swagger UI

Once the Wasteless Backend is up and running, you can access the Swagger UI through this URL:
`http://localhost:8080/swagger-ui.html`

Once you have created an account (See `Authentication` Swagger definition), retrieve the token
from the log in Authorization response header.
You can use this token in the authorize form (right-top button) without the bearer suffix to make
all the next requests authenticated.

[Immutables Instructions]: https://immutables.github.io/apt.html
[Immutables]: https://help.github.com/en/articles/connecting-to-github-with-ssh
[Immutables]: https://immutables.github.io/
[docker-compose]: https://docs.docker.com/compose
[docker-install-ubuntu]: https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu
[docker-postinstall-linux]: https://docs.docker.com/engine/installation/linux/linux-postinstall
[docker]: https://docs.docker.com/
[github-ssh]: https://help.github.com/en/articles/connecting-to-github-with-ssh
[maven-release-plugin]: http://maven.apache.org/maven-release/maven-release-plugin/
[maven]: https://maven.apache.org
[mongodb]: https://www.mongodb.com
