package com.mark8.view;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mark8.R;
import com.mark8.ViewModel.OrderingViewModel;
import com.mark8.databinding.ActivityOrderProductBinding;
import com.mark8.model.Ordering;
import com.mark8.storage.LoginUtils;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.mark8.utils.Constant.ORDER;
import static com.mark8.utils.Constant.PRODUCTID;

public class OrderProductActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "OrderProductActivity";
    private ActivityOrderProductBinding binding;
    private OrderingViewModel orderingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_product);

        orderingViewModel = ViewModelProviders.of(this).get(OrderingViewModel.class);

        binding.addCard.setOnClickListener(this);

        populateSpinner();
    }

    private void orderProduct() {
        Log.e(TAG, "orderProduct: " );
        String nameOnCard = binding.nameOnCard.getText().toString().trim();
        String cardNumber = binding.cardNumber.getText().toString().trim();

        String year = binding.spinnerYear.getSelectedItem().toString().toLowerCase();
        String month = binding.spinnerMonth.getSelectedItem().toString().toLowerCase();
        String fullDate = year + "-" + month + "-00";

        int userId = LoginUtils.getInstance(this).getUserInfo().getId();
        Intent intent = getIntent();
        int productId = intent.getIntExtra(PRODUCTID, 0);

        Ordering ordering = new Ordering(nameOnCard,cardNumber,fullDate,userId,productId);

        orderingViewModel.orderProduct(ordering).observe(this, responseBody -> {
            try {
                Toast.makeText(OrderProductActivity.this, responseBody.string() + "", Toast.LENGTH_SHORT).show();
                finish();
                Intent homeIntent = new Intent(OrderProductActivity.this, ProductActivity.class);
                startActivity(homeIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//        Intent homeIntent = new Intent(OrderProductActivity.this, ProductActivity.class);
//        startActivity(homeIntent);
        Intent StatusActivityIntent = new Intent(OrderProductActivity.this, StatusActivity.class);
        // Pass an object of order class
     //  StatusActivityIntent.putExtra(ORDER, (order));
        startActivity(StatusActivityIntent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addCard) {
            orderProduct();
        }
    }

    private void populateSpinner() {
        String[] years = {"2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text, years );
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerYear.setAdapter(langAdapter);

        String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
        ArrayAdapter<CharSequence> langAdapter2 = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text, months );
        langAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerMonth.setAdapter(langAdapter2);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindowYear = (android.widget.ListPopupWindow) popup.get(binding.spinnerYear);
            android.widget.ListPopupWindow popupWindowMonth = (android.widget.ListPopupWindow) popup.get(binding.spinnerMonth);

            // Set popupWindow height to 500px
            popupWindowYear.setHeight(500);
            popupWindowMonth.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }
}
