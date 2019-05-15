package com.angelo.karma.interfaces;

public interface OnLatestUpdateCheckListener {
    void onSuccess(String latestUpdate);
    void onFailure();
}
