package com.taskproject.app.ui.users;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.taskproject.app.App;
import com.taskproject.app.MainActivity;
import com.taskproject.app.R;
import com.taskproject.app.User;
import com.taskproject.app.ui.AdapterUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UsersFragment extends Fragment {

    private UsersViewModel viewModel;
    private RecyclerView recycler_view;
    private AdapterUser adapterUser;

    public UsersFragment() {
        super(R.layout.fragment_users);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);
        adapterUser = new AdapterUser();
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(false);
        recycler_view.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recycler_view.setAdapter(adapterUser);
        Toast.makeText(requireActivity(), "Users", Toast.LENGTH_LONG).show();

        App.DB.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        Map<String, Object> map = null;
                        List<User> userList = new ArrayList<>();
                        User user = null;
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            map = documentSnapshot.getData();
                            user = new User(map);
                            userList.add(user);
                        }
                        adapterUser.update(userList);
                        Log.d(App.TAG, "DocumentSnapshot data: " + querySnapshot.getDocuments());
                    } else {
                        Log.d(App.TAG, "No such document");
                    }
                } else {
                    Log.d(App.TAG, "get failed with ", task.getException());
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
