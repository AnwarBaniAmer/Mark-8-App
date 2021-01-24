package com.mark8.view;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mark8.R;
import com.mark8.databinding.ActivityStatusBinding;
import com.mark8.model.Order;

import static com.mark8.utils.Constant.ORDER;
import static com.mark8.utils.Constant.PRODUCTID;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStatusBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_status);

        // Receive the order object
        Intent intent = getIntent();
     //   Order order = (Order) intent.getSerializableExtra(ORDER);
        Order order = new Order();
        order.setProductId(1);
        order.setOrderDateStatus("Date");
        order.setOrderNumber("5623");
        order.setProductName("LEIFARNE");
        order.setProductPrice(55.00);
        order.setShippingPhone("+962791488012");
        order.setUserName("Majd");
        order.setOrderDate("2021-1-23");
        order.setShippingAddress("Amman-Mekka street");

                productId = order.getProductId();
        binding.orderDate.setText(order.getOrderDate());
        binding.orderNumber.setText(order.getOrderNumber());
        binding.userName.setText(order.getUserName());
        binding.userAddress.setText(order.getShippingAddress());
        binding.userPhone.setText(order.getShippingPhone());
        binding.txtProductName.setText(order.getProductName());
        binding.txtProductPrice.setText(String.valueOf(order.getProductPrice()));
        String status = getString(R.string.item, order.getOrderDateStatus());
        binding.orderStatus.setText(status);

        binding.reOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reOrder) {
            Intent reOrderIntent = new Intent(this, OrderProductActivity.class);
            reOrderIntent.putExtra(PRODUCTID, productId);
            startActivity(reOrderIntent);
        }
    }
}
