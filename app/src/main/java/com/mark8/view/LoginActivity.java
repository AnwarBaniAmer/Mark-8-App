package com.mark8.view;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.mark8.R;
import com.mark8.ViewModel.LoginViewModel;
import com.mark8.databinding.ActivityLoginBinding;
import com.mark8.storage.LoginUtils;
import com.mark8.utils.Validation;
import com.mark8.view.ui.map.MapsActivity;

import static com.mark8.storage.LanguageUtils.loadLocale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.buttonLogin.setOnClickListener(this);
        binding.textViewSignUp.setOnClickListener(this);
        binding.forgetPassword.setOnClickListener(this);

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24); // your drawable
        binding.toolbar.setTitle("");
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If user logged in, go directly to ProductActivity
        if (LoginUtils.getInstance(this).isLoggedIn()) {
            goToProductActivity();
        }
    }

    private void logInUser() {
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();

        if (email.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.email_required));
            binding.inputEmail.requestFocus();
        }

        if (Validation.isValidEmail(email)) {
            binding.inputEmail.setError(getString(R.string.enter_a_valid_email_address));
            binding.inputEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.password_required));
            binding.inputPassword.requestFocus();
            return;
        }

        if (!Validation.isValidPassword(password)) {
            binding.inputPassword.setError(getString(R.string.password__at_least_8_characters));
            binding.inputPassword.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);



//        loginViewModel.getLoginResponseLiveData(email,password).observe(this, loginApiResponse -> {
//            if (!loginApiResponse.isError()) {
//                LoginUtils.getInstance(this).saveUserInfo(loginApiResponse);
//                Toast.makeText(this, loginApiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                goToProductActivity();
//            }else {
//                progressDialog.cancel();
//                Toast.makeText(this, loginApiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        progressDialog.show();
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                logInUser();
                break;
            case R.id.textViewSignUp:
                goToSignUpActivity();
                break;
            case R.id.forgetPassword:
                goToPasswordAssistantActivity();
                break;
        }
    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void goToProductActivity() {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToPasswordAssistantActivity() {
        Intent intent = new Intent(this, PasswordAssistantActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressed();
    }
}
