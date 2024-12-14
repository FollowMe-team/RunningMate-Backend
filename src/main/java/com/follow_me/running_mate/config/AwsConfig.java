package com.follow_me.running_mate.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.first}")
    private String firstRegion;

    @Value("${cloud.aws.region.second}")
    private String secondRegion;

    @Value("${cloud.aws.s3.bucket.image}")
    private String imageBucket;

    @Value("${cloud.aws.s3.bucket.course}")
    private String courseBucket;

    private AWSCredentials credentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    @Primary
    public AmazonS3 amazonS3Image() {
        return AmazonS3ClientBuilder.standard()
            .withRegion(firstRegion)
            .withCredentials(new AWSStaticCredentialsProvider(credentials()))
            .build();
    }

    @Bean
    @Qualifier("amazonS3Course")
    public AmazonS3 amazonS3Course() {
        return AmazonS3ClientBuilder.standard()
            .withRegion(secondRegion)
            .withCredentials(new AWSStaticCredentialsProvider(credentials()))
            .build();
    }

    @Bean
    public AWSLambda awsLambda() {
        return AWSLambdaClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials()))
            .withRegion(firstRegion)
            .build();
    }

    @Bean
    public String imageBucket() {
        return imageBucket;
    }

    @Bean
    @Qualifier("courseBucket")
    public String courseBucket() {
        return courseBucket;
    }
}



