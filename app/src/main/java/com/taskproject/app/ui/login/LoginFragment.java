package com.taskproject.app.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.taskproject.app.MainActivity;
import com.taskproject.app.R;

import org.jetbrains.annotations.NotNull;


public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private EditText edit_text_email;
    private EditText edit_text_password;
    private Button button_login;

    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.attemptLogin();
            }
        }
    };

    private View.OnClickListener sign_in_google_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.attemptSignInGoogle();
            }
        }
    };

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);
        edit_text_email = view.findViewById(R.id.edit_text_email);
        edit_text_password = view.findViewById(R.id.edit_text_password);
        button_login = view.findViewById(R.id.button_login);
        button_login.setOnClickListener(login_click_listener);
        ((Button) view.findViewById(R.id.button_sign_in_google)).setOnClickListener(sign_in_google_click_listener);
        loginViewModel.bind(this);
        loginViewModel.emailMutableLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                edit_text_email.setText(s);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
