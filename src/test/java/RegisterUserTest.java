import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import resources.BaseURI;
import resources.TestDataGenerator;

public class RegisterUserTest extends BaseURI {

    private TestDataGenerator generator;
    private UserStep step;

    @Before
    public void setUp() {
        setBaseURI();
        generator = new TestDataGenerator();
        step = new UserStep();
    }

    @Test
    @DisplayName("POST /auth/register: basic test")
    @Description("Check user registration via POST /auth/register")
    public void checkRegisterUserSuccess200() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        step.clearTestData(response);
        step.checkResponseStatus200(response);
        step.checkSuccessTrueInResponse(response);
        step.checkAccessTokenInResponse(response);
        step.checkRefreshTokenInResponse(response);
    }

    @Test
    @DisplayName("POST /auth/register: check user name and email in response")
    @Description("Check that POST /auth/register returns correct user name and email in response")
    public void checkRegisterUserReturnsNameEmail() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        step.clearTestData(response);
        step.checkUserInfoInResponse(user, response);
    }


    @Test
    @DisplayName("POST /auth/register: duplicated users are not allowed")
    @Description("Check that registration of duplicated users is not allowed")
    public void checkRegisterDuplicatedUsers403() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response1 = step.sendPOSTRegisterUser(user);
        step.checkResponseStatus200(response1);
        Response response2 = step.sendPOSTRegisterUser(user);
        step.clearTestData(response1);
        step.checkResponseStatus403(response2);
        step.checkSuccessFalseInResponse(response2);
        step.checkUserExistsMessageInResponse(response2);
    }
}
