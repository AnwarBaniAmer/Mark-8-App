package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.model.NewsFeedResponse;
import com.mark8.repository.NewsFeedRepository;

public class NewsFeedViewModel extends AndroidViewModel {

    private NewsFeedRepository newsFeedRepository;

    public NewsFeedViewModel(@NonNull Application application) {
        super(application);
        newsFeedRepository = new NewsFeedRepository(application);
    }

    public LiveData<NewsFeedResponse> getPosters() {
        return newsFeedRepository.getPosters();
    }
}
