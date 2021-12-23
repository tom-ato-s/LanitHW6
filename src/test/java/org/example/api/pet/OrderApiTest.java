


package org.example.api.pet;

        import io.restassured.RestAssured;
        import io.restassured.builder.RequestSpecBuilder;
        import io.restassured.filter.log.LogDetail;
        import io.restassured.filter.log.ResponseLoggingFilter;
        import io.restassured.http.ContentType;
        import org.example.model.Order;
        import org.testng.Assert;
        import org.testng.annotations.BeforeClass;
        import org.testng.annotations.Test;
        import java.io.IOException;
        import java.util.Random;

        import static io.restassured.RestAssured.given;
        import static io.restassured.RestAssured.proxy;

public class OrderApiTest {
    private static int id;
    @BeforeClass
    public void prepareOrder() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))

                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());
    }

    /**
     * Простейшая проверка: создаём объект, сохраняем на сервере и проверяем, что при запросе возвращается
     * "тот же" объект
     */
    @Test(priority = 1)
    public void checkObjectSaveOrder() throws InterruptedException {
        Order order = new Order();
         id = (1+new Random().nextInt(10));
        int petId = new Random().nextInt(500000);
        int quantity = (1+new Random().nextInt(10));

        order.setId(id);
        order.setPetId(petId);
        order.setQuantity(quantity);

        given()
                .body(order)
                .when()
                .post("/store/order")

                .then() // ТОГДА: (указывает, что после этой части будут выполняться проверки-утверждения)
                .statusCode(200); // например проверка кода ответа.он просто выдёргивается из текста ответа

        Thread.sleep(100000);
        Order actual = (Order)
                given()
                        .pathParam("orderId", id)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body();
        Assert.assertEquals(actual.getId(), order.getId());

    }

    @Test (priority = 2)
    public void tetDeleteOrder() throws IOException, InterruptedException {
        Thread.sleep(100000);
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .pathParam("petId", id)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        Thread.sleep(100000);
        given()
                .pathParam("petId", id)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }
}
