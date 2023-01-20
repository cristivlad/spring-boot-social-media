package com.example.springbootsocialmedia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.thymeleaf.Thymeleaf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
class HomeControllerTest {

    @Autowired
    WebTestClient webClient;
    @MockBean
    ImageService service;

    @Test
    void baseRouteShouldListAllImages() {
        Image alphaImage = new Image("1", "alpha.png");
        Image bravoImage = new Image("2", "bravo.png");
        when(service.findAllImages()).thenReturn(Flux.just(alphaImage, bravoImage));

        EntityExchangeResult<String> result = webClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk().expectBody(String.class).returnResult();

        verify(service).findAllImages();
        verifyNoMoreInteractions(service);
//        assertTrue(result.getResponseBody().contains("<title>Learning Spring Boot: Spring-a-Gram</title>"));
    }

    @Test
    void fetchingImageShouldWork() {
        when(service.findOneImage(anyString())).thenReturn(Mono.just(new ByteArrayResource("data".getBytes())));

        webClient.get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("data");

        verify(service).findOneImage("alpha.png");
        verifyNoMoreInteractions(service);
    }

    @Test
    void fetchingNullImageShouldFail() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(new IOException("Bad file"));
        when(service.findOneImage(anyString())).thenReturn(Mono.just(resource));

        webClient.get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Couldn't find alpha.png => Bad file");

        verify(service).findOneImage("alpha.png");
        verifyNoMoreInteractions(service);
    }

    @Test
    void deleteImageShouldWork() {
        Image alphaImage = new Image("1", "alpha.png");
        when(service.deleteImage(anyString())).thenReturn(Mono.empty());

        webClient.delete().uri("/images/alpha.png")
                .exchange()
                        .expectStatus().isOk();
//                .expectStatus().isSeeOther()
//                .expectHeader().valueEquals("location", "/");

        verify(service).deleteImage("alpha.png");
        verifyNoMoreInteractions(service);
    }
}