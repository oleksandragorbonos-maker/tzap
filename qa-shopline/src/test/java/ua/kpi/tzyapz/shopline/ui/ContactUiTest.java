package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("TC-009: контактна форма — валідація повідомлення")
class ContactUiTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,800");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("TC-009: повідомлення з 49 символами не надсилається — показує помилку мінімум 50 символів")
    void givenMessage49Chars_whenSubmit_validationErrorIsShown() {
        driver.get(BASE_URL + "contact");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='first-name']"))).sendKeys("Test");
        driver.findElement(By.cssSelector("[data-test='last-name']")).sendKeys("User");
        driver.findElement(By.cssSelector("[data-test='email']")).sendKeys("test@example.com");
        new Select(driver.findElement(By.cssSelector("[data-test='subject']")))
                .selectByValue("warranty");
        driver.findElement(By.cssSelector("[data-test='message']")).sendKeys("A".repeat(49));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='contact-submit']"))).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='message-error']")));
        assertTrue(error.getText().contains("minimal 50 characters"),
                "Очікувалося 'minimal 50 characters', отримано: " + error.getText());
        assertTrue(driver.findElements(By.cssSelector(".alert-success")).isEmpty(),
                "Форма надіслала повідомлення попри 49 символів");
    }
}
