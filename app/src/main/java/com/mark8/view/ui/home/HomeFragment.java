package com.mark8.view.ui.home;

import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mark8.R;
import com.mark8.ViewModel.HistoryViewModel;
import com.mark8.ViewModel.ProductViewModel;
import com.mark8.ViewModel.UploadPhotoViewModel;
import com.mark8.ViewModel.UserImageViewModel;
import com.mark8.adapter.ProductAdapter;
import com.mark8.databinding.FragmentHomeBinding;
import com.mark8.model.Product;
import com.mark8.receiver.NetworkChangeReceiver;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.OnNetworkListener;
import com.mark8.utils.Slide;
import com.mark8.view.AllLaptopsActivity;
import com.mark8.view.AllMobilesActivity;
import com.mark8.view.DetailsActivity;
import com.mark8.view.SearchActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mark8.utils.Constant.LOCALHOST;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.InternetUtils.isNetworkConnected;
import static com.mark8.view.AccountActivity.historyIsDeleted;

public class HomeFragment extends Fragment implements View.OnClickListener, OnNetworkListener, ProductAdapter.ProductAdapterOnClickHandler,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private ProductAdapter mobileAdapter;
    private ProductAdapter laptopAdapter;
    private ProductAdapter historyAdapter;

    private ProductViewModel productViewModel;
    private HistoryViewModel historyViewModel;
    private UploadPhotoViewModel uploadPhotoViewModel;
    private UserImageViewModel userImageViewModel;

    private Snackbar snack;

    private CircleImageView circleImageView;
    private Uri selectedImage;

    private NetworkChangeReceiver mNetworkReceiver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        int userID = LoginUtils.getInstance(getActivity()).getUserInfo().getId();

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        productViewModel.loadMobiles("mobile", userID);
        productViewModel.loadLaptops("mobile", userID);
        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        historyViewModel.loadHistory(userID);
        uploadPhotoViewModel = ViewModelProviders.of(this).get(UploadPhotoViewModel.class);
        userImageViewModel = ViewModelProviders.of(this).get(UserImageViewModel.class);

        snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);

        binding.included.content.txtSeeAllMobiles.setOnClickListener(this);
        binding.included.content.txtSeeAllLaptops.setOnClickListener(this);
        binding.included.content.txtCash.setOnClickListener(this);
        binding.included.content.txtReturn.setOnClickListener(this);
        binding.included.txtSearch.setOnClickListener(this);

        setUpViews();

        getMobiles();
        getLaptops();
        getHistory();
        getUserImage();

        flipImages(Slide.getSlides());

        mNetworkReceiver = new NetworkChangeReceiver();
        mNetworkReceiver.setOnNetworkListener(this);

        return view;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSeeAllMobiles:
                Intent mobileIntent = new Intent(getActivity(), AllMobilesActivity.class);
                startActivity(mobileIntent);
                break;
            case R.id.txtSeeAllLaptops:
                Intent laptopIntent = new Intent(getActivity(), AllLaptopsActivity.class);
                startActivity(laptopIntent);
                break;
            case R.id.profile_image:
                //  showCustomAlertDialog();
                break;
            case R.id.txtCash:
                //   showNormalAlertDialog(getString(R.string.cash));
                break;
            case R.id.txtReturn:
                // showNormalAlertDialog(getString(R.string.returnProduct));
                break;
            case R.id.txtSearch:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        // Pass an object of product class
        intent.putExtra(PRODUCT, (product));
        startActivity(intent);
    }

    @Override
    public void onNetworkConnected() {
        hideSnackBar();
        showOrHideViews(View.VISIBLE);
        getMobiles();
        getLaptops();
    }

    @Override
    public void onNetworkDisconnected() {

    }

    private void setUpViews() {


        binding.included.content.listOfMobiles.setHasFixedSize(true);
        binding.included.content.listOfMobiles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.included.content.listOfMobiles.setItemAnimator(null);

        binding.included.content.listOfLaptops.setHasFixedSize(true);
        binding.included.content.listOfLaptops.setItemAnimator(null);

        binding.included.content.historyList.setHasFixedSize(true);
        binding.included.content.historyList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.included.content.historyList.setItemAnimator(null);

        mobileAdapter = new ProductAdapter(getActivity(), this);
        laptopAdapter = new ProductAdapter(getActivity(), this);
        historyAdapter = new ProductAdapter(getActivity(), this);

        if (historyIsDeleted) {
            binding.included.content.textViewHistory.setVisibility(View.GONE);
        }
    }

    private void getMobiles() {
        if (isNetworkConnected(getActivity())) {
            productViewModel.productPagedList.observe(this, new Observer<PagedList<Product>>() {
                @Override
                public void onChanged(@Nullable PagedList<Product> products) {
                    mobileAdapter.submitList(products);
                }
            });

            binding.included.content.listOfMobiles.setAdapter(mobileAdapter);
            mobileAdapter.notifyDataSetChanged();
        } else {
            showOrHideViews(View.INVISIBLE);
            showSnackBar();
        }
    }

    private void getLaptops() {
        if (isNetworkConnected(getActivity())) {
            productViewModel.laptopPagedList.observe(this, new Observer<PagedList<Product>>() {
                @Override
                public void onChanged(@Nullable PagedList<Product> products) {
                    laptopAdapter.submitList(products);
                }
            });

            binding.included.content.listOfLaptops.setAdapter(laptopAdapter);
            laptopAdapter.notifyDataSetChanged();
        } else {
            showOrHideViews(View.INVISIBLE);
            showSnackBar();
        }
    }

    private void getHistory() {
        if (isNetworkConnected(getActivity())) {
            historyViewModel.historyPagedList.observe(this, new Observer<PagedList<Product>>() {
                @Override
                public void onChanged(@Nullable PagedList<Product> products) {
                    binding.included.content.historyList.setAdapter(historyAdapter);
                    historyAdapter.submitList(products);
                    historyAdapter.notifyDataSetChanged();

                    products.addWeakCallback(null, productCallback);
                }
            });
        } else {
            showOrHideViews(View.VISIBLE);
            binding.included.content.textViewHistory.setVisibility(View.GONE);
            showSnackBar();
        }

    }

    private void flipImages(ArrayList<Integer> images) {
        for (int image : images) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(image);
            binding.included.content.imageSlider.addView(imageView);
        }

        binding.included.content.imageSlider.setFlipInterval(2000);
        binding.included.content.imageSlider.setAutoStart(true);

        // Set the animation for the view that enters the screen
        binding.included.content.imageSlider.setInAnimation(getActivity(), R.anim.slide_in_right);
        // Set the animation for the view leaving th screen
        binding.included.content.imageSlider.setOutAnimation(getActivity(), R.anim.slide_out_left);
    }

    private void getUserImage() {
        userImageViewModel.getUserImage(LoginUtils.getInstance(getActivity()).getUserInfo().getId()).observe(this, response -> {
            if (response != null) {
                String imageUrl = LOCALHOST + response.getImage().replaceAll("\\\\", "/");

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.profile_picture)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)
                        .dontAnimate()
                        .dontTransform();

                Glide.with(getContext())
                        .load(imageUrl)
                        .apply(options)
                        .into(circleImageView);
            }
        });
    }

    public void showSnackBar() {
        snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
        snack.show();
    }

    public void hideSnackBar() {
        snack.dismiss();
    }

    private void showOrHideViews(int view) {
        binding.included.content.textViewMobiles.setVisibility(view);
        binding.included.content.txtSeeAllMobiles.setVisibility(view);
        binding.included.content.textViewLaptops.setVisibility(view);
        binding.included.content.txtSeeAllLaptops.setVisibility(view);
        binding.included.content.txtCash.setVisibility(view);
        binding.included.content.txtReturn.setVisibility(view);
    }

    private PagedList.Callback productCallback = new PagedList.Callback() {
        @Override
        public void onChanged(int position, int count) {
            Log.d(TAG, "onChanged: " + count);
        }

        @Override
        public void onInserted(int position, int count) {
            Log.d(TAG, "onInserted: " + count);
            if (count != 0) {
                binding.included.content.textViewHistory.setVisibility(View.VISIBLE);
                historyAdapter.notifyOnInsertedItem(position);
                getHistory();
            }
        }

        @Override
        public void onRemoved(int position, int count) {
            Log.d(TAG, "onRemoved: " + count);
        }
    };
}