package com.mark8.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mark8.R;
import com.mark8.ViewModel.CategoryViewModel;
import com.mark8.adapter.ProductAdapter;
import com.mark8.databinding.ActivityCategoryBinding;
import com.mark8.model.Product;
import com.mark8.receiver.NetworkChangeReceiver;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.Constant;
import com.mark8.utils.OnNetworkListener;

import static com.mark8.storage.LanguageUtils.loadLocale;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.InternetUtils.isNetworkConnected;

public class CategoryActivity extends AppCompatActivity implements ProductAdapter.ProductAdapterOnClickHandler, OnNetworkListener {

    private static final String TAG = "CategoryActivity";
    private ActivityCategoryBinding binding;
    private ProductAdapter productAdapter;
    private CategoryViewModel categoryViewModel;
    private String category;
    private Snackbar snack;
    private NetworkChangeReceiver mNetworkReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_category);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(getResources().getString(R.string.app_name));

        // This line shows Up button
     //   actionBar.setDisplayHomeAsUpEnabled(true);

        snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);

        // Get Category from ProductActivity Intent
        Intent intent = getIntent();
        category = intent.getStringExtra(Constant.CATEGORY);

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24); // your drawable
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        // Update Toolbar

        binding.toolbar.setTitle(category);
        int userID = LoginUtils.getInstance(this).getUserInfo().getId();
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        categoryViewModel.loadProductsByCategory(category.toLowerCase(), userID);

        setupRecyclerViews();

        getProductsByCategory();

        mNetworkReceiver = new NetworkChangeReceiver();
        mNetworkReceiver.setOnNetworkListener(this);
    }

    private void setupRecyclerViews() {
        binding.categoryList.setLayoutManager(new GridLayoutManager(this, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));
        binding.categoryList.setHasFixedSize(true);
        productAdapter = new ProductAdapter(this,this);
    }


    public void getProductsByCategory() {
        if (isNetworkConnected(this)) {
            categoryViewModel.categoryPagedList.observe(this, new Observer<PagedList<Product>>() {
                @Override
                public void onChanged(@Nullable PagedList<Product> products) {
                    productAdapter.notifyDataSetChanged();
                    productAdapter.submitList(products);
                }
            });

            binding.categoryList.setAdapter(productAdapter);
        }
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(CategoryActivity.this, DetailsActivity.class);
        // Pass an object of product class
        intent.putExtra(PRODUCT, (product));
        startActivity(intent);
    }

    @Override
    public void onNetworkConnected() {
        hideSnackBar();
        getProductsByCategory();
    }

    @Override
    public void onNetworkDisconnected() {
        showSnackBar();
    }

    public void showSnackBar() {
        snack.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        snack.show();
    }

    public void hideSnackBar() {
        snack.dismiss();
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerNetworkBroadcastForNougat();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkReceiver);
    }
}
