
package tests;

import java.time.Duration;

import org.openqa.selenium.By;
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

    // 1. Positive Login Test
    @Test
    public void positiveLoginTest() {

        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            LoginPage loginPage = new LoginPage(driver);

            loginPage.openLoginPage();
            loginPage.enterEmail("test@qabrains.com");
            loginPage.enterPassword("Password123");
            loginPage.clickLogin();

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.urlToBe(
                    "https://practice.qabrains.com/ecommerce"));

            Assert.assertEquals(
                    driver.getCurrentUrl(),
                    "https://practice.qabrains.com/ecommerce",
                    "Login did not navigate to the products page."
            );

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
            loginPage.enterEmail("test@qabrains.com");
            loginPage.enterPassword("WrongPassword123");
            loginPage.clickLogin();

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.urlContains("/login"));

            Assert.assertTrue(
                    driver.getCurrentUrl().contains("/login"),
                    "Expected login to be rejected."
            );

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

            LoginPage loginPage = new LoginPage(driver);

            loginPage.openLoginPage();
            loginPage.enterEmail("test@qabrains.com");
            loginPage.enterPassword("Password123");
            loginPage.clickLogin();

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.urlToBe(
                    "https://practice.qabrains.com/ecommerce"));

            WebElement body = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.tagName("body"))
            );

            String pageText = body.getText();

            Assert.assertTrue(
                    pageText.contains("Sample Shirt Name")
                    || pageText.contains("Sample Shoe Name")
                    || pageText.contains("Sample Jacket Name"),
                    "Product listing was not displayed."
            );

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

            LoginPage loginPage = new LoginPage(driver);

            loginPage.openLoginPage();
            loginPage.enterEmail("test@qabrains.com");
            loginPage.enterPassword("Password123");
            loginPage.clickLogin();

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.urlToBe(
                    "https://practice.qabrains.com/ecommerce"));

            By sortDropdown = By.xpath(
                    "//*[normalize-space()='Order by']"
                    + "/following::*[@role='combobox'][1]"
            );

            WebElement dropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(sortDropdown)
            );

            dropdown.click();

            By ascendingOption = By.xpath(
                    "//*[normalize-space()='A to Z (Ascending)']"
            );

            WebElement option = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            ascendingOption)
            );

            Assert.assertTrue(
                    option.isDisplayed(),
                    "A to Z sorting option was not displayed."
            );

            System.out.println(
                    "PASS: A to Z sorting option is displayed."
            );

        } finally {
            driver.quit();
        }
    }


    // 5. Data Provider for Negative Login Test
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

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.urlContains("/login"));

            Assert.assertTrue(
                    driver.getCurrentUrl().contains("/login"),
                    "Invalid login should remain on the login page."
            );

            System.out.println(
                    "PASS: Data-driven negative login test."
            );

        } finally {
            driver.quit();
        }
    }

}
