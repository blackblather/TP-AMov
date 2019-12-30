package com.tp_amov;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InGamePlayerInfoFragment extends Fragment {
    private class Player{
        private String username, imgPath;
        Player(String username, String imgPath){
            this.username = username;
            this.imgPath = imgPath;
        }

        String getUsername() {
            return username;
        }

        public String getImgPath() {
            return imgPath;
        }
    }
    private ArrayList<Player> players = new ArrayList<>();

    public InGamePlayerInfoFragment() {
        // Required empty public constructor
    }

    private void FillPlayersArray(ArrayList<String> usernames, ArrayList<String> imgPaths){
        if(usernames != null && imgPaths != null) {
            if (usernames.size() == imgPaths.size()) {
                for (int i = 0; i < usernames.size(); i++)
                    players.add(new Player(usernames.get(i), imgPaths.get(i)));
            } else
                throw new IllegalArgumentException("Arguments must be of the same length");
        } else
            throw new NullPointerException("Arguments cannot be null");
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

    public void onSetDataForInGamePlayerInfo(ArrayList<String> usernames, ArrayList<String> imgPaths){
        Bundle args = new Bundle();
        args.putStringArrayList(SelectUserActivity.EXTRA_USERNAMES, usernames);
        args.putStringArrayList(SelectUserActivity.EXTRA_IMG_PATHS, imgPaths);
        FillPlayersArray(usernames,imgPaths);
        TextView InGameCurrentUser = (TextView) this.getView().findViewById(R.id.in_game_current_user_txt);
        ImageView InGameCurrentPic = (ImageView) this.getView().findViewById(R.id.profile_image);
        InGameCurrentPic.setImageBitmap(StringToBitMap(imgPaths.get(0)));
        //InGameCurrentPic.setImageResource(R.drawable.ic_default_user_icon);
        InGameCurrentUser.setText(players.get(0).getUsername());
        this.setArguments(args);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
