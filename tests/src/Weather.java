import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class Weather {
    //The response of a request made by REST Assured.
    public Response response;

    //Allows you to specify how the request will look like.
    public RequestSpecification request;

    /*A validatable response of a request made by REST Assured.
    Usage: json = response.then().statusCode(statusCode) returns response when statusCode is 200
    */
    public ValidatableResponse json;
    public String appid = "947f419b84fc8b8fcc3a52d96efb6d80";


}
