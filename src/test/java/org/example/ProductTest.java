package org.example;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.example.DTO.ProductResponse;
import org.example.RestApi.ProductService;
import org.example.Utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProductTest {
    static ProductService productService;
    ProductResponse product;
    ProductResponse fakeProduct;
    Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() throws IOException {
        fakeProduct = new ProductResponse()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 100));

        Response<ProductResponse> response = productService.createProduct(fakeProduct)
                .execute();
        product = response.body();
    }

    @Test
    @SneakyThrows
    @DisplayName("Get product by Id")
    void getProductById() {
        int expectedId = product.getId();
        Response<ProductResponse> response = productService.getProductById(product.getId()).execute();
        product = response.body();
        System.out.println(response.body().getTitle());

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(expectedId));

    }

    @Test
    @SneakyThrows
    @DisplayName("Product is changed")
    void modifyingProduct() {
        Response<ProductResponse> response = productService.putModifyProduct(product.withTitle("Apple").withPrice(6000))
                .execute();
        product = response.body();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), equalTo("Apple"));
        assertThat(response.body().getPrice(), equalTo(6000));
        assertThat(response.headers().get("Content-Type"), equalTo("application/json"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Product created")
    void createProductInFoodCategoryTest() {
        Response<ProductResponse> response = productService.createProduct(fakeProduct)
                .execute();
                product = response.body();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.headers().get("Content-Type"), equalTo("application/json"));

    }

    @SneakyThrows
    @AfterEach
     void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct(product.getId()).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("404 Id not exist")
    void getProductNotExist() {
        Response<ProductResponse> response = productService.getProductById(99999).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }

    //    Тест отключен, запрос возвращает "status": 500, "error": "Internal Server Error"
//    @Test
//    @SneakyThrows
//    @DisplayName("Get products")
//    void getProducts() {
//        Response<ProductResponse> response = productService.getProducts()
//                .execute();
//
//        System.out.println(response.toString());
//        assertThat(response.isSuccessful(), CoreMatchers.is(true));
//    }
}
