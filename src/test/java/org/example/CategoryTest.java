package org.example;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.example.RestApi.CategoryService;
import org.example.Utils.RetrofitUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CategoryTest {
    static CategoryService categoryService;
    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @SneakyThrows
    @Test
    void GetCategoryById() {
        Response<ResponseBody> response = categoryService.getCategory(1).execute();

        assertThat(response.body().getId(), equalTo(1));
    }
}