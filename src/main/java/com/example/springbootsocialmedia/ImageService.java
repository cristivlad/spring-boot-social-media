package com.example.springbootsocialmedia;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Paths.get;
import static org.springframework.util.FileCopyUtils.copy;
import static org.springframework.util.FileSystemUtils.deleteRecursively;
import static reactor.core.publisher.Mono.fromSupplier;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static String UPLOAD_ROOT = "upload-dir";
    private final ResourceLoader resourceLoader;
    private final ImageRepository imageRepository;

    public Flux<Image> findAllImages() {
        return imageRepository.findAll();
    }

    public Mono<Resource> findOneImage(String filename) {
        return fromSupplier(() -> resourceLoader.getResource("file: " + UPLOAD_ROOT + "/" + filename));
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(file -> {
            Mono<Image> saveDbImage = imageRepository.save(new Image(UUID.randomUUID().toString(), file.filename()));
            Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT, file.filename()).toFile())
                    .log("createImage-picktarget")
                    .map(destFile -> {
                        try {
                            destFile.createNewFile();
                            return destFile;
                        } catch (IOException exception) {
                            throw new RuntimeException();
                        }
                    })
                    .log("createImage-newfile")
                    .flatMap(file::transferTo)
                    .log("createImage-copy");
            return Mono.when(saveDbImage, copyFile);
        })
                .then();
    }

    public Mono<Void> deleteImage(String filename) {
        Mono<Void> deleteDbImage = imageRepository.findByName(filename)
                .flatMap(imageRepository::delete);
        Mono<Void> deleteFile = Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
            } catch (IOException exception) {
                throw new RuntimeException();
            }
        });
        return Mono.when(deleteDbImage, deleteFile).then();
    }

    @Bean
    CommandLineRunner setUp() {
        return ImageService::run;
    }
    private static void run(String... args) throws IOException {
        deleteRecursively(new File(UPLOAD_ROOT));

        createDirectory(get(UPLOAD_ROOT));

        copy("Test File", new FileWriter(UPLOAD_ROOT + "/learning-spring-boot-cover.jpg"));
        copy("Test File2", new FileWriter(UPLOAD_ROOT + "/learning-spring-boot-2nd-edition-cover.jpg"));
        copy("Test File3", new FileWriter(UPLOAD_ROOT + "/bazinga.png"));
    }
}
