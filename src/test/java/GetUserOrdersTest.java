import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import resources.BaseURI;
import resources.TestDataGenerator;

public class GetUserOrdersTest extends BaseURI {

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
    @DisplayName("GET /api/orders: Not authorized user")
    @Description("Check that GET /api/orders fails if user is not authorized")
    public void checkGetUserOrdersNotAuthorized401() {
        Response response = step1.sendGETUserOrdersList("");
        step.checkResponseStatus401(response);
        step.checkSuccessFalseInResponse(response);
        step.checkUserNotAuthorizedMessageInResponse(response);
    }

    @Test
    @DisplayName("GET /api/orders: Authorized user - 200 success")
    @Description("Check that GET /api/orders returns code '200' and 'success:true' for authorized user")
    public void checkGetUserOrdersAuthorized200(){
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        Response response1 = step1.sendGETUserOrdersList(step.checkAccessTokenInResponse(response));
        step.clearTestData(response);
        step.checkResponseStatus200(response1);
        step.checkSuccessTrueInResponse(response1);
    }

    @Test
    @DisplayName("GET /api/orders: check number of orders returned")
    @Description("Check that GET /api/orders returns correct number of user's orders in array")
    public void checkGetUserOrdersQuantityInList(){
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        Orders orders = step1.sendGETUserOrders(step.checkAccessTokenInResponse(response));
        step.clearTestData(response);
        Assert.assertEquals(2, orders.getOrders().length);
    }

    @Test
    @DisplayName("GET /api/orders: check 'total' and 'totalToday' values in response")
    @Description("Check that GET /api/orders returns correct number of user's orders in 'total' and 'totalToday' fields")
    public void checkGetUserOrdersTotalValues() {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        Orders orders = step1.sendGETUserOrders(step.checkAccessTokenInResponse(response));
        step.clearTestData(response);
        Assert.assertEquals(2, orders.getTotal());
        Assert.assertEquals(2, orders.getTotalToday());
    }

    @Test
    @DisplayName("GET /api/orders: check order numbers in response")
    @Description("Check that GET /api/orders returns order numbers that belongs to a current user")
    public void checkGetUserOrders() throws InterruptedException {
        User user = new User(generator.getEmail(), generator.getPassword(), generator.getName());
        Response response = step.sendPOSTRegisterUser(user);
        // Workaround для ошибки 429 <Too many requests>, чтобы тесты могли закончить проверку своей функциональности:
        Thread.sleep(100);
        Response order1 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        Response order2 = step1.sendPOSTOrders(step.checkAccessTokenInResponse(response), step1.getRandom5Ingredients());
        int orderNumber1 = step1.checkOrderNumberInResponse(order1);
        int orderNumber2 = step1.checkOrderNumberInResponse(order2);
        Orders orders = step1.sendGETUserOrders(step.checkAccessTokenInResponse(response));
        int actualOrderNumber1 = orders.getOrders()[0].getNumber();
        int actualOrderNumber2 = orders.getOrders()[1].getNumber();
        step.clearTestData(response);
        Assert.assertEquals(orderNumber1, actualOrderNumber1);
        Assert.assertEquals(orderNumber2, actualOrderNumber2);
    }

}
