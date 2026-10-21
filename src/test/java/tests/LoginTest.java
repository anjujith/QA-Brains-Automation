
package tests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
        return By.cssSelector("#ecommerce-header span[role='button']");
    }

    private void openCart(WebDriver driver, WebDriverWait wait) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");

        WebElement cart = wait.until(
                ExpectedConditions.elementToBeClickable(cartIconLocator()));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", cart);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[normalize-space(.)='+']")));

        System.out.println("Cart panel opened successfully.");
    }

    private List<String> getProductNames(
            WebDriver driver, WebDriverWait wait, By locator) {

        List<WebElement> elements = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));

        List<String> names = new ArrayList<>();

        for (WebElement element : elements) {
            String name = element.getText().trim();

            if (!name.isEmpty()) {
                names.add(name);
            }
        }

        return names;
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
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(shirt))
                            .isDisplayed(),
                    "Sample shirt is not displayed.");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(shoe))
                            .isDisplayed(),
                    "Sample shoe is not displayed.");

            System.out.println("PASS: Product listing test.");

        } finally {
            driver.quit();
        }
    }

    // 4. Product Sorting Test
    @Test
    public void productSortingTest() throws InterruptedException {

        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            // Step 1: Login
            login(driver);

            WebDriverWait wait = createWait(driver);

            // Step 2: Locate product names
            By productNames = By.cssSelector(
                    "a.font-oswald[href*='/ecommerce/product-details']"
            );

            // Step 3: Get product order before sorting
            List<String> initialNames =
                    getProductNames(driver, wait, productNames);

            System.out.println("Before sorting: " + initialNames);

            // Step 4: Open sorting dropdown
            By sortingDropdown = By.cssSelector("[role='combobox']");

            clickAfterScroll(driver, wait, sortingDropdown);

            // Step 5: Find the A to Z option
            By ascendingOption = By.xpath(
                    "//*[normalize-space()='A to Z (Ascending)']"
            );

            // Find a visible matching option
            List<WebElement> options = wait.until(d -> {
                List<WebElement> visibleOptions = new ArrayList<>();

                for (WebElement element : d.findElements(ascendingOption)) {
                    if (element.isDisplayed()) {
                        visibleOptions.add(element);
                    }
                }

                return visibleOptions.isEmpty() ? null : visibleOptions;
            });

            WebElement option = options.get(options.size() - 1);

            // Step 6: Select A to Z
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", option);

            // Allow the page to update
            Thread.sleep(2000);

            // Step 7: Read actual product order
            List<String> actualNames =
                    getProductNames(driver, wait, productNames);

            // Step 8: Create expected alphabetical order
            List<String> expectedNames =
                    new ArrayList<>(actualNames);

            expectedNames.sort(String.CASE_INSENSITIVE_ORDER);

            // Step 9: Print and verify
            System.out.println("Actual order: " + actualNames);
            System.out.println("Expected A-Z order: " + expectedNames);

            Assert.assertEquals(
                    actualNames,
                    expectedNames,
                    "Product sorting failed: products are not in A-Z order."
            );

            System.out.println("PASS: A-to-Z sorting verified.");

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
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[normalize-space()='$49.99']")))
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

            By removeButton = By.xpath(
                    "//button[contains(translate(.,"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),'remove from cart')]");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    removeButton)).isDisplayed(),
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
                    + "'abcdefghijklmnopqrstuvwxyz'),'remove from cart')]");

            clickAfterScroll(driver, wait, removeButton);

            By addButton = addToCartButtonFor("Sample Shirt Name");

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(addButton));

            Assert.assertTrue(
                    driver.findElement(addButton).isDisplayed(),
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
                    ExpectedConditions.elementToBeClickable(plusButton));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", plus);

            By quantityTwo = By.xpath(
                    "//span[contains(@class,'border')"
                    + " and normalize-space(.)='2']");

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(quantityTwo));

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

            firstName.sendKeys("Anjana");
            lastName.sendKeys("Jithesh");

            By continueButton = By.xpath(
                    "//button[.//span[normalize-space()='Continue']]");

            clickAfterScroll(driver, wait, continueButton);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Checkout: Overview']")));

            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[normalize-space()='Sample Shirt Name']")))
                            .isDisplayed(),
                    "Product is missing from checkout overview.");

            By totalPrice = By.xpath(
                    "//*[contains(normalize-space(.), '$52.49')]");

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    totalPrice)).isDisplayed(),
                    "Expected order total $52.49 was not displayed.");

            By finishButton = By.xpath(
                    "//button[.//span[normalize-space()='Finish']]");

            clickAfterScroll(driver, wait, finishButton);

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
