package tests;

import client.User;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
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

        // Регистрация пользователя через API
        registerUser();
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
        Response response = UserClient.postCreateNewUser(new User("Гарри", email, password));
        accessToken = response.jsonPath().getString("accessToken"); // Получение токена
        // Проверка успешной регистрации
        response.then().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Переход в личный кабинет.")
    @Description("Проверка перехода по клику на 'Личный кабинет'.")
    public void transitionToProfilePageTest() {
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
        mainPage.clickOnAccountButton();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.authorization(email, password); // Залогиниться

        mainPage.clickOnAccountButton();

        ProfilePage profilePage = new ProfilePage(driver);

        Assert.assertTrue(driver.findElement(profilePage.textOnProfilePage).isDisplayed());
    }

    @Test
    @DisplayName("Переход в конструктор из личного кабинета.")
    @Description("Проверка перехода на вкладку 'Конструктор' из страницы авторизации пользователя.")
    public void transitionToConstructorFromProfilePageTest() {
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
        mainPage.clickOnAccountButton();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.authorization(email, password); // Залогиниться

        mainPage.clickOnConstructorButton(); // Переход в конструктор

        Assert.assertTrue(driver.findElement(mainPage.textBurgerMainPage).isDisplayed());
    }

    @Test
    @DisplayName("Клик по логотипу 'Stellar Burgers'.")
    @Description("Проверка перехода в конструктор при нажатии на логотип 'Stellar Burgers'.")
    public void transitionToStellarBurgersFromProfilePageTest() {
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
        mainPage.clickOnAccountButton();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.authorization(email, password); // Залогиниться

        mainPage.clickOnLogo(); // Клик по логотипу

        Assert.assertTrue(driver.findElement(mainPage.textBurgerMainPage).isDisplayed());
    }

    @Test
    @DisplayName("Выход из аккаунта")
    @Description("Проверка выхода по кнопке 'Выйти' в личном кабинете.")
    public void exitFromProfileTest() {
        MainPage mainPage = new MainPage(driver);
        mainPage.waitForLoadMainPage();
        mainPage.clickOnAccountButton();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitForLoadEntrance();
        loginPage.authorization(email, password); // Залогиниться

        mainPage.clickOnAccountButton();

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.clickOnExitButton();

        Assert.assertTrue(driver.findElement(loginPage.entrance).isDisplayed());
    }

    @After
    public void tearDown() {
        // Удаление пользователя после теста
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
        // Закрытие браузера
        driver.quit();
    }
}