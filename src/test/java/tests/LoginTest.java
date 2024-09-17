package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.After;
import org.openqa.selenium.chrome.ChromeOptions;
import page_object.LoginPage;
import page_object.MainPage;
import page_object.RecoverPasswordPage;
import page_object.RegisterPage;

import java.util.concurrent.TimeUnit;


@RunWith(Parameterized.class)
public class LoginTest {
    private WebDriver driver;
    private String driverType;
    private final static String EMAIL = "harrypotter@yandex.ru";
    private final static String PASSWORD = "harry1234";

    public LoginTest(String driverType) {
        this.driverType = driverType;
    }

    @Before
    public void startUp() {
        if (driverType.equals("chromedriver")) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            ChromeOptions options = new ChromeOptions();
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.navigate().to("https://stellarburgers.nomoreparties.site/");
        } else if (driverType.equals("yandexdriver")) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.setBinary("/Applications/Yandex.app/Contents/MacOS/Yandex");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.navigate().to("https://stellarburgers.nomoreparties.site/");
        }
    }

    @Parameterized.Parameters(name = "Результаты проверок браузера: {0}")
    public static Object[][] getDataDriver() {
        return new Object[][]{
                {"chromedriver"},
                {"yandexdriver"},
        };
    }

    // Метод для регистрации пользователя
    private void registerUser() {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnLoginButton(); // Переход на страницу авторизации
        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickOnRegister(); // Переход на страницу регистрации
        RegisterPage registerPage = new RegisterPage(driver);

        // Используем фиксированный email и пароль
        registerPage.registration("Гарри", EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("Вход по кнопке 'Войти в аккаунт'.")
    @Description("Проверка кнопки 'Войти в аккаунт' на главной странице лендинга.")
    public void enterByLoginButtonTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(EMAIL, PASSWORD);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @Test
    @DisplayName("Вход по кнопке 'Личный Кабинет'.")
    @Description("Проверка кнопки 'Личный Кабинет' на хедере главной страницы.")
    public void enterByPersonalAccountButtonTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(EMAIL, PASSWORD);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации.")
    @Description("Проверка входа через форму регистрации.")
    public void enterByRegistrationFormTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(EMAIL, PASSWORD); // Используем ранее зарегистрированные данные
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @Test
    @DisplayName("Вход через кнопку в форме восстановления пароля.")
    @Description("Проверка входа через форму восстановления пароля.")
    public void enterByPasswordRecoveryFormatTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickOnForgotPasswordLink();
        RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(driver);
        recoverPasswordPage.waitForLoadedRecoverPassword();
        recoverPasswordPage.clickOnLoginLink();
        loginPage.authorization(EMAIL, PASSWORD);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @After
    public void tearDown() {
        // Закрытие браузера
        driver.quit();
    }
}