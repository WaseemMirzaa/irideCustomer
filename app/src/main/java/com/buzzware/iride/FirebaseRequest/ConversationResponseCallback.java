package com.buzzware.iride.FirebaseRequest;


import com.buzzware.iride.models.ConversationModel;

import java.util.List;

public interface ConversationResponseCallback {
    void onResponse(List<ConversationModel> list, boolean isError, String message);
}
