package com.mark8.view;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.mark8.R;
import com.mark8.ViewModel.FavoriteViewModel;
import com.mark8.adapter.WishListAdapter;
import com.mark8.databinding.ActivityWishlistBinding;
import com.mark8.model.Product;
import com.mark8.storage.LoginUtils;

import java.util.List;

import static com.mark8.storage.LanguageUtils.loadLocale;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.InternetUtils.isNetworkConnected;

public class WishListActivity extends AppCompatActivity {

    private ActivityWishlistBinding binding;
    private WishListAdapter wishListAdapter;
    private List<Product> favoriteList;
    private FavoriteViewModel favoriteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wishlist);


        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24); // your drawable
        binding.toolbar.setTitle(getResources().getString(R.string.my_wishList));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        setUpRecyclerView();

        getFavorites();
    }

    private void setUpRecyclerView() {
        binding.favoriteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.favoriteList.setHasFixedSize(true);
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
    }

    private void getFavorites() {
        if (isNetworkConnected(this)) {
            favoriteViewModel.getFavorites(LoginUtils.getInstance(this).getUserInfo().getId()).observe(this, favoriteApiResponse -> {
                if (favoriteApiResponse != null) {
                    favoriteList = favoriteApiResponse.getFavorites();
                    if (favoriteList.size() == 0) {
                        binding.noBookmarks.setVisibility(View.VISIBLE);
                        binding.emptyWishlist.setVisibility(View.VISIBLE);
                    }else {
                        binding.favoriteList.setVisibility(View.VISIBLE);
                    }
                    wishListAdapter = new WishListAdapter(getApplicationContext(), favoriteList, product -> {
                        Intent intent = new Intent(WishListActivity.this, DetailsActivity.class);
                        // Pass an object of product class
                        intent.putExtra(PRODUCT, (product));
                        startActivity(intent);
                    },this);
                }
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.favoriteList.setAdapter(wishListAdapter);
                wishListAdapter.notifyDataSetChanged();
            });
        }else {
            binding.emptyWishlist.setVisibility(View.VISIBLE);
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.emptyWishlist.setText(getString(R.string.no_internet_connection));
        }
    }

}
