package com.example.springbootsocialmedia;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
public class BlockingImageService {

    private final ImageService imageService;

    public List<Image> findAllImages() {
        return imageService.findAllImages()
                .collectList()
                .block(ofSeconds(10));
    }

    public Resource findOneImage(String filename) {
        return imageService.findOneImage(filename)
                .block(ofSeconds(30));
    }

    public void createImage(List<FilePart> files) {
        imageService.createImage(Flux.fromIterable(files))
                .block(ofMinutes(1));
    }

    public void deleteImage(String filename) {
        imageService.deleteImage(filename)
                .block(ofSeconds(10));
    }
}
