package com.taskproject.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class MainViewModel extends ViewModel {
    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    public void bind(MainActivity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(User.KEY)) {
            userMutableLiveData.setValue(null);
            return;
        }
        String unic = sharedPreferences.getString(User.KEY, "0");
        if(unic.equals("0")) {
            userMutableLiveData.setValue(null);
            return;
        }
        App.DB.collection("users").document(unic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        System.out.println("onComplete");
                        User user = null;
                        if (task.isSuccessful() && task.getResult() != null) {
                            Map<String, Object> map = task.getResult().getData();
                            if(map != null) {
                                user = User.Create(map, task.getResult().getId());
                            }
                        } else {
                            Log.w(App.TAG, "Error getting documents.", task.getException());
                        }
                        userMutableLiveData.setValue(user);
                    }
                });
    }

    public void setUser(FragmentActivity activity, User user) {
        userMutableLiveData.setValue(user);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.KEY, user.unic);
        editor.apply();
    }
}
