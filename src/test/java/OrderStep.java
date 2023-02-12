import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class OrderStep {

    @Step("Send GET /api/orders request, return response as Response")
    public Response sendGETUserOrdersList(String accessToken) {
        Response response =
                given()
                        .auth().oauth2(accessToken)
                        .get("/api/orders");
        return response;
    }

    @Step("Send GET /api/orders request, return response as Orders class")
    public Orders sendGETUserOrders(String accessToken) {
        Orders orders =
                given()
                        .auth().oauth2(accessToken)
                        .get("/api/orders")
                        .body().as(Orders.class);
        return orders;
    }

    @Step("Send GET /api/ingredients request")
    public Response sendGETIngredients() {
        Response response =
                given()
                        .get("/api/ingredients");
        return response;
    }

    @Step("Send POST /api/orders request")
    public Response sendPOSTOrders(String accessToken, ArrayList ingredients) {
        Order order = new Order(ingredients);
        Response response =
                given()
                        .auth().oauth2(accessToken)
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .post("/api/orders");
        return response;
    }

    @Step("Check status code '400'")
    public void checkResponseStatus400(Response response) {
        response.then().statusCode(400);
    }

    @Step("Check status code '500'")
    public void checkResponseStatus500(Response response) {
        response.then().statusCode(500);
    }

    @Step("Check 'Ingredient ids must be provided' message in response")
    public void checkNoIngredientsMessageInResponse(Response response) {
        response.then().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Check burger name in response")
    public void checkBurgerNameInResponse(Response response) {
        response.then().assertThat().body("name", notNullValue());
        response.then().assertThat().body("name", containsString("бургер"));
    }

    @Step("Check order number in response")
    public int checkOrderNumberInResponse(Response response) {
        response.then().assertThat().body("order.number", notNullValue());
        int number = response.then().extract().path("order.number");
        return number;
    }

    @Step("Get random 5 ingredients")
    public ArrayList getRandom5Ingredients() {
        Response response = sendGETIngredients();
        ArrayList<String> array = response.then().extract().path("data._id");
        Random random = new Random();
        ArrayList<String> ingredients = new ArrayList<>();
        for (int i=1; i<6; i++){
            ingredients.add(array.get(random.nextInt(array.size())).toString());
        }
        return ingredients;
    }
    @Step("Extract first 5 ingredients from response")
    public ArrayList<String> extractIngredientsFromResponse(Response response) {
        ArrayList<String> actualIngredients = new ArrayList<>();
        for (int i=0; i<5; i++){
            String path = String.format("order.ingredients[%s]._id", i);
            actualIngredients.add(response.then().extract().path(path));
        }
        return actualIngredients;
    }
    
}
