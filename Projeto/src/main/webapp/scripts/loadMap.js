// Note: This example requires that you consent to location sharing when
// prompted by your browser. If you see the error "The Geolocation service
// failed.", it means you probably did not give permission for the browser to
// locate you.
let map, infoWindow;
let mapPreview;
var previewMarker;
var clickListener, clickmarker;
var geoHashArray = [];
var geoHash;
var dragListener;
var markers = [];

//First style is no POI style, then enable public parks, medical and government places
var myStyles =[
  {
    featureType: "poi",
    elementType: "labels",
    stylers: [
      { visibility: "off" }
    ]
  },
  {
    featureType: "poi.medical",
    elementType:"labels",
    stylers:[
      { visibility: "on"}
    ]
  },
  {
    featureType: "poi.park",
    elementType:"labels",
    stylers:[
      { visibility: "on"}
    ]
  },
  {
    featureType: "poi.government",
    elementType:"labels",
    stylers:[
      { visibility: "on"}
    ]
  }
];

function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    //center at FCT NOVA
    center: { lat: 38.66128, lng: - 9.20343 },
    zoom: 12,
    //disableDefaultUI: true,
    
          zoomControl: false,
          mapTypeControl: true,
          scaleControl: false,
          streetViewControl: false,
          rotateControl: false,
          fullscreenControl: false,
      
      minZoom: 7,
    styles:myStyles,
    gestureHandling: "greedy"
  });

  createEventButton();
  locationButton();
  searchBar();

  initMapPreview();

  let tileListener = google.maps.event.addListenerOnce(map,'tilesloaded', function(){
    loadEventWithID();
    dragListener = google.maps.event.addListener(map, 'dragend', function(event) {
      loadEventWithID();
    });
      var visible = true;
    dragListener = google.maps.event.addListener(map, 'zoom_changed', function(event) {
        loadEventWithID();
        var zoom = map.getZoom();
      
        if (!visible && zoom >= 9) {
            visible = !visible;
            for (i = 0; i < markers.length; i++) {
             
                markers[i].setMap(map);
                markers[i].setOptions({ 'opacity': 1.0 })
                markers[i].setAnimation(google.maps.Animation.DROP);
            }
        }
        else if (visible && zoom < 9) {
            visible = !visible;
            for (i = 0; i < markers.length; i++) {
                markers[i].setAnimation(google.maps.Animation.BOUNCE);
                timeOutHideAnimation(i);
               // markers[i].setMap(null);
            }
        }

      /*  for (i = 0; i < markers.length; i++) {
           // console.log("Hello?");
            markers[i].setVisible(zoom >= 9);
        }*/

    });
  });



}

function timeOutHideAnimation(i) {
    setTimeout((function () {
        markers[i].setOptions({ 'opacity': 0.750 })
        setTimeout((function () {
            markers[i].setOptions({ 'opacity': 0.5 })
            setTimeout((function () {
                markers[i].setOptions({ 'opacity': 0.250 })
                setTimeout((function () {
                    markers[i].setMap(null);
                }), 100);
            }), 100);
        }), 100);
    }), 100);
}


function initMapPreview() {
  mapPreview = new google.maps.Map(document.getElementById("mapPreview"), {
    //center at FCT NOVA
    center: { lat: 38.66128, lng: - 9.20343 },
    zoom: 6,
    disableDefaultUI: true,
    styles:myStyles,
      gestureHandling: "greedy",
    minZoom: 12
  });



//You have to make some functions to center the map on the location of the event

}

