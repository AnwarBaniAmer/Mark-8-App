package com.mark8.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryApiResponse {

    @SerializedName("history")
    private List<Product> historyList;

    public List<Product> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<Product> historyList) {
        this.historyList = historyList;
    }
}
