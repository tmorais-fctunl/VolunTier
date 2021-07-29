package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.unl.fct.di.aldeia.apdc2021.R;

public class EditLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private LatLng curLoc = new LatLng(38.66027149660053,  -9.20960571616888);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        double[] coords = getIntent().getDoubleArrayExtra("coords");
        curLoc = new LatLng(coords[0], coords[1]);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapEditLocationFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        MarkerOptions options = new MarkerOptions().position(curLoc).title("Current Event Location");
        CameraPosition pos = CameraPosition.builder().target(curLoc).zoom(15).build();
        gmap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

        Button cancel = findViewById(R.id.editLocCancelButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("lat",latLng.latitude);
                returnIntent.putExtra("lon",latLng.longitude);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}