package com.example.springbootsocialmedia;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

    static ChromeDriverService service;
    static ChromeDriver driver;
    @LocalServerPort
    int port;

    @BeforeAll
    public static void setUp() throws IOException {
        System.setProperty("webdriver.chrome.driver", "ext/chromedriver");
        service = ChromeDriverService.createDefaultService();
        driver = new ChromeDriver(service);
        Path testResults = Paths.get("build", "test-results");
        if (!Files.exists(testResults))
            Files.createDirectory(testResults);
    }

    @AfterAll
    public static void tearDown() {
        service.stop();
    }

    @Test
    public void homePageShouldWork() throws IOException {
        driver.get("http://localhost:" + port);

        takeScreenshot("homePageShouldWork-1");

        assertEquals(driver.getTitle(), "Learning Spring Boot: Spring-a-Gram");

        String pageContent = driver.getPageSource();

        assertTrue(pageContent
                .contains("<a href=\"/images/bazinga.png/raw\">"));

        WebElement element = driver.findElement(
                By.cssSelector("a[href*=\"bazinga.png\"]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().perform();

        takeScreenshot("homePageShouldWork-2");

        driver.navigate().back();
    }

    private void takeScreenshot(String name) throws IOException {
        FileCopyUtils.copy(
                driver.getScreenshotAs(OutputType.FILE),
                new File("build/test-results/TEST-" + name + ".png"));
    }
}
