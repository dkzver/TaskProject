package com.taskproject.app;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class App extends Application {
    public static final String TAG = "PROJECT_TASK";
    public static FirebaseFirestore DB;

    @Override
    public void onCreate() {
        super.onCreate();
        DB = FirebaseFirestore.getInstance();
    }

    public static Bitmap GetBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public interface TokenListener {
        void onTken(String token);
    }

    public static void GetToken(TokenListener listener) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String token = "";
                        if (task.isSuccessful()) {
                            token = task.getResult();
                        }
                        listener.onTken(token);
                    }
                });
    }
}
