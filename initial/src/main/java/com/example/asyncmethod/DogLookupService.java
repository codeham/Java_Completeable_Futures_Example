package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class DogLookupService {
    private static final Logger logger = LoggerFactory.getLogger(DogLookupService.class);

    private final RestTemplate restTemplate;

    private final DogImagesRepo dogImagesRepo;

    public DogLookupService(RestTemplateBuilder restTemplateBuilder, DogImagesRepo dogImagesRepo) {
        this.restTemplate = restTemplateBuilder.build();
        this.dogImagesRepo = dogImagesRepo;
    }

    public void saveDogImage(Dog dog){
        DogImages dogImage = new DogImages();
        dogImage.setUrl(dog.getMessage());
        dogImage.setStatus(dog.getStatus());
        dogImagesRepo.save(dogImage);
        logger.info("Saved Dog Image to H2 Database");
    }

    @Async
    public CompletableFuture<Dog> fetchDog() throws InterruptedException{
        logger.info("Fetching dog...");
        String url = String.format("https://dog.ceo/api/breeds/image/random");
        Dog results = restTemplate.getForObject(url, Dog.class);
        Thread.sleep(1000L);
        logger.info("Dog Image Fetched: " + results.getMessage());
        return CompletableFuture.completedFuture(results);
    }

    public void downloadImageVoid(String imageUrl) throws IOException{
        Image image = null;
        try{
            URL url = new URL(imageUrl);
            image = ImageIO.read(url);
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
    }


    public Image downloadImage(String imageUrl) throws IOException{
        Image image = null;
        try{
            URL url = new URL(imageUrl);
            image = ImageIO.read(url);
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }

        return image;
    }

    @Async
    public CompletableFuture<Image> downloadImageAsync(String imageUrl){
        Image image = null;
        try{
            logger.info("Downloading Dog Image...");
            URL url = new URL(imageUrl);
            image = ImageIO.read(url);
            BufferedImage bf = ImageIO.read(url);
            Path fileName = Paths.get(url.toString());
            File outputImageFile = new File("/Users/cristianavinalopez/Downloads/Music_Downloads/gs-async-method-main/initial/src/main/resources/output_images/" +
                    fileName.getFileName().toString());
            logger.info("Writing to path: " + outputImageFile.toString());
            ImageIO.write(bf, "jpg", outputImageFile);
        }catch(IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(image);
    }
}
