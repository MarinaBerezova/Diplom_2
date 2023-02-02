import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import resources.BaseURI;
import resources.TestDataGenerator;

@RunWith(Parameterized.class)
public class UpdateUserProfileTest extends BaseURI {

    private String fieldToUpdate;
    private TestDataGenerator generator;
    private UserStep step;

    public UpdateUserProfileTest(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {"email"},
                {"password"},
                {"name"},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = testInstance;
        generator = new TestDataGenerator();
        step = new UserStep();
    }

    @Test
    @DisplayName("PATCH /auth/user: update user profile")
    @Description("Check that email, password or name can be changed via PATCH /auth/user")
    public void checkUpdateUserProfileSuccess200() throws InterruptedException {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response1 = step.sendPOSTRegisterUser(user);
        Response response2 = step.sendPATCHUser(step.checkAccessTokenInResponse(response1), fieldToUpdate, step.updateFieldInUserObject(user, fieldToUpdate));
        step.checkResponseStatus200(response2);
        step.checkSuccessTrueInResponse(response2);
        step.checkUserInfoInResponse(user, response2);
        // Проверка логина обновленного юзера:
        Response response3 = step.sendPOSTLoginUser(user);
        step.checkResponseStatus200(response3);
        step.clearTestData(response3);
        // Workaround для ошибки 429 <Too many requests>, чтобы тесты могли закончить проверку своей функциональности:
        Thread.sleep(100);
    }
}
