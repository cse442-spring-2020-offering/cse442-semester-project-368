package com.example.a368.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/fetch_schedule.php";

    private FriendsViewModel friendsViewModel;
    RecyclerView.LayoutManager layoutManager;
    FriendsListAdapter mAdapter;
    ArrayList<Friend> list = new ArrayList<Friend>();




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel =
                ViewModelProviders.of(this).get(FriendsViewModel.class);
        View root = inflater.inflate(R.layout.friends_layout, container, false);

        FloatingActionButton fabAddFriend = root.findViewById(R.id.add_friend);
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddFriendActivity.class));
            }
        });

        Friend f1 = new Friend("Mario Speedwagon", "mario@speedwagon.com");
        Friend f2 = new Friend("Petey Cruiser", "petey@cruiser.com");
        Friend f3 = new Friend("Anna Sthesia", "anna@sthesia.com");
        Friend f4 = new Friend("Paul Molive", "paul@molive.com");
        Friend f5 = new Friend("Anna Mull", "anna@mull.com");
        Friend f6 = new Friend("Gail Forcewind", "gail@forewind.com");
        Friend f7 = new Friend("Paige Turner", "paige@turner.com");
        Friend f8 = new Friend("Walter Melon", "walter@melon.com");

        list.add(f1);
        list.add(f2);
        list.add(f3);
        list.add(f4);
        list.add(f5);
        list.add(f6);
        list.add(f7);
        list.add(f8);

        RecyclerView recyclerView = root.findViewById(R.id.friendListRecycler);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FriendsListAdapter(list, this.getContext());
        recyclerView.setAdapter(mAdapter);


        return root;
    }
}