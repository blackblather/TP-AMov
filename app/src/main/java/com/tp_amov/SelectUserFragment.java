package com.tp_amov;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tp_amov.controllers.sql.UserController;

abstract class SelectUserFragment extends Fragment {
    String game_mode;
    private boolean useWebservice = false;
    private UserController userController;

    SelectUserFragment(){ }

    void SetUseWebservice(boolean useWebservice) {
        this.useWebservice = useWebservice;
    }

    boolean GetUseWebservice(){
        return useWebservice;
    }

    public UserController getUserController() {
        return userController;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userController = new UserController(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(userController != null)
            userController.Close();
    }
}