function createEventButton() {
  const createEventButton = document.createElement('button');
  createEventButton.textContent = "Create new event";
  createEventButton.classList.add("custom-map-control-button");
  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(createEventButton);
  createEventButton.addEventListener("click", () => {
    //Allow user to create a new event in the side bar
      let sidebar = document.getElementById("sidebar_content");
      document.getElementById("sidebar_content_event_list").style.display = 'none';

    $.get("contents/createEventForm.html", function (data) {
        $('#sidebar_content').html(data);
    });
    //document.getElementById("pac-input").style.display="none";
    map.setOptions({ draggableCursor: 'default' });
    clickListener = google.maps.event.addListener(map, "click", function(event) {
      clickMarker(event.latLng);
      let latLng = event.latLng.lat()+","+event.latLng.lng();
      document.getElementById("evLoc").value = latLng;
      //console.log(latLng);
      getReverseGeocodingData(event.latLng.lat(), event.latLng.lng(), function(locationData) {
        console.log(locationData);
        document.getElementById("evLocName").innerHTML = locationData;
      });


    });



  });
}

function createEventInMap(attributes) {

    //props for the event marker
    const pos = {
        lat: attributes.location[0],
        lng: attributes.location[1]
    };

    var contentString = "<p style='text-align: center; font-size: 140%'>" + attributes.name + "</p>" +
        "<label style=\"font-size: 110%; text-align: center \">Event Description:</label>" +
        "<p style=\"display: inline-block; text-align: center\">"+attributes.description+"</p>" +
        "<br>" +
        "<label style=\"font-size: 110%; text-align: center\">Category:</label>" +
        "<p style=\"display:inline-block; text-align: center\">" + attributes.category + "</p>" +
        "<br>" +
        "<button style='margin: auto' type = \"button\" onclick = \"displayEventSideBar(\'" + attributes.event_id + "\')\">View more details</button>";


    $("#sidebar_content_event_list").append($("<div style='text-align: center; border-style: solid; border-color: lightgray; border-width: 1px'>" + contentString + "</div><br>"));

    contentString = "<div style='text-align: center'>" + contentString + "</div>";

    var props = {
        coords: pos,
        content: contentString,
        title: attributes.event_id
    }
    //add user location marker
    addMarker(props);

}

/*function requestGeoCode(latLng){
  let URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latLng+"&key=AIzaSyCxgVR80ZfBsHQjmUlAJVsHrEZRP4Irk50";
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open("POST", URL, false);
  xmlhttp.setRequestHeader("Content-Type", "application/json");
  xmlhttp.send(ItemJSON);
  if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

      const obj = JSON.parse(xmlhttp.responseText);
    }
}*/

//Funcao para testes do Franca
function loadEventWithID() {
   /* let id = $("#loadEventID").val();
    if (!id) {
        alert("Mete um id");
    }
    loadEvent(id, true);*/
    /*navigator.geolocation.getCurrentPosition(
        (position) => {
            const pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude,
            };*/


    let pos = map.getCenter();
    let bounds = map.getBounds();
    let ne = bounds.getNorthEast().lat();
    geohash = Geohash.encode(pos.lat(), pos.lng(), 3);
    console.log("Zoom level: " + map.getZoom());
    if (map.getZoom() < 8) {

        console.log("Zoom is way out to load events. Zoom in a little to avoid loading all the events of the world resulting in the biggest lag since the ZON company arrived.");
        return false;
    }
    if (geoHashArray.includes(geohash)) {
        console.log("Already loaded events for this geohash. Exiting...");
        return false;
    }
    let dif = Math.abs(ne - pos.lat());

            var urlvariable = "/rest/searchEventsByRange";
            var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET EVENTS
            var xmlhttp = new XMLHttpRequest();
            var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
            var ItemJSON = '{"email": "' + userId +
                '", "token": "' + token +
                '", "location": ["' + pos.lat() + '","' + pos.lng() + '"]' +
               // ', "radius": "' + dif +
                '}';
            xmlhttp.open("POST", URL, true);
            xmlhttp.setRequestHeader("Content-Type", "application/json");
            xmlhttp.onload = function (oEvent) {
                if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
                    alert("Couldn't load events, message: " + xmlhttp.status);
                    return false;
                }
                const attributes = JSON.parse(xmlhttp.responseText);
                const events = attributes.events;
                console.log("Loading "+events.length+" events.");
                var obj;
                for (var i = 0; i < events.length; i++) {
                    obj = events[i];
                    timeOutAddition(obj, i);
                    //loadEventMiniature(obj);
                    //loadEvent(obj.event_id, true);
                }
                geoHashArray.push(attributes.region_hash);
            };
            xmlhttp.send(ItemJSON);

       /* },
        () => {
            handleLocationError(true);
        }
    );*/
}

