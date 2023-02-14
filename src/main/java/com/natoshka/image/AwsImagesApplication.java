package com.natoshka.image;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Develop a web application that allows users to upload images to the application.
 * The uploaded images should be stored in an S3 bucket.
 * The application should process the images and generate thumbnails for each uploaded image.
 * The thumbnails should also be stored in the S3 bucket.
 * The application should store image metadata (such as image name, size, and upload date) in an RDS database.
 * The main application should be deployed on EC2 instances and should use SQS to send image for generating thumbnails.
 *
 */

@EnableScheduling
@EnableEncryptableProperties
@SpringBootApplication(scanBasePackages = {"com.natoshka.image"})
public class AwsImagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsImagesApplication.class, args);
	}

}
