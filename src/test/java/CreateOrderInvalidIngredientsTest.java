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

import java.util.ArrayList;

@RunWith(Parameterized.class)
public class CreateOrderInvalidIngredientsTest extends BaseURI {

    private boolean isAuthorizedUser;
    private TestDataGenerator generator;
    private UserStep step;
    private OrderStep step1;


    public CreateOrderInvalidIngredientsTest(boolean isAuthorizedUser) {
        this.isAuthorizedUser = isAuthorizedUser;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {true},
                {false},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = testInstance;
        generator = new TestDataGenerator();
        step = new UserStep();
        step1 = new OrderStep();
    }

    @Test
    @DisplayName("POST /api/orders: Create order - invalid ingredients")
    @Description("Check that POST /api/orders fails if invalid ingredients provided")
    public void checkCreateOrderInvalidIngredients500() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        ArrayList<String> invalidList = new ArrayList<>();
        invalidList.add("1111111111");
        Response response1;
        if(isAuthorizedUser) {
            response1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), invalidList);
        } else {
            response1 = step1.sendPOSTOrders("", invalidList);
        }
        step1.checkResponseStatus500(response1);
        step.clearTestData(response);
    }
}
