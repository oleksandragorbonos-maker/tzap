package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Tag("ui")
@DisplayName("TC-028, TC-014: кошик — додавання і зміна кількості")
class CartUiTest {

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
    @DisplayName("TC-028: додавання першого товару до кошика збільшує cart-quantity до 1")
    void givenProductPage_whenAddToCart_cartQuantityIsOne() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test^='product-01']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='add-to-cart']"))).click();

        WebElement cartQty = wait.until(d -> {
            WebElement el = d.findElement(By.cssSelector("[data-test='cart-quantity']"));
            return "1".equals(el.getText()) ? el : null;
        });
        assertEquals("1", cartQty.getText());
    }

    @Test
    @DisplayName("TC-014: зміна кількості товару в кошику перераховує Total")
    void givenItemInCart_whenQuantityChanged_totalIsUpdated() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test^='product-01']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='add-to-cart']"))).click();
        wait.until(d -> {
            WebElement el = d.findElement(By.cssSelector("[data-test='cart-quantity']"));
            return "1".equals(el.getText()) ? el : null;
        });

        driver.get(BASE_URL + "checkout");
        String initialTotal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='cart-total']"))).getText();

        WebElement qtyInput = driver.findElement(By.cssSelector("[data-test='product-quantity']"));
        qtyInput.clear();
        qtyInput.sendKeys("2");
        qtyInput.sendKeys(Keys.TAB);

        wait.withMessage("Total не змінився після зміни кількості").until(d ->
                !d.findElement(By.cssSelector("[data-test='cart-total']")).getText().equals(initialTotal));

        String newTotal = driver.findElement(By.cssSelector("[data-test='cart-total']")).getText();
        assertNotEquals(initialTotal, newTotal, "Total не змінився після зміни кількості на 2");
    }
}
