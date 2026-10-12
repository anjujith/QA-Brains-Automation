
package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    By emailField = By.name("email");
    By passwordField = By.name("password");
    By loginButton = By.cssSelector("button.btn-submit");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openLoginPage() {
        driver.get("https://practice.qabrains.com/ecommerce/login");
    }

    public void enterEmail(String email) {
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(emailField)
        ).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(passwordField)
        ).sendKeys(password);
    }


public void clickLogin() {

    WebElement button = wait.until(
        ExpectedConditions.visibilityOfElementLocated(loginButton)
    );

    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            button
        );

    wait.until(
        ExpectedConditions.elementToBeClickable(loginButton)
    ).click();
}


    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
