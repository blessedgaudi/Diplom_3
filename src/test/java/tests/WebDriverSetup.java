package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class WebDriverSetup {
    private WebDriver driver;

    public WebDriverSetup(String driverType) {
        if (driverType.equals("chromedriver")) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            ChromeOptions options = new ChromeOptions();
            driver = new ChromeDriver(options);
            initializeDriver();
        } else if (driverType.equals("yandexdriver")) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.setBinary("/Applications/Yandex.app/Contents/MacOS/Yandex");
            driver = new ChromeDriver(options);
            initializeDriver();
        }
    }

    private void initializeDriver() {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to("https://stellarburgers.nomoreparties.site/");
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
