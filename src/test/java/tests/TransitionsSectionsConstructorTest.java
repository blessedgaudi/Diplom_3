package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import page_object.MainPage;


@RunWith(Parameterized.class)
public class TransitionsSectionsConstructorTest {

    private WebDriver driver;
    private String driverType;

    public TransitionsSectionsConstructorTest(String driverType) {
        this.driverType = driverType;
    }

    @Before
    public void startUp() {
        // Добавляем WebDriver
        WebDriverSetup webDriverSetup = new WebDriverSetup(driverType);
        driver = webDriverSetup.getDriver();
    }

    @Parameterized.Parameters(name = "Результаты проверок браузера: {0}")
    public static Object[][] getDataDriver() {
        return new Object[][]{
                {"chromedriver"},
                {"yandexdriver"},
        };
    }

    @Test
    @DisplayName("Переход в раздел 'Булки'.")
    @Description("Проверка перехода в раздел 'Булки', и появление картинки с булкой.")
    public void transitionToBunsInConstructorTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnSaucesButton();
        mainPage.clickOnBunsButton();
        mainPage.checkToppingBun();
    }

    @Test
    @DisplayName("Переход в раздел 'Соусы'.")
    @Description("Проверка перехода в раздел 'Соусы', и появление картинки с соусом.")
    public void transitionToSaucesInConstructorTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnSaucesButton();
        mainPage.checkToppingSauce();
    }

    @Test
    @DisplayName("Переход в раздел 'Начинки'.")
    @Description("Проверка перехода в раздел 'Начинки', и появление картинки с начинкой.")
    public void transitionToFillingsInConstructorTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnFillingButton();
        mainPage.checkToppingFillings();
    }

    @After
    public void tearDown() {
        // Закрытие браузера
        WebDriverSetup webDriverSetup = new WebDriverSetup(driverType);
        webDriverSetup.closeDriver();
    }

}