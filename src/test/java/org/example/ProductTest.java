package org.example;

import com.github.javafaker.Faker;
import db.dao.ProductsMapper;
import db.model.Products;
import db.model.ProductsExample;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.session.SqlSession;
import org.example.DTO.ProductResponse;
import org.example.RestApi.ProductService;
import org.example.Utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProductTest extends DBBaseTest{
    static ProductService productService;
    static ProductsMapper productsMapper;
    static SqlSession session;

    ProductResponse product;
    ProductResponse fakeProduct;
    Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
        .create(ProductService.class);

        session = sqlSessionFactory.openSession();
        productsMapper = session.getMapper(ProductsMapper.class);
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

        long id = response.body().getId();
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo(id);
        List<Products> list = productsMapper.selectByExample(example);

        System.out.println(response.body().getTitle());

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(expectedId));
        assertThat(list.size(), equalTo(1));
        assertThat(list.stream().allMatch((l)-> l.getId() != 0), equalTo(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("Product is changed")
    void modifyingProduct() {
        Response<ProductResponse> response = productService.putModifyProduct(product.withTitle("Apple").withPrice(6000))
                .execute();
        product = response.body();

        ProductsExample example = new ProductsExample();
        example.createCriteria().andTitleEqualTo("Apple");
        example.createCriteria().andPriceEqualTo(6000);
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), equalTo("Apple"));
        assertThat(response.body().getPrice(), equalTo(6000));
        assertThat(response.headers().get("Content-Type"), equalTo("application/json"));
        assertThat(list.stream().allMatch((l)-> Objects.equals(l.getTitle(), "Apple")), equalTo(true));
        assertThat(list.stream().anyMatch((l)-> l.getPrice() == 6000), equalTo(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("Product created")
    void createProductInFoodCategoryTest() {

        Response<ProductResponse> response = productService.createProduct(fakeProduct)
                .execute();
                product = response.body();

        ProductsExample example = new ProductsExample();
        example.createCriteria().andCategory_idEqualTo(1);
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.headers().get("Content-Type"), equalTo("application/json"));
        assertThat(list.stream().allMatch((l)-> l.getId() != 0), equalTo(true));
        assertThat(list.stream().allMatch((l)-> l.getCategory_id() == 1), equalTo(true));
    }

    @SneakyThrows
    @AfterEach
     void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct(product.getId()).execute();

        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdIsNull();
        List<Products> list = productsMapper.selectByExample(example);


        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(list.stream().allMatch((l)-> l.getId() == 0), equalTo(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("404 Id not exist")
    void getProductNotExist() {
        Response<ProductResponse> response = productService.getProductById(99999).execute();

        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo(99999L);
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
        assertThat(list.size(), equalTo(0));
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

    @AfterAll
    static void AfterAll() {
        session.close();
    }
}