function timeOutAddition(obj, i) {
    setTimeout((function () {
        loadEventMiniature(obj);
    }), i * 20);
}

function loadEventMiniature(attributes) {
    //props for the event marker
    const pos = {
        lat: attributes.location[0],
        lng: attributes.location[1]
    };
    //infowindow content
    var category = getCategory(attributes.category);
    var start = new Date(attributes.start_date);
    var end = new Date(attributes.end_date);
    start = "Start: " + start.getDate() + "/" + start.getMonth() + "/" + start.getFullYear() + " " + start.getHours() + ":" + start.getMinutes();
    end = "End: " + end.getDate() + "/" + end.getMonth() + "/" + end.getFullYear() + " " + end.getHours() + ":" + end.getMinutes();
    var contentString = 
        "<p style='text-align: center; font-size: 150%; color: #009999'>" + attributes.name + "</p>" +
        
        "<p style=\"display:inline-block; text-align: center; margin-left: 4px; font-size:140%\">" + category + "</p>" +
        "<br>" +
        "<label style=\"font-size: 110%; text-align: center \">Event Schedule:</label>" +
        "<p style=\"text-align: center\">" + start + "</p>" +
        "<p style=\"text-align: center\">" + end + "</p>" +
        "<i style=\"\" class=\"fa fa-user-o\" style=\"\"></i><p style=\"display:inline-block; margin-left: 10px\">"+attributes.num_participants+"</p>" +
        "<br>" +
        "<button class=\"btn btn-primary\" style='margin-bottom: 10px' type = \"button\" onclick = \"loadEvent(\'" + attributes.event_id + "\', false"+")\">View more details</button>";

   
    //side panel content
    var sideContentString = "<div style='text-align: center; border-style: solid; border-color: lightgray; border-width: 1px'>" + contentString;
    
    //last touches to info window's content
    contentString = "<div style='text-align: center'>" + contentString + "</div>";

    var props = {
        coords: pos,
        content: contentString,
        title: attributes.event_id
    }
    //add user location marker
    addMarker(props);
    let i = markers.length;

    //add the content to the side panel with additional touches
    var goToButton = "<button class=\"btn btn-secondary\" style='margin-left:10px; margin-bottom: 10px' type = \"button\" onclick = \"goToEvent(\'" + i + "\')\">Go to</button>";
    sideContentString = sideContentString + goToButton + "</div><br>";
    $("#sidebar_content_event_list").append($(sideContentString));

}

function goToEvent(i) {
    let marker = markers[i - 1];
    map.panTo(marker.getPosition());
    new google.maps.event.trigger(marker, 'click');
}

//Requires callback function
function getReverseGeocodingData(lat, lng, callback) {
    var address;
    var latlng = new google.maps.LatLng(lat, lng);
    // This is making the Geocode request
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({ 'latLng': latlng }, function (results, status) {
        if (status !== google.maps.GeocoderStatus.OK) {
            alert(status);
        }
        // This is checking to see if the Geoeode Status is OK before proceeding
        if (status == google.maps.GeocoderStatus.OK) {
            address = (results[0].formatted_address);
            callback(address);
            //console.log(address);

        }
    });

}


