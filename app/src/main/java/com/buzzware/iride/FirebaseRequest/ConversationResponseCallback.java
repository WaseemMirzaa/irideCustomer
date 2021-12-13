package com.buzzware.iride.FirebaseRequest;


import com.buzzware.iride.models.ConversationModel;
import com.buzzware.iride.models.LastMessageModel;

import java.util.List;

public interface ConversationResponseCallback {
    void onResponse(List<LastMessageModel> list, boolean isError, String message);
}
