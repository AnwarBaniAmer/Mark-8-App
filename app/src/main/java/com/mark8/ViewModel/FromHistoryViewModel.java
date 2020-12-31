package com.mark8.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

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
