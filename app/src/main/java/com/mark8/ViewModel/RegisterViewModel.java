package com.mark8.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mark8.model.RegisterApiResponse;
import com.mark8.model.User;
import com.mark8.repository.RegisterRepository;

public class RegisterViewModel extends AndroidViewModel {

    private RegisterRepository registerRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        registerRepository = new RegisterRepository(application);
    }


    public LiveData<RegisterApiResponse> getRegisterResponseLiveData(User user) {
        return registerRepository.getRegisterResponseData(user);
    }
}
