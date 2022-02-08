package com.taskproject.app.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public MutableLiveData<String> emailMutableLiveData = new MutableLiveData<>();
    public void bind(LoginFragment loginFragment) {
        emailMutableLiveData.setValue("2_k@inbox.ru");
    }
}
