# DITAS - SecureDBService

This service takes a POST request with a MultipartFile in the body and returns an URL with which you can retrieve the file later. 
The file is stored encrypted in a Minio DB that only the service has access to. 
Whenever the service shuts down the file is lost since the key is only stored in memory. 
The file is retrieved by a simple GET request to the service with the URL that was returned earlier.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Installing

For the docker approach, you can use the provided dockerfile to build a running artifact as a Docker container.

build the docker container:
```
docker build -f Dockerfile.artifact .
```

run the Docker container:
```
docker run [container-id]
```

## Configuration
Default port is 8080

## Built With

* [minio](https://github.com/minio/minio)

## License

This project is licensed under the Apache 2.0 - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

This is being developed for the [DITAS Project](https://www.ditas-project.eu/)
