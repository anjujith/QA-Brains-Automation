
package utils;

import org.openqa.selenium.WebDriver;

public class BrowserUtils {

    public static void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }

    public static void openUrl(WebDriver driver, String url) {
        driver.get(url);
    }

    public static void closeBrowser(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}
