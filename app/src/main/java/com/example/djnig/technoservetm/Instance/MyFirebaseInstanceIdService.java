package com.example.djnig.technoservetm.Instance;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIdService(){
        super();
    }

    @Override
    public void onTokenRefresh() {
        String refresTocken=FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refresTocken);
     //   super.onTokenRefresh();
    }
    public void sendRegistrationToServer(String tocken){


    }
}
