package com.mark8.view;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mark8.R;
import com.mark8.ViewModel.ReviewViewModel;
import com.mark8.ViewModel.ToCartViewModel;
import com.mark8.adapter.ReviewAdapter;
import com.mark8.databinding.ActivityDetailsBinding;
import com.mark8.model.Cart;
import com.mark8.model.Product;
import com.mark8.model.Review;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.RequestCallback;

import java.util.List;

import static com.mark8.storage.LanguageUtils.loadLocale;
import static com.mark8.utils.Constant.LOCALHOST;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.Constant.PRODUCTID;
import static com.mark8.utils.Constant.PRODUCT_ID;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailsActivity";

    private ActivityDetailsBinding binding;
    private ReviewViewModel reviewViewModel;
    private ToCartViewModel toCartViewModel;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        toCartViewModel = ViewModelProviders.of(this).get(ToCartViewModel.class);

        binding.txtSeeAllReviews.setOnClickListener(this);
        binding.writeReview.setOnClickListener(this);
        binding.addToCart.setOnClickListener(this);
        binding.buy.setOnClickListener(this);

        getProductDetails();

        setUpRecycleView();

        getReviewsOfProduct();
    }

    private void setUpRecycleView() {
        binding.listOfReviews.setHasFixedSize(true);
        binding.listOfReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.listOfReviews.setItemAnimator(null);
    }

    private void getProductDetails() {
        // Receive the product object
        product = getIntent().getParcelableExtra(PRODUCT);
        Log.d(TAG, "isFavourite " + product.isFavourite() + " isInCart " + product.isInCart());

//        // Set Custom ActionBar Layout
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setCustomView(R.layout.action_bar_title_layout);
     //   ((TextView) findViewById(R.id.action_bar_title)).setText(product.getProductName());
        binding.toolbar.setTitle(product.getProductName());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_keyboard_backspace_24));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.nameOfProduct.setText(product.getProductName());
        binding.priceOfProduct.setText(String.valueOf(product.getProductPrice() + " "+getResources().getString(R.string.currency)));

       //   String imageUrl = LOCALHOST + product.getProductImage().replaceAll("\\\\", "/");
          String imageUrl = product.getProductSupplier();

        Log.e(TAG, "getProductDetails: "+imageUrl );
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.dummy_databookshelf);
        requestOptions.error(R.drawable.dummy_databookshelf);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(binding.imageOfProduct);
    }

    private void getReviewsOfProduct() {
        reviewViewModel.getReviews(product.getProductId()).observe(this, reviewApiResponse -> {
            if (reviewApiResponse != null) {
                reviewList = reviewApiResponse.getReviewList();
                reviewAdapter = new ReviewAdapter(getApplicationContext(), reviewList);
                binding.listOfReviews.setAdapter(reviewAdapter);
                //reviewAdapter.notifyOnInsertedItem();
                reviewAdapter.notifyDataSetChanged();
            }

            if (reviewList.size() == 0) {
                binding.listOfReviews.setVisibility(View.GONE);
                binding.txtFirst.setVisibility(View.VISIBLE);
            } else {
                binding.listOfReviews.setVisibility(View.VISIBLE);
                binding.txtFirst.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtSeeAllReviews) {
            Intent allReviewIntent = new Intent(DetailsActivity.this, AllReviewsActivity.class);
            allReviewIntent.putExtra(PRODUCT_ID, product.getProductId());
            startActivity(allReviewIntent);
        } else if (view.getId() == R.id.writeReview) {
            Intent allReviewIntent = new Intent(DetailsActivity.this, WriteReviewActivity.class);
            allReviewIntent.putExtra(PRODUCT_ID, product.getProductId());
            startActivity(allReviewIntent);
        } else if (view.getId() == R.id.addToCart) {
//            insertToCart(() -> {
//                product.setIsInCart(true);
//            });
//            Intent cartIntent = new Intent(DetailsActivity.this, CartActivity.class);
//            startActivity(cartIntent);

            binding.addToCart.setBackgroundColor(getResources().getColor( R.color.green));
            binding.addToCart.setTextColor(getResources().getColor(R.color.white));
            binding.addToCart.setText(getResources().getString(R.string.added));
        } else if (view.getId() == R.id.buy) {
            Intent shippingIntent = new Intent(DetailsActivity.this, ShippingAddressActivity.class);
            shippingIntent.putExtra(PRODUCTID, product.getProductId());
            startActivity(shippingIntent);
        }
    }

    private void insertToCart(RequestCallback callback) {
        Cart cart = new Cart(LoginUtils.getInstance(this).getUserInfo().getId(), product.getProductId());
        toCartViewModel.addToCart(cart, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReviewsOfProduct();
    }


}
