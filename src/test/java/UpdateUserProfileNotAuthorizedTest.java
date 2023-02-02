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
public class UpdateUserProfileNotAuthorizedTest extends BaseURI {

    private String fieldToUpdate;
    private TestDataGenerator generator;
    private UserStep step;

    public UpdateUserProfileNotAuthorizedTest(String fieldToUpdate) {
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
    @DisplayName("PATCH /auth/user: update user profile - Not authorized user")
    @Description("Check that PATCH /auth/user fails if user is not authorized")
    public void checkUpdateUserProfileNotAuthorized401() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response1 = step.sendPOSTRegisterUser(user);
        Response response2 = step.sendPATCHUser("", fieldToUpdate, step.updateFieldInUserObject(user, fieldToUpdate));
        step.checkResponseStatus401(response2);
        step.checkSuccessFalseInResponse(response2);
        step.checkUserNotAuthorizedMessageInResponse(response2);
        step.clearTestData(response1);
    }
}
