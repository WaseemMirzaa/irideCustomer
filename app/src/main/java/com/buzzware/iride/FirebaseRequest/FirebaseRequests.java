package com.buzzware.iride.FirebaseRequest;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.buzzware.iride.models.ConversationModel;
import com.buzzware.iride.models.LastMessageModel;
import com.buzzware.iride.models.MessageModel;
import com.buzzware.iride.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRequests {
    public static FirebaseRequests firebaseRequests;
    Context context;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    public static FirebaseRequests GetFirebaseRequests(Context context)
    {
        if(firebaseRequests == null)
        {
            firebaseRequests= new FirebaseRequests(context);
        }
        return firebaseRequests;
    }

    public FirebaseRequests(Context context) {
        this.context = context;
        ///init firebase
        mAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
    }


    public void GetConversationList(ConversationResponseCallback callback, String uID, Context context)
    {

        final List<ConversationModel>[] list = new List[]{new ArrayList<>()};
        firebaseFirestore.collection("Chat").whereEqualTo("participants."+uID, true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    list[0].clear();
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        LastMessageModel lastMessageModel= documentSnapshot.get("lastMessage", LastMessageModel.class);
                        GetUserData(documentSnapshot.getId(), lastMessageModel, context, uID, list[0], callback);
                    }
                }else{

                    callback.onResponse(list[0], true, error.getMessage());
                }
            }
        });
    }

    private void GetUserData(String conversationID, LastMessageModel lastMessageModel, Context context, String myID, List<ConversationModel> list, ConversationResponseCallback callback) {
        String userID="";
        if(lastMessageModel.getFromID().equals(myID))
        {
            userID= lastMessageModel.getToID();
        }
        if(lastMessageModel.getToID().equals(myID)){
            userID= lastMessageModel.getFromID();
        }

        final DocumentReference documentReferenceUser = firebaseFirestore.collection("Users").document(userID);
        documentReferenceUser.addSnapshotListener(((Activity) context) , new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null)
                {
                    User userModel = documentSnapshot.toObject(User.class);
                    String id= documentSnapshot.getId();
                    userModel.id=id;
                    list.add(new ConversationModel(conversationID, userModel.id, userModel.firstName, userModel.image, lastMessageModel.getContent(),lastMessageModel.getToID()));
                    callback.onResponse(list, false, "Null");
                }
            }
        });
    }

    public void LoadMessages(MessagesResponseCallback callback, Context context, String conversationID) {
        List<MessageModel> messageModels= new ArrayList<>();
        firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    for(DocumentChange documentSnapshot : value.getDocumentChanges()){
                        MessageModel messageModel= documentSnapshot.getDocument().toObject(MessageModel.class);
                        messageModels.add(messageModel);
                    }

                    callback.onResponse(messageModels, false, "Null");
                }else{
                    callback.onResponse(messageModels, true, error.getMessage());
                }
            }
        });
    }



}
