package com.taskproject.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taskproject.app.R;
import com.taskproject.app.User;
import com.taskproject.app.ui.users.UsersFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<HolderUser> {
    private List<User> userList = new ArrayList<>();
    private UsersFragment fragment;

    public AdapterUser(UsersFragment fragment) {

        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new HolderUser(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false), fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderUser holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void update(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }
}
