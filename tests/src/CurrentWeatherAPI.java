import io.restassured.http.ContentType;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.Matchers.equalTo;

public class CurrentWeatherAPI extends Weather {

    private String ENDPOINT_GET_WEATHER_DATA = "http://api.openweathermap.org/data/2.5/weather";
    private String ENDPOINT_GET_CITIES_IN_CYCLE = "http://api.openweathermap.org/data/2.5/find";


    @Test
    private void testGetByCityID_Valid() {
        String id = "2988507";
        given().
                pathParam("id",id).
                pathParam("APPID", appid).
                expect().statusCode(200).when().
                get(ENDPOINT_GET_WEATHER_DATA +"?id={id}&APPID={APPID}").
                then().
                assertThat().
                contentType(ContentType.JSON).
                body("name",containsString("Paris"));
    }

    @Test
    private void testGetByCityID_Invalid() {
        String id = "_0";
        given().
                pathParam("id",id).
                pathParam("APPID", appid).
                expect().statusCode(400).when().
                get(ENDPOINT_GET_WEATHER_DATA +"?id={id}&APPID={APPID}").
                then().assertThat().body("message", containsString("is not a city ID"));
    }

    @Test
    private void testGetByCityID_ResponseStructure(){
        String id = "2643743";
        request = given().
                pathParam("id",id).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_WEATHER_DATA +"?id={id}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("coord.size()",equalTo(2),
                "weather.size()",equalTo(4),
                "base.size()",equalTo(1),
                "main.size()",equalTo(5),
                "visibility.size()",equalTo(1),
                "wind.size()",equalTo(2),
                "clouds.size()",equalTo(1),
                "dt.size()",equalTo(1),
                "dt.size()",equalTo(6),
                "name",containsString("London"));
    }

    @Test
    private void testGetByZipCode_Valid() {
        String zip = "20121";
        String country = "IT";
        given().
                contentType(ContentType.JSON).
                pathParam("zip",zip).
                pathParam("country",country).
                pathParam("APPID", appid).
                expect().statusCode(200).when().
                get(ENDPOINT_GET_WEATHER_DATA +"?zip={zip},{country}&APPID={APPID}").
                then().
                assertThat().
                body("name",containsString("Milano"));
    }

    @Test
    private void testGetByZipCode_GeoCoordinates() {
        String zip = "1000";
        String country = "BG";
        request = given().
                pathParam("zip",zip).
                pathParam("country", country).
                pathParam("APPID", appid);
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_WEATHER_DATA +"?zip={zip},{country}&units=metric&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("coord.lon",containsString("23.32"),
                "coord.lat",containsString("42.68"),
                "id", containsString("729968"),
                "name",containsString("Krasno Selo"));
    }

    @Test
    private void testGetByZipCode_ResponseTime() {
        String zip = "60329"; //Frankfurt am Main
        String country = "DE";
        given().
                pathParam("zip",zip).
                pathParam("country", country).
                pathParam("APPID", appid).
                expect().statusCode(200).when().
                get(ENDPOINT_GET_WEATHER_DATA +"?zip={zip},{country}&units=metric&APPID={APPID}").
                then().
                assertThat().
                contentType(ContentType.JSON).time(lessThan(1000L)); // Milliseconds
    }

    @Test
    private void testGetByCitiesInCycle_ResponseLength(){
        String lat = "37.39";
        String lon= "-122.09";
        String cnt = "5";
        request = given().
                pathParam("lat", lat).
                pathParam("lon",lon).
                pathParam("cnt", cnt).
                pathParam("APPID", "947f419b84fc8b8fcc3a52d96efb6d80");
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_CITIES_IN_CYCLE+"?lat={lat}&lon={lon}&cnt={cnt}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("list.size()",equalTo(5),
                "name",containsString("Mountain View"));
    }

    @Test
    private void testGetByCitiesInCycle_NearestCities(){
        String lat = "51.51";
        String lon= "-0.13";
        String cnt = "5";
        request = given().
                pathParam("lat", lat).
                pathParam("lon", lon).
                pathParam("cnt", cnt).
                pathParam("APPID", "947f419b84fc8b8fcc3a52d96efb6d80");
        response = request.expect().statusCode(200).when().get(ENDPOINT_GET_CITIES_IN_CYCLE+"?lat={lat}&lon={lon}&cnt={cnt}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON);
        expect().body("list.name[0]",containsString("London"),
                "list.name[1]",containsString("City of Westminster"),
                "list.name[2]",containsString("Lambeth"),
                "list.name[3]",containsString("Clerkenwell"),
                "list.name[4]",containsString("City of London"));
    }


    @Test
    private void testGetByCitiesInCycle_MaxCnt(){
        String lat = "51.51";
        String lon= "-0.13";
        String cnt = "51";
        request = given().
                pathParam("lat", lat).
                pathParam("lon", lon).
                pathParam("cnt", cnt).
                pathParam("APPID", "947f419b84fc8b8fcc3a52d96efb6d80");
        response = request.expect().statusCode(400).when().get(ENDPOINT_GET_CITIES_IN_CYCLE+"?lat={lat}&lon={lon}&cnt={cnt}&APPID={APPID}");
        json = response.then().contentType(ContentType.JSON).
                assertThat().body("message", containsString("cnt from 1 to 50"));
    }
}






