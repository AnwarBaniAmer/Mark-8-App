package com.mark8.view;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mark8.R;
import com.mark8.ViewModel.ShippingViewModel;
import com.mark8.databinding.ActivityShippingAddressBinding;
import com.mark8.model.Shipping;
import com.mark8.storage.LoginUtils;

import java.io.IOException;

import static com.mark8.utils.Constant.PRODUCTID;

public class ShippingAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ShippingAddressActivity";
    private ActivityShippingAddressBinding binding;

    private ShippingViewModel shippingViewModel;
    MapView mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shipping_address);

        shippingViewModel = ViewModelProviders.of(this).get(ShippingViewModel.class);
        mMap = binding.map;
        binding.proceed.setOnClickListener(this);
        binding.toolbar.setTitle(getResources().getString(R.string.enter_a_shipping_address));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_keyboard_backspace_24));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.txtName.setText(LoginUtils.getInstance(this).getUserInfo().getName());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.proceed) {
            addShippingAddress();
        }
    }

    private void addShippingAddress() {
        Log.e(TAG, "addShippingAddress: " );
        String address = binding.address.getText().toString().trim();
        String city = binding.city.getText().toString().trim();
        String country = binding.country.getText().toString().trim();
        String zip = binding.zip.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        int userId = LoginUtils.getInstance(this).getUserInfo().getId();
        Intent intent = getIntent();
        int productId = intent.getIntExtra(PRODUCTID, 0);

        Shipping shipping = new Shipping(address, city, country, zip, phone, userId, productId);

        shippingViewModel.addShippingAddress(shipping).observe(this, responseBody -> {
            try {
                Toast.makeText(ShippingAddressActivity.this, responseBody.string() + "", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent orderProductIntent = new Intent(ShippingAddressActivity.this, OrderProductActivity.class);
            orderProductIntent.putExtra(PRODUCTID, productId);
            startActivity(orderProductIntent);
        });
        Intent orderProductIntent = new Intent(ShippingAddressActivity.this, OrderProductActivity.class);
        orderProductIntent.putExtra(PRODUCTID, productId);
        startActivity(orderProductIntent);
    }

//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//            mMap.setMyLocationEnabled(true);
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                    @Override
//                    public void onMyLocationChange(Location arg0) {
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
//                    }
//                });
//            }
//        }
//    }
}
