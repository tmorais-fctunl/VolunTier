//VARS:
var route_permissions = false;
//COMMENTS:
var route_comments_cursor;
var route_commentsCursors = [];
var route_isEditing = false;
let route_prevSectionContent = '';
var route_isDeleting = false;
//ROUTE:
var permissions = false;
//PARTICIPANTS:
var routeParticipant_cursor = 0;
var routeParticipantCursors = [];
var route_num_participants = 0;

//ROUTE GET, LOAD AND FILL
function createRouteRequest() {
    if (!tryAuthentication())
        return false;
    var urlvariable = "/rest/route/create";
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    let events = [];
    $("#createRouteForm #events a").each(function () {
        events.push('' + $(this).html());
    });
    let eventsJson = "[";
    for (var i = 0; i < events.length; i++) {
        eventsJson = eventsJson.concat('"' + events[i] + '"');
        if (i + 1 < events.length)
            eventsJson = eventsJson.concat(",");
    }
    eventsJson = eventsJson.concat("]");
    
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_name": "' + $("#routeName").val() +
        '", "description": "' + $("#routeDesc").val() +
        '", "event_ids": '+ eventsJson +
       
        '}';
    console.log(ItemJSON);
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //ADD ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            const obj = JSON.parse(xmlhttp.responseText);
            alert("Create event: SUCCESS. Id: " + obj.route_id);
            // localStorage.setItem(obj.event_id, ItemJSON);
            $('span#disableCreate').trigger('click');
            loadRoute(obj.route_id, true);
            markers.forEach(function (el) {
                if (el.visibility == "PRIVATE")
                    el.marker.setVisible(true);
            });
            return false;

        }
        else {
            alert("Create route: UNSUCCESS");
            if (xmlhttp.status == 403) {
                alert(xmlhttp.responseText);
                return;
            }
            return false;
        }
    }
    xmlhttp.send(ItemJSON);
}


function loadRoute(id, createInMap) {
    $("body").css("cursor", "progress");
    getRoute(id, function (data) {
        if (!data) {
            alert("Could not load route");
        }
        else {
            loadRouteTab(id);
            fillRouteAttributes(data);
            if (createInMap)
                loadRouteMiniature(data);
        }
        $("body").css("cursor", "default");
    });
    return false;
}

function getRoute(routeID, callback) {

    var urlvariable = "/rest/route/data";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + routeID +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load route info, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if (tryAuthentication())
                    getRoute(routeID, callback);
                return;
            }
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        callback(attributes);

    };
    xmlhttp.send(ItemJSON);
}

function fillRouteAttributes(attributes) {

    document.getElementById("route_id").innerHTML = attributes.route_id;
    document.getElementById("route_name").innerHTML = attributes.route_name;
    document.getElementById("route_joined_capacity").innerHTML = attributes.num_participants;
    route_num_participants = attributes.num_participants;
    document.getElementById("route_description").innerHTML = attributes.description;
    //RATING:
    let rating = attributes.avg_rating;
    $("#route_rating").html(rating);
    if (rating <= 0.5) {
        $("#route_rating").css("color", "red");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
    }
    else if (rating <= 1.5) {
        $("#route_rating").css("color", "#FF4D00");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
    }
    else if (rating < 2.5) {
        $("#route_rating").css("color", "orange");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');

    }
    else if (rating < 3.5) {
        $("#route_rating").css("color", "yellow");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
    }
    else if (rating < 4.5) {
        $("#route_rating").css("color", "lightgreen");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:white; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
    }
    else if (rating < 5) {
        $("#route_rating").css("color", "green");
        $("#route_rating").append('<br>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
        $("#route_rating").append('<i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i>');
    }
    let myRating = attributes.my_rating;
    for (var x = 1; x <= 5; x++)
        $("#star" + x).attr("onclick", "");
    switch (myRating) {
        case 1.0:
            $("#star1").trigger("click");
            break;
        case 2.0:
            $("#star2").trigger("click");
            break;
        case 3.0:
            $("#star3").trigger("click");
            break;
        case 4.0:
            $("#star4").trigger("click");
            break;
        case 5.0:
            $("#star5").trigger("click");
            break;
        default: break;
    }
    for (var x = 1; x <= 5; x++)
        $("#star" + x).attr("onclick", "rateRoute(event)");

    //EDIT:
    if (attributes.status == "OWNER")
        document.getElementById("editRouteBtn").style.display = "block";
    else
        document.getElementById("editRouteBtn").style.display = "none";


    
    //Remove previous directions and add new on preview of route
    let events = attributes.events;
    var directions = [];;
    for (var j = 0; j < events.length; j++) {
        directions.push(events[j].event_id);
        
    }
    showRouteDirectionsPreview(directions);
    /*
    //Address
    getReverseGeocodingData(location.lat, location.lng, function (address) {
        document.getElementById("route_address").innerHTML = address;
    });

    */
    //handle the join/ask to join button:
    handleRouteMainButton(attributes.status);

    //date:
    var createdOn = new Date(attributes.creation_date);
    let hour = ("0" + createdOn.getHours()).slice(-2);
    let min = ("0" + createdOn.getMinutes()).slice(-2)
    createdOn = createdOn.getDate() + "/" + (parseInt(createdOn.getMonth()) + 1) + "/" + createdOn.getFullYear() + " " + hour + ":" + min;
    document.getElementById("route_created").innerHTML = "Created on: " + createdOn;

    //Participants:
    handleRouteParticipants();

    //Comments:
    if (attributes.status == "CREATOR" || attributes.status == "MOD")
        route_permissions = true;
    else
        route_permissions = false;
    //console.log("Are you allowed to comment: " + (attributes.status == "PARTICIPANT" || attributes.status == "OWNER"));
    route_newCommentVerification(attributes.status == "PARTICIPANT" || attributes.status == "CREATOR" || attributes.status == "MOD");
    route_handleComments();
    if (attributes.status == "NON_PARTICIPANT")
        $("#route_rating_stars").hide();
    else
        $("#route_rating_stars").show();

    //Photos:

    //The rest
    
    /*
    */
    //THE EVENTS AND THEIR POINTS:
    fillRouteEvents(events);

}

//EVENTS:
function fillRouteEvents(events) {
    let route_events_section = $("#Route #route_events");
    route_events_section.empty();
    var content;
    for (var i = 0; i < events.length; i++) {
        content = '<div class="" style="text-align:center; width:100%">' +
            '<a href="" id="route_event_' + events[i].event_id + '" onclick="return loadEvent(\'' + events[i].event_id + '\', false)">' + String.fromCharCode(65 + i) + ": " + events[i].name + '</a></div>';
        route_events_section.append(content);
    }
}

//ROUTE BUTTON
function handleRouteMainButton(status) {
    let button = $("#Route #joinBtn");
    button.attr("disabled", false);
    button.unbind("mouseenter mouseleave");
    button.css({
        width: "",
        height: ""
    });
    console.log("In button function");
    if (status == "CREATOR") {
        button.hide();
        return;
    }
    button.show();
    if (status == "PARTICIPANT") {
        button.css("background-color", "#eead2d");
        button.html("Leave");
        button.attr("onclick", "leaveRoute()");
        return;
    }
    if (status == "NON_PARTICIPANT") {
        button.css("background-color", "#0aa4ec");
        button.html("Join");
        button.attr("onclick", "joinRoute()");
        return;
    }
    return;
}

function leaveRoute() {
    //Http request:

    let route_id = document.getElementById("route_id").innerHTML;
    var urlvariable = "/rest/route/removeParticipant";
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id +
        '", "participant": "' + userId +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LEAVE ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't leave route");
            return;
        }
        //Success:
        let button = $("#Route #joinBtn");
        button.css("background-color", "#0aa4ec");
        button.html("Join");
        button.attr("onclick", "joinRoute()");
        route_num_participants--;
        handleRouteParticipants();
        route_handleComments();
        route_newCommentVerification(false);
        document.getElementById("route_joined_capacity").innerHTML = route_num_participants;
        $("#route_rating_stars").hide();
        return;
    }
    xmlhttp.send(ItemJSON);
}

function joinRoute() {
    //Http request:
    let route_id = document.getElementById("route_id").innerHTML;
    var urlvariable = "/rest/route/participate"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id +
        '"}';
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Participate ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't join route")
            if (xmlhttp.status == 403) {
                alert("You can't join the route as some of it's events are either private (and you don't participante in them) or they're full");
                return;
            }
            return;
        }
        let button = $("#Route #joinBtn");
            button.css("background-color", "#eead2d");
            button.html("Leave");
            button.attr("onclick", "leaveRoute()");
            route_num_participants++;
            handleRouteParticipants();
            route_handleComments();
            route_newCommentVerification(true);
            document.getElementById("route_joined_capacity").innerHTML = route_num_participants;
            $("#route_rating_stars").show();
        return;
    }
    xmlhttp.send(ItemJSON);
}

//ROUTE PARTICIPANTS
function removeRouteParticipant(username) {
    let route_id = document.getElementById("route_id").innerHTML;
    var urlvariable = "/rest/removeParticipant"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id +
        '", "participant": "' + username +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LEAVE ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't remove user from route");
            return;
        }
        //Success:


    }
    xmlhttp.send(ItemJSON);

}

function handleRouteParticipants() {
    let route_id = document.getElementById("route_id").innerHTML;
    let participantElement = $("#route_participants");
    participantElement.empty();
    routeParticipant_cursor = 0;
    routeParticipantCursors = [];
    routeParticipantCursors.push(0);
    fillRouteParticipants(route_id, routeParticipant_cursor);
}

//next is a boolean that when true loads the next participants, when false the previous. for the route
function roamParticipantsRoute(next) {
    let route_id = document.getElementById("route_id").innerHTML;


    if (next && (routeParticipant_cursor + 1 < routeParticipantCursors.length)) {
        $("#route_participants_page_" + routeParticipant_cursor).hide();
        routeParticipant_cursor++;
        fillRouteParticipants(route_id, routeParticipantCursors[routeParticipant_cursor]);
    }
    else if (!next && ((routeParticipant_cursor - 1) >= 0)) {
        $("#route_participants_page_" + routeParticipant_cursor).hide();
        routeParticipant_cursor--;
        fillRouteParticipants(route_id, routeParticipantCursors[routeParticipant_cursor]);
    }
}

function fillRouteParticipants(route_id, cursor) {

    let participantElement = $("#route_participants");
    //participantElement.empty();

    //if they are loaded:
    if ($("#route_participants_page_" + routeParticipant_cursor).length) {
        console.log("Page Showing: " + routeParticipant_cursor);
        $("#route_participants_page_" + routeParticipant_cursor).show();
        return;
    }
    //else...
    //If the participants arent loaded:

    var urlvariable = "/rest/route/participants";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET Participants ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id;
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load route participants, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if (tryAuthentication())
                    fillRouteParticipants(route_id, cursor);
                return;
            }
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);

        //Fill and update the participants
        let participants = attributes.participants;
        let content = '<div id="route_participants_page_' + routeParticipant_cursor + '" style="display:inline-block; margin-left: 5px">';
        console.log("Participants page: " + routeParticipant_cursor + " with " + participants.length + " participants");
        for (i = 0; i < participants.length; i++) {
            content = content.concat('<span style="display:none">' + participants[i].email + '</span>');
            if (participants[i].pic != '')
                content = content.concat('<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">');
            if (participants[i].role == "CREATOR") {
                let creator = $("#route_creator");
                creator.attr("onclick", "return loadUser(\'" + participants[i].email + "\')")
                creator.html(participants[i].username);
                creator.css("color", "green");

                content = content.concat('<span style="display:inline-block; margin-left:5px"><i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');
            }
            else if (participants[i].role == "MOD")
                content = content.concat('<span style="display:inline-block; margin-left:5px"><i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-user-secret" aria-hidden="true"></i><a style="color: blue; margin-left:4px" href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');
            else
                content = content.concat('<span style="display:inline-block; margin-left:5px"><a href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');


        }
        content = content.concat('</div>');
        participantElement.append(content);
        let results = attributes.results;
        if (results != "NO_MORE_RESULTS")
            routeParticipantCursors.push(attributes.cursor);
    };
    xmlhttp.send(ItemJSON);




}

//ROUTE COMMENTS
//Handle comments loads or deloads the comments based on user participation in route, and also enables/disables the comment box
function route_handleComments() {
    let route_id = document.getElementById("route_id").innerHTML;
    let commentElement = $("#route_comments");
    commentElement.empty();
    route_comments_cursor = 0;
    route_commentsCursors = [];
    route_commentsCursors.push(0);
    fillRouteComments(route_id, route_comments_cursor)
}

function route_newCommentVerification(canShow) {
    let new_comment_section = $("#route_new_comment_section");
    if (canShow) {
        console.log("Showing comment box");
        new_comment_section.html(
            '<h6 class="mb-0" style="width:100%; text-align:center">Write a Comment</h6>' +
            '<textarea onkeyup="countChar(this, \'charNumCommentRoute\')" style="vertical-align: top; max-width:94%; min-width:94%; margin-left:3%; margin-right:3%; margin-top:4px;" id="newRouteComment" name="Your comment" placeholder="Remember all comments are subject to review" rows="4" cols="50" class="createEventFormInput"></textarea>' +
            '<div id="charNumCommentRoute" style="margin-left: 95%; font-size: 80%">500</div>' +
            '<button style="display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="submitCommentBtn" onclick="route_submitComment()" disabled="disabled" class="btn btn-primary">Submit</button>'
        );
    }
    else {
        console.log("Hide comment box");
        new_comment_section.html('<h6 class="mb-0" style="width:100%; text-align:center">You can not comment as you are not a participant.</h6>');
    }
}

function route_roamComments(next) {
    console.log("Roam activated, route_commentsCursors length: " + route_commentsCursors.length);
    let route_id = document.getElementById("route_id").innerHTML;
    console.log("before cursor: " + route_comments_cursor);

    if (next && (route_comments_cursor + 1 < route_commentsCursors.length)) {
        route_comments_cursor++;
        fillRouteComments(route_id, route_commentsCursors[route_comments_cursor]);
    }

}

function fillRouteComments(route_id, cursor) {
    console.log("activated fill with cursor: " + cursor + " 'page' " + route_comments_cursor);
    let commentElement = $("#route_comments");
    //commentElement.empty();

    var urlvariable = "/rest/getChat";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET COMMENTS ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id +
        '", "latest_first": "' + "true";
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load route comments, message: " + xmlhttp.status);
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);

        //Fill and update the comments
        let comments = attributes.comments;
        let results = attributes.results;
        var timestamp;

        if (results != "NO_MORE_RESULTS")
            route_commentsCursors.push(attributes.cursor);

        console.log("I have acquired the comments, and the number of comments is: " + comments.length);
        console.log("Next cursor is: " + attributes.cursor);
        if (comments.length == 0) {
            route_roamComments(true);
            return false;
        }

        let deleteEditButtons = '';
        let likes = '';
        for (i = 0; i < comments.length; i++) {
            timestamp = new Date(comments[i].timestamp);
            timestamp = timestamp.getDate() + "/" + timestamp.getMonth() + "/" + timestamp.getFullYear() + " " + timestamp.getHours() + ":" + timestamp.getMinutes();

            //Check for user and moderators
            if (comments[i].email == userId)
                deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return route_deleteComment(\'' + comments[i].comment_id + '\')">' + "Delete" + '</a></span>' +
                    '<span style="margin-left:5px; display:inline-block"><a href="" style="color:blue; font-size: 0.7em" onclick="return route_editComment(\'' + comments[i].comment_id + '\')">' + "Edit" + '</a></span>';
            else if (route_permissions)
                deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return route_deleteComment(\'' + comments[i].comment_id + '\')">' + "Delete" + '</a></span>';
            if (comments[i].like_status)
                likes = '<span id="route_likes" style="margin-left:5px; color:blue"><i style="color:green" onclick="return route_likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + comments[i].likes + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'
            else
                likes = '<span id="route_likes" style="margin-left:5px; color:blue"><i style="color:blue" onclick="return route_likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + comments[i].likes + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'



            commentElement.append(
                '<div id="comment_' + comments[i].comment_id + '">' +
                '<div class="row" style="margin-top:10px">' +
                '<p id="route_user_email" style="display:none">' + comments[i].email + '</p>' +
                '<div class="col-sm-6">' +
                //'<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20">' +
                '<span id="route_user" style="margin-left:5px"><a href="" onclick="return loadUser(\'' + comments[i].email + '\')">' + comments[i].username + '</a><span>' +
                '<span id="route_timestamp" style="color: lightgray; display: inline; margin-left:5px">' + timestamp + '</span>' +
                '</div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-sm-12" id="route_comment_section">' +
                '<span  id="route_comment" style="margin-left:5px; display:inline-block; color: black; text-align: justify; text-justify: inter-word;">' + comments[i].comment + '</span>' +
                '</div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-sm-12">' +
                likes +
                '</div>' +
                '</div>' +
                '</div>'
            );

        }

    };
    xmlhttp.send(ItemJSON);




}

