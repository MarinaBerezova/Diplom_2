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
public class RegisterUserMandatoryFieldsTest extends BaseURI {

    private String email;
    private String password;
    private String name;
    private UserStep step;

    public RegisterUserMandatoryFieldsTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {null, new TestDataGenerator().getPassword(), new TestDataGenerator().getName()},
                {new TestDataGenerator().getEmail(), null, new TestDataGenerator().getName()},
                {new TestDataGenerator().getEmail(), new TestDataGenerator().getPassword(), null},
                {"", new TestDataGenerator().getPassword(), new TestDataGenerator().getName()},
                {new TestDataGenerator().getEmail(), "", new TestDataGenerator().getName()},
                {new TestDataGenerator().getEmail(), new TestDataGenerator().getPassword(), ""},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = testInstance;
        step = new UserStep();
    }

    @Test
    @DisplayName("POST /auth/register: mandatory fields check")
    @Description("Check that POST /auth/register fails if one of required fields is not sent")
    public void checkRegisterUserMandatoryFields403() throws InterruptedException {
        User user = new User(email, password, name);
        Response response = step.sendPOSTRegisterUser(user);
        step.checkResponseStatus403(response);
        step.checkSuccessFalseInResponse(response);
        step.checkMissingRequiredFieldsMessageInResponse(response);
        // Workaround для ошибки 429 <Too many requests>, чтобы тесты могли закончить проверку своей функциональности:
        Thread.sleep(100);
    }
}
