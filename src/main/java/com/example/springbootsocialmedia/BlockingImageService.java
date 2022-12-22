package com.example.springbootsocialmedia;

import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
public class BlockingImageService {

    private final ImageService imageService;

    public List<Image> findAllImages() {
        return imageService.findAllImages()
                .collectList()
                .block(ofSeconds(10));
    }
}
