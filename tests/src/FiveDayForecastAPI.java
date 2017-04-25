import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;

public class FiveDayForecastAPI extends Weather {

    private String ENDPOINT_GET_FORECAST_DATA = "http://api.openweathermap.org/data/2.5/forecast";

    @Test
    private void testGetByCityName_Valid() {
        String cityName = "Barcelona";
        String country ="ES";
        request = given().
                pathParam("q",cityName).
                pathParam("country",country).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?q={q},{country}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id",is("3128760"),
                "city.coord.lat",equalTo("41.3888"),
                "city.coord.lon",equalTo("2.159"));
    }

    @Test
    private void testGetByCityName_ResponseTime() {
        String cityName = "Stuttgart";
        String country ="DE";
        given().
                pathParam("q",cityName).
                pathParam("country",country).
                pathParam("APPID", appid).
                expect().statusCode(200).when().
                get(ENDPOINT_GET_FORECAST_DATA +"?q={q},{country}&APPID={APPID}").
                then().
                assertThat().
                contentType(ContentType.JSON).time(lessThan(700L)); // Milliseconds
    }

    @Test
    private void testGetByCityName_WithoutCityName() {
        String cityName = "";
        String country ="BG";
        request = given().
                pathParam("q",cityName).
                pathParam("country",country).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?q={q},{country}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id",containsString("727011"),
                "city.name",containsString("Sofia"),
                "city.coord.lat",containsString("42.6975"),
                "city.coord.lon",contains("23.3242"));
    }

    @Test
    private void testGetByGeoLocation_Valid() {
        String lat = "50.4333";
        String lon ="30.5167";
        request = given().
                pathParam("lat",lat).
                pathParam("lon",lon).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?lat={lat}&lon={lon}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id",containsString("703448"),
                "city.name",containsString("Kiev"),
                "city.country",containsString("UA"));
    }

    @Test
    private void testGetByGeoLocation_Invalid(){
        String lat = "";
        String lon= "";
        request = given().
                pathParam("lat", lat).
                pathParam("lon", lon).
                pathParam("APPID", appid);
        response = request.expect().statusCode(400).when().get(ENDPOINT_GET_FORECAST_DATA +"?lat={lat}&lon={lon}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON).
                assertThat().body("message", containsString("Nothing to geocode"));
    }

    @Test
    private void testGetByGeoLocation_NumberOfCities() {
        String lat = "50.4333";
        String lon = "30.5167";
        request = given().
                pathParam("lat", lat).
                pathParam("lon", lon).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?lat={lat}&lon={lon}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id.size()",equalTo(1));
    }

    @Test
    private void testGetByZipCode_Valid() {
        String zip = "2100";
        String country ="DK";
        request = given().
                pathParam("zip",zip).
                pathParam("country",country).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?zip={zip},{country}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id",containsString("2618425"),
                "city.name",containsString("Copenhagen"),
                "city.coord.lat",containsString("55.6759"),
                "city.coord.lon",containsString("12.5655"),
                "city.population",containsString("15000"));
    }

    @Test
    private void testGetByZipCode_Invalid() {
        String zip = "727011";
        given().
                pathParam("zip",zip).
                pathParam("APPID", appid).
                when().
                get(ENDPOINT_GET_FORECAST_DATA +"?zip={zip}&APPID={APPID}").
                then().
                assertThat().
                statusCode(404).
                body("message",containsString("city not found"));
    }

    @Test
    private void testGetByZipCode_ResponseLength() {
        String zip = "5800";
        String country = "BG";
        request = given().
                pathParam("zip",zip).pathParam("country",country).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_FORECAST_DATA +"?zip={zip},{country}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("city.id",equalTo(36));
    }
}
