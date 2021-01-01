package com.mark8.view;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.Toast;

import com.mark8.R;
import com.mark8.ViewModel.SearchViewModel;
import com.mark8.adapter.SearchAdapter;
import com.mark8.databinding.ActivityResultBinding;
import com.mark8.model.Product;
import com.mark8.storage.LoginUtils;

import java.util.List;

import static com.mark8.utils.Constant.KEYWORD;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.InternetUtils.isNetworkConnected;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private SearchAdapter searchAdapter;
    private List<Product> searchedList;
    private SearchViewModel searchViewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result);

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(KEYWORD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(keyword);

        userId = LoginUtils.getInstance(this).getUserInfo().getId();

        if (isNetworkConnected(getApplicationContext())) {
            Search(keyword);
        }
    }

    private void Search(String query) {

        binding.listOfSearchedList.setHasFixedSize(true);
        binding.listOfSearchedList.setLayoutManager(new GridLayoutManager(this, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));

        searchViewModel.getProductsBySearch(query, userId).observe(this, productApiResponse -> {
            if (productApiResponse != null) {
                searchedList = productApiResponse.getProducts();
                if (searchedList.isEmpty()) {
                    Toast.makeText(ResultActivity.this, "No Result", Toast.LENGTH_SHORT).show();
                }

                searchAdapter = new SearchAdapter(getApplicationContext(), searchedList, new SearchAdapter.SearchAdapterOnClickHandler() {
                    @Override
                    public void onClick(Product product) {
                        Intent intent = new Intent(ResultActivity.this, DetailsActivity.class);
                        // Pass an object of product class
                        intent.putExtra(PRODUCT, (product));
                        startActivity(intent);
                    }
                },this);
            }
            binding.listOfSearchedList.setAdapter(searchAdapter);
        });
    }
}
