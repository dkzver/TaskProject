package com.taskproject.app.ui.users;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.taskproject.app.App;
import com.taskproject.app.RoomParticipants;
import com.taskproject.app.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class UsersViewModel extends ViewModel {

    public MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> userUnicMutableLiveData = new MutableLiveData<>();

    private void getUsersWithoutUser(FragmentActivity activity) {
        System.out.println("getUsersWithoutUser");
        App.DB.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        buildUserList(querySnapshot);
                        Log.d(App.TAG, "DocumentSnapshot data: " + querySnapshot.getDocuments());
                    } else {
                        Log.d(App.TAG, "No such document");
                    }
                } else {
                    Log.d(App.TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getUsersWithUser(User user, FragmentActivity activity) {
        System.out.println("getUsersWithUser");
        final CollectionReference ref = App.DB.collection("users");
        final Query query = ref.whereNotEqualTo("unic", user.unic);
        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && !value.isEmpty()) {
                    List<DocumentSnapshot> documentReference = value.getDocuments();
                    buildUserList(value);
                }
            }
        });
    }
    public void bind(UsersFragment loginFragment) {

        SharedPreferences sharedPreferences = loginFragment.requireActivity().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(User.KEY)) {
            userUnicMutableLiveData.setValue(null);
            getUsersWithoutUser(loginFragment.requireActivity());
            return;
        }
        String unic = sharedPreferences.getString(User.KEY, "0");
        if(unic.equals("0")) {
            userUnicMutableLiveData.setValue(null);
            getUsersWithoutUser(loginFragment.requireActivity());
            return;
        }
        System.out.println("unic " + unic);
        App.DB.collection("users").document(unic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        System.out.println("onComplete");
                        User user = null;
                        if (task.isSuccessful() && task.getResult() != null) {
                            System.out.println("Task");
                            System.out.println(task);
                            Map<String, Object> map = task.getResult().getData();
                            if(map != null) {
                                user = User.Create(map, task.getResult().getId());
                            }
                        }
                        if(user != null) {
                            System.out.println("unic " + user.unic);
                            System.out.println("unic " + unic);
                            userUnicMutableLiveData.setValue(user.unic);
                            getUsersWithUser(user, loginFragment.requireActivity());
                        } else {
                            userUnicMutableLiveData.setValue(null);
                            getUsersWithoutUser(loginFragment.requireActivity());
                        }
                    }
                });
    }

    private void buildUserList(QuerySnapshot querySnapshot) {
        System.out.println("Build user list");
        List<User> userList = new ArrayList<>();
        System.out.println(querySnapshot);
        System.out.println(querySnapshot.getDocuments());
        System.out.println(querySnapshot.getDocuments().size());
        System.out.println("___________________");
        User user = null;
        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
            System.out.println(documentSnapshot);
            System.out.println(documentSnapshot.getData());
            user = new User(documentSnapshot.getData(), documentSnapshot.getId());
            userList.add(user);
            System.out.println("unic " + user.unic);
            System.out.println("___________________");
        }
        System.out.println("size " + userList.size());
        listMutableLiveData.setValue(userList);
    }

    public void write(String participant_unic, FragmentActivity activity) {
        String user_unic = userUnicMutableLiveData.getValue();
        System.out.println("write " + participant_unic);
        System.out.println("write " + user_unic);

        RoomParticipants r1 = new RoomParticipants();
        r1.unic = String.valueOf(Calendar.getInstance().getTimeInMillis());
        r1.user_unic = user_unic;
        r1.participant_unic = user_unic;
        r1.owner = "1";
        Task<DocumentReference> taskDocumentReference = App.DB.collection("room_participants").add(r1);
        Task<Void> taskSet = documentReference.set(user);
        taskSet.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(activity.getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.setUser(activity, user);
            }
        });

//        final CollectionReference ref = App.DB.collection("room_participants");
//        final Query query = ref.whereEqualTo("user_unic", unic).
//                whereEqualTo("social_id", account.getId());
//        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                String unic = "0";
//                if(value != null && !value.isEmpty()) {
//                    List<DocumentSnapshot> documentReference = value.getDocuments();
//                    if(documentReference.size() > 0) {
//                        DocumentSnapshot documentSnapshot = documentReference.get(0);
//                        if(documentSnapshot != null) {
//                            unic = documentSnapshot.getId();
//                        }
//                    }
//                }
//                if(unic.equals("0")) {
//                } else {
//                    user.unic = unic;
//                    viewModel.setUser(activity, user);
//                }
//            }
//        });
    }
}
