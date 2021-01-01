package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.repository.FromHistoryRepository;

import okhttp3.ResponseBody;

public class FromHistoryViewModel extends AndroidViewModel {

    private FromHistoryRepository fromHistoryRepository;

    public FromHistoryViewModel(@NonNull Application application) {
        super(application);
        fromHistoryRepository = new FromHistoryRepository(application);
    }

    public LiveData<ResponseBody> removeAllFromHistory() {
        return fromHistoryRepository.removeAllFromHistory();
    }
}
