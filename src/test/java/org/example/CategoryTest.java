package org.example;

import db.dao.CategoriesMapper;
import db.model.Categories;
import db.model.CategoriesExample;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;
import org.example.DTO.GetCategoryResponse;
import org.example.RestApi.CategoryService;
import org.example.Utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CategoryTest extends DBBaseTest {
    static CategoryService categoryService;
    static CategoriesMapper categoriesMapper;
    static SqlSession session;

    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit()
        .create(CategoryService.class);

        session = sqlSessionFactory.openSession();
        categoriesMapper = session.getMapper(CategoriesMapper.class);

    }

    @SneakyThrows
    @Test
    @DisplayName("Get category by Id")
    void GetCategoryById() {
        Response<GetCategoryResponse> response = categoryService.getCategory(2).execute();
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andIdEqualTo(2);
        List<Categories> list = categoriesMapper.selectByExample(example);

        System.out.println(response.body().getTitle());

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(2));
        assertThat(response.body().getTitle(), equalTo("Electronic"));
        assertThat(list.size(), equalTo(1));
        assertThat(list.stream().allMatch((l)-> l.getId() == 2), equalTo(true));

        response.body().getProducts().forEach(product ->
        assertThat(product.getCategoryTitle(), equalTo("Electronic")));
    }

    @SneakyThrows
    @Test
    @DisplayName("404 zero Id")
    void GetCategoryByZeroId() {
        Response<GetCategoryResponse> response = categoryService.getCategory(0).execute();
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andIdEqualTo(0);
        List<Categories> list = categoriesMapper.selectByExample(example);

        System.out.println("Wrong id, id > 0 expected\n" + response.code());

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
        assertThat(list.size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    @DisplayName("404 too much Id")
    void GetCategoryByTooMuchId() {
        Response<GetCategoryResponse> response = categoryService.getCategory(12).execute();
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andIdEqualTo(0);
        List<Categories> list = categoriesMapper.selectByExample(example);

        System.out.println("Id is not exist\n" + response.code());

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
        assertThat(list.size(), equalTo(0));
    }

    @AfterAll
    static void AfterAll() {
        session.close();
    }
}