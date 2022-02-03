package org.example.RestApi;

import okhttp3.ResponseBody;
import org.example.DTO.ProductResponse;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface ProductService {

    @GET("products")
    Call<ProductResponse> getProducts();

    @GET("products/{id}")
    Call<ProductResponse> getProductById(@Path("id") int id);

    @POST("products")
    Call<ProductResponse> createProduct(@Body ProductResponse createProductRequest);

    @PUT("products")
    Call<ProductResponse> putModifyProduct(@Body ProductResponse putModifyProductRequest);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);

}
