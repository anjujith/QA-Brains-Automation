
package tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import pages.LoginPage;

public class LoginTest {

    private static final String PRODUCTS_URL =
            "https://practice.qabrains.com/ecommerce";

    private WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private void login(WebDriver driver) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.openLoginPage();
        loginPage.enterEmail("test@qabrains.com");
        loginPage.enterPassword("Password123");
        loginPage.clickLogin();

        createWait(driver).until(
                ExpectedConditions.urlToBe(PRODUCTS_URL));
    }

    // Click an element after scrolling it into view.
    private void clickAfterScroll(
            WebDriver driver, WebDriverWait wait, By locator) {

        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                element);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator));

        try {
            wait.until(
                    ExpectedConditions.elementToBeClickable(locator)).click();

        } catch (ElementClickInterceptedException e) {
            element = driver.findElement(locator);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
        }
    }

    private By addToCartButtonFor(String productName) {
        return By.xpath(
                "//*[normalize-space()='" + productName + "']"
                + "/following::button[contains(translate(.,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'add to cart')][1]");
    }

    private void addShirtToCart(
            WebDriver driver, WebDriverWait wait) {

        clickAfterScroll(
                driver, wait, addToCartButtonFor("Sample Shirt Name"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(translate(.,"
                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                + "'abcdefghijklmnopqrstuvwxyz'),'remove from cart')]")));
    }

    private By cartIconLocator() {
        return By.cssSelector(
                "#ecommerce-header span[role='button']");
    }

    private void openCart(WebDriver driver, WebDriverWait wait) {
        By cartIcon = cartIconLocator();

        WebElement cart = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cartIcon));

        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, 0);");

        cart = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cartIcon));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", cart);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[normalize-space(.)='+']")));

        System.out.println("Cart panel opened successfully.");
    }

    // 1. Positive Login Test
    @Test
    public void positiveLoginTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            Assert.assertEquals(
                    driver.getCurrentUrl(), PRODUCTS_URL);

            System.out.println("PASS: Positive login test.");

        } finally {
            driver.quit();
        }
    }

    // 2. Negative Login Test
    @Test
    public void negativeLoginTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            LoginPage loginPage = new LoginPage(driver);
            loginPage.openLoginPage();
            loginPage.enterEmail("wrong@qabrains.com");
            loginPage.enterPassword("WrongPassword123");
            loginPage.clickLogin();

            WebDriverWait wait = createWait(driver);

            wait.until(d ->
                    !d.getCurrentUrl().equals(PRODUCTS_URL)
                    || !d.findElement(By.tagName("body"))
                            .getText().isBlank());

            Assert.assertNotEquals(
                    driver.getCurrentUrl(), PRODUCTS_URL,
                    "Invalid credentials should not log in.");

            System.out.println("PASS: Negative login test.");

        } finally {
            driver.quit();
        }
    }

    // 3. Product Listing Test
    @Test
    public void productListingTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);

            By shirt = By.xpath(
                    "//*[normalize-space()='Sample Shirt Name']");
            By shoe = By.xpath(
                    "//*[normalize-space()='Sample Shoe Name']");

            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            shirt)).isDisplayed(),
                    "Sample shirt is not displayed.");

            Assert.assertTrue(
                    driver.findElement(shoe).isDisplayed(),
                    "Sample shoe is not displayed.");

            System.out.println("PASS: Product listing test.");

        } finally {
            driver.quit();
        }
    }

    // 4. Product Sorting Test
    @Test
    public void productSortingTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);

            By sortingDropdown = By.cssSelector("[role='combobox']");
            clickAfterScroll(driver, wait, sortingDropdown);

            By ascendingOption = By.xpath(
                    "//*[normalize-space()='A to Z (Ascending)']");

            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            ascendingOption)).isDisplayed(),
                    "A to Z sorting option is not displayed.");

            System.out.println(
                    "PASS: A to Z sorting option is displayed.");

        } finally {
            driver.quit();
        }
    }

    // 5. Invalid Login Data Provider
    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][] {
            {"wrong@qabrains.com", "WrongPassword123"},
            {"", "Password123"},
            {"test@qabrains.com", ""}
        };
    }

    // 6. Data-Driven Negative Login Test
    @Test(dataProvider = "invalidLoginData")
    public void dataDrivenNegativeLoginTest(
            String email, String password) {

        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            LoginPage loginPage = new LoginPage(driver);
            loginPage.openLoginPage();

            if (!email.isEmpty()) {
                loginPage.enterEmail(email);
            }

            if (!password.isEmpty()) {
                loginPage.enterPassword(password);
            }

            loginPage.clickLogin();

            WebDriverWait wait = createWait(driver);

            wait.until(d ->
                    !d.getCurrentUrl().equals(PRODUCTS_URL)
                    || !d.findElement(By.tagName("body"))
                            .getText().isBlank());

            Assert.assertNotEquals(
                    driver.getCurrentUrl(), PRODUCTS_URL,
                    "Invalid login data should not log in.");

            System.out.println(
                    "PASS: Data-driven negative login test.");

        } finally {
            driver.quit();
        }
    }

    // 7. Product Details Verification Test
    @Test
    public void productDetailsVerificationTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);

            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[normalize-space()='Sample Shirt Name']")))
                            .isDisplayed(),
                    "Product name is not displayed.");

            Assert.assertTrue(
                    driver.findElement(
                            By.xpath("//*[normalize-space()='$49.99']"))
                            .isDisplayed(),
                    "Product price is not displayed.");

            System.out.println(
                    "PASS: Product details verification test.");

        } finally {
            driver.quit();
        }
    }

    // 8. Add to Cart Test
    @Test
    public void addToCartTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);
            addShirtToCart(driver, wait);

            Assert.assertTrue(
                    driver.findElement(By.xpath(
                            "//button[contains(translate(.,"
                            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                            + "'abcdefghijklmnopqrstuvwxyz'),"
                            + "'remove from cart')]")).isDisplayed(),
                    "Product was not added to the cart.");

            System.out.println("PASS: Add to Cart test.");

        } finally {
            driver.quit();
        }
    }

    // 9. Remove from Cart Test
    @Test
    public void removeFromCartTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);
            addShirtToCart(driver, wait);

            By removeButton = By.xpath(
                    "//button[contains(translate(.,"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),"
                    + "'remove from cart')]");

            clickAfterScroll(driver, wait, removeButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    addToCartButtonFor("Sample Shirt Name")));

            Assert.assertTrue(
                    driver.findElement(
                            addToCartButtonFor("Sample Shirt Name"))
                            .isDisplayed(),
                    "Product was not removed from the cart.");

            System.out.println("PASS: Remove from Cart test.");

        } finally {
            driver.quit();
        }
    }

    // 10. Update Quantity Test
    @Test
    public void updateQuantityTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);
            addShirtToCart(driver, wait);
            openCart(driver, wait);

            By plusButton = By.xpath(
                    "//button[normalize-space(.)='+']");

            WebElement plus = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            plusButton));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    plus);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", plus);

            By quantityTwo = By.xpath(
                    "//span[contains(@class,'border')"
                    + " and normalize-space(.)='2']");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    quantityTwo));

            Assert.assertEquals(
                    driver.findElement(quantityTwo).getText(),
                    "2",
                    "Quantity was not updated to 2.");

            By updatedTotal = By.xpath(
                    "//*[contains(normalize-space(.), '$99.98')]");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    updatedTotal)).isDisplayed(),
                    "Total price was not updated to $99.98.");

            System.out.println(
                    "PASS: Quantity updated to 2 and total is $99.98.");

        } finally {
            driver.quit();
        }
    }

    // 11. Checkout Test
    @Test
    public void checkoutTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);
            addShirtToCart(driver, wait);
            openCart(driver, wait);

            By checkoutButton = By.xpath(
                    "//button[.//span[normalize-space()='Checkout']]");

            clickAfterScroll(driver, wait, checkoutButton);

            WebElement firstName = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[placeholder='Ex. John']")));

            WebElement lastName = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[placeholder='Ex. Doe']")));

            WebElement zipCode = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input.form-control[value='1207']")));

            firstName.sendKeys("Anjana");
            lastName.sendKeys("Jithesh");

            Assert.assertEquals(
                    zipCode.getAttribute("value"),
                    "1207",
                    "Zip code is incorrect.");

            By continueButton = By.xpath(
                    "//button[.//span[normalize-space()='Continue']]");

            clickAfterScroll(driver, wait, continueButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Checkout: Overview']")));

            By totalPrice = By.xpath(
                    "//*[contains(normalize-space(.), '$52.49')]");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    totalPrice)).isDisplayed(),
                    "Expected order total $52.49 was not displayed.");

            System.out.println(
                    "PASS: Checkout information submitted.");

        } finally {
            driver.quit();
        }
    }

    // 12. Order Confirmation Test
    @Test
    public void orderConfirmationTest() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            login(driver);

            WebDriverWait wait = createWait(driver);

            // Add product to cart.
            addShirtToCart(driver, wait);
            openCart(driver, wait);

            // Open checkout.
            By checkoutButton = By.xpath(
                    "//button[.//span[normalize-space()='Checkout']]");

            clickAfterScroll(driver, wait, checkoutButton);

            // Enter customer information.
            WebElement firstName = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[placeholder='Ex. John']")));

            WebElement lastName = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[placeholder='Ex. Doe']")));

            firstName.sendKeys("Anjana");
            lastName.sendKeys("Jithesh");

            // Continue to the overview page.
            By continueButton = By.xpath(
                    "//button[.//span[normalize-space()='Continue']]");

            clickAfterScroll(driver, wait, continueButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Checkout: Overview']")));

            // Verify product.
            Assert.assertTrue(
                    driver.findElement(
                            By.xpath("//*[normalize-space()='Sample Shirt Name']"))
                            .isDisplayed(),
                    "Product is missing from checkout overview.");

            // Verify total price using a flexible locator.
            By totalPrice = By.xpath(
                    "//*[contains(normalize-space(.), '$52.49')]");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    totalPrice)).isDisplayed(),
                    "Expected order total $52.49 was not displayed.");

            // Finish the order.
            By finishButton = By.xpath(
                    "//button[.//span[normalize-space()='Finish']]");

            clickAfterScroll(driver, wait, finishButton);

            // Verify that the Finish action navigates away from overview.
            wait.until(d ->
                    d.findElements(By.xpath(
                            "//*[normalize-space()='Checkout: Overview']"))
                            .isEmpty());

            Assert.assertTrue(
                    driver.findElements(By.xpath(
                            "//*[normalize-space()='Checkout: Overview']"))
                            .isEmpty(),
                    "The checkout overview is still displayed after Finish.");

            System.out.println(
                    "PASS: Finish action navigated away from checkout overview.");

        } finally {
            driver.quit();
        }
    }
}
