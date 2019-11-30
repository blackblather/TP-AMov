package com.tp_amov;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InGamePlayerInfoFragment extends Fragment {
    private class Player{
        private String username, imgPath;
        Player(String username, String imgPath){
            this.username = username;
            this.imgPath = imgPath;
        }

        public String getUsername() {
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

    static InGamePlayerInfoFragment newInstance(ArrayList<String> usernames, ArrayList<String> imgPaths) {
        InGamePlayerInfoFragment fragment = new InGamePlayerInfoFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SelectUserActivity.EXTRA_USERNAMES, usernames);
        args.putStringArrayList(SelectUserActivity.EXTRA_IMG_PATHS, imgPaths);
        fragment.setArguments(args);
        return fragment;
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
        if (getArguments() != null)
            FillPlayersArray(getArguments().getStringArrayList(SelectUserActivity.EXTRA_USERNAMES),
                    getArguments().getStringArrayList(SelectUserActivity.EXTRA_IMG_PATHS));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView InGameCurrentUser = (TextView) view.findViewById(R.id.in_game_current_user_txt);
        InGameCurrentUser.setText(players.get(0).getUsername());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_game_player_info, container, false);
    }
}
