package com.example.springbootsocialmedia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {
    @Test
    void imagesManagedByLombokShouldWork() {
        Image image = new Image("id", "file-name.jpg");

        assertEquals("id", image.getId());
        assertEquals("file-name.jpg", image.getName());
    }
}