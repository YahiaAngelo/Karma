package com.angelo.karma.interfaces;

import com.angelo.karma.classes.User;

public interface OnFetchUserListener {
    void onSuccess(User user);
    void onFailure();
}