let searchMarkers = [];
function searchBar() {
  // Create the search box and link it to the UI element.
  const input = document.createElement("input");
  input.setAttribute("id","pac-input");
  input.classList.add("custom-map-control-button");
  input.placeholder = "Search for a location";
  const searchBox = new google.maps.places.SearchBox(input);

  //input.classList.add("custom-map-control-button");
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });
  // Listen for the event fired when the user selects a prediction and retrieve
  // more details for that place.
  searchBox.addListener("places_changed", () => {
    const places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }
    // Clear out the old markers.
    searchMarkers.forEach((marker) => {
      marker.setMap(null);
    });
    searchMarkers = [];
    // For each place, get the icon, name and location.
    const bounds = new google.maps.LatLngBounds();
    places.forEach((place) => {
      if (!place.geometry || !place.geometry.location) {
        console.log("Returned place contains no geometry");
        return;
      }
      const icon = {
        url: place.icon,
        size: new google.maps.Size(71, 71),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(17, 34),
        scaledSize: new google.maps.Size(25, 25),
      };
      // Create a marker for each place.
      searchMarkers.push(
        new google.maps.Marker({
          map,
          icon,
          title: place.name,
          position: place.geometry.location,
        })
      );

      if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }

      let evLoc = document.getElementById("evLoc");
      if(evLoc) {
        let latLng = place.geometry.location.lat()+","+place.geometry.location.lng();
        evLoc.value = latLng
        getReverseGeocodingData(place.geometry.location.lat(), place.geometry.location.lng(), function(locationData) {
          console.log(locationData);
          document.getElementById("evLocName").innerHTML = locationData;
        });

      }
    });
    map.fitBounds(bounds);
  });
}



function locationButton() {
  const locationButton = document.createElement("button");
  locationButton.textContent = "Pan to Current Location";
  locationButton.classList.add("custom-map-control-button");
  map.controls[google.maps.ControlPosition.TOP_CENTER].push(locationButton);
  locationButton.addEventListener("click", () => {
    // Try HTML5 geolocation.
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };

          map.setCenter(pos);
          map.setZoom(14);
          //properties for user icon
          var userIcon = {
            url: 'https://static.thenounproject.com/png/39498-200.png', // url
            scaledSize: new google.maps.Size(35, 35), // scaled size
            origin: new google.maps.Point(0,0), // origin
            anchor: new google.maps.Point(17.5, 35) // anchor
          }
          //props for the user marker
          var props = {
            coords:pos,
            iconImage:userIcon
          }
          //add user location marker
          addMarker(props);

        },
        () => {
          handleLocationError(true);
        }
      );
    } else {
      // Browser doesn't support Geolocation
      handleLocationError(false);
    }
  });
}


function handleLocationError(browserHasGeolocation) {
  alert(browserHasGeolocation
    ? "Error: The Geolocation service failed. You can still create events and routes, however to route to your location you will need to refresh the page and allow access to it"
    : "Error: Your browser doesn't support geolocation."
  );
}



function addMarker(props){
  var marker = new google.maps.Marker({
    position:props.coords,
      map: map,
     animation: google.maps.Animation.DROP
  });
  if(props.iconImage){
    marker.setIcon(props.iconImage);
  }
  if(props.content){
    var infoWindow = new google.maps.InfoWindow({
      content:props.content
    });
      marker.addListener("click", () => {
          infoWindow.open({
              anchor: marker,
              map,
              shouldFocus: false
          });
          let zoom = map.getZoom();
          if (zoom<14)
            map.setZoom(14);
          map.setCenter(marker.getPosition());
      });
  }
  if (props.title) {
      marker.setTitle(props.title);
    }
    markers.push(marker);
  return marker;
}

function clickMarker(location) {
  if (clickmarker == null) {
    //console.log("null");
    const icon = {
      url: "https://i.pinimg.com/originals/25/62/aa/2562aacd1a4c2af60cce9629b1e05cf2.png",
      scaledSize: new google.maps.Size(35, 35), // scaled size
      origin: new google.maps.Point(0,0), // origin
      anchor: new google.maps.Point(17.5, 35) // anchor
      };
    clickmarker = new google.maps.Marker({
          position: location,
          map: map,
          icon: icon
      });
  } else {
    clickmarker.setPosition(location);
  }
}
