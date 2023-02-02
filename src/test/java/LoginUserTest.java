import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import resources.BaseURI;
import resources.TestDataGenerator;

public class LoginUserTest extends BaseURI {

    private TestDataGenerator generator;
    private UserStep step;

    @Before
    public void setUp() {
        RestAssured.baseURI = testInstance;
        generator = new TestDataGenerator();
        step = new UserStep();
    }

    @Test
    @DisplayName("POST /auth/login: basic test")
    @Description("Check user authorization via POST /auth/login")
    public void checkLoginUserSuccess200() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        step.sendPOSTRegisterUser(user);
        Response response = step.sendPOSTLoginUser(user);
        step.checkResponseStatus200(response);
        step.checkSuccessTrueInResponse(response);
        step.checkAccessTokenInResponse(response);
        step.checkRefreshTokenInResponse(response);
        step.checkUserInfoInResponse(user, response);
        step.clearTestData(response);
    }
}
