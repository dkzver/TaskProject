package com.taskproject.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taskproject.app.R;
import com.taskproject.app.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<HolderUser> {
    private List<User> userList = new ArrayList<>();
    @NonNull
    @NotNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new HolderUser(view);
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
