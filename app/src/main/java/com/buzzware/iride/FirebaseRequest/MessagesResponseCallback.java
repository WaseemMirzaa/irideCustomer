package com.buzzware.iride.FirebaseRequest;


import com.buzzware.iride.models.MessageModel;

import java.util.List;

public interface MessagesResponseCallback {
    void onResponse(List<MessageModel> list, boolean isError, String message);
}
