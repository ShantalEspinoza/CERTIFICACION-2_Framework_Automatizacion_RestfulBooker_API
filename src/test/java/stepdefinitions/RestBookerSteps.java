package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.AuthCreds;
import models.Booking;
import models.BookingDates;
import org.junit.jupiter.api.Assertions;

public class RestBookerSteps {

    private Response response;
    private String token;
    private int bookingId;

    @Given("la URI base es {string}")
    public void laUriBaseEs(String uri) {
        RestAssured.baseURI = uri;
    }

    @When("envio una peticion POST a {string} con credenciales {string} y {string}")
    public void envioUnaPeticionPostAConCredencialesY(String endpoint, String username, String password) {
        AuthCreds creds = new AuthCreds(username, password);
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(creds)
                .post(endpoint);
    }

    @Then("el status code de la respuesta debe ser {int}")
    public void elStatusCodeDeLaRespuestaDebeSer(int statusCode) {
        Assertions.assertEquals(statusCode, response.statusCode(), "Status code incorrecto");
    }

    @Then("la respuesta debe contener un {string} no nulo")
    public void laRespuestaDebeContenerUnNoNulo(String key) {
        token = response.jsonPath().getString(key);
        Assertions.assertNotNull(token, "El token no se generó");
    }

    @When("envio una peticion POST a {string} con el nombre {string}, apellido {string} y precio {int}")
    public void envioUnaPeticionPostAConElNombreApellidoYPrecio(String endpoint, String firstname, String lastname, int price) {
        BookingDates dates = new BookingDates("2026-01-01", "2026-01-10");
        Booking booking = new Booking(firstname, lastname, price, true, dates, "Breakfast");

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(booking)
                .post(endpoint);
    }

    @Then("la respuesta de creacion debe contener el nombre {string}")
    public void laRespuestaDeCreacionDebeContenerElNombre(String expectedName) {
        Assertions.assertEquals(expectedName, response.jsonPath().getString("booking.firstname"));
    }

    @Then("se debe generar un {string}")
    public void seDebeGenerarUn(String key) {
        bookingId = response.jsonPath().getInt(key);
        Assertions.assertTrue(bookingId > 0, "No se generó un ID de reserva válido");
    }

    @Given("que existe una reserva previamente creada con el nombre {string}")
    public void queExisteUnaReservaPreviamenteCreadaConElNombre(String name) {
        envioUnaPeticionPostAConElNombreApellidoYPrecio("/booking", name, "Espinoza", 100);
        bookingId = response.jsonPath().getInt("bookingid");
    }

    @When("envio una peticion GET al endpoint {string} con ese ID")
    public void envioUnaPeticionGetAlEndpointConEseId(String endpoint) {
        response = RestAssured.given().get(endpoint + bookingId);
    }

    @Then("el cuerpo de la respuesta debe coincidir con el nombre {string} y apellido {string}")
    public void elCuerpoDeLaRespuestaDebeCoincidirConElNombreYApellido(String name, String lastname) {
        Assertions.assertEquals(name, response.jsonPath().getString("firstname"));
        Assertions.assertEquals(lastname, response.jsonPath().getString("lastname"));
    }

    @Given("que genero un token de autenticacion con {string} y {string}")
    public void queGeneroUnTokenDeAutenticacionConY(String username, String password) {
        envioUnaPeticionPostAConCredencialesY("/auth", username, password);
        token = response.jsonPath().getString("token");
    }

    @When("envio una peticion PUT para actualizar el nombre a {string} y apellido {string} con precio {int}")
    public void envioUnaPeticionPutParaActualizarElNombreAApellidoConPrecio(String name, String lastname, int price) {
        BookingDates dates = new BookingDates("2026-02-01", "2026-02-10");
        Booking booking = new Booking(name, lastname, price, true, dates, "Lunch");

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + token)
                .body(booking)
                .put("/booking/" + bookingId);
    }

    @Then("el cuerpo de la respuesta debe mostrar el nombre actualizado {string}")
    public void elCuerpoDeLaRespuestaDebeMostrarElNombreActualizado(String expectedName) {
        Assertions.assertEquals(expectedName, response.jsonPath().getString("firstname"));
    }

    @When("envio una peticion DELETE para borrar esa reserva")
    public void envioUnaPeticionDeleteParaBorrarEsaReserva() {
        response = RestAssured.given()
                .header("Cookie", "token=" + token)
                .delete("/booking/" + bookingId);
    }

    @Then("si envio un GET a ese ID, el status code debe ser {int}")
    public void siEnvioUnGetAEseIdElStatusCodeDebeSer(int statusCode) {
        Response getResponse = RestAssured.given().get("/booking/" + bookingId);
        Assertions.assertEquals(statusCode, getResponse.statusCode());
    }
}
