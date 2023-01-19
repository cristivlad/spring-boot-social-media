package com.example.springbootsocialmedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class ImageRepositoryTest {
    @Autowired
    ImageRepository repository;
    @Autowired
    ReactiveMongoOperations operations;

    @BeforeEach
    public void setUp() {
        operations.dropCollection(Image.class);
        operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
        operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
        operations.insert(new Image("3", "bazinga.png"));

//        operations.findAll(Image.class).forEach(image -> System.out.println(image.toString()));
        operations.findAll(Image.class).subscribe(System.out::println);
    }

    @Test
    void findAllShouldWork() {
        Flux<Image> images = repository.findAll();
        StepVerifier.create(images)
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(results -> {
                    assertEquals(3, results.size());
                    assertTrue(results.contains("learning"));
                })
                .expectComplete().verify();
    }

    @Test
    void findByNameShouldWork() {
        Mono<Image> image = repository.findByName("bazinga.png");
        StepVerifier.create(image)
                .expectNextMatches(results -> {
                    assertEquals("bazinga.png", results.getName());
                    assertEquals("3", results.getId());
                    return true;
                });
    }
}