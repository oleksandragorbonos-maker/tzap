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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("TC-001 (F-60): пошук товарів без входу")
class SearchUiTest {

    private static final String BASE_URL = "https://practicesoftwaretesting.com/";
    private static final String QUERY = "pliers";

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
    @DisplayName("TC-022: після пошуку «Hammer» натискання «X» скидає результати")
    void givenSearchQuery_whenResetSearch_moreProductsAppear() {
        driver.get(BASE_URL);
        String firstNameBefore = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='product-name']"))).getText();

        WebElement searchField = driver.findElement(By.cssSelector("[data-test='search-query']"));
        searchField.sendKeys("Hammer");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='search-submit']"))).click();

        wait.withMessage("Результати пошуку «Hammer» не завантажились").until(d -> {
            List<WebElement> names = d.findElements(By.cssSelector("[data-test='product-name']"));
            return !names.isEmpty()
                    && names.stream().allMatch(n -> n.getText().toLowerCase().contains("hammer"));
        });
        int hammerCount = driver.findElements(By.cssSelector("[data-test='product-name']")).size();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='search-reset']"))).click();

        wait.withMessage("Після очищення список не збільшився").until(d ->
                d.findElements(By.cssSelector("[data-test='product-name']")).size() > hammerCount);

        List<String> allNames = driver.findElements(By.cssSelector("[data-test='product-name']"))
                .stream().map(WebElement::getText).toList();
        assertAll(
                () -> assertFalse(allNames.isEmpty(), "Список після очищення порожній"),
                () -> assertTrue(allNames.size() > hammerCount,
                        "Після очищення не більше товарів ніж при пошуку «Hammer»"),
                () -> assertTrue(allNames.contains(firstNameBefore),
                        "Перша картка «" + firstNameBefore + "» відсутня після очищення пошуку")
        );
    }

    @Test
    @DisplayName("TC-001: запит «pliers» у нижньому регістрі повертає лише товари зі словом Pliers у назві")
    void givenLowercaseQuery_whenSearch_allProductNamesContainQuery() {
        driver.get(BASE_URL);
        WebElement searchField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='search-query']")));
        searchField.sendKeys(QUERY);
        driver.findElement(By.cssSelector("[data-test='search-submit']")).click();

        wait.withMessage("Не всі назви карток містять «" + QUERY + "»").until(d -> {
            List<WebElement> names = d.findElements(By.cssSelector("[data-test='product-name']"));
            return !names.isEmpty()
                    && names.stream().allMatch(n -> n.getText().toLowerCase().contains(QUERY));
        });

        List<String> names = driver.findElements(By.cssSelector("[data-test='product-name']"))
                .stream().map(WebElement::getText).toList();
        assertAll(
                () -> assertFalse(names.isEmpty(), "Список результатів порожній"),
                () -> assertTrue(names.stream().allMatch(n -> n.toLowerCase().contains(QUERY)),
                        "Є назва без слова «" + QUERY + "»: " + names)
        );
    }
}
