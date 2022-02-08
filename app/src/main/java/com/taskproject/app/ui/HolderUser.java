package com.taskproject.app.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taskproject.app.R;
import com.taskproject.app.User;
import com.taskproject.app.ui.users.UsersFragment;

import org.jetbrains.annotations.NotNull;

public class HolderUser extends RecyclerView.ViewHolder {

    private final TextView text_view_user_name;
    private final TextView text_view_user_email;

    public HolderUser(@NonNull @NotNull View itemView, UsersFragment fragment) {
        super(itemView);
        text_view_user_name = itemView.findViewById(R.id.text_view_user_name);
        text_view_user_email = itemView.findViewById(R.id.text_view_user_email);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showDialog((String) itemView.getTag());
            }
        });
    }

    public void bind(User user) {
        itemView.setTag(user.unic);
        text_view_user_name.setText(user.name);
        text_view_user_email.setText(user.email);
    }
}
