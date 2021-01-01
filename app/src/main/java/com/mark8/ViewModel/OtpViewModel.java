package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.model.Otp;
import com.mark8.repository.OtpRepository;

public class OtpViewModel extends AndroidViewModel {

    private OtpRepository otpRepository;

    public OtpViewModel(@NonNull Application application) {
        super(application);
        otpRepository = new OtpRepository(application);
    }

    public LiveData<Otp> getOtpCode(String email) {
        return otpRepository.getOtpCode(email);
    }
}
