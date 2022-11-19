package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final GitHubLookupService gitHubLookupService;

    private final DogLookupService dogLookupService;

    public AppRunner(GitHubLookupService gitHubLookupService, DogLookupService dogLookupService) {
        this.gitHubLookupService = gitHubLookupService;
        this.dogLookupService = dogLookupService;
    }

    @Override
    public void run(String... args) throws Exception {
//        executeCompFutureWithCompose();
        executeCompFutureAndSaveToDb();
    }


    public void executeCompFutureWithThenApply() throws Exception{
        long start = System.currentTimeMillis();
        List<CompletableFuture<Image>> completableFuturesImageList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            CompletableFuture<Dog> completableFutureDog = dogLookupService.fetchDog();
            CompletableFuture<Image> completableFutureImage = completableFutureDog.thenApply(dogFuture -> {
                Image downloadedImage = null;
                try{
                    downloadedImage = dogLookupService.downloadImage(dogFuture.getMessage());
                }catch(Exception e){
                    logger.error(e.getMessage());
                }
                return downloadedImage;
            });
            completableFuturesImageList.add(completableFutureImage);
        }
        CompletableFuture.allOf(completableFuturesImageList.toArray(new CompletableFuture[completableFuturesImageList.size()])).join();
        printLogger(completableFuturesImageList, start);
    }

    public void executeCompFutureWithCompose() throws Exception{
        long start = System.currentTimeMillis();
        List<CompletableFuture<Image>> completableFuturesImageList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            CompletableFuture<Dog> completableFutureDogs = dogLookupService.fetchDog().exceptionally(e -> {
                logger.error("Something went wrong while fetching your dog picture ! " + e.getMessage());
                return null;
            });
            
            CompletableFuture<Image> completableFutureImages = completableFutureDogs.thenCompose(dog -> {
                return dogLookupService.downloadImageAsync(dog.getMessage());
            }).exceptionally(e -> {
                logger.error("Something went wrong while downloading your dog picture ! " + e.getMessage());
                return null;
            });

            completableFuturesImageList.add(completableFutureImages);
        }

        CompletableFuture.allOf(completableFuturesImageList.toArray(new CompletableFuture[completableFuturesImageList.size()])).join();
        printLogger(completableFuturesImageList, start);
    }

    public void executeCompFutureAndSaveToDb() throws Exception{
        long start = System.currentTimeMillis();
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        for(int i = 0; i < 20; i ++){

            CompletableFuture<Void> completableFutureDogs = dogLookupService.fetchDog().exceptionally(e -> {
                logger.error("Something went wrong while fetching your dog picture ! " + e.getMessage());
                return null;
            }).thenAccept(dog -> {
                dogLookupService.saveDogImage(dog);
                dogLookupService.downloadImageAsync(dog.getMessage());
            }).exceptionally(e -> {
                logger.error("An error occured when saving your dog image to H2 database: " + e.getMessage());
                return null;
            });

            completableFutures.add(completableFutureDogs);
        }
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
//        printLoggerFutures(completableFutures, start);

    }

    public void printLogger(List<CompletableFuture<Image>> completableFuturesImageList, Long start) throws Exception{
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        for(CompletableFuture<Image> completableFutureImage : completableFuturesImageList){
            logger.info("--> " + completableFutureImage.get());
            logger.info("");
        }
    }

    public void printLoggerFutures(List<CompletableFuture<Void>> completableFuturesList, Long start) throws Exception{
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        for(CompletableFuture<Void> completableFuture : completableFuturesList){
            logger.info("--> " + completableFuture.get());
            logger.info("");
        }
    }

}