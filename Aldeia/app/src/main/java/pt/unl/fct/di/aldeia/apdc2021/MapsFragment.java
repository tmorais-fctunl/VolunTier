package pt.unl.fct.di.aldeia.apdc2021;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventRouteCrossReference;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.RouteEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.RouteWithEvents;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReplyUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReplyUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.category.EventCategoryActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.AddEventResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.AddRouteResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.GeoHashUtil;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivityTransitionHandler;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.RoomViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.SearchEventsResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.SearchRoutesResult;

public class MapsFragment extends Fragment {

    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private LatLng curLoc = null;
    private GoogleMap gmap;
    private List<String> hashesFullyLoaded;
    private Map<String,Marker> markers;
    private List<String> hashesDisplayed;
    private Map<String,Polyline> routesPolylines;
    private MainLoggedInViewModel viewModel;
    private RoomViewModel roomViewModel;
    private boolean creatingEvent;
    private boolean creatingRoute;
    private boolean areEventsVisible;
    private boolean loadingEvents;
    private boolean loadingRoutes;
    private boolean eventMode;
    private MapsFragment mFragment;
    private GeoApiContext geoApiContext=null;
    private Map<String,RouteWithEvents> allRoutes;
    private double[] searchloc=null;
    private List<String> newRouteEventIds;
    private String lastHashConsulted=null;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainLoggedInViewModel.class);
        roomViewModel=new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapsFragment);
        final ImageButton createEvent = view.findViewById(R.id.createEventButton);
        final Button routeSubmit = view.findViewById(R.id.submit_route_button);
        final Button cancelRouteCreation = view.findViewById(R.id.cancel_route_button);
        final ImageView goToCategory = view.findViewById(R.id.maps_category_selecter);
        final TextView eventChoose = view.findViewById(R.id.eventChoose);
        final TextView routeChoose = view.findViewById(R.id.routeChoose);
        final Switch mode=view.findViewById(R.id.mapSwitch); //mode is unchecked ->events else routes
        mode.setChecked(false);
        mode.setClickable(false);
        viewModel.resetAddRoute();
        creatingEvent = false;
        creatingRoute=false;
        hashesFullyLoaded = roomViewModel.getHashesFullyLoaded();
        viewModel.resetEventSearch();
        viewModel.resetRoutesSearch();
        markers=new HashMap<String,Marker>();
        mFragment=this;
        loadingEvents =false;
        loadingRoutes=false;
        eventMode=true;
        routesPolylines=new HashMap<String, Polyline>();
        allRoutes=new HashMap<String, RouteWithEvents>();
        newRouteEventIds= new ArrayList<String>();
        hashesDisplayed =new ArrayList<String>();

        goToCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventCategoryActivity.class);
                startActivity(intent);
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventMode){
                    mode.setClickable(false);
                    eventChoose.setVisibility(View.VISIBLE);
                    creatingEvent = true;
                    cancelRouteCreation.setVisibility(View.VISIBLE);
                }else{
                    mode.setClickable(false);
                    creatingRoute=true;
                    hideRoutes();
                    showEvents();
                    routeChoose.setVisibility(View.VISIBLE);
                    routeSubmit.setVisibility(View.VISIBLE);
                    routeSubmit.setClickable(true);
                    cancelRouteCreation.setVisibility(View.VISIBLE);
                }

                createEvent.setVisibility(View.INVISIBLE);

            }
        });
        if(geoApiContext==null){
            geoApiContext= new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                gmap.setMinZoomPreference(7);
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
                mode.setClickable(true);
                gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (creatingEvent) {
                            eventChoose.setVisibility(View.INVISIBLE);
                            viewModel.setEventCoordinates(latLng);
                            viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Create Event"));
                            creatingEvent = false;
                            cancelRouteCreation.setVisibility(View.GONE);
                            mode.setClickable(true);
                        }
                    }
                });
                routeSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(creatingRoute){
                            Iterator<String> it = newRouteEventIds.iterator();
                            while(it.hasNext()){
                                markers.get(it.next()).setIcon(BitmapDescriptorFactory.defaultMarker());
                            }
                            if (newRouteEventIds.size()<2||newRouteEventIds.size()>9){
                                Toast.makeText(mapFragment.getContext(), "You must select 2 to 9 events to create a route", Toast.LENGTH_SHORT).show();
                                newRouteEventIds=new ArrayList<String>();
                            }else{
                                viewModel.setRouteCoordinates(newRouteEventIds);
                                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Create Route"));
                            }
                            cancelRouteCreation.setVisibility(View.GONE);
                            routeChoose.setVisibility(View.GONE);
                            routeSubmit.setVisibility(View.GONE);
                            creatingRoute=false;
                            createEvent.setVisibility(View.VISIBLE);
                        }
                    }
                });
                cancelRouteCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(creatingEvent){
                            creatingEvent=false;
                            eventChoose.setVisibility(View.GONE);
                        }
                        if(creatingRoute) {
                            Iterator<String> it = newRouteEventIds.iterator();
                            while (it.hasNext()) {
                                markers.get(it.next()).setIcon(BitmapDescriptorFactory.defaultMarker());
                            }
                            newRouteEventIds=new ArrayList<String>();
                            routeChoose.setVisibility(View.GONE);
                            routeSubmit.setClickable(false);
                            routeSubmit.setVisibility(View.GONE);
                            creatingRoute=false;
                            hideEvents();
                            showRoutes();
                        }
                        mode.setClickable(true);
                        cancelRouteCreation.setVisibility(View.GONE);
                        createEvent.setVisibility(View.VISIBLE);
                    }
                });
                gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(!marker.getTitle().equals("You are here")){
                            if(!creatingRoute){
                                viewModel.setEvent_id(marker.getTitle());
                                viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Event Info"));
                            }
                            else{
                                String id= marker.getTitle();
                                if(!newRouteEventIds.contains(id)){
                                    newRouteEventIds.add(id);
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                }else{
                                    newRouteEventIds.remove(id);
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                                }
                            }
                        }
                        return false;
                    }
                });
                gmap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        viewModel.setRoute_id((String) polyline.getTag());
                        viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler("Route Info"));
                    }
                });
                gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        CameraPosition cameraPosition = googleMap.getCameraPosition();
                        if(areEventsVisible && cameraPosition.zoom < 9.0) {
                            areEventsVisible=false;
                            hideEvents();
                            hideRoutes();
                            mode.setClickable(false);
                            createEvent.setVisibility(View.GONE);
                        } else if (!areEventsVisible && cameraPosition.zoom>=9.0) {
                            createEvent.setVisibility(View.VISIBLE);
                            areEventsVisible=true;
                            if(eventMode||creatingRoute){
                                hideRoutes();
                                showEvents();
                            }else{
                                hideEvents();
                                showRoutes();
                            }
                            mode.setClickable(true);
                        }
                        if(!lastHashConsulted.equals(GeoHashUtil.convertCoordsToGeoHashLowPrecision(cameraPosition.target.latitude,cameraPosition.target.longitude))&&(!hashesDisplayed.contains(GeoHashUtil.convertCoordsToGeoHashLowPrecision(cameraPosition.target.latitude,cameraPosition.target.longitude)))){
                            loadEvents(cameraPosition.target.latitude,cameraPosition.target.longitude);
                        }
                    }
                });
                roomViewModel.getEventQueryResult().observe(mapFragment.getViewLifecycleOwner(), new Observer<List<EventEntity>>() {
                    @Override
                    public void onChanged(List<EventEntity> eventEntities) {
                        if (eventEntities.isEmpty()){
                            return;
                        }else {
                            if(loadingEvents){
                                loadingEvents =false;
                                Iterator<EventEntity> it = eventEntities.iterator();
                                while (it.hasNext()){
                                    EventEntity current=it.next();
                                    if(markers.get(current.getEvent_id())==null){
                                        LatLng coordinates = new LatLng(current.getLatitude(), current.getLongitude());
                                        MarkerOptions marker = new MarkerOptions();
                                        marker.position(coordinates);
                                        marker.title(current.getEvent_id());
                                        if(mode.isChecked()){
                                            marker.visible(false);
                                        }
                                        markers.put(current.getEvent_id(),gmap.addMarker(marker));
                                    }
                                }
                                roomViewModel.loadRoutes();

                            }
                        }
                    }
                });
                roomViewModel.getRoutesWithEvents().observe(mapFragment.getViewLifecycleOwner(), new Observer<List<RouteWithEvents>>() {
                    @Override
                    public void onChanged(List<RouteWithEvents> routeWithEvents) {
                        Iterator<RouteWithEvents> it=routeWithEvents.iterator();
                        while (it.hasNext()){
                            RouteWithEvents current=it.next();
                            allRoutes.put(current.route.getRoute_id(),current);
                        }
                    }
                });

                roomViewModel.getRoutesQueryResult().observe(mapFragment.getViewLifecycleOwner(), new Observer<List<RouteEntity>>() {
                    @Override
                    public void onChanged(List<RouteEntity> routeEntities) {
                        if (routeEntities.isEmpty()){
                            return;
                        }else {
                            if(loadingRoutes){
                                String hashedLoc=routeEntities.get(0).getHashed_location();
                                loadingRoutes =false;
                                boolean somethingWentWrong=false;
                                Iterator<RouteEntity> it = routeEntities.iterator();
                                while (it.hasNext()){
                                    boolean cantAddThisRoute=false;
                                    RouteEntity current=it.next();
                                    RouteWithEvents routeWithEvents=allRoutes.get(current.getRoute_id());
                                    Iterator<EventEntity> itEvents=routeWithEvents.eventsOnRoute.iterator();
                                    List<Marker> passagePoints= new ArrayList<Marker>();
                                    while (itEvents.hasNext()){
                                        EventEntity currentEvent=itEvents.next();
                                        if(markers.get(currentEvent.getEvent_id())==null){
                                            cantAddThisRoute=true;
                                            somethingWentWrong=true;
                                        }
                                        passagePoints.add(markers.get(currentEvent.getEvent_id()));
                                    }
                                    if(passagePoints.size()<2){
                                        cantAddThisRoute=true;
                                        roomViewModel.deleteRoute(current.getRoute_id());
                                        roomViewModel.deleteCrossOverFromRouteID(current.getRoute_id());
                                    }
                                    if(!cantAddThisRoute){
                                        calculateDirections(passagePoints.remove(0),passagePoints.remove(passagePoints.size()-1),passagePoints, current.getRoute_id());
                                    }
                                }
                                if (!somethingWentWrong) {
                                    hashesDisplayed.add(hashedLoc);
                                }
                            }
                        }
                    }
                });

                viewModel.getAddEventResult().observe(mapFragment.getViewLifecycleOwner(), new Observer<AddEventResult>() {
                    @Override
                    public void onChanged(@Nullable AddEventResult addEventResult) {
                        if(addEventResult == null) {
                            return;
                        }
                        if (addEventResult.getSuccess() != null) {
                            EventEntity event = addEventResult.getSuccess();
                            LatLng coordinates = new LatLng(event.getLatitude(), event.getLongitude());
                            MarkerOptions marker = new MarkerOptions();
                            marker.position(coordinates);
                            marker.title(event.getEvent_id());
                            roomViewModel.insertEvent(event);
                            markers.put(event.getEvent_id(),gmap.addMarker(marker));
                        }
                    }
                });

                viewModel.getDeletedEvent().observe(mapFragment.getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(s==null){
                            return;
                        }
                        if(markers.containsKey(s)){
                            markers.get(s).setVisible(false);
                            markers.remove(s);
                        }
                    }
                });

                viewModel.getDeletedRoute().observe(mapFragment.getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(s==null){
                            return;
                        }
                        if(routesPolylines.containsKey(s)){
                            Iterator<EventEntity> it=allRoutes.get(s).eventsOnRoute.iterator();
                            while(it.hasNext()){
                                EventEntity current=it.next();
                                markers.get(current.getEvent_id()).setVisible(false);
                            }
                            allRoutes.remove(s);
                            routesPolylines.get(s).setVisible(false);
                            routesPolylines.remove(s);
                        }
                    }
                });

                viewModel.getUpdatedEvent().observe(mapFragment.getViewLifecycleOwner(), new Observer<EventMarkerData>() {
                    @Override
                    public void onChanged(EventMarkerData updatedEvent) {
                        if(updatedEvent==null){
                            return;
                        }
                        Iterator<Marker> it=markers.values().iterator();
                        while(it.hasNext()){
                            Marker aux=it.next();
                            if(aux.getTitle().equals(updatedEvent.getEvent_id())){
                                aux.setPosition(new LatLng(updatedEvent.getCoordinates()[0],updatedEvent.getCoordinates()[1]));
                                CameraPosition pos = CameraPosition.builder().target(curLoc).zoom(15).build();
                                gmap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                                return;
                            }
                        }
                    }
                });

                mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            eventMode=true;
                            showEvents();
                            hideRoutes();
                        }else{
                            eventMode=false;
                            hideEvents();
                            showRoutes();
                        }
                    }
                });

                viewModel.getSearchEventsResult().observe(mapFragment.getViewLifecycleOwner(), new Observer<SearchEventsResult>() {
                    @Override
                    public void onChanged(@Nullable SearchEventsResult searchEventsResult) {
                        if(searchEventsResult == null) {
                            return;
                        }
                        if(searchEventsResult.getError()!=null){
                            Toast.makeText(mapFragment.getContext(), searchEventsResult.getError(), Toast.LENGTH_SHORT).show();
                        }
                        if (searchEventsResult.getSuccess() != null) {
                            List<SearchEventsReplyUnit> events =searchEventsResult.getSuccess().getEvents();
                            Iterator<SearchEventsReplyUnit> it = events.iterator();
                            List<EventEntity> entities=new ArrayList<EventEntity>();
                            while (it.hasNext()){
                                SearchEventsReplyUnit current=it.next();
                                if(markers.get(current.getEvent_id())==null){
                                    if(compareDate(current.getEnd_date())){
                                        EventEntity aux= new EventEntity(current.getEvent_id(),current.getLocation()[0],current.getLocation()[1],current.getName(),
                                                current.getNum_participants(),current.getStart_date(),current.getEnd_date(),searchEventsResult.getSuccess().getRegion_hash());
                                        entities.add(aux);
                                        LatLng coordinates = new LatLng(current.getLocation()[0], current.getLocation()[1]);
                                        MarkerOptions marker = new MarkerOptions();
                                        marker.position(coordinates);
                                        marker.title(current.getEvent_id());
                                        if(mode.isChecked()){
                                            marker.visible(false);
                                        }else{
                                            marker.visible(true);
                                        }
                                        markers.put(current.getEvent_id(),gmap.addMarker(marker));
                                    }
                                }
                            }
                            roomViewModel.insertMultipleEvents(entities);
                            UserAuthenticated userAuth=viewModel.getUserAuth();
                            viewModel.searchRoutes(new SearchEventsData(userAuth.getEmail(), userAuth.getTokenID(), searchloc ));
                        }
                    }
                });


                viewModel.getSearchRoutesResult().observe(mapFragment.getActivity(), new Observer<SearchRoutesResult>() {
                    @Override
                    public void onChanged(SearchRoutesResult searchRoutesResult) {
                        if(searchRoutesResult==null){
                            return;
                        }
                        if(searchRoutesResult.getSuccess()!=null){
                            boolean somethingCantLoad=false;
                            SearchRoutesReply result = searchRoutesResult.getSuccess();
                            Iterator<SearchRoutesReplyUnit> itRoutes =result.getRoutes().iterator();
                            while(itRoutes.hasNext()){
                                SearchRoutesReplyUnit currentRoute=itRoutes.next();
                                if(routesPolylines.get(currentRoute.getRoute_id())==null){
                                    List<EventRouteCrossReference> crossoversToAdd= new ArrayList<EventRouteCrossReference>();
                                    boolean routeAborted=false;

                                    Iterator<SearchEventsReplyUnit> itEvents= currentRoute.getEvents().iterator();
                                    List<Marker> passagePoints= new ArrayList<Marker>();
                                    while (itEvents.hasNext()){
                                        SearchEventsReplyUnit currentEvent=itEvents.next();
                                        if(markers.get(currentEvent.getEvent_id())==null){
                                            routeAborted=true;
                                            somethingCantLoad=true;
                                            break;
                                        }
                                        passagePoints.add(markers.get(currentEvent.getEvent_id()));
                                        crossoversToAdd.add(new EventRouteCrossReference(currentEvent.getEvent_id(),currentRoute.getRoute_id()));
                                    }
                                    if(!routeAborted){
                                        roomViewModel.insertCrossovers(crossoversToAdd);
                                        roomViewModel.insertRoute(new RouteEntity(currentRoute.getRoute_id(),searchRoutesResult.getSuccess().getRegion_hash()));
                                        calculateDirections(passagePoints.remove(0),passagePoints.remove(passagePoints.size()-1),passagePoints, currentRoute.getRoute_id());
                                    }
                                }
                            }if(!somethingCantLoad){
                                if(!hashesDisplayed.contains(result.getRegion_hash())){
                                    hashesDisplayed.add(result.getRegion_hash());
                                }
                            }else{
                                hashesFullyLoaded.remove(result.getRegion_hash());
                                roomViewModel.removeHashFullyLoaded(result.getRegion_hash());
                            }
                        }
                    }
                });
                viewModel.getAddRouteResult().observe(mapFragment.getActivity(), new Observer<AddRouteResult>() {
                    @Override
                    public void onChanged(AddRouteResult addRouteResult) {
                        if(addRouteResult==null){
                            return;
                        }
                        if (addRouteResult.getError()!=null){
                            newRouteEventIds= new ArrayList<String>();
                            Toast.makeText(mapFragment.getContext(), addRouteResult.getError(), Toast.LENGTH_SHORT).show();
                            mode.setClickable(true);
                            hideEvents();
                            showRoutes();
                        }
                        if(addRouteResult.getSuccess()!=null){
                            RouteID route_id=addRouteResult.getSuccess();
                            Double lat=markers.get(newRouteEventIds.get(0)).getPosition().latitude;
                            Double lon=markers.get(newRouteEventIds.get(0)).getPosition().longitude;
                            String hashed_loc=GeoHashUtil.convertCoordsToGeoHashLowPrecision(lat,lon);
                            List<EventRouteCrossReference> crossoversToAdd= new ArrayList<EventRouteCrossReference>();
                            List<Marker> passagePoints= new ArrayList<Marker>();
                            roomViewModel.insertRoute(new RouteEntity(route_id.getRoute_id(),hashed_loc));
                            Iterator<String> it=newRouteEventIds.iterator();
                            while(it.hasNext()){
                                String event_id=it.next();
                                passagePoints.add(markers.get(event_id));
                                crossoversToAdd.add(new EventRouteCrossReference(event_id,route_id.getRoute_id()));
                            }
                            calculateDirections(passagePoints.remove(0),passagePoints.remove(passagePoints.size()-1),passagePoints, route_id.getRoute_id());
                            roomViewModel.insertCrossovers(crossoversToAdd);
                            newRouteEventIds=new ArrayList<String>();
                            hideEvents();
                            showRoutes();
                            mode.setClickable(true);
                        }
                    }
                });
            }
        });



    }


    private boolean compareDate(String endDate){
        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String currentDate = formatD.format(new Date());
        String eDate = endDate.substring(0,endDate.indexOf("T"));
        eDate = eDate.concat("-").concat(endDate.substring(endDate.indexOf("T")+1, endDate.length()-4));
        try {
            int a = 2;
            Date cD = formatD.parse(currentDate);
            Date eD = formatD.parse(eDate);
            if(cD.compareTo(eD) > 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
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
                            loadEvents(location.getLatitude(),location.getLongitude());
                            curLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            BitmapDescriptor bitmap = BitmapFromVector(getActivity(), R.drawable.ic_your_location);
                            MarkerOptions options = new MarkerOptions().position(curLoc).title("You are here").icon(bitmap);
                            CameraPosition pos = CameraPosition.builder().target(curLoc).zoom(15).build();
                            areEventsVisible=true;
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                            markers.put("CurrLoc",googleMap.addMarker(options));
                        }
                    });
                }
            }
        });
    }

    private void loadEvents(Double lat,Double lon){
        String hashedLoc=GeoHashUtil.convertCoordsToGeoHashLowPrecision(lat,lon);
        lastHashConsulted=hashedLoc;
        if(hashesFullyLoaded.contains(hashedLoc)){
            loadingEvents = true;
            loadingRoutes=true;
            roomViewModel.setCurrentGeoHash(GeoHashUtil.convertCoordsToGeoHashLowPrecision(lat,lon));
        }
        else{
            hashesFullyLoaded.add(hashedLoc);
            roomViewModel.addHashFullyLoaded(hashedLoc);
            UserAuthenticated userAuth=viewModel.getUserAuth();
            double[] coords= {lat,lon};
            searchloc=coords;
            viewModel.searchEvents(new SearchEventsData(userAuth.getEmail(), userAuth.getTokenID(), coords ));
        }
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

    private void hideEvents(){
        Iterator<Marker> it=markers.values().iterator();
        while(it.hasNext()){
            Marker aux=it.next();
            if(!aux.getTitle().equals("You are here")){
                aux.setVisible(false);
            }
        }
    }
    private void showEvents(){
        Iterator<Marker> it=markers.values().iterator();
        while(it.hasNext()){
            it.next().setVisible(true);
        }
    }

    private void showRoutes(){
        Iterator<Polyline> it=routesPolylines.values().iterator();
        while (it.hasNext()){
            Polyline current=it.next();
            current.setVisible(true);
            Iterator<EventEntity> id = allRoutes.get((String)current.getTag()).eventsOnRoute.iterator();
            while (id.hasNext()){
                markers.get(id.next().getEvent_id()).setVisible(true);
            }
        }

    }

    private void hideRoutes(){
        Iterator<Polyline> it=routesPolylines.values().iterator();
        while (it.hasNext()){
            it.next().setVisible(false);
        }
    }

    private void calculateDirections(Marker origin ,Marker destination,List<Marker> waypoints, String routeId){

        com.google.maps.model.LatLng destinationLatLng = new com.google.maps.model.LatLng(
                destination.getPosition().latitude,
                destination.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        com.google.maps.model.LatLng[] waypointVector=new com.google.maps.model.LatLng[waypoints.size()];
        int counter=0;
        Iterator<Marker> it = waypoints.iterator();
        while(it.hasNext()){
            Marker current=it.next();
            waypointVector[counter++]=new com.google.maps.model.LatLng(current.getPosition().latitude,current.getPosition().longitude);

        }
        directions.waypoints(waypointVector);
        directions.origin(
                new com.google.maps.model.LatLng(
                        origin.getPosition().latitude,
                        origin.getPosition().longitude
                )
        );
        directions.destination(destinationLatLng).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result, routeId);
            }

            @Override
            public void onFailure(Throwable e) {
                //nada
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result, String routeId){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = gmap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                    polyline.setClickable(true);
                    if(eventMode){
                        polyline.setVisible(false);
                    }
                    polyline.setTag(routeId);

                    routesPolylines.put(routeId,polyline);
                    Iterator<EventEntity> it=allRoutes.get(routeId).eventsOnRoute.iterator();
                    while(it.hasNext()){
                        EventEntity current=it.next();
                        markers.get(current.getEvent_id()).setVisible(true);
                    }
                }
            }
        });
    }

}