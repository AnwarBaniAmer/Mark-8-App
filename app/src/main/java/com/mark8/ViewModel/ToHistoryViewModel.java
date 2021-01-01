package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.model.History;
import com.mark8.repository.ToHistoryRepository;

import okhttp3.ResponseBody;

public class ToHistoryViewModel extends AndroidViewModel {

    private ToHistoryRepository toHistoryRepository;

    public ToHistoryViewModel(@NonNull Application application) {
        super(application);
        toHistoryRepository = new ToHistoryRepository(application);
    }

    public LiveData<ResponseBody> addToHistory(History history) {
        return toHistoryRepository.addToHistory(history);
    }
}
