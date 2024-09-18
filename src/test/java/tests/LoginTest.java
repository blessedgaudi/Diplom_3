package tests;

import client.User;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.junit.After;
import page_object.LoginPage;
import page_object.MainPage;
import page_object.RecoverPasswordPage;
import page_object.RegisterPage;
import client.UserClient; // Импортируем UserClient
import io.restassured.response.Response;


@RunWith(Parameterized.class)
public class LoginTest {
    private WebDriver driver;
    private String driverType;
    private String email;
    private String password;
    private String accessToken; // Переменная для токена

    public LoginTest(String driverType) {
        this.driverType = driverType;
    }

    @Before
    public void startUp() {

        // Добавляем WebDriver
        WebDriverSetup webDriverSetup = new WebDriverSetup(driverType);
        driver = webDriverSetup.getDriver();

        // Инициализация генератора данных
        Faker faker = new Faker();
        email = faker.internet().emailAddress(); // Генерация уникального email
        password = faker.internet().password(); // Генерация уникального пароля
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

        // Используем сгенерированные email и пароль
        Response response = UserClient.postCreateNewUser(new User("Гарри", email, password));
        accessToken = response.jsonPath().getString("accessToken"); // Получение токена
        // Проверка успешной регистрации
        response.then().assertThat().statusCode(201).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Вход по кнопке 'Войти в аккаунт'.")
    @Description("Проверка кнопки 'Войти в аккаунт' на главной странице лендинга.")
    public void enterByLoginButtonTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(email, password);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @Test
    @DisplayName("Вход по кнопке 'Личный Кабинет'.")
    @Description("Проверка кнопки 'Личный Кабинет' на хедере главной страницы.")
    public void enterByPersonalAccountButtonTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(email, password);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации.")
    @Description("Проверка входа через форму регистрации.")
    public void enterByRegistrationFormTest() {
        registerUser(); // Регистрация перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.authorization(email, password); // Используем ранее зарегистрированные данные
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
        loginPage.authorization(email, password);
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
    }

    @After
    public void tearDown() {
        // Удаление пользователя после теста
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
        // Закрытие браузера
        WebDriverSetup webDriverSetup = new WebDriverSetup(driverType);
        webDriverSetup.closeDriver();
    }
}