function route_likeDislikeComment(id) {
    let route_id = document.getElementById("route_id").innerHTML;
    let like = "#Route #comment_" + id + " #route_likes";
    var urlvariable = "/rest/likeComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LIKE COMMENT ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + route_id +
        '", "comment_id": "' + id + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't toggle comment like, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if (tryAuthentication())
                    likeDislikeComment(id);
                return;
            }
            return false;
        }
        //Success
        //If like, change color to green and increase likes by 1
        //Else change color to blue and decrease likes by 1
        if ($(like + " i").css("color") == "rgb(0, 0, 255)") { ///(0, 0, 255) == blue
            $(like + " i").css("color", "green");
            let number = parseInt($(like + " #nLikes").html());
            number = number + 1;
            $(like + " #nLikes").html(number);
            return false;
        }
        else {
            $(like + " i").css("color", "blue");
            let number = parseInt($(like + " #nLikes").html());
            number = number - 1;
            $(like + " #nLikes").html(number);
            return false;
        }
        //console.log($(like + " i").css("color"));
    }
    xmlhttp.send(ItemJSON);

}

function route_submitComment() {
    let commentElement = document.getElementById("newRouteComment");
    let comment = document.getElementById("newRouteComment").value;
    commentElement.value = '';
    var urlvariable = "/rest/postComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //POST COMMENT ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + document.getElementById("route_id").innerHTML +
        '", "comment": "' + comment + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load route participants, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if (tryAuthentication())
                    submitComment();
                return;
            }
            return false;
        }
        //success, load comment at top of comments:
        let comment_id = JSON.parse(xmlhttp.response).comment_id;
        let comments = document.getElementById("route_comments").innerHTML;
        $("#route_comments").empty();
        let deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return route_deleteComment(\'' + comment_id + '\')">' + "Delete" + '</a></span>' +
            '<span style="margin-left:5px; display:inline-block"><a href="" style="color:blue; font-size: 0.7em" onclick="return route_editComment(\'' + comment_id + '\')">' + "Edit" + '</a></span>';
        let commentElement =
            '<div id="comment_' + comment_id + '">' +
            '<div class="row" style="margin-top:10px">' +
            '<p style="display:none">' + userId + '</p>' +
            '<div class="col-sm-6">' +
            //'<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20">' +
            '<span id="route_user" style="margin-left:5px"><a href="">' + document.getElementById('user_tag').innerHTML + '</a><span>' +
            '<span id="route_timestamp" style="color: lightgray; display: inline; margin-left:5px">' + 'Just now' + '</span>' +
            '</div>' +
            '</div>' +
            '<div class="row">' +
            '<div class="col-sm-12" id="route_comment_section">' +
            '<span id="route_comment" style="margin-left:5px; display:inline-block; color: black; text-align: justify; text-justify: inter-word;">' + comment + '</span>' +
            '</div>' +
            '</div>' +
            '<div class="row">' +
            '<div class="col-sm-12">' +
            '<span id="route_likes" style="margin-left:5px; color:blue"><i style="color:blue" onclick="return route_likeDislikeComment(\'' + comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + "0" + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>' +

            '</div>' +
            '</div>' +
            '</div>';









        $("#route_comments").append(commentElement);
        $("#route_comments").append(comments);
        countChar(document.getElementById("newRouteComment"), "charNumCommentRoute");

    }
    xmlhttp.send(ItemJSON);



}

function route_deleteComment(comment_id) {
    if (route_isDeleting) {
        alert("Finish deleting the other comment first");
        return false;
    }
    if (route_isEditing) {
        alert("Finish your editing on the other comment first");
        return false;
    }
    route_isDeleting = true;

    let commentId = "#Route #comment_" + comment_id;
    let section = $(commentId + " #route_comment_section");
    route_prevSectionContent = section.html();
    let prevComment = $(commentId + " #route_comment").html();

    section.html('<h6 class="mb-0" style="width:100%; text-align:center">Are you sure?</h6>' +
        '<div style="margin: auto; text-align:center">' +
        '<button style="color: white;background-color: red; display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="confirmDeleteBtn" onclick="route_confirmDeleteComment(\'' + comment_id + '\', true)" class="btn btn-primary">Confirm</button>' +
        '<button style="color: white;background-color: blue; display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="confirmDeleteBtn" onclick="route_confirmDeleteComment(\'' + comment_id + '\', false)" class="btn btn-primary">Cancel</button>' +
        '</div>'
    );





    return false;
}

