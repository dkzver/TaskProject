package com.taskproject.app.ui.users;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        adapterUser = new AdapterUser(this);
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(false);
        recycler_view.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recycler_view.setAdapter(adapterUser);
        viewModel.bind(this);
        viewModel.listMutableLiveData.observe(requireActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                adapterUser.update(userList);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showDialog(String participant_unic) {
        if(requireActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) requireActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Write");
            builder.setPositiveButton("Write", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewModel.write(participant_unic, activity);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
