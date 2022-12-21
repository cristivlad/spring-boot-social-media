package com.example.springbootsocialmedia;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static reactor.core.publisher.Mono.just;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;

    @GetMapping
    public String greeting(@RequestParam(required = false, defaultValue = "") String name) {
        return "".equals(name) ? "Hey!" : "Hey, " + name + "!";
    }

    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        return ResponseEntity.badRequest()
                                .body("Couldn't find " + filename + " => " + e.getMessage());
                    }
                });
    }

    @PostMapping(BASE_PATH)
    public Mono<String> createFile(@RequestPart(name = "file")Flux<FilePart> files) {
        return imageService.createImage(files)
                .then(just("redirect:/"));
    }

    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono<String> deleteFile(@PathVariable String filename) {
        return imageService.deleteImage(filename)
                .then(just("redirect:/"));
    }
}
