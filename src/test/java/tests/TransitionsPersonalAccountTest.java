package tests;

import client.User;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import page_object.LoginPage;
import page_object.MainPage;
import page_object.ProfilePage;
import page_object.RegisterPage;
import client.UserClient;
import io.restassured.response.Response;



@RunWith(Parameterized.class)
public class TransitionsPersonalAccountTest {

    private WebDriver driver;
    private String driverType;
    private String email;
    private String password;
    private String accessToken; // Переменная для токена
    private Faker faker;

    public TransitionsPersonalAccountTest(String driverType) {
        this.driverType = driverType;
    }

    @Before
    public void startUp() {
        faker = new Faker(); // Инициализация генератора данных
        email = faker.internet().emailAddress(); // Генерация уникального email
        password = faker.internet().password(); // Генерация уникального пароля

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

    // Метод для регистрации пользователя перед тестами авторизации
    private void registerUser() {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnAccountButton(); // Переход на страницу авторизации
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.waitForLoadRegisterPage(); // Ожидание загрузки страницы регистрации
        // Регистрация нового пользователя
        Response response = UserClient.postCreateNewUser(new User("Гарри", email, password));
        accessToken = response.jsonPath().getString("accessToken"); // Получение токена
        // Проверка успешной регистрации
        Assert.assertTrue("Не удалось зарегистрировать пользователя", response.jsonPath().getBoolean("success"));
    }

    @Test
    @DisplayName("Переход в личный кабинет.")
    @Description("Проверка перехода по клику на 'Личный кабинет'.")
    public void transitionToProfilePageTest() {
        registerUser(); // Регистрация пользователя перед авторизацией
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        Assert.assertTrue("Страница авторизации не отобразилась", driver.findElement(loginPage.entrance).isDisplayed());
    }

    @Test
    @DisplayName("Переход в конструктор из личного кабинета.")
    @Description("Проверка перехода на вкладку 'Конструктор' из страницы авторизации пользователя.")
    public void transitionToConstructorFromProfilePageTest() {
        registerUser(); // Регистрация пользователя перед авторизацией
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForInvisibilityLoadingAnimation();
        mainPage.clickOnAccountButton();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.clickOnConstructorButton();
        mainPage.waitForLoadMainPage();
        Assert.assertTrue("Переход в конструктор из личного кабинета не прошел", driver.findElement(mainPage.textBurgerMainPage).isDisplayed());
    }

    @Test
    @DisplayName("Клик по логотипу 'Stellar Burgers'.")
    @Description("Проверка перехода в конструктор при нажатии на логотип 'Stellar Burgers'.")
    public void transitionToStellarBurgersFromProfilePageTest() {
        registerUser(); // Регистрация пользователя перед авторизацией
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnAccountButton();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.clickOnLogo();
        mainPage.waitForLoadMainPage();
        Assert.assertTrue("Конструктор при клике на логотип не загрузился", driver.findElement(mainPage.textBurgerMainPage).isDisplayed());
    }

    @Test
    @DisplayName("Выход из аккаунта")
    @Description("Проверка выхода по кнопке 'Выйти' в личном кабинете.")
    public void exitFromProfileTest() {
        registerUser(); // Регистрация пользователя перед авторизацией
        MainPage mainPage = new MainPage(driver);
        mainPage.clickOnAccountButton();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.authorization(email, password);
        mainPage.waitForLoadMainPage();
        mainPage.clickOnAccountButton();
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.waitForLoadProfilePage();
        profilePage.clickOnExitButton();
        mainPage.waitForInvisibilityLoadingAnimation();
        Assert.assertTrue("Не удалось выйти из аккаунта", driver.findElement(loginPage.entrance).isDisplayed());
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