import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import resources.BaseURI;
import resources.TestDataGenerator;

import java.util.ArrayList;

public class CreateOrderTest extends BaseURI {

    private TestDataGenerator generator;
    private UserStep step;
    private OrderStep step1;

    @Before
    public void setUp() {
        setBaseURI();
        generator = new TestDataGenerator();
        step = new UserStep();
        step1 = new OrderStep();
    }

    @Test
    @DisplayName("POST /api/orders: Create order - not authorized user - 200 success")
    @Description("Check that POST /api/orders returns code 200 'success: true' and order 'number' for not authorized user")
    public void checkCreateOrderNotAuthorizedUser200() {
        Response response = step1.sendPOSTOrders("", step1.getRandom5Ingredients());
        step.checkResponseStatus200(response);
        step.checkSuccessTrueInResponse(response);
        step1.checkOrderNumberInResponse(response);
    }

    @Test
    @DisplayName("POST /api/orders: Create order - not authorized user - check burger name")
    @Description("Check that POST /api/orders returns burger name for not authorized user")
    public void checkCreateOrderNotAuthorizedUserBurgerName() {
        Response response = step1.sendPOSTOrders("", step1.getRandom5Ingredients());
        step1.checkBurgerNameInResponse(response);
    }

    @Test
    @DisplayName("POST /api/orders: Create order - Authorized user - 200 success")
    @Description("Check that POST /api/orders returns code 200 'success: true' and order 'number' for authorized user")
    public void checkCreateOrderAuthorizedUser200() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        Response response1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step.clearTestData(response);
        step.checkResponseStatus200(response1);
        step.checkSuccessTrueInResponse(response1);
        step1.checkOrderNumberInResponse(response1);
    }

    @Test
    @DisplayName("POST /api/orders: Create order - Authorized user - check burger name")
    @Description("Check that POST /api/orders returns burger name for authorized user")
    public void checkCreateOrderAuthorizedUserBurgerName() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        Response response1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step.clearTestData(response);
        step1.checkBurgerNameInResponse(response1);
    }

    @Test
    @DisplayName("POST /api/orders - Authorized user - check order ingredients in response")
    @Description("Check that POST /api/orders creates order with correct ingredients")
    public void checkCreateOrderIngredients() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        ArrayList<String> desiredIngredients = step1.getRandom5Ingredients();
        Response response1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), desiredIngredients);
        ArrayList<String> actualIngredients= step1.extractIngredientsFromResponse(response1);
        step.clearTestData(response);
        Assert.assertEquals(desiredIngredients, actualIngredients);
    }

    @Test
    @DisplayName("POST /api/orders - Authorized user - check user name and email in response")
    @Description("Check that POST /api/orders creates order with correct user name and email")
    public void checkCreateOrderUserNameEmail() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        Response response1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step.clearTestData(response);
        Assert.assertEquals(generator.getName(), response1.then().extract().path("order.owner.name"));
        Assert.assertEquals(generator.getEmail(), response1.then().extract().path("order.owner.email"));
    }
}
