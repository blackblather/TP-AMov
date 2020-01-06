package com.tp_amov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tp_amov.models.sql.User;
import com.tp_amov.tools.ProfilePictureTools;

import java.util.ArrayList;

public class InGamePlayerInfoFragment extends Fragment {
    private int turn = 0;
    private ArrayList<User> users ;
    private ArrayList<String> encodedImages ;

    public InGamePlayerInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        ((BoardActivity)getActivity()).inGamePlayerInfoFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_game_player_info, container, false);
    }

    void onSetDataForInGamePlayerInfo(ArrayList<User> users, ArrayList<String> encodedImages){
        this.users = users;
        this.encodedImages = encodedImages;
        ProfilePictureTools PPT = new ProfilePictureTools();

        TextView InGameCurrentUser = (TextView) this.getView().findViewById(R.id.in_game_current_user_txt);
        ImageView InGameCurrentPic = (ImageView) this.getView().findViewById(R.id.profile_image);

        InGameCurrentPic.setImageBitmap(PPT.StringToBitMap(encodedImages.get(0)));
        InGameCurrentUser.setText(users.get(0).getUsername());

    }

    public void UpdateNameplateData(int turn){
        if(turn != this.turn)
            this.turn = turn;
        //TOOLS
        ProfilePictureTools PPT = new ProfilePictureTools();
        //GETS COMPONENTS
        TextView InGameCurrentUser = (TextView) this.getView().findViewById(R.id.in_game_current_user_txt);
        ImageView InGameCurrentPic = (ImageView) this.getView().findViewById(R.id.profile_image);
        //SETS COMPONENT
        InGameCurrentPic.setImageBitmap(PPT.StringToBitMap(encodedImages.get(this.turn)));
        InGameCurrentUser.setText(users.get(this.turn).getUsername());
    }

    public void updateScoreOnView(int updatedScore){
        TextView score = (TextView) this.getView().findViewById(R.id.in_game_score_text);
        String updatedScoreStr = Integer.toString(updatedScore);
        score.setText(updatedScoreStr);
    }




}
