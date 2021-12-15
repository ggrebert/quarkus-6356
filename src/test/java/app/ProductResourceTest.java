package app;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.UUID;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class ProductResourceTest {

    private static final String PRODUCT_PATH = "/api/products";
    private static String PRODUCT_ID_1;
    private static String PRODUCT_ID_2;

    @Test
    @Order(1)
    @DisplayName("Get all products with empty database")
    public void testFindEmpty() {
        given()
          .when().get(PRODUCT_PATH)
          .then()
            .statusCode(200)
            .body(is("[]"));
    }

    @Test
    @Order(1)
    @DisplayName("Get 404 when product not found")
    public void testGetUnknownProduct() {
        given()
          .when().get(PRODUCT_PATH + "/" + UUID.randomUUID())
          .then()
            .statusCode(404);
    }

    @Test
    @Order(1)
    @DisplayName("Delete 404 when product not found")
    public void testDeleteUnknownProduct() {
        given()
          .when().delete(PRODUCT_PATH + "/" + UUID.randomUUID())
          .then()
            .statusCode(404);
    }

    @Test
    @Order(2)
    @DisplayName("Create new product")
    public void testCreateProduct() {
        Product product = given()
          .body("{\"title\":\"test\"}")
            .contentType(ContentType.JSON)
          .when().post(PRODUCT_PATH)
          .then()
            .statusCode(201)
            .body("title", is("test"))
            .extract().as(Product.class);

        PRODUCT_ID_1 = product.id.toString();
    }

    @Test
    @Order(3)
    @DisplayName("Get all products with one product")
    public void testFindOne() {
        given()
            .when().get(PRODUCT_PATH)
          .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].id", is(PRODUCT_ID_1))
            .body("[0].title", is("test"));
    }

    @Test
    @Order(3)
    @DisplayName("Get product by ID")
    public void testGetProduct() {
        given()
          .when().get(PRODUCT_PATH + "/" + PRODUCT_ID_1)
          .then()
            .statusCode(200)
            .body("id", is(PRODUCT_ID_1))
            .body("title", is("test"));
    }

    @Test
    @Order(3)
    @DisplayName("Create new product with same title")
    public void testCreateProductWithSameTitle() {
        given()
          .body("{\"title\":\"test\"}")
            .contentType(ContentType.JSON)
          .when().post(PRODUCT_PATH)
          .then()
            .statusCode(409);
    }

    @Test
    @Order(4)
    @DisplayName("Create another product")
    public void testCreateProduct2() {
        Product product = given()
          .body("{\"title\":\"test2\"}")
            .contentType(ContentType.JSON)
          .when().post(PRODUCT_PATH)
          .then()
             .statusCode(201)
             .body("title", is("test2"))
             .extract().as(Product.class);

        PRODUCT_ID_2 = product.id.toString();
    }

    @Test
    @Order(5)
    @DisplayName("Update product with existing title")
    public void testUpdateProductWithExistingTitle() {
        given()
          .body("{\"title\":\"test2\"}")
            .contentType(ContentType.JSON)
          .when().put(PRODUCT_PATH + "/" + PRODUCT_ID_1)
          .then()
            .statusCode(409);
    }

    @Test
    @Order(5)
    @DisplayName("Update product")
    public void testUpdateProduct() {
        given()
          .body("{\"title\":\"title_updated\"}")
            .contentType(ContentType.JSON)
          .when().put(PRODUCT_PATH + "/" + PRODUCT_ID_1)
          .then()
            .statusCode(200)
            .body("id", is(PRODUCT_ID_1))
            .body("title", is("title_updated"));
    }

    @Test
    @Order(6)
    @DisplayName("Delete product 1")
    public void testDeleteProduct1() {
        given()
          .when().delete(PRODUCT_PATH + "/" + PRODUCT_ID_1)
          .then()
            .statusCode(204);
    }

    @Test
    @Order(6)
    @DisplayName("Delete product 2")
    public void testDeleteProduct2() {
        given()
          .when().delete(PRODUCT_PATH + "/" + PRODUCT_ID_2)
          .then()
            .statusCode(204);
    }

    @Test
    @Order(7)
    @DisplayName("Get all products with empty database")
    public void testFindEmptyAfterDelete() {
        given()
          .when().get(PRODUCT_PATH)
          .then()
            .statusCode(200)
            .body(is("[]"));
    }
}
