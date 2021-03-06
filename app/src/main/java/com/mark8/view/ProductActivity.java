package com.mark8.view;

import android.Manifest;
import android.app.Dialog;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.mark8.databinding.ActivityProductBinding;
import com.mark8.model.Product;
import com.mark8.receiver.NetworkChangeReceiver;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.OnNetworkListener;
import com.mark8.utils.Slide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mark8.storage.LanguageUtils.getEnglishState;
import static com.mark8.storage.LanguageUtils.loadLocale;
import static com.mark8.storage.LanguageUtils.setEnglishState;
import static com.mark8.storage.LanguageUtils.setLocale;
import static com.mark8.utils.Constant.CAMERA_PERMISSION_CODE;
import static com.mark8.utils.Constant.CAMERA_REQUEST;
import static com.mark8.utils.Constant.CATEGORY;
import static com.mark8.utils.Constant.GALLERY_REQUEST;
import static com.mark8.utils.Constant.LOCALHOST;
import static com.mark8.utils.Constant.PRODUCT;
import static com.mark8.utils.Constant.READ_EXTERNAL_STORAGE_CODE;
import static com.mark8.utils.ImageUtils.getImageUri;
import static com.mark8.utils.ImageUtils.getRealPathFromURI;
import static com.mark8.utils.InternetUtils.isNetworkConnected;
import static com.mark8.view.AccountActivity.historyIsDeleted;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener, OnNetworkListener, ProductAdapter.ProductAdapterOnClickHandler,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ProductActivity";
    private ActivityProductBinding binding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        int userID = LoginUtils.getInstance(this).getUserInfo().getId();

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        productViewModel.loadMobiles("mobile", userID);
        productViewModel.loadLaptops("laptop", userID);
        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        historyViewModel.loadHistory(userID);
        uploadPhotoViewModel = ViewModelProviders.of(this).get(UploadPhotoViewModel.class);
        userImageViewModel = ViewModelProviders.of(this).get(UserImageViewModel.class);

        snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);

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

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orders, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void setUpViews() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        //   ActionBar actionbar = getSupportActionBar();
        DrawerLayout drawer = binding.drawerLayout;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setTitle(getResources().getString(R.string.app_name));


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
        toggle.setHomeAsUpIndicator(R.drawable.menu); //set your own
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });
        binding.navView.setNavigationItemSelectedListener(this);

        View headerContainer = binding.navView.getHeaderView(0);
        circleImageView = headerContainer.findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(this);
        TextView userName = headerContainer.findViewById(R.id.nameOfUser);
        userName.setText(LoginUtils.getInstance(this).getUserInfo().getName());
        TextView userEmail = headerContainer.findViewById(R.id.emailOfUser);
        userEmail.setText(LoginUtils.getInstance(this).getUserInfo().getEmail());

        binding.included.content.listOfMobiles.setHasFixedSize(true);
        binding.included.content.listOfMobiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.included.content.listOfMobiles.setItemAnimator(null);

        binding.included.content.listOfLaptops.setHasFixedSize(true);
        binding.included.content.listOfLaptops.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.included.content.listOfLaptops.setItemAnimator(null);

        binding.included.content.historyList.setHasFixedSize(true);
        binding.included.content.historyList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.included.content.historyList.setItemAnimator(null);

        mobileAdapter = new ProductAdapter(this, this);
        laptopAdapter = new ProductAdapter(this, this);
        historyAdapter = new ProductAdapter(this, this);

        if (historyIsDeleted) {
            binding.included.content.textViewHistory.setVisibility(View.GONE);
        }
    }

    private void getMobiles() {
        if (isNetworkConnected(this)) {
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
        if (isNetworkConnected(this)) {
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
        if (isNetworkConnected(this)) {
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
            showOrHideViews(View.INVISIBLE);
            binding.included.content.textViewHistory.setVisibility(View.GONE);
            showSnackBar();
        }

    }

    private void flipImages(ArrayList<Integer> images) {
        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(image);
            binding.included.content.imageSlider.addView(imageView);
        }

        binding.included.content.imageSlider.setFlipInterval(2000);
        binding.included.content.imageSlider.setAutoStart(true);

        // Set the animation for the view that enters the screen
        binding.included.content.imageSlider.setInAnimation(this, R.anim.slide_in_right);
        // Set the animation for the view leaving th screen
        binding.included.content.imageSlider.setOutAnimation(this, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSeeAllMobiles:
                Intent mobileIntent = new Intent(this, AllMobilesActivity.class);
                startActivity(mobileIntent);
                break;
            case R.id.txtSeeAllLaptops:
                Intent laptopIntent = new Intent(this, AllLaptopsActivity.class);
                startActivity(laptopIntent);
                break;
            case R.id.profile_image:
                showCustomAlertDialog();
                break;
            case R.id.txtCash:
                showNormalAlertDialog(getString(R.string.cash));
                break;
            case R.id.txtReturn:
                showNormalAlertDialog(getString(R.string.returnProduct));
                break;
            case R.id.txtSearch:
                Intent searchIntent = new Intent(ProductActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    private void showNormalAlertDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null).show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkGreen));
    }

    private void showCustomAlertDialog() {
        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_image_dialog);

        Button takePicture = dialog.findViewById(R.id.takePicture);
        Button useGallery = dialog.findViewById(R.id.useGallery);

        takePicture.setEnabled(true);
        useGallery.setEnabled(true);

        takePicture.setOnClickListener(v -> {
            launchCamera();
            dialog.cancel();
        });

        useGallery.setOnClickListener(v -> {
            getImageFromGallery();
            dialog.cancel();
        });

        dialog.show();
    }

    private void getImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ProductActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
            } else {
                try {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                    startActivityForResult(chooserIntent, GALLERY_REQUEST);
                } catch (Exception exp) {
                    Log.i("Error", exp.toString());
                }
            }
        }
    }

    private void launchCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            circleImageView.setImageURI(selectedImage);

            String filePath = getRealPathFromURI(this, selectedImage);
            Log.d(TAG, "onActivityResult: " + filePath);

            uploadPhoto(String.valueOf(filePath));
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            circleImageView.setImageBitmap(photo);

            Uri uriForImage = getImageUri(this, photo);
            String filePath = getRealPathFromURI(this, uriForImage);
            Log.d(TAG, "onActivityResult: Camera" + filePath);

            uploadPhoto(String.valueOf(filePath));

        }
    }

    private void uploadPhoto(String pathname) {
        uploadPhotoViewModel.uploadPhoto(pathname).observe(this, responseBody -> {
            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserImage() {
        userImageViewModel.getUserImage(LoginUtils.getInstance(this).getUserInfo().getId()).observe(this, response -> {
            if (response != null) {
                String imageUrl = LOCALHOST + response.getImage().replaceAll("\\\\", "/");

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.profile_picture)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)
                        .dontAnimate()
                        .dontTransform();

                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .apply(options)
                        .into(circleImageView);
            }
        });
    }

    public void showSnackBar() {
        snack.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        snack.show();
    }

    public void hideSnackBar() {
        snack.dismiss();
    }

    private void registerNetworkBroadcastForNougat() {
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

    @Override
    public void onNetworkConnected() {
        hideSnackBar();
        showOrHideViews(View.VISIBLE);
        getMobiles();
        getLaptops();
    }

    @Override
    public void onNetworkDisconnected() {
        showSnackBar();
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(ProductActivity.this, DetailsActivity.class);
        // Pass an object of product class
        intent.putExtra(PRODUCT, (product));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem addMenu = menu.findItem(R.id.action_addProduct);
        if (LoginUtils.getInstance(this).getUserInfo().isAdmin()) {
            addMenu.setVisible(true);
        } else {
            addMenu.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent cartIntent = new Intent(this, CartActivity.class);
                startActivity(cartIntent);
                return true;
            case R.id.action_addProduct:
                Intent addProductIntent = new Intent(this, AddProductActivity.class);
                startActivity(addProductIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrHideViews(int view) {
        binding.included.content.textViewMobiles.setVisibility(view);
        binding.included.content.txtSeeAllMobiles.setVisibility(view);
        binding.included.content.textViewLaptops.setVisibility(view);
        binding.included.content.txtSeeAllLaptops.setVisibility(view);
        binding.included.content.txtCash.setVisibility(view);
        binding.included.content.txtReturn.setVisibility(view);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, ProductActivity.class));
        } else if (id == R.id.nav_laptops) {
            goToCategoryActivity("Laptop");
        } else if (id == R.id.nav_babies) {
            goToCategoryActivity("Baby");
        } else if (id == R.id.nav_toys) {
            goToCategoryActivity("Toy");
        } else if (id == R.id.nav_trackOrder) {
            Intent orderIntent = new Intent(this, OrdersActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_myAccount) {
            Intent accountIntent = new Intent(this, AccountActivity.class);
            startActivity(accountIntent);
        } else if (id == R.id.nav_newsFeed) {
            Intent newsFeedIntent = new Intent(this, NewsFeedActivity.class);
            startActivity(newsFeedIntent);
        } else if (id == R.id.nav_wishList) {
            Intent wishListIntent = new Intent(this, WishListActivity.class);
            startActivity(wishListIntent);
        } else if (id == R.id.nav_language) {
            showLanguageCustomAlertDialog();
        }
        else if (id == R.id.nav_setting) {
            showLanguageCustomAlertDialog();
        }
        else if (id == R.id.nav_setting) {
            showLanguageCustomAlertDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToProductActivity() {
        startActivity(new Intent(this, ProductActivity.class));
    }

    private void showLanguageCustomAlertDialog() {
        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_language_dialog);

        Button english = dialog.findViewById(R.id.txtEnglish);
        Button arabic = dialog.findViewById(R.id.txtArabic);

        if (getEnglishState(this)) {
            english.setEnabled(false);
            english.setAlpha(.5f);
            arabic.setEnabled(true);
        } else {
            arabic.setEnabled(false);
            arabic.setAlpha(.5f);
            english.setEnabled(true);
        }

        english.setOnClickListener(v -> {
            english.setEnabled(true);
            chooseEnglish();
            dialog.cancel();
        });

        arabic.setOnClickListener(v -> {
            arabic.setEnabled(true);
            chooseArabic();
            dialog.cancel();
        });

        dialog.show();
    }

    private void chooseArabic() {
        setLocale(this,"ar");
        recreate();
        Toast.makeText(this, "Arabic", Toast.LENGTH_SHORT).show();
        setEnglishState(this, false);
    }

    private void chooseEnglish() {
        setLocale(this,"en");
        recreate();
        Toast.makeText(this, "English", Toast.LENGTH_SHORT).show();
        setEnglishState(this, true);
    }

    private void goToCategoryActivity(String category) {
        Intent categoryIntent = new Intent(this, CategoryActivity.class);
        categoryIntent.putExtra(CATEGORY, category);
        startActivity(categoryIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            closeApplication();
        }
    }

    private void closeApplication() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.want_to_exit)
                .setPositiveButton(R.string.ok, (dialog, which) -> finish())
                .setNegativeButton(R.string.cancel, null)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkGreen));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darkGreen));
    }


    @Override
    protected void onResume() {
        super.onResume();
        productViewModel.invalidate();
        getMobiles();
        getLaptops();
        historyViewModel.invalidate();
        getHistory();
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
