package com.example.MyAppChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.MyAppChat.model.UserModel;
import com.example.MyAppChat.utils.AndroidUtil;
import com.example.MyAppChat.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::handleIntent, 3000);
    }

    private void handleIntent() {
        if (getIntent().getExtras() != null) {
            // from notification
            String userId = getIntent().getExtras().getString("userId");

            if (userId != null) {
                FirebaseUtil.allUserCollectionReference().document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                UserModel model = task.getResult().toObject(UserModel.class);
                                startChatActivity(model);
                            } else {
                                // Handle error or log a message
                                startLoginActivity();
                            }
                        });
            } else {
                // Handle the case where userId is null
                startLoginActivity();
            }
        } else {
            // No extras in the intent
            startLoginActivity();
        }
    }

    private void startChatActivity(UserModel userModel) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mainIntent);

        Intent intent = new Intent(this, ChatActivity.class);
        AndroidUtil.passUserModelAsIntent(intent, userModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        if (FirebaseUtil.isLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
        }
        finish();
    }
}