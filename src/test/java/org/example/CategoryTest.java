package org.example;

import lombok.SneakyThrows;
import org.example.DTO.GetCategoryResponse;
import org.example.RestApi.CategoryService;
import org.example.Utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CategoryTest {
    static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit()
        .create(CategoryService.class);
    }

    @SneakyThrows
    @Test
    @DisplayName("Get category by Id")
    void GetCategoryById() {
        Response<GetCategoryResponse> response = categoryService.getCategory(2).execute();
        System.out.println(response.body().getTitle());

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(2));
        assertThat(response.body().getTitle(), equalTo("Electronic"));

        response.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Electronic")));

    }

    @SneakyThrows
    @Test
    @DisplayName("404 zero Id")
    void GetCategoryByZeroId() {
        Response<GetCategoryResponse> response = categoryService.getCategory(0).execute();
        System.out.println("Wrong id, id > 0 expected\n" + response.code());

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }

    @SneakyThrows
    @Test
    @DisplayName("404 too much Id")
    void GetCategoryByTooMuchId() {
        Response<GetCategoryResponse> response = categoryService.getCategory(12).execute();
        System.out.println("Id is not exist\n" + response.code());

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }
}