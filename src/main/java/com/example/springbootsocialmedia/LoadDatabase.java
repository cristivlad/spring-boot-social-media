package com.example.springbootsocialmedia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LoadDatabase {

    private static void accept(Image image) {
        System.out.println(image.toString());
    }

    @Bean
    CommandLineRunner init(ChapterRepository repository) {
        return args -> Flux.just(
            new Chapter("Quick start with Java"),
            new Chapter("Reactive Web with Spring Boot"),
            new Chapter("... and more!"))
                .flatMap(repository::save)
                .subscribe(System.out::println);
    }

    @Bean
    CommandLineRunner initMongo(ReactiveMongoOperations operations) {
        return args -> {
            operations.dropCollection(Image.class);

            operations.insert(new Image("1", "learning-spring-boot-Cover.jpg"));
            operations.insert(new Image("2", "learning-spring-boot-2nd-ed-Cover.jpg"));
            operations.insert(new Image("3", "bazinga.png"));

            operations.findAll(Image.class).subscribe(LoadDatabase::accept);
        };
    }
}
