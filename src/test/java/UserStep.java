import io.qameta.allure.Step;
import io.restassured.response.Response;
import resources.TestDataGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class UserStep {

    @Step("Send POST /auth/register request")
    public Response sendPOSTRegisterUser(User user) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .post("/api/auth/register");
        return response;
    }

    @Step("Send POST /auth/login request")
    public Response sendPOSTLoginUser(User user) {
        UserCredentials credentials = new UserCredentials(user.getEmail(), user.getPassword());
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credentials)
                        .post("/api/auth/login");
        return response;
    }

    @Step("Send POST /auth/login request")
    public Response sendPOSTLoginUser(String email, String password) {
        UserCredentials credentials = new UserCredentials(email, password);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credentials)
                        .post("/api/auth/login");
        return response;
    }

    @Step("Send PATCH /auth/user request")
    public Response sendPATCHUser(String accessToken, String field, String value) {
        Response response =
                given()
                        .auth().oauth2(accessToken)
                        .header("Content-type", "application/json")
                        .and()
                        .body(String.format("{\"%s\":\"%s\"}", field, value))
                        .patch("/api/auth/user");
        return response;
    }

    @Step("Send DELETE /auth/user request")
    public Response sendDELETEUser(String accessToken) {
        Response response =
                given()
                        .auth().oauth2(accessToken)
                        .delete("/api/auth/user");
        return response;
    }

    @Step("Check status code '200'")
    public void checkResponseStatus200(Response response) {
        response.then().statusCode(200);
    }

    @Step("Check status code '202'")
    public void checkResponseStatus202(Response response) {
        response.then().statusCode(202);
    }

    @Step("Check status code '401'")
    public void checkResponseStatus401(Response response) {
        response.then().statusCode(401);
    }

    @Step("Check status code '403'")
    public void checkResponseStatus403(Response response) {
        response.then().statusCode(403);
    }

    @Step("Check 'success: true' in response")
    public void checkSuccessTrueInResponse(Response response) {
        response.then().assertThat().body("success", equalTo(true));
    }

    @Step("Check 'success: false' in response")
    public void checkSuccessFalseInResponse(Response response) {
        response.then().assertThat().body("success", equalTo(false));
    }

    @Step("Check 'user' block in response with email and name")
    public void checkUserInfoInResponse(User user, Response response) {
        response.then().assertThat().body("user.email", equalTo(user.getEmail()));
        response.then().assertThat().body("user.name", equalTo(user.getName()));
    }

    @Step("Check access token in response")
    public String checkAccessTokenInResponse(Response response) {
        response.then().assertThat().body("accessToken", notNullValue());
        response.then().assertThat().body("accessToken", startsWith("Bearer "));
        String accessToken = response.then().extract().path("accessToken");
        accessToken=accessToken.substring(7);
        return accessToken;
    }

    @Step("Check refresh token in response")
    public String checkRefreshTokenInResponse(Response response) {
        response.then().assertThat().body("refreshToken", notNullValue());
        String refreshToken = response.then().extract().path("refreshToken");
        return refreshToken;
    }

    @Step("Check 'User already exists' message in response")
    public void checkUserExistsMessageInResponse(Response response) {
        response.then().assertThat().body("message", equalTo("User already exists"));
    }

    @Step("Check 'You should be authorised' message in response")
    public void checkUserNotAuthorizedMessageInResponse(Response response) {
        response.then().assertThat().body("message", equalTo("You should be authorised"));
    }

    @Step("Check 'email or password are incorrect' message in response")
    public void checkLoginFailedMessageInResponse(Response response) {
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Step("Check 'Email, password and name are required fields' message in response")
    public void checkMissingRequiredFieldsMessageInResponse(Response response) {
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Clear test data (delete test user)")
    public void clearTestData(Response response) {
        String accessToken=checkAccessTokenInResponse(response);
        Response response1 = sendDELETEUser(accessToken);
        checkResponseStatus202(response1);
    }

    @Step("Update one of user object fields")
    public String updateFieldInUserObject (User user, String field){
        String newValue = null;
        switch (field) {
            case "email":
                user.setEmail(new TestDataGenerator().getEmail());
                newValue = user.getEmail();
                break;
            case "password":
                user.setPassword(new TestDataGenerator().getPassword());
                newValue = user.getPassword();
                break;
            case "name":
                user.setName(new TestDataGenerator().getName());
                newValue = user.getName();
                break;
        }
        return newValue;
    }

}
