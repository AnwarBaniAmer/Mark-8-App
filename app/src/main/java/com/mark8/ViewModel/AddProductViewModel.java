package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.repository.AddProductRepository;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AddProductViewModel extends AndroidViewModel {

    private AddProductRepository addProductRepository;

    public AddProductViewModel(@NonNull Application application) {
        super(application);
        addProductRepository = new AddProductRepository(application);
    }

    public LiveData<ResponseBody> addProduct(Map<String, RequestBody> productInfo, MultipartBody.Part image) {
        return addProductRepository.addProduct(productInfo,image);
    }
}
