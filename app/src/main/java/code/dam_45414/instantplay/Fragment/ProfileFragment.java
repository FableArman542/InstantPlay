package code.dam_45414.instantplay.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import code.dam_45414.instantplay.Adapter.ProfilePostsAdapter;
import code.dam_45414.instantplay.ChangeActivity;
import code.dam_45414.instantplay.Model.Post;
import code.dam_45414.instantplay.Model.User;
import code.dam_45414.instantplay.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    int LOCATION_REQUEST_CODE = 1001;

    private TextView username;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;

    private String cityAdd;

    private TextView city;
    private TextView bio;
    private CircleImageView image;

    private ViewGroup _container;
    private FirebaseUser firebaseUser;

    StorageReference storageReference;

    private ImageView changeprofile;
    private ImageButton settings;

    private RecyclerView recyclerView;
    private List<Post> mUsers;
    private ProfilePostsAdapter adapter;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.view = view;
        _container = container;

        settings = view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                SettingsFragment fragment = new SettingsFragment();
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Upload new picture
        storageReference = FirebaseStorage.getInstance().getReference("users");

        changeprofile = view.findViewById(R.id.changeprofile);
        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeActivity.class);
                startActivity(intent);
            }
        });

        bio = view.findViewById(R.id.bioTxt);
        image = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.usernameProfile);

        getBio ();

        // Show posts
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        mUsers = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getContext(), mUsers);

        recyclerView.setAdapter(adapter);

        readPosts();

        return view;
    }

    private void readPosts () {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(firebaseUser.getUid())) mUsers.add(post);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getBio () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(firebaseUser.getUid())) {

                        bio.setText(user.getBio());
                        username.setText(user.getUsername());
                        Glide.with(getContext()).load(user.getImageurl()).into(image);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else  {
            askLocationPermission ();
        }

    }

    private void getLastLocation () {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        Log.d(TAG, "onSuccess: " + location.getLatitude());
                        Log.d(TAG, "onSuccess: " + location.getLongitude());
                        Log.d(TAG, "onSuccess: " + addresses.get(0).getAddressLine(0));

                        cityAdd = addresses.get(0).getAdminArea();
                        city = view.findViewById(R.id.cityNameTxt);
                        city.setText(cityAdd);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });
    }

    private void askLocationPermission () {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted
                getLastLocation();
            } else {
                // Not Granted
            }
        }
    }

}
