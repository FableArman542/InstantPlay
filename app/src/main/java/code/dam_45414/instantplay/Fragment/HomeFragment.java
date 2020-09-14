package code.dam_45414.instantplay.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import code.dam_45414.instantplay.Adapter.StoriesAdapter;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;
import code.dam_45414.instantplay.ShowAdsActivity;

public class HomeFragment extends Fragment {

    Button sports, hobbies, food, dating;

    ViewGroup _container;

    RecyclerView recyclerView;
    StoriesAdapter storiesAdapter;
    List<User> mUsers;

    private List<String> followingList;

    ImageButton inbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _container = container;

        inbox = view.findViewById(R.id.inboxbtn);

        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                MessageFragment fragment = new MessageFragment();
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        recyclerView = view.findViewById(R.id.horizontalScrollView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mUsers = new ArrayList<>();
        storiesAdapter = new StoriesAdapter(getContext(), mUsers);

        recyclerView.setAdapter(storiesAdapter);

        checkFollowing();
        //readUsers();

        return view;
    }

    private  void checkFollowing () {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                    //Log.d("FOLLOW", snapshot.getKey());
                }
                //Toast.makeText(getContext(),"followingList"+followingList.get(0), Toast.LENGTH_SHORT).show();
                //Log.d("FOLLOW", "2");
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(FirebaseAuth.getInstance().getUid())) { continue; }
                    for (String id : followingList) {
                        if (user.getId().equals(id)) {
                            mUsers.add(user);
                        }
                    }
                }
                storiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sports = _container.findViewById(R.id.sportsBtn);
        hobbies = _container.findViewById(R.id.hobbiesBtn);
        food = _container.findViewById(R.id.foodBtn);
        dating = _container.findViewById(R.id.datingBtn);

        sports.setOnClickListener(CategoryClicked(0));
        hobbies.setOnClickListener(CategoryClicked(1));
        food.setOnClickListener(CategoryClicked(2));
        dating.setOnClickListener(CategoryClicked(3));
    }

    public View.OnClickListener CategoryClicked (final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowAdsActivity.class);
                switch (i) {
                    case 0:
                        intent.putExtra("cat", "Sports");
                        break;
                    case 1:
                        intent.putExtra("cat", "Hobbies");
                        break;
                    case 2:
                        intent.putExtra("cat", "Food");
                        break;
                    case 3:
                        intent.putExtra("cat", "Dating");
                        break;
                }
                startActivity(intent);
            }
        };
    }
}
