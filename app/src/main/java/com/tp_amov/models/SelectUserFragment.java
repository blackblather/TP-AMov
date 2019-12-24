package com.tp_amov.models;

import androidx.fragment.app.Fragment;

public abstract class SelectUserFragment extends Fragment {
    private boolean useWebservice = false;

    public void SetUseWebservice(boolean useWebservice) {
        this.useWebservice = useWebservice;
    }
    public boolean GetUseWebservice(){
        return useWebservice;
    }
}
