package pt.unl.fct.di.aldeia.apdc2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.AddEventResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.UpdateProfileDataFormState;

public class MapsFragment extends Fragment {

    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private LatLng curLoc = new LatLng(38.66027149660053,  -9.20960571616888);
    GoogleMap gmap;


    private MainLoggedInViewModel viewModel;
    private boolean creating;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapsFragment);
        final ImageButton createEvent = view.findViewById(R.id.createEventButton);
        final TextView newEvent = view.findViewById(R.id.eventText);
        final TextView eventChoose = view.findViewById(R.id.eventChoose);
        creating = false;

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creating = true;
                createEvent.setVisibility(View.INVISIBLE);
                newEvent.setVisibility(View.INVISIBLE);
                eventChoose.setVisibility(View.VISIBLE);
            }
        });
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (creating) {
                            eventChoose.setVisibility(View.INVISIBLE);
                            viewModel.setEventCoordinates(latLng);
                            viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Create Event"));
                            creating = false;
                        }
                    }
                });
                gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        viewModel.setEvent_id(marker.getTitle());
                        viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Event Info"));
                        return false;
                    }
                });
            }
        });
        viewModel.getAddEventResult().observe(this.getViewLifecycleOwner(), new Observer<AddEventResult>() {
            @Override
            public void onChanged(@Nullable AddEventResult addEventResult) {
                if(addEventResult == null) {
                    return;
                }
                if (addEventResult.getSuccess() != null) {
                    EventMarkerData event = addEventResult.getSuccess();
                    LatLng coordinates = new LatLng(event.getCoordinates()[0], event.getCoordinates()[1]);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(coordinates);
                    marker.title(event.getEvent_id());
                    gmap.addMarker(marker);
                }
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation () {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            curLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            BitmapDescriptor bitmap = BitmapFromVector(getActivity(), R.drawable.ic_your_location);
                            MarkerOptions options = new MarkerOptions().position(curLoc).title("You are here").icon(bitmap);
                            CameraPosition pos = CameraPosition.builder().target(curLoc).zoom(18).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}