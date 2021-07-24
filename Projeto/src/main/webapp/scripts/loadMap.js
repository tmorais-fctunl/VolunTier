// Note: This example requires that you consent to location sharing when
// prompted by your browser. If you see the error "The Geolocation service
// failed.", it means you probably did not give permission for the browser to
// locate you.
let map, infoWindow, directionsService, directionsRenderer;
let mapPreview;
let mapRoutePreview, directionsServicePreview, directionsRendererPreview;
var previewmarker = null;
var clickListener, clickmarker;
var geoHashArray = [];
var geoHash;
var dragListener;
var markers = [];
var routes = [];
var clickEvents = [];
var notRouting = true;
const labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
let labelIndex = 0;

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

  eventRouteToggle();
  createEventButton();
  createRouteButton();
  locationButton();
  searchBar();
  directionsService = new google.maps.DirectionsService();
  directionsRenderer = new google.maps.DirectionsRenderer();
  directionsRenderer.setMap(map);  
    initMapPreview();
    initMapRoutePreview();

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
             
                markers[i].marker.setMap(map);
                markers[i].marker.setOptions({ 'opacity': 1.0 })
                markers[i].marker.setAnimation(google.maps.Animation.DROP);
            }
            for (i = 0; i < routes.length; i++) {

                routes[i].marker.setMap(map);
                routes[i].marker.setOptions({ 'opacity': 1.0 })
                routes[i].marker.setAnimation(google.maps.Animation.DROP);
            }
        }
        else if (visible && zoom < 9) {
            visible = !visible;
            for (i = 0; i < markers.length; i++) {
                markers[i].marker.setAnimation(google.maps.Animation.BOUNCE);
                timeOutHideAnimation(i);
               // markers[i].setMap(null);
            }
            for (i = 0; i < routes.length; i++) {
                routes[i].marker.setAnimation(google.maps.Animation.BOUNCE);
                timeOutHideRouteAnimation(i);
                // markers[i].setMap(null);
            }
        }
    });
  });
   

}

function timeOutHideAnimation(i) {
    setTimeout((function () {
        markers[i].marker.setOptions({ 'opacity': 0.750 })
        setTimeout((function () {
            markers[i].marker.setOptions({ 'opacity': 0.5 })
            setTimeout((function () {
                markers[i].marker.setOptions({ 'opacity': 0.250 })
                setTimeout((function () {
                    markers[i].marker.setMap(null);
                }), 100);
            }), 100);
        }), 100);
    }), 100);
}

function timeOutHideRouteAnimation(i) {
    setTimeout((function () {
        routes[i].marker.setOptions({ 'opacity': 0.750 })
        setTimeout((function () {
            routes[i].marker.setOptions({ 'opacity': 0.5 })
            setTimeout((function () {
                routes[i].marker.setOptions({ 'opacity': 0.250 })
                setTimeout((function () {
                    routes[i].marker.setMap(null);
                }), 100);
            }), 100);
        }), 100);
    }), 100);
}

function initMapPreview() {
  mapPreview = new google.maps.Map(document.getElementById("mapPreview"), {
    //center at FCT NOVA
    center: { lat: 38.66128, lng: - 9.20343 },
    zoom: 13,
    disableDefaultUI: true,
    styles:myStyles,
      gestureHandling: "greedy",
    minZoom: 12
  });
}

function initMapRoutePreview() {
    mapRoutePreview = new google.maps.Map(document.getElementById("mapRoutePreview"), {
        //center at FCT NOVA
        center: { lat: 38.66128, lng: - 9.20343 },
        zoom: 13,
        disableDefaultUI: true,
        styles: myStyles,
        gestureHandling: "greedy",
        minZoom: 10
    });
    directionsServicePreview = new google.maps.DirectionsService();
    directionsRendererPreview = new google.maps.DirectionsRenderer();
    directionsRendererPreview.setMap(mapRoutePreview); 
}

 

function eventRouteToggle() {
    const toggleEventRouteButton = document.createElement('button');
    toggleEventRouteButton.textContent = "Show only events";
    toggleEventRouteButton.classList.add("custom-map-control-button");
    map.controls[google.maps.ControlPosition.RIGHT_TOP].push(toggleEventRouteButton);
    toggleEventRouteButton.addEventListener("click", () => {
        if (toggleEventRouteButton.textContent == "Show only events") {
            document.getElementById("sidebar_content_event_list").style.display = '';
            document.getElementById("EventsTitle").style.display = '';
            document.getElementById("sidebar_content_route_list").style.display = 'none';
            document.getElementById("RoutesTitle").style.display = 'none';

            toggleEventRouteButton.textContent = "Show only routes";
            for (var i = 0; i < markers.length; i++) {
                markers[i].marker.setVisible(true);
            }
            for (var i = 0; i < routes.length; i++) {
                routes[i].marker.setVisible(false);
            }
        }
        else if (toggleEventRouteButton.textContent == "Show only routes") {
            document.getElementById("sidebar_content_event_list").style.display = 'none';
            document.getElementById("EventsTitle").style.display = 'none';
            document.getElementById("sidebar_content_route_list").style.display = '';
            document.getElementById("RoutesTitle").style.display = '';
            toggleEventRouteButton.textContent = "Show events and routes";
            for (var i = 0; i < markers.length; i++) {
                markers[i].marker.setVisible(false);
            }
            for (var i = 0; i < routes.length; i++) {
                routes[i].marker.setVisible(true);
            }
        }
        else {
            document.getElementById("sidebar_content_event_list").style.display = '';
            document.getElementById("EventsTitle").style.display = '';
            document.getElementById("sidebar_content_route_list").style.display = '';
            document.getElementById("RoutesTitle").style.display = '';
            toggleEventRouteButton.textContent = "Show only events";
            for (var i = 0; i < markers.length; i++) {
                markers[i].marker.setVisible(true);
            }
            for (var i = 0; i < routes.length; i++) {
                routes[i].marker.setVisible(true);
            }
        }
    });
}

