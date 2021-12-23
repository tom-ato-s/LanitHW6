/*
тесты для тестирования работы на сайте https://petstore.swagger.io
end point /store/order
в тестах проставлены засыпание потока для корректной работы.
*/


package org.example.api.pet;

        import io.restassured.RestAssured;
        import io.restassured.builder.RequestSpecBuilder;
        import io.restassured.filter.log.LogDetail;
        import io.restassured.filter.log.ResponseLoggingFilter;
        import io.restassured.http.ContentType;
        import org.example.model.Order;
        import org.example.model.Pet;
        import org.testng.Assert;
        import org.testng.annotations.BeforeClass;
        import org.testng.annotations.Test;
        import java.io.IOException;
        import java.util.Random;

        import static io.restassured.RestAssured.given;


public class OrderApiTest {
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
     * Простейшая проверка: создаём объект заказа, сохраняем на сервере и проверяем, что при запросе возвращается
     * "тот же" объект
     */
    @Test(priority = 1)
    public void checkObjectSaveOrder() throws InterruptedException, IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        Order order = new Order();
        int ipOrder = Integer.parseInt(System.getProperty("id"));
        int petId = new Random().nextInt(500000);
        int quantity = 1;
        order.setPetId(petId);
        order.setQuantity(quantity);
        order.setId(ipOrder);

         given()
                .body(order)
                .when()
                .post("/store/order")

                .then()
                .statusCode(200);

        Thread.sleep(30000);
        Order actual = (Order)
                given()
                        .pathParam("orderId", ipOrder)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Order.class);
        Assert.assertEquals(actual.getPetId(), order.getPetId());

    }

    /**
     * Удаляем созданный объект заказа, и после этого проверяем - есть ли на сайте тот же объект
     **/
    @Test (priority = 2)
    public void tetDeleteOrder() throws IOException, InterruptedException {
        Thread.sleep(100000);
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        int idOrder = Integer.parseInt(System.getProperty("id"));
        given()
                .pathParam("orderId", idOrder)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        Thread.sleep(100000);
        given()
                .pathParam("orderId", idOrder)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }
}