function route_confirmDeleteComment(comment_id, confirm) {
    let commentId = "#Route #comment_" + comment_id;
    if (!confirm) {
        //cancel

        let section = $(commentId + " #route_comment_section");
        section.html(route_prevSectionContent);


    }
    else {
        //delete
        var urlvariable = "/rest/deleteComment";
        var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //delete comment REST URL
        var xmlhttp = new XMLHttpRequest();
        var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
        var ItemJSON = '{"email": "' + userId +
            '", "token": "' + token +
            '", "route_id": "' + document.getElementById("route_id").innerHTML +
            '", "comment_id": "' + comment_id +
            '"}';
        xmlhttp.open("POST", URL, true);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        xmlhttp.onload = function (oEvent) {
            if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
                alert("Couldn't delete comment, message: " + xmlhttp.status);
                route_isDeleting = false;
                if (xmlhttp.status == 403) {
                    if (tryAuthentication())
                        route_confirmDeleteComment(comment_id, confirm);
                    return;
                }
                return false;
            }
            $(commentId).remove();
        };
        xmlhttp.send(ItemJSON);
    }
    route_isDeleting = false;
    return false;
}

function route_editComment(comment_id) {
    if (route_isEditing) {
        alert("Finish your editing on the other comment first");
        return false;
    }
    if (route_isDeleting) {
        alert("Finish deleting the other comment first");
        return false;
    }

    route_isEditing = true;
    let commentId = "#Route #comment_" + comment_id;
    let section = $(commentId + " #route_comment_section");
    route_prevSectionContent = section.html();

    let prevComment = $(commentId + " #route_comment").html();


    section.html(
        '<textarea onkeyup="countChar(this, \'CommentCharNumRoute\')" style="vertical-align: top; max-width:94%; min-width:94%; margin-left:3%; margin-right:3%; margin-top:4px;" id="editRouteComment" name="Your comment" placeholder="Remember all comments are subject to review" rows="4" cols="50" class="createEventFormInput"></textarea>' +
        '<div id="CommentCharNumRoute" style="margin-left: 95%; font-size: 80%">500</div>' +
        '<button style="display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="route_submitEditBtn" onclick="route_submitEdit(\'' + comment_id + '\')" disabled="disabled" class="btn btn-primary">Submit</button>'
    );
    $(commentId + " #editRouteComment").val(prevComment);
    $(commentId + " #delEditBtnsDiv").hide();

    $('body').on("keyup", '#editRouteComment', function () {
        if ($(this).val() != "") $('#route_submitEditBtn').removeAttr('disabled');
        else $('#route_submitEditBtn').prop("disabled", true);
    });
    $('body , #editRouteComment').trigger("keyup");
    return false;
}

function route_submitEdit(comment_id) {
    let newComment = $("#Route #comment_" + comment_id + " #editRouteComment").val();

    let section = $("#Route #comment_" + comment_id + " #route_comment_section");
    var urlvariable = "/rest/updateComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Update comment REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + document.getElementById("route_id").innerHTML +
        '", "comment_id": "' + comment_id +
        '", "comment": "' + newComment +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't edit comment, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if (tryAuthentication())
                    route_submitEdit(comment_id);
                return;
            }
            return false;
        }
        let newComment = $("#Route #comment_" + comment_id + " #editRouteComment").val();
        section.html(route_prevSectionContent);
        $("#Route #comment_" + comment_id + " #route_comment").html(newComment);
        $("#Route #comment_" + comment_id + " #delEditBtnsDiv").show();
        route_isEditing = false;

    };
    xmlhttp.send(ItemJSON);
}

//RATE:
function rateRoute(event) {
    //console.log(event.target.value);
    let routeID = document.getElementById("route_id").innerHTML;
    var urlvariable = "/rest/route/rate";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //RATE ROUTE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "route_id": "' + routeID +
        '", "rating": "' + event.target.value +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't load route info, message: " + xmlhttp.status);
            return false;
        }
        //Success:
        console.log("Rating successfuly submited :)");
    }
    xmlhttp.send(ItemJSON);
}