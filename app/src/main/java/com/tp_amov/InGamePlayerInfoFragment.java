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
    private ArrayList<User> users = new ArrayList<>();

    public InGamePlayerInfoFragment() {
        // Required empty public constructor
    }

/*    private void FillPlayersArray(ArrayList<String> usernames, ArrayList<String> imgPaths){
        if(usernames != null && imgPaths != null) {
            if (usernames.size() == imgPaths.size()) {
                for (int i = 0; i < usernames.size(); i++)
                    players.add(new Player(usernames.get(i), imgPaths.get(i)));
            } else
                throw new IllegalArgumentException("Arguments must be of the same length");
        } else
            throw new NullPointerException("Arguments cannot be null");
    }*/

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
        Bundle args = new Bundle();
        ProfilePictureTools PPT = new ProfilePictureTools();

        args.putSerializable(SelectUserActivity.EXTRA_USERS, users);
        args.putStringArrayList(SelectUserActivity.EXTRA_ENCODED_IMAGES, encodedImages);

        TextView InGameCurrentUser = (TextView) this.getView().findViewById(R.id.in_game_current_user_txt);
        ImageView InGameCurrentPic = (ImageView) this.getView().findViewById(R.id.profile_image);

        InGameCurrentPic.setImageBitmap(PPT.StringToBitMap(encodedImages.get(0)));
        InGameCurrentUser.setText(users.get(0).getUsername());

        this.setArguments(args);
    }
}
