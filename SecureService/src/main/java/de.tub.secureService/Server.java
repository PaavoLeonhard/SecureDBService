package de.tub.secureService;

import de.tub.secureService.service.MinioUploader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Server {

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}

	@Bean
	public MinioUploader minioUploader() {
		MinioUploader minio = new MinioUploader();
		return minio;
	}
}
