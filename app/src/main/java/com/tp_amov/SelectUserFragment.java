package com.tp_amov;

import androidx.fragment.app.Fragment;
import com.tp_amov.controllers.sql.UserController;

abstract class SelectUserFragment extends Fragment {
    private boolean useWebservice = false;
    private UserController userController;

    SelectUserFragment(){
        userController = new UserController(getContext());
    }

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
    public void onDestroy() {
        super.onDestroy();
        userController.Close();
    }
}
