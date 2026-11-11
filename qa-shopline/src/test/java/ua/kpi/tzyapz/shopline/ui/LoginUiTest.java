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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("ui")
@DisplayName("TC-004: вхід до облікового запису")
class LoginUiTest {

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
    @DisplayName("TC-004: зареєстрована адреса з неправильним паролем показує повідомлення про помилку")
    void givenRegisteredEmailWrongPassword_whenLogin_errorMessageIsShown() {
        driver.get(BASE_URL + "auth/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='email']"))).sendKeys("shopline.ta.test@example.com");
        driver.findElement(By.cssSelector("[data-test='password']")).sendKeys("Test-pass1");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='login-submit']"))).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='login-error']")));

        assertEquals("Invalid email or password", error.getText());
    }
}
