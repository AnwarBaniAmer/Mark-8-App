package com.mark8.view;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mark8.R;
import com.mark8.ViewModel.PasswordViewModel;
import com.mark8.databinding.ActivityPasswordBinding;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.Validation;

import java.io.IOException;

import static com.mark8.storage.LanguageUtils.loadLocale;
import static com.mark8.view.AuthenticationActivity.isActivityRunning;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PasswordActivity";
    private ActivityPasswordBinding binding;
    private PasswordViewModel passwordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password);


        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24); // your drawable
        binding.toolbar.setTitle(getResources().getString(R.string.change_password));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        passwordViewModel = ViewModelProviders.of(this).get(PasswordViewModel.class);

        binding.saveChanges.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        if(isActivityRunning){
            binding.currentPassword.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveChanges:
                updatePassword();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void updatePassword() {
        int userId = LoginUtils.getInstance(this).getUserInfo().getId();
        String oldPassword = LoginUtils.getInstance(this).getUserInfo().getPassword();
        String currentPassword = binding.currentPassword.getText().toString();
        String newPassword = binding.newPassword.getText().toString();
        String retypePassword =binding.retypePassword.getText().toString();

        if(!currentPassword.equals(oldPassword)){
            binding.currentPassword.setError(getString(R.string.enter_current_password));
            binding.currentPassword.requestFocus();
            return;
        }

        if (!Validation.isValidPassword(newPassword)) {
            binding.newPassword.setError(getString(R.string.password__at_least_8_characters));
            binding.newPassword.requestFocus();
            return;
        }

        if (!Validation.isValidPassword(newPassword) || !(retypePassword.equals(newPassword))) {
            binding.retypePassword.setError(getString(R.string.password_not_match));
            binding.retypePassword.requestFocus();
            return;
        }

        passwordViewModel.updatePassword(newPassword, userId).observe(this, responseBody -> {
            try {
                Toast.makeText(PasswordActivity.this, responseBody.string(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
