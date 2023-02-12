import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import resources.BaseURI;
import resources.TestDataGenerator;


@RunWith(Parameterized.class)
public class LoginUserInvalidCredentialsTest extends BaseURI {

    private String email;
    private String password;
    private UserStep step;

    public LoginUserInvalidCredentialsTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {"example_auto0203@yandex.ru", "QwEry123"},
                {"example_auto0203@yandex.ru", "QwErty1233"},
                {"example_auto0203@yandex.ru", "qwerty123"},
                {"example_aut0203@yandex.ru", "QwErty123"},
                {"example.auto0203@yandex.ru", "QwErty123"},
                {"exampleauto0203@yandex.ru", "QwErty123"},
                {"example_auto0203", "QwErty123"},
                {"example_auto0203@example.com", "QwErty123"},
        };
    }

    @Before
    public void setUp() {
        setBaseURI();
        step = new UserStep();
    }

    @Test
    @DisplayName("POST /auth/login: check login with invalid login/password pair")
    @Description("Check that POST /auth/login fails if login/password pair is invalid")
    public void checkLoginUserInvalidCredentials401() throws InterruptedException {
            User user = new User("example_auto0203@yandex.ru", "QwErty123", new TestDataGenerator().getName());
            Response response1 = step.sendPOSTRegisterUser(user);
            step.checkResponseStatus200(response1);
            Response response2 = step.sendPOSTLoginUser(email, password);
            step.clearTestData(response1);
            step.checkResponseStatus401(response2);
            step.checkSuccessFalseInResponse(response2);
            step.checkLoginFailedMessageInResponse(response2);
            // Workaround для ошибки 429 <Too many requests>, чтобы тесты могли закончить проверку своей функциональности:
            Thread.sleep(100);
    }
}
