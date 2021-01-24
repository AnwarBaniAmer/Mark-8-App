package com.mark8.net;

import com.mark8.model.Cart;
import com.mark8.model.CartApiResponse;
import com.mark8.model.Favorite;
import com.mark8.model.FavoriteApiResponse;
import com.mark8.model.History;
import com.mark8.model.HistoryApiResponse;
import com.mark8.model.Image;
import com.mark8.model.LoginApiResponse;
import com.mark8.model.NewsFeedResponse;
import com.mark8.model.OrderApiResponse;
import com.mark8.model.Ordering;
import com.mark8.model.Otp;
import com.mark8.model.ProductApiResponse;
import com.mark8.model.RegisterApiResponse;
import com.mark8.model.Review;
import com.mark8.model.ReviewApiResponse;
import com.mark8.model.Shipping;
import com.mark8.model.User;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("users/register")
    Call<RegisterApiResponse> createUser(@Body User user);

    @GET("users/login")
    Call<LoginApiResponse> logInUser(@Query("email") String email, @Query("password") String password);

    @DELETE("users/{userId}")
    Call<ResponseBody> deleteAccount(@Path("userId") int userId);

    @Multipart
    @PUT("users/upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part userPhoto, @Part("id") RequestBody userId);

    @PUT("users/info")
    Call<ResponseBody> updatePassword(@Query("password") String password, @Query("id") int userId);

    @Multipart
    @POST("products/insert")
    Call<ResponseBody> insertProduct(@PartMap Map<String, RequestBody> productInfo, @Part MultipartBody.Part image);

    @GET("users/getImage")
    Call<Image> getUserImage(@Query("id") int userId);

    @GET("users/otp")
    Call<Otp> getOtp(@Query("email") String email);

    @GET("products")
    Call<ProductApiResponse> getProducts(@Query("page") int page);

    @GET("products")
    Call<ProductApiResponse> getProductsByCategory(@Query("category") String category, @Query("userId") int userId,@Query("page") int page);

    @GET("products")
    Call<ProductApiResponse> getProductsByCategory(@Query("category") String category,@Query("page") int page);

    @GET("products/search")
    Call<ProductApiResponse> searchForProduct(@Query("q") String keyword, @Query("userId") int userId);

    @POST("favorites/add")
    Call<ResponseBody> addFavorite(@Body Favorite favorite);

    @DELETE("favorites/remove")
    Call<ResponseBody> removeFavorite(@Query("userId") int userId, @Query("productId") int productId);

    @GET("favorites")
    Call<FavoriteApiResponse> getFavorites(@Query("userId") int userId);

    @POST("carts/add")
    Call<ResponseBody> addToCart(@Body Cart cart);

    @DELETE("carts/remove")
    Call<ResponseBody> removeFromCart(@Query("userId") int userId, @Query("productId") int productId);

    @GET("carts")
    Call<CartApiResponse> getProductsInCart(@Query("userId") int userId);

    @POST("history/add")
    Call<ResponseBody> addToHistory(@Body History history);

    @DELETE("history/remove")
    Call<ResponseBody> removeAllFromHistory();

    @GET("history")
    Call<HistoryApiResponse> getProductsInHistory(@Query("userId") int userId, @Query("page") int page);

    @POST("review/add")
    Call<ResponseBody> addReview(@Body Review review);

    @GET("review")
    Call<ReviewApiResponse> getAllReviews(@Query("productId") int productId);

    @GET("posters")
    Call<NewsFeedResponse> getPosters();

    @GET("orders/get")
    Call<OrderApiResponse> getOrders(@Query("userId") int userId);

    @POST("address/add")
    Call<ResponseBody> addShippingAddress(@Body Shipping shipping);

    @POST("orders/add")
    Call<ResponseBody> orderProduct(@Body Ordering ordering);
}