function createEventButton() {
  const createEventButton = document.createElement('button');
  createEventButton.textContent = "Create new event";
  createEventButton.classList.add("custom-map-control-button");
  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(createEventButton);
    createEventButton.addEventListener("click", () => {
        notRouting = true;
    //Allow user to create a new event in the side bar
      let sidebar = document.getElementById("sidebar_content");
      document.getElementById("sidebar_content_event_list").style.display = 'none';
      document.getElementById("EventsTitle").style.display = 'none';
      document.getElementById("sidebar_content_route_list").style.display = 'none';
      document.getElementById("RoutesTitle").style.display = 'none';

      jQuery.ajax({
          url: "contents/createEventForm.html",
          success: function (data) {
              $('#sidebar_content').html(data);
          },
          async: true,
          complete: function () {
              updateRouteFormInputs();
          }
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

function createRouteButton() {
    
    labelIndex = 0;
    const createRouteButton = document.createElement('button');
    createRouteButton.textContent = "Create new Route";
    createRouteButton.classList.add("custom-map-control-button");
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(createRouteButton);
    createRouteButton.addEventListener("click", () => {
        markers.forEach(function (el) {
            if (el.visibility == "PRIVATE")
                el.marker.setVisible(false);
        });
        //Allow user to create a new ~route in the side bar
        let sidebar = document.getElementById("sidebar_content");
        document.getElementById("sidebar_content_event_list").style.display = 'none';
        document.getElementById("EventsTitle").style.display = 'none';
        document.getElementById("sidebar_content_route_list").style.display = 'none';
        document.getElementById("RoutesTitle").style.display = 'none';

        jQuery.ajax({
            url: "contents/createRouteForm.html",
            success: function (data) {
                $('#sidebar_content').html(data);
            },
            async: true,
            complete: function () {
                updateRouteFormInputs();
            }
        });
        //document.getElementById("pac-input").style.display="none";
        map.setOptions({ draggableCursor: 'default' });
        notRouting = false;
    });

}

function loadEventWithID() {
    //$("body").css("cursor", "progress");
    let pos = map.getCenter();
    let bounds = map.getBounds();
    let ne = bounds.getNorthEast();
    let sw = bounds.getSouthWest();
    let geohashes = [];
    //GET CENTER POINT
    let centergeo = Geohash.encode(pos.lat(), pos.lng(), 3);
    if (!geoHashArray.includes(centergeo))
        geohashes.push({ geohash: centergeo, pos: pos });
    //GET 4 POINTS OF THE MAP: LEFT MID, BOTTOM MID, RIGHT MID, TOP MID
    //LEFT MID:
    let lmgeo = Geohash.encode(ne.lat(), pos.lng(), 3);
    if (!geoHashArray.includes(lmgeo))
        geohashes.push({ geohash: lmgeo, pos: new google.maps.LatLng(ne.lat(), pos.lng())});
    //RIGHT MID:
    let rmgeo = Geohash.encode(sw.lat(), pos.lng(), 3);
    if (!geoHashArray.includes(rmgeo))
        geohashes.push({ geohash: rmgeo, pos: new google.maps.LatLng(sw.lat(), pos.lng())});
    //BOTTOM MID:
    let bmgeo = Geohash.encode(pos.lat(), sw.lng(), 3);
    if (!geoHashArray.includes(bmgeo))
        geohashes.push({ geohash: bmgeo, pos: new google.maps.LatLng(pos.lat(), sw.lng())});
    //TOP MID:
    let tmgeo = Geohash.encode(pos.lat(), ne.lng(), 3);
    if (!geoHashArray.includes(tmgeo))
        geohashes.push({ geohash: tmgeo, pos: new google.maps.LatLng(pos.lat(), ne.lng())});



    console.log("Zoom level: " + map.getZoom());
    if (map.getZoom() < 9) {
        console.log("Zoom is way out to load events. Zoom in a little to avoid loading all the events of the world resulting in the biggest lag since the ZON company arrived.");
        return false;
    }
    console.log(geohashes);
    for (var i = 0; i < geohashes.length; i++) {
        console.log("Checking geohash: "+(i+1)+"/"+geohashes.length);
        if (geoHashArray.includes(geohashes[i].geohash)) {
            console.log("Already loaded events for this geohash. Exiting...");
            continue;
        }

        //Get Events and Routes
        searchEventsByRange(geohashes[i].pos);
        searchRoutesByRange(geohashes[i].pos);
        geoHashArray.push(geohashes[i].geohash);
    }
    $("body").css("cursor", "default");

}

function searchEventsByRange(pos) {
    var urlvariable = "/rest/searchEventsByRange";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET EVENTS
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "location": ["' + pos.lat() + '","' + pos.lng() + '"]' +
        '}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load events, message: " + xmlhttp.status);
            if (xmlhttp.status == 403)
            if (tryAuthentication())
                searchEventsByRange(pos);

            $("body").css("cursor", "default");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        const events = attributes.events;
        console.log("Loading " + events.length + " events.");
        var obj;
        var now = new Date();
        var exp;
        for (var i = 0; i < events.length; i++) {
            obj = events[i];

            //Ver se expirou
            exp = new Date(attributes.end_date);
            //if (exp > now)
                timeOutAddition(obj, i);
        }
        
    };
    xmlhttp.send(ItemJSON);
}

function searchRoutesByRange(pos) {
    var urlvariable = "/rest/searchRoutesByRange";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET ROUTES
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "location": ["' + pos.lat() + '","' + pos.lng() + '"]' +
        '}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load routes, message: " + xmlhttp.status);
            if (xmlhttp.status == 403)
            if (tryAuthentication())
                searchRoutesByRange(pos);
            $("body").css("cursor", "default");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        const routes = attributes.routes;
        console.log("Loading " + routes.length + " routes.");
        var obj;
        var now = new Date();
        var exp;
        for (var i = 0; i < routes.length; i++) {
            obj = routes[i];

            //Ver se expirou
            exp = new Date(attributes.end_date);
            //if (exp > now)
                timeOutRouteAddition(obj, i);
        }
    };
    xmlhttp.send(ItemJSON);
}


function timeOutAddition(obj, i) {
    setTimeout((function () {
        loadEventMiniature(obj);
    }), i * 20);
}

function timeOutRouteAddition(obj, i) {
    setTimeout((function () {
        loadRouteMiniature(obj);
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
    var hour, min;
    hour = ("0" + start.getHours()).slice(-2);
    min = ("0" + start.getMinutes()).slice(-2)
    start = "Start: " + start.getDate() + "/" + (parseInt(start.getMonth()) + 1) + "/" + start.getFullYear() + " " + hour + ":" + min;
    hour = ("0" + end.getHours()).slice(-2);
    min = ("0" + end.getMinutes()).slice(-2)
    end = end.getDate() + "/" + (parseInt(end.getMonth()) + 1) + "/" + end.getFullYear() + " " + hour + ":" + min;
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

   
   
    var sideContentString = "<div id='eventsectionid_" + attributes.event_id+"' style='text-align: center; border-style: solid; border-color: white; border-width: 1px; border-radius: 12px; box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px; background-color: #FDFDFD'>" + contentString;
    
    //last touches to info window's content
    contentString = "<div style='text-align: center'>" + contentString + "</div>";
    var props;
    if (attributes.visibility == "PUBLIC")
        props = {
            coords: pos,
            content: contentString,
            title: attributes.name
        }
    else
        props = {
            coords: pos,
            content: contentString,
            title: attributes.name
        }
    //add user location marker
    addMarker(props, attributes.event_id, attributes.name, attributes.visibility);
    let i = markers.length;

    //add the content to the side panel with additional touches
    var goToButton = "<button id=\"gotobutton\" class=\"btn btn-secondary\" style='margin-left:10px; margin-bottom: 10px' type = \"button\" onclick = \"goToEvent(\'" + i + "\')\">Go to</button>";
    sideContentString = sideContentString + goToButton + "</div><br>";
    $("#sidebar_content_event_list").append($(sideContentString));

}

function loadRouteMiniature(attributes) {
    //props for the event marker

    const pos = {
        lat: attributes.events[0].location[0],
        lng: attributes.events[0].location[1]
    };
    //infowindow content
    var avg_rating = attributes.avg_rating + '';
    let events = attributes.events;
    let eventsString = "[";
    var event;
    for (var j = 0; j < events.length; j++) {
        event = events[j];
        eventsString = eventsString.concat("'" + event.event_id + "'");
        if (j + 1 < events.length)
            eventsString = eventsString.concat(",");
    }
    eventsString = eventsString.concat("]");
    // console.log(JSON.stringify(event_locations));
    
   
        
   
    avg_rating = parseFloat(avg_rating);
    var start = new Date(attributes.start_date);
    var end = new Date(attributes.end_date);
    var hour, min;
    hour = ("0" + start.getHours()).slice(-2);
    min = ("0" + start.getMinutes()).slice(-2)
    start = "Start: " + start.getDate() + "/" + (parseInt(start.getMonth())+1) + "/" + start.getFullYear() + " " + hour + ":" + min;
    hour = ("0" + end.getHours()).slice(-2);
    min = ("0" + end.getMinutes()).slice(-2)
    end = end.getDate() + "/" + (parseInt(end.getMonth()) + 1) + "/" + end.getFullYear() + " " + hour + ":" + min;
    var contentString =
        "<p style='text-align: center; font-size: 150%; color: #009999'>" + attributes.route_name + "</p>" +
        "<p style=\"display:inline-block; text-align: center; margin-left: 4px; font-size:140%\">" + avg_rating + "/5</p>" +
        "<br>" +
        "<i style=\"\" class=\"fa fa-user-o\" style=\"\"></i><p style=\"display:inline-block; margin-left: 10px\">" + attributes.num_participants + "</p>" +
        "<br>" +
        "<button class=\"btn btn-primary\" style='margin-bottom: 10px; margin-left:5px' type = \"button\" onclick = \"loadRoute(\'" + attributes.route_id + "\'" + ")\">View more details</button>" +
        "<button class=\"btn btn-primary\" style='margin-bottom: 10px; margin-left:5px' type = \"button\" onclick = \"showRouteDirections(" + eventsString + ", false" + ")\">Show Directions</button>" +
        "<button class=\"btn btn-primary\" style='margin-bottom: 10px; margin-left:5px' type = \"button\" onclick = \"showRouteDirections(" + "''" + ", true" + ")\">Hide Directions</button>";



    var sideContentString = "<div id='routesectionid_" + attributes.route_id + "' style='text-align: center; border-style: solid; border-color: white; border-width: 1px; border-radius: 12px; box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px; background-color: #FDFDFD'>" + contentString;

    //last touches to info window's content
    contentString = "<div style='text-align: center'>" + contentString + "</div>";

    var RouteIcon = {
        url: "http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png", // url
        scaledSize: new google.maps.Size(45, 40) // scaled size
        //origin: new google.maps.Point(0, 0), // origin
        //anchor: new google.maps.Point(22.5, 40) // anchor
    }

    var props = {
        coords: pos,
        content: contentString,
        title: attributes.event_id,
        iconImage:RouteIcon 
    }
    //add user location marker
    addRouteMarker(props, attributes.route_id);
    let i = routes.length;

    //add the content to the side panel with additional touches
    var goToButton = "<button id=\"gotobutton\" class=\"btn btn-secondary\" style='margin-left:10px; margin-bottom: 10px' type = \"button\" onclick = \"goToRoute(\'" + i + "\')\">Go to</button>";
    sideContentString = sideContentString + goToButton + "</div><br>";
    $("#sidebar_content_route_list").append($(sideContentString));

}

function showRouteDirections(events, hide) {
    console.log(events);
    if (hide) {
        directionsRenderer.setDirections({ routes: [] });
        return false;
    }
    
    let origin = markers.find(element => element.event_id == events[0]);
    if (!origin) {
        loadEvent(events[0], true);
        origin = markers.find(element => element.event_id == events[0]);
    }
    let destination = markers.find(element => element.event_id == events[events.length - 1]);
    if (!destination) {
        loadEvent(events[events.length -1], true);
        origin = markers.find(element => element.event_id == events[events.length - 1]);
    }
    let waypoints = [];
    for (var i = 1; i < events.length - 1; i++) {
        let wp = markers.find(element => element.event_id == events[i]);
        if (!wp) {
            loadEvent(events[i], true);
            wp = markers.find(element => element.event_id == events[i]);
        }
        waypoints.push({
            location: wp.marker.position,
            stopover: true,
        });
    }
    calcRoute(origin.marker.position, destination.marker.position, waypoints);
    return false;
}

function showRouteDirectionsPreview(events) {
    console.log(events);
    directionsRendererPreview.setDirections({ routes: [] });
    let origin = markers.find(element => element.event_id == events[0]);
    let destination = markers.find(element => element.event_id == events[events.length - 1]);
    let waypoints = [];
    for (var i = 1; i < events.length - 1; i++) {
        waypoints.push({
            location: markers.find(element => element.event_id == events[i]).marker.position,
            stopover: true,
        });
    }
    calcRoutePreview(origin.marker.position, destination.marker.position, waypoints);
    //mapRoutePreview.panTo(origin.getPosition());
    return false;
}

function calcRoute(origin, destination, waypoints) {
    var request = {
        origin: origin,
        destination: destination,
        waypoints:waypoints,
        travelMode: 'DRIVING'
    };
    directionsService.route(request, function (result, status) {
        if (status == 'OK') {
            directionsRenderer.setDirections(result);
        }
    });
}

function calcRoutePreview(origin, destination, waypoints) {
    var request = {
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        travelMode: 'DRIVING'
    };
    directionsServicePreview.route(request, function (result, status) {
        if (status == 'OK') {
            directionsRendererPreview.setDirections(result);
        }
    });
}

function goToRoute(i) {
    let marker = routes[i - 1].marker;
    map.panTo(marker.getPosition());
    new google.maps.event.trigger(marker, 'click');
}

function goToEvent(i) {
    let marker = markers[i - 1].marker;
    map.panTo(marker.getPosition());
    new google.maps.event.trigger(marker, 'click');
}

function hideMarker(i) {
    let marker = markers[i - 1];
    marker.setVisible(false);
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

function addRouteMarker(props, route_id) {
    var marker = new google.maps.Marker({
        position: props.coords,
        map: map,
        animation: google.maps.Animation.DROP
    });
    if (props.iconImage) {
        marker.setIcon(props.iconImage);
    }
    if (props.content) {
        var infoWindow = new google.maps.InfoWindow({
            content: props.content
        });
        marker.addListener("click", () => {
            infoWindow.open({
                anchor: marker,
                map,
                shouldFocus: false
            });
            let zoom = map.getZoom();
            if (zoom < 14)
                map.setZoom(14);
            map.setCenter(marker.getPosition());
        });
    }
    if (props.title) {
        marker.setTitle(props.title);
    }
    routes.push({ marker: marker, route_id: route_id });
    return marker;
}


function addMarker(props, event_id, event_name, visibility){
  var marker = new google.maps.Marker({
    position:props.coords,
      map: map,
      animation: google.maps.Animation.DROP,
     label:null
  });
  if(props.iconImage){
    marker.setIcon(props.iconImage);
  }
  if(props.content){
    var infoWindow = new google.maps.InfoWindow({
      content:props.content
    });
      clickEvents.push({
          marker: marker,
          listener: marker.addListener("click", () => {
              if (notRouting) {
                  infoWindow.open({
                      anchor: marker,
                      map,
                      shouldFocus: false
                  });
                  let zoom = map.getZoom();
                  if (zoom < 14)
                      map.setZoom(14);
                  map.setCenter(marker.getPosition());
              }
              else {
                  if (labelIndex < 9 && marker.getLabel() == null) {
                      $("#createRouteForm #events").append('<div style="width:100%; text-align:center"><span style="text-align:center">' + event_name + '</span><i style="color:red; margin-left: 5px; display:inline-block" onclick="$(this).parent().remove(); setMarkerLabelNull(\''+event_id+'\'); updateRouteFormEvents()" class="fa fa-times userOptions" aria-hidden="true"></i><a id="createroute_event_id" style="display:none">' + event_id + '</a></div>');
                      
                      let label = labels[labelIndex % labels.length];
                      console.log(label);
                      labelIndex++;
                      marker.setLabel(label);
                  }
              }
          })
      });
  }
  if (props.title) {
      marker.setTitle(props.title);
    }
    markers.push({ marker: marker, event_id: event_id, visibility: visibility });
  return marker;
}

function setMarkerLabelNull(event_id) {
    markers.find(el => el.event_id == event_id).marker.setLabel(null);
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

function previewMarker(lat, lng) {
    
    let latLng = new google.maps.LatLng(lat, lng);
    if (previewmarker == null) {
        const icon = {
            url: "https://i.pinimg.com/originals/25/62/aa/2562aacd1a4c2af60cce9629b1e05cf2.png",
            scaledSize: new google.maps.Size(35, 35), // scaled size
            origin: new google.maps.Point(0, 0), // origin
            anchor: new google.maps.Point(17.5, 35) // anchor
        };
        previewmarker = new google.maps.Marker({
            position: latLng,
            map: mapPreview,
            icon: icon
        });
    }
    else {
        previewmarker.setPosition(latLng);
    }
    mapPreview.panTo(previewmarker.getPosition());
    
}