package com.mark8.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.mark8.model.Review;
import com.mark8.repository.WriteReviewRepository;

import okhttp3.ResponseBody;

public class WriteReviewViewModel extends AndroidViewModel {

    private WriteReviewRepository writeReviewRepository;

    public WriteReviewViewModel(@NonNull Application application) {
        super(application);
        writeReviewRepository = new WriteReviewRepository(application);
    }

    public LiveData<ResponseBody> writeReview(Review review) {
        return writeReviewRepository.writeReview(review);
    }
}
