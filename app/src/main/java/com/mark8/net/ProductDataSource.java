package com.mark8.net;

import androidx.paging.PageKeyedDataSource;
import androidx.annotation.NonNull;

import android.util.Log;

import com.mark8.model.Product;
import com.mark8.model.ProductApiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDataSource extends PageKeyedDataSource<Integer, Product> {

    private static final int FIRST_PAGE = 1;
    public static final int PAGE_SIZE = 20;
    private String category;
    private int userId;

    ProductDataSource(String category, int userId) {
        this.category = category;
        this.userId = userId;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Product> callback) {
        List<Product> productList = new ArrayList<Product>();
        // public Product(String productName, double productPrice, int productQuantity, String productSupplier, String productCategory) {

        productList.add(new Product("LEIFARNE", 70.00, 1, "https://www.ikea.com/jo/en/images/products/leifarne-chair-with-armrests-dark-yellow-dietmar-chrome-plated__0745145_PE743604_S5.JPG?f=xxl", "Furniture", "https://www.ikea.com/jo/en/images/products/leifarne-chair-with-armrests-dark-yellow-dietmar-chrome-plated__0745145_PE743604_S5.JPG?f=xxl"));
        productList.add(new Product("LED spotlight, black", 12.00, 1, "https://www.ikea.com/jo/en/images/products/omlopp-led-spotlight-black__0606481_PE682371_S5.JPG?f=g", "Furniture", "https://www.ikea.com/jo/en/images/products/omlopp-led-spotlight-black__0606481_PE682371_S5.JPG?f=g"));
        productList.add(new Product("Modular fabric sofas", 689.00, 1, "https://www.ikea.com/jo/en/images/products/groenlid-3-seat-sofa-with-chaise-longue-ljungen-light-green__0577265_PE668719_S5.JPG?f=s", "Furniture", "https://www.ikea.com/jo/en/images/products/groenlid-3-seat-sofa-with-chaise-longue-ljungen-light-green__0577265_PE668719_S5.JPG?f=s"));
        productList.add(new Product("BESTÅ", 265.00, 1, "https://www.ikea.com/jo/en/images/products/besta-storage-combination-with-doors-black-brown-kallviken-dark-grey-concrete-effect__0845512_PE630719_S5.JPG?f=g", "Furniture", "https://www.ikea.com/jo/en/images/products/besta-storage-combination-with-doors-black-brown-kallviken-dark-grey-concrete-effect__0845512_PE630719_S5.JPG?f=g"));
        productList.add(new Product("HAVSTA", 150.00, 1, "https://www.ikea.com/jo/en/images/products/havsta-cabinet-with-plinth-white__0846119_PE718247_S5.JPG?f=g", "Furniture", "https://www.ikea.com/jo/en/images/products/havsta-cabinet-with-plinth-white__0846119_PE718247_S5.JPG?f=g"));
        productList.add(new Product("3-seat sofa, Lejde red-brown", 819.00, 1, "https://www.ikea.com/jo/en/images/products/lidhult-3-seat-sofa-lejde-red-brown__0619208_PE688989_S5.JPG?f=s", "Furniture", "https://www.ikea.com/jo/en/images/products/lidhult-3-seat-sofa-lejde-red-brown__0619208_PE688989_S5.JPG?f=s"));
        productList.add(new Product("Baby SNIGLAR", 59.00, 1, "https://www.ikea.com/jo/en/images/products/sniglar-cot-beech__0637930_PE698615_S5.JPG?f=s", "Furniture", "https://www.ikea.com/jo/en/images/products/sniglar-cot-beech__0637930_PE698615_S5.JPG?f=s"));
        productList.add(new Product("BookShelf IKEA", 55.00, 1, "https://m2.ikea.cn/PIAimages/0659297_PE710588_S5.JPG?f=s", "Furniture", "https://m2.ikea.cn/PIAimages/0659297_PE710588_S5.JPG?f=s"));
        productList.add(new Product("White BookShelf IKEA", 66.00, 1, "https://www.ikea.cn/cn/en/images/products/billy-bookcase-white__0394567_PE561390_S5.JPG?f=s", "Furniture", "https://www.ikea.cn/cn/en/images/products/billy-bookcase-white__0394567_PE561390_S5.JPG?f=s"));

        callback.onResult(productList, null, FIRST_PAGE + 1);
//        RetrofitClient.getInstance()
//                .getApi().getProductsByCategory(category, 1, 1)
//                .enqueue(new Callback<ProductApiResponse>() {
//                    @Override
//                    public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
//                        Log.v("onResponse", "Succeeded " + response.body().getProducts());
//
//                        if (response.body().getProducts() == null) {
//                            return;
//                        }
//
//                        if (response.body() != null) {
//                            callback.onResult(response.body().getProducts(), null, FIRST_PAGE + 1);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProductApiResponse> call, Throwable t) {
//                        Log.v("onFailure", "product data sourceFailed to get Products");
//                        Log.e("onFailure", "product data source: " + "if null , add products manually");
//                        Log.e("getCause", "onFailure: ",t.getCause() );
//                    }
//
//
//                });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Product> callback) {
        RetrofitClient.getInstance()
                .getApi().getProductsByCategory(category, userId, params.key)
                .enqueue(new Callback<ProductApiResponse>() {
                    @Override
                    public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                        Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                        if (response.body() != null) {
                            // Passing the loaded database and the previous page key
                            callback.onResult(response.body().getProducts(), adjacentKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                        Log.v("onFailure", "Failed to previous Products");

                    }
                });
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Product> callback) {
        RetrofitClient.getInstance()
                .getApi().getProductsByCategory(category, userId, params.key)
                .enqueue(new Callback<ProductApiResponse>() {
                    @Override
                    public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                        if (response.body() != null) {
                            // If the response has next page, increment the next page number
                            Integer key = response.body().getProducts().size() == PAGE_SIZE ? params.key + 1 : null;

                            // Passing the loaded database and next page value
                            callback.onResult(response.body().getProducts(), key);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                        Log.v("onFailure", "Failed to get next Products");
                    }
                });
    }
}
