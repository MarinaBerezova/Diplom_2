package resources;

import io.restassured.RestAssured;

public abstract class BaseURI {

    public void setBaseURI(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

}
