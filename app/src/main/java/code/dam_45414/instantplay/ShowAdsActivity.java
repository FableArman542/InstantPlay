package code.dam_45414.instantplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import code.dam_45414.instantplay.Adapter.AdAdapter;
import code.dam_45414.instantplay.Model.AdItem;

public class ShowAdsActivity extends AppCompatActivity {

    private static final String TAG = "ShowAdsActivity";
    int LOCATION_REQUEST_CODE = 1003;

    Button createAdButton;
    TextView cat;

    private RecyclerView recyclerView;
    private AdAdapter adapter;
    private List<AdItem> mItems;

    private FirebaseUser firebaseUser;

    private String category;
    private String cityAdd;

    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ads);
        Intent intent = getIntent();

        category = intent.getStringExtra("cat");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cat = findViewById(R.id.catTxt);
        cat.setText(category);

        createAdButton = findViewById(R.id.createButton);
        recyclerView = findViewById(R.id.adRecycler);

        createAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowAdsActivity.this, AdActivity.class);
                intent.putExtra("cat", category);
                startActivity(intent);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mItems = new ArrayList<>();
        adapter = new AdAdapter(getApplicationContext(), mItems, category);
        recyclerView.setAdapter(adapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        back = findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void readAds () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Announces").child(category);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mItems.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    AdItem i = snapshot.getValue(AdItem.class);
                    if (cityAdd != null) {
                        if (/*!i.getId().equals(firebaseUser.getUid()) && */i.getCity().equals(cityAdd)) { mItems.add(i); }
                    }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                        readAds();

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
