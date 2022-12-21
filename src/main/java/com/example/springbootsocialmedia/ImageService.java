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

import static java.lang.String.valueOf;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Paths.get;
import static org.springframework.util.FileCopyUtils.copy;
import static org.springframework.util.FileSystemUtils.deleteRecursively;
import static reactor.core.publisher.Flux.empty;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Mono.fromSupplier;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static String UPLOAD_ROOT = "upload-dir";
    private final ResourceLoader resourceLoader;

    public Flux<Image> findAllImages() {
        try {
            return fromIterable(newDirectoryStream(get(UPLOAD_ROOT)))
                    .map(path -> new Image(valueOf(path.hashCode()), path.getFileName().toString()));
        } catch (IOException exc) {
            return empty();
        }
    }

    public Mono<Resource> findOneImage(String filename) {
        return fromSupplier(() -> resourceLoader.getResource("file: " + UPLOAD_ROOT + "/" + filename));
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(filePart -> filePart.transferTo(get(UPLOAD_ROOT, filePart.filename()).toFile())).then();
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
