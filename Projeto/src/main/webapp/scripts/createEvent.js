//EVENT:
var permissions = false;
var myRole = '';

//COMMENTS:
var comments_cursor;
var commentsCursors = [];
var isEditing = false;
let prevSectionContent = '';
var isDeleting = false;

//PARTICIPANTS:
var participant_cursor;
var participantCursors = [];
var event_capacity = 0;
var event_num_participants = 0;
//EDIT:
var EventVisibility = false;
var eventInfoElements = [];
var eventInfoElementsRef = $("#Event .info");
updateEventInfoElements();

//images:
var filedata = [];
var prevPics = [];

//GET, LOAD AND FILL EVENT:
function countChar(val, id) {
    var len = val.value.length;
    if (len > 500) {
        val.value = val.value.substring(0, 500);
    } else {
        $('#' + id).text(500 - len);
    }
};

//Talvez array com comentarios e participantes para quando o user der previous ou next nao pedir novamente ao server
function createEventRequest() {
    if (!tryAuthentication)
        return false;

//Just check if the date-time are valid
  var now = new Date();
  now.setTime(now.getTime()-now.getTimezoneOffset()*60*1000);
  minDate = now.toISOString().substring(0,10);
  console.log(minDate);
  $('#evDate').prop('min', minDate);
  minTime = now.toISOString().substring(11,16);
  console.log(minTime);
  $('#evTime').prop('min', minTime);

  DateTimeListenerFunction();

    if (!allFormInputsValid()) {
    alert("Can not create the event until all conditions are met.");
    return false;
  }

  var urlvariable = "/rest/addEvent";
  var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
  var location = $("#evLoc").val().split(",");
  var now = new Date($("#evDate").val()+"T"+$("#evTime").val());
  var end = new Date($("#evDateEnd").val()+"T"+$("#evTimeEnd").val());
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_name": "' + $("#evName").val() +
        '", "description": "' + $("#evDesc").val() +
        '", "location": ["' + location[0] + '","' + location[1] + '"]' +
        ', "start_date": "' + now.toISOString() +
        '", "end_date": "' + end.toISOString() +
        '", "category": "' + $("#eventCategory").val() +
        '", "profile": "' + $("#eventPrivacyLock").val() +
        '", "difficulty": "' + $("#evDificulty").val() +
  '"}';
  console.log(ItemJSON);
  var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //ADD EVENT REST URL
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open("POST", URL, true);
  xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            const obj = JSON.parse(xmlhttp.responseText);
            alert("Create event: SUCCESS. Id: " + obj.event_id);
            // localStorage.setItem(obj.event_id, ItemJSON);
            $('span#disableCreate').trigger('click');
            loadEvent(obj.event_id, true);
            return false;

        }
        else {
            alert("Create event: UNSUCCESS");
            if (xmlhttp.status == 429) {
                alert("You have reached your daily limit of 4 events. Wait 24h to create more.");
                return;
            }
            return false;
        }
    }
    xmlhttp.send(ItemJSON);
}

function getEvent(eventID, callback) {

    var urlvariable = "/rest/getEvent";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + eventID +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load event info, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                getEvent(eventID, callback);
                return;
            }
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        callback(attributes);

    };
    xmlhttp.send(ItemJSON);
}

//CreateInMap is a flag to determine if it should create the marker in the maps window
function loadEvent(eventID, createInMap) {
    $("body").css("cursor", "progress");
    getEvent(eventID, function (data) {
        if (!data) {
            alert("Could not load event");
        }
        else {
            loadEventTab(eventID);
            fillEventAttributes(data);

            if (createInMap)
                loadEventMiniature(data);

        }
        $("body").css("cursor", "default");
    });
    return false;
}

function fillEventAttributes(attributes) {

    document.getElementById("event_id").innerHTML = attributes.event_id;
    document.getElementById("event_name").innerHTML = attributes.name;
    document.getElementById("event_category").innerHTML = getCategory(attributes.category);
    document.getElementById("event_categoryRaw").innerHTML = attributes.category;
    document.getElementById("event_profile").innerHTML = attributes.profile;
    document.getElementById("event_joined_capacity").innerHTML = attributes.num_participants + "/" + attributes.capacity;
    let dificulty = attributes.difficulty;
    $("#event_dificulty").html(dificulty);
    switch (dificulty) {
        case 1: $("#event_dificulty").css("color", "green"); break;
        case 2: $("#event_dificulty").css("color", "lightgreen"); break;
        case 3: $("#event_dificulty").css("color", "yellow"); break;
        case 4: $("#event_dificulty").css("color", "orange"); break;
        case 5: $("#event_dificulty").css("color", "red"); break;
    }

    if (attributes.status == "OWNER") {
        document.getElementById("editEventBtn").style.display = "block";
        $("#presenceEventCode").show();
        $("#qrcode_section").show();
        $("#leaveEventCode").show();
        fillCodes();
        
    }
    else {
        document.getElementById("editEventBtn").style.display = "none";
        $("#presenceEventCode").hide();
        $("#leaveEventCode").hide();
        $("#qrcode_section").hide();
        $("#presenceEventCode").attr("src", "");
        $("#leaveEventCode").attr("src", "");
    }

    event_capacity = attributes.capacity;
    event_num_participants = attributes.num_participants;

    
    //Remove previous markers and add marker on preview of event
    let location = {
        lat: attributes.location[0],
        lng: attributes.location[1]
    }
    previewMarker(location.lat, location.lng);


    //Address
    getReverseGeocodingData(location.lat, location.lng, function (address) {
        document.getElementById("event_address").innerHTML = address;
    });

    //handle the join/ask to join button:
    handleEventMainButton(attributes.status, attributes.profile, attributes.num_participants, attributes.capacity);


    //date:
    var start = new Date(attributes.start_date);
    var end = new Date(attributes.end_date);
    document.getElementById("event_start_raw").innerHTML = start.toISOString();
    document.getElementById("event_end_raw").innerHTML = end.toISOString();
    var hour, min;
    hour = ("0" + start.getHours()).slice(-2);
    min = ("0" + start.getMinutes()).slice(-2)
    //start.setTime(start.getTime() + start.getTimezoneOffset() * 60 * 1000);
    //end.setTime(end.getTime() + end.getTimezoneOffset() * 60 * 1000);
    var createdOn = new Date(attributes.creation_date);
    start = start.getDate() + "/" + (parseInt(start.getMonth()) + 1) + "/" + start.getFullYear() + " " + hour + ":" + min;
    hour = ("0" + end.getHours()).slice(-2);
    min = ("0" + end.getMinutes()).slice(-2)
    end = end.getDate() + "/" + (parseInt(end.getMonth()) + 1) + "/" + end.getFullYear() + " " + hour + ":" + min;
    hour = ("0" + createdOn.getHours()).slice(-2);
    min = ("0" + createdOn.getMinutes()).slice(-2)
    createdOn = createdOn.getDate() + "/" + (parseInt(createdOn.getMonth())+1) + "/" + createdOn.getFullYear() + " " + hour + ":" + min;
    document.getElementById("event_created").innerHTML = "Created on: " + createdOn;
    document.getElementById("event_start").innerHTML = "Starts on: " + start;
    document.getElementById("event_end").innerHTML = "Ends on: " + end;

    //Description
    document.getElementById("event_description").innerHTML = attributes.description;
    //Contact
    if (attributes.contact == "")
        document.getElementById("event_contact").innerHTML = "Not available";
    else
        document.getElementById("event_contact").innerHTML = attributes.contact;

    //Websites:
    fillRefs("event_website", attributes.website);
    fillRefs("event_facebook", attributes.facebook);
    fillRefs("event_twitter", attributes.twitter);
    fillRefs("event_instagram", attributes.instagram);


    //Participants:
    handleParticipants();

    //Comments:
    if (attributes.status == "OWNER" || attributes.status == "MOD")
        permissions = true;
    else
        permissions = false;
    myRole = attributes.status;
    console.log("Are you allowed to comment: " + (attributes.status == "PARTICIPANT" || attributes.status == "OWNER"));
    newCommentVerification(attributes.status == "PARTICIPANT" || attributes.status == "OWNER" || attributes.status == "MOD");
    handleComments();
   
    //Photos:
    filedata = [];
    let pics = attributes.pics;
    //for main:
    /*if (pics.length>0)
        requestEventPictureGCS(pics[0].dwld_url.url, "EventImage");*/

    for (var j = 0; j < 7; j++) {
        let eventpic = $("#event_image_" + j);
        eventpic.css("display", "none");
        eventpic.attr("src", "");

    }
    for (var j = 1; j < 7; j++) {
        let eventpic = $("#event_image_" + j + "_preview");
        eventpic.css("display", "none");
        eventpic.attr("src", "");
    }

    for (var j = 0; j < pics.length; j++) {
        //Add img to the container
        //Update that img with the src
        //Only add as there are pics.
        requestEventPictureGCS(pics[j].dwld_url.url, pics[j].pic_id);
    }

    if (pics.length == 0 || (pics.length == 1 && pics[0].pic_id==0)) {
        $("#Event .GaleryContainer .caption-container").hide();
    }
    else {
        $("#Event .GaleryContainer .caption-container").show();
    }

    
    //The rest
 


}

//EVENT BUTTON ACTION
/*
*  When an event is loaded the Join/Ask to join button suffers changes:
*  if the event is owned by the user, change the button to "Delete" with red button color
*  if the user participates, change the button to "Leave" with toasted yellow button color. If clicked decrease participant count (Ignore participants row for now)
*  if the user doesnt participate, change the button to Join keeping the blue color. Can be disabled if its maxed out
*  Buttons Join and Leave are interchangeable as the user joins or leaves an event
*  
*  If the event is private the button changes to "Ask to join", keeping the blue color.
*  If the user presses it, change it and keep it as "Awaiting aprovance", which when hovered can display "Cancel"
*/
function handleEventMainButton(status, profile, num_participants, capacity) {
    let button = $("#Event #joinBtn");
    button.attr("disabled", false);
    button.unbind("mouseenter mouseleave");
    button.css({
        width: "",
        height: ""
    });

    console.log("In button function");

    //Change to delete if owner:
    if (status == "OWNER") {
        button.css("background-color", "red");
        button.html("Delete");
        button.attr("onclick", "deleteEvent()");
        //change onclick to delete
        return;
    }

    //Check if private
    if (profile == "PRIVATE") {
        if (status == "PARTICIPANT") {
            button.css("background-color", "#eead2d");
            button.html("Leave");
            button.attr("onclick", "leaveEvent(false)");
            return;
        }
        if (status == "NON_PARTICIPANT") {
            button.css("background-color", "#0aa4ec");
            button.html("Ask to join");
            button.attr("onclick", "joinEvent(false)");
            if (num_participants == capacity)
                button.attr("disabled", "disabled");
            return;
        }
        if (status == "PENDING") {
            button.html("Awaiting approvance");
            button.attr("onclick", "cancelEventJoin(false)");
            button.css("background-color", "#0aa4ec");
           
            button.mouseenter(function () {
                $(this).css({
                    width: $(this).outerWidth(),
                    height: $(this).outerHeight()
                });
                $(this).html("Cancel");
                button.css("background-color", "#999999");
            }).mouseleave(function () {
                $(this).html("Awaiting approvance");
                button.css("background-color", "#0aa4ec");
            });
            return;
        }
    }
    else {    
        if (status == "PARTICIPANT") {
            button.css("background-color", "#eead2d");
            button.html("Leave");
            button.attr("onclick", "leaveEvent(true)");
            return;
        }
        if (status == "NON_PARTICIPANT") {
            button.css("background-color", "#0aa4ec");
            button.html("Join");
            button.attr("onclick", "joinEvent(true)");
            if (num_participants == capacity)
                button.attr("disabled", "disabled");
            return;
        }
    }

    return;
}

function deleteEvent() {
    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/updateEvent/remove"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");  
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '"}';

        
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //DELETE EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't delete event");

            return;
        }
        closeEventTab();
        //Mais alguma coisa...?
        //Delete da lista de eventos e do mapa
        let btnref = "#eventsectionid_" + event_id + " #gotobutton";
        console.log(btnref);
        let fnc = $("#eventsectionid_" + event_id + " #gotobutton").attr("onclick");
        let str = fnc.split("goToEvent(")[1];
        console.log(str);
        let markerId = str.charAt(1);
        hideMarker(markerId);
        $("#eventsectionid_" + event_id).remove();
    }
    xmlhttp.send(ItemJSON);


}
//public true means the event is public, false private.
function leaveEvent(public) {
    //Http request:

    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/removeParticipant"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "participant": "' + userId +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LEAVE EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't leave event");
            return;
        }
        //Success:
        let button = $("#joinBtn");
        button.css("background-color", "#0aa4ec");
        if (public) {
            button.html("Join");
            button.attr("onclick", "joinEvent(true)");
        }
        else {
            button.html("Ask to join");
            button.attr("onclick", "joinEvent(false)");
        }
        event_num_participants--;
        handleParticipants();
        handleComments();
        newCommentVerification(false);
        document.getElementById("event_joined_capacity").innerHTML = event_num_participants + "/" + event_capacity;

        return;

    }
    xmlhttp.send(ItemJSON);
}

function joinEvent(public) {
    //Http request:
    if (!tryAuthentication())
        return

    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/participateEvent"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Participate EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't join event")
            if (xmlhttp.status == 403) {
                return;
            }
            return;
        }
        let button = $("#joinBtn");
        if (public) {
            button.css("background-color", "#eead2d");
            button.html("Leave");
            button.attr("onclick", "leaveEvent(true)");
            event_num_participants++;
            handleParticipants();
            handleComments();
            newCommentVerification(true);
            document.getElementById("event_joined_capacity").innerHTML = event_num_participants + "/" + event_capacity;
        }
        else {
            button.html("Awaiting approvance");
            button.attr("onclick", "cancelEventJoin(false)");
            button.css("background-color", "#0aa4ec");
            
            button.mouseenter(function () {
                $(this).css({
                    width: $(this).outerWidth(),
                    height: $(this).outerHeight()
                });
                $(this).html("Cancel");
                $(this).css("background-color", "#999999");
            }).mouseleave(function () {
                $(this).html("Awaiting approvance");
                $(this).css("background-color", "#0aa4ec");
            });
        }
        return;

    }
    xmlhttp.send(ItemJSON);
}

function cancelEventJoin(public) {
    


    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/declineRequest"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "participant": "' + userId +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Participate EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't cancel join ask event")
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                cancelEventJoin(public);
                return;
            }
        }
        let button = $("#joinBtn");
        button.css("background-color", "#0aa4ec");
        button.unbind("mouseenter");
        button.unbind("mouseleave");
        button.css({
            width: "",
            height: ""
        });
        if (public) {
            button.html("Join");
            button.attr("onclick", "joinEvent(true)");
        }
        else {
            button.html("Ask to join");
            button.attr("onclick", "joinEvent(false)");
        }
        return;
    }
    xmlhttp.send(ItemJSON);



    


}

//PARTICIPANTS
function removeParticipant(username) {
    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/removeParticipant"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "participant": "' + username +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LEAVE EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            console.log("Couldn't remove user from event");
            return;
        }
        //Success:
        

    }
    xmlhttp.send(ItemJSON);
}

function handleParticipants() {
    let event_id = document.getElementById("event_id").innerHTML;
    let participantElement = $("#event_participants");
    participantElement.empty();
    participant_cursor = 0;
    participantCursors = [];
    participantCursors.push(0);
    fillEventParticipants(event_id, participant_cursor);
}

//next is a boolean that when true loads the next participants, when false the previous. for the event
function roamParticipants(next) {
    let event_id = document.getElementById("event_id").innerHTML;
    

    if (next && (participant_cursor + 1 <participantCursors.length)) {
        $("#event_participants_page_" + participant_cursor).hide();
        participant_cursor++;
        fillEventParticipants(event_id, participantCursors[participant_cursor]);
    }
    else if (!next && ((participant_cursor - 1) >= 0)) {
        $("#event_participants_page_" + participant_cursor).hide();
        participant_cursor--;
        fillEventParticipants(event_id, participantCursors[participant_cursor]);
    }    
}

function fillEventParticipants(event_id, cursor) {

   let participantElement = $("#event_participants");
    //participantElement.empty();

    //if they are loaded:
    if ($("#event_participants_page_" + participant_cursor).length) {
        console.log("Page Showing: " + participant_cursor);
        $("#event_participants_page_" + participant_cursor).show();
        return;
    }
    //else...
    //If the participants arent loaded:

    var urlvariable = "/rest/getParticipants";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET Participants EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id;
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load event participants, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                fillEventParticipants(event_id, cursor);
                return;
            }
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);

        //Fill and update the participants
        let participants = attributes.participants;
        let content = '<div id="event_participants_page_' + participant_cursor + '" style="display:inline-block; margin-left: 5px">';
        console.log("Participants page: " + participant_cursor + " with " + participants.length + " participants");
        for (i = 0; i < participants.length; i++) {
            content = content.concat('<div style="display: inline-block"><span style="display:none">' + participants[i].email + '</span>');
            if (participants[i].pic != '')
                content = content.concat('<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">');
            if (participants[i].role == "OWNER") {
                let creator = $("#event_creator");
                creator.attr("onclick", "return loadUser(\'" + participants[i].email + "\')")
                creator.html(participants[i].username);
                creator.css("color", "green");
                content = content.concat('<span style="display:inline-block; margin-left:5px"><i style="font-size: 0.7em; color:yellow; text-shadow: -1px 0 #000, 0 1px #000, 1px 0 #000, 0 -1px #000" class="fa fa-star" aria-hidden="true"></i><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');
            }
            else if (participants[i].role == "MOD") {
                if (myRole == "OWNER")
                    content = content.concat('<span style="display:inline-block; margin-left:5px"><i style="font-size: 0.7em; color:green;" class="fa fa-user-secret userOptions" onclick="removeMod(this, \'' + participants[i].email +'\')" aria-hidden="true"></i><a style="color: blue; margin-left:4px" href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a><i style="color:red; margin-left: 5px" onclick="removeUser(this, \''+participants[i].email+'\')" class="fa fa-times userOptions" aria-hidden="true"></i></span>');
                else
                    content = content.concat('<span style="display:inline-block; margin-left:5px"><i style="font-size: 0.7em; color:green;" class="fa fa-user-secret userOptions" aria-hidden="true"></i><a style="color: blue; margin-left:4px" href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');
            }
            else {
                if (permissions)
                    if (myRole == "OWNER")
                        content = content.concat('<span style="display:inline-block; margin-left:5px"><a href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a><i style="font-size: 0.7em; color:white; margin-left:5px" class="fa fa-user-secret userOptions" onclick="makeMod(this, \'' + participants[i].email + '\')"" aria-hidden="true"></i><i onclick="removeUser(this, \'' + participants[i].email +'\')" style="color:red; margin-left:5px" class="fa fa-times userOptions" aria-hidden="true"></i></span>');
                    else
                        content = content.concat('<span style="display:inline-block; margin-left:5px"><a href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a><i onclick="removeUser(this, \'' + participants[i].email +'\')" style="color:red; margin-left:5px" class="fa fa-times userOptions" aria-hidden="true"></i></span>');
                else
                    content = content.concat('<span style="display:inline-block; margin-left:5px"><a href="" onclick="return loadUser(\'' + participants[i].email + '\')">' + participants[i].username + '</a></span>');
            }
                

            content = content.concat(" |</div>");
        }
        content = content.concat('</div>');
        participantElement.append(content);
        let results = attributes.results;
        if (results != "NO_MORE_RESULTS")
            participantCursors.push(attributes.cursor);
    };
    xmlhttp.send(ItemJSON);




}

function makeMod(btn, user) {
    var urlvariable = "/rest/moderator/add";
    let event_id = document.getElementById("event_id").innerHTML;
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //MAKE ADMIN EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "mod": "' + user + '"}';

    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldnt make user admin");
            return false;
        }
        //Success:
        $(btn).css("color", "green");
        $(btn).attr("onclick", "removeMod(this, '" + user + "')");
    }
    xmlhttp.send(ItemJSON);

    
}

function removeMod(btn, user) {
    var urlvariable = "/rest/moderator/remove";
    let event_id = document.getElementById("event_id").innerHTML;
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //REMOVE ADMIN EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "mod": "' + user + '"}';

    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldnt remove user admin");
            return false;
        }
        //Success
        $(btn).css("color", "white");
        $(btn).attr("onclick", "makeMod(this, '" + user + "')");
    }
    xmlhttp.send(ItemJSON);
}

function removeUser(btn, user) {
    removeParticipant(user);
    $(btn).closest("div").remove()
    handleParticipants();

}

//COMMENTS
//Handle comments loads or deloads the comments based on user participation in event, and also enables/disables the comment box
function handleComments() {
    let event_id = document.getElementById("event_id").innerHTML;
    let commentElement = $("#event_comments");
    commentElement.empty();
    comments_cursor = 0;
    commentsCursors = [];
    commentsCursors.push(0);
    fillEventComments(event_id, comments_cursor)
}

function newCommentVerification(canShow) {
    let new_comment_section = $("#new_comment_section");
    if (canShow) {
        console.log("Showing comment box");
        new_comment_section.html(
            '<h6 class="mb-0" style="width:100%; text-align:center">Write a Comment</h6>' +
            '<textarea onkeyup="countChar(this, \'charNumComment\')" style="vertical-align: top; max-width:94%; min-width:94%; margin-left:3%; margin-right:3%; margin-top:4px;" id="newEventComment" name="Your comment" placeholder="Remember all comments are subject to review" rows="4" cols="50" class="createEventFormInput"></textarea>' +
            '<div id="charNumComment" style="margin-left: 95%; font-size: 80%">500</div>' +
            '<button style="display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="submitCommentBtn" onclick="submitComment()" disabled="disabled" class="btn btn-primary">Submit</button>'
        );
    }
    else {
        console.log("Hide comment box");
        new_comment_section.html('<h6 class="mb-0" style="width:100%; text-align:center">You can not comment as you are not a participant.</h6>');
    }
}

function roamComments(next) {
    console.log("Roam activated, commentsCursors length: " + commentsCursors.length);
    let event_id = document.getElementById("event_id").innerHTML;
    console.log("before cursor: " + comments_cursor);

    if (next && (comments_cursor + 1 < commentsCursors.length)) {
        comments_cursor++;
        fillEventComments(event_id, commentsCursors[comments_cursor]);
    }
    /*else if (!next && (0 <= comments_cursor - 1)) {
        comments_cursor--;
        fillEventComments(event_id, comments_cursor);
    }*/
}

function fillEventComments(event_id, cursor) {
    console.log("activated fill with cursor: " + cursor + " 'page' " + comments_cursor);
    let commentElement = $("#event_comments");
    //commentElement.empty();

    var urlvariable = "/rest/getChat";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET COMMENTS EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "latest_first": "' + "true";
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load event comments, message: " + xmlhttp.status);
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);

        //Fill and update the comments
        let comments = attributes.comments;
        let results = attributes.results;
        var timestamp;

        if (results != "NO_MORE_RESULTS")
            commentsCursors.push(attributes.cursor);

        console.log("I have acquired the comments, and the number of comments is: " + comments.length);
        console.log("Next cursor is: " + attributes.cursor);
        if (comments.length == 0) {
            roamComments(true);
            return false;
        }

        let deleteEditButtons = '';
        let likes = '';
        for (i = 0; i < comments.length; i++) {
            timestamp = new Date(comments[i].timestamp);
            timestamp = timestamp.getDate() + "/" + timestamp.getMonth() + "/" + timestamp.getFullYear() + " " + timestamp.getHours() + ":" + timestamp.getMinutes();

            //Check for user and moderators
            if (comments[i].email == userId)
                deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return deleteComment(\'' + comments[i].comment_id + '\')">' + "Delete" + '</a></span>' +
                    '<span style="margin-left:5px; display:inline-block"><a href="" style="color:blue; font-size: 0.7em" onclick="return editComment(\'' + comments[i].comment_id + '\')">' + "Edit" + '</a></span>';
            else if (permissions)
                deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return deleteComment(\'' + comments[i].comment_id + '\')">' + "Delete" + '</a></span>';
            if (comments[i].like_status)
                likes = '<span id="event_likes" style="margin-left:5px; color:blue"><i style="color:green" onclick="return likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + comments[i].likes + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'
            else
                likes = '<span id="event_likes" style="margin-left:5px; color:blue"><i style="color:blue" onclick="return likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + comments[i].likes + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'



            commentElement.append(
                '<div id="comment_' + comments[i].comment_id + '">' +
                '<div class="row" style="margin-top:10px">' +
                '<p id="event_user_email" style="display:none">' + comments[i].email + '</p>' +
                '<div class="col-sm-6">' +
                //'<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20">' +
                '<span id="event_user" style="margin-left:5px"><a href="" onclick="return loadUser(\'' + comments[i].email + '\')">' + comments[i].username + '</a><span>' +
                '<span id="event_timestamp" style="color: lightgray; display: inline; margin-left:5px">' + timestamp + '</span>' +
                '</div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-sm-12" id="event_comment_section">' +
                '<span  id="event_comment" style="margin-left:5px; display:inline-block; color: black; text-align: justify; text-justify: inter-word;">' + comments[i].comment + '</span>' +
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

function likeDislikeComment(id) {
    let event_id = document.getElementById("event_id").innerHTML;
    let like = "#Event #comment_" + id + " #event_likes";
    var urlvariable = "/rest/likeComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LIKE COMMENT EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "comment_id": "' + id + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't toggle comment like, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
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

function submitComment() {
    let commentElement = document.getElementById("newEventComment");
    let comment = document.getElementById("newEventComment").value;
    commentElement.value = '';
    var urlvariable = "/rest/postComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //POST COMMENT EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + document.getElementById("event_id").innerHTML +
        '", "comment": "' + comment + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load event participants, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                submitComment();
                return;
            }
            return false;
        }
        //success, load comment at top of comments:
        let comment_id = JSON.parse(xmlhttp.response).comment_id;
        let comments = document.getElementById("event_comments").innerHTML;
        $("#event_comments").empty();
        let deleteEditButtons = '<span style="margin-left:5px; display:inline-block"><a href="" style="color:red; font-size: 0.7em" onclick="return deleteComment(\'' + comment_id + '\')">' + "Delete" + '</a></span>' +
            '<span style="margin-left:5px; display:inline-block"><a href="" style="color:blue; font-size: 0.7em" onclick="return editComment(\'' + comment_id + '\')">' + "Edit" + '</a></span>';
        let commentElement =
            '<div id="comment_' + comment_id + '">' +
            '<div class="row" style="margin-top:10px">' +
            '<p style="display:none">' + userId + '</p>' +
            '<div class="col-sm-6">' +
            //'<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20">' +
            '<span id="event_user" style="margin-left:5px"><a href="">' + document.getElementById('user_tag').innerHTML + '</a><span>' +
            '<span id="event_timestamp" style="color: lightgray; display: inline; margin-left:5px">' + 'Just now' + '</span>' +
            '</div>' +
            '</div>' +
            '<div class="row">' +
            '<div class="col-sm-12" id="event_comment_section">' +
            '<span id="event_comment" style="margin-left:5px; display:inline-block; color: black; text-align: justify; text-justify: inter-word;">' + comment + '</span>' +
            '</div>' +
            '</div>' +
            '<div class="row">' +
            '<div class="col-sm-12">' +
            '<span id="event_likes" style="margin-left:5px; color:blue"><i style="color:blue" onclick="return likeDislikeComment(\'' + comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i><div style="display:inline-block; margin-left:2px" id="nLikes">' + "0" + '</div></span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>' +

            '</div>' +
            '</div>' +
            '</div>';









        $("#event_comments").append(commentElement);
        $("#event_comments").append(comments);
        countChar(document.getElementById("newEventComment"), "charNumComment");

    }
    xmlhttp.send(ItemJSON);



}

function deleteComment(comment_id) {
    if (isDeleting) {
        alert("Finish deleting the other comment first");
        return false;
    }
    if (isEditing) {
        alert("Finish your editing on the other comment first");
        return false;
    }
    isDeleting = true;
  
    let commentId = "#Event #comment_" + comment_id;
    let section = $(commentId + " #event_comment_section");
    prevSectionContent = section.html();
    let prevComment = $(commentId + " #event_comment").html();

    section.html('<h6 class="mb-0" style="width:100%; text-align:center">Are you sure?</h6>' +
        '<div style="margin: auto; text-align:center">' +
        '<button style="color: white;background-color: red; display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="confirmDeleteBtn" onclick="confirmDeleteComment(\'' + comment_id + '\', true)" class="btn btn-primary">Confirm</button>' +
        '<button style="color: white;background-color: blue; display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="confirmDeleteBtn" onclick="confirmDeleteComment(\'' + comment_id + '\', false)" class="btn btn-primary">Cancel</button>' +
        '</div>'
    );



    

    return false;
}

function confirmDeleteComment(comment_id, confirm) {
    let commentId = "#Event #comment_" + comment_id;
    if (!confirm) {
        //cancel
        
        let section = $(commentId + " #event_comment_section");
        section.html(prevSectionContent);


    }
    else {
        //delete
        var urlvariable = "/rest/deleteComment";
        var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //delete comment REST URL
        var xmlhttp = new XMLHttpRequest();
        var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
        var ItemJSON = '{"email": "' + userId +
            '", "token": "' + token +
            '", "event_id": "' + document.getElementById("event_id").innerHTML +
            '", "comment_id": "' + comment_id +
            '"}';
        xmlhttp.open("POST", URL, true);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        xmlhttp.onload = function (oEvent) {
            if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
                alert("Couldn't delete comment, message: " + xmlhttp.status);
                isDeleting = false;
                if (xmlhttp.status == 403) {
                    if(tryAuthentication())
                    confirmDeleteComment(comment_id, confirm);
                    return;
                }
                return false;
            }
            $(commentId).remove();
        };
        xmlhttp.send(ItemJSON);
    }
    isDeleting = false;
    return false;
}

function editComment(comment_id) {
    if (isEditing) {
        alert("Finish your editing on the other comment first");
        return false;
    }
    if (isDeleting) {
        alert("Finish deleting the other comment first");
        return false;
    }
    
    isEditing = true;
    let commentId = "#Event #comment_" + comment_id;
    let section = $(commentId + " #event_comment_section");
    prevSectionContent = section.html();
   
    let prevComment = $(commentId + " #event_comment").html();
    
    
    section.html(
        '<textarea onkeyup="countChar(this, \'CommentCharNum\')" style="vertical-align: top; max-width:94%; min-width:94%; margin-left:3%; margin-right:3%; margin-top:4px;" id="editEventComment" name="Your comment" placeholder="Remember all comments are subject to review" rows="4" cols="50" class="createEventFormInput"></textarea>' +
        '<div id="CommentCharNum" style="margin-left: 95%; font-size: 80%">500</div>' +
        '<button style="display:inline-flex; vertical-align:auto; margin:auto; height:auto; width:auto; font-size: 0.7em" id="submitEditBtn" onclick="submitEdit(\''+comment_id+'\')" disabled="disabled" class="btn btn-primary">Submit</button>'
    );
    $(commentId + " #editEventComment").val(prevComment);
    $(commentId + " #delEditBtnsDiv").hide();

    $('body').on("keyup", '#editEventComment', function () {
        if ($(this).val() != "") $('#submitEditBtn').removeAttr('disabled');
        else $('#submitEditBtn').prop("disabled", true);
    });
    $('body , #editEventComment').trigger("keyup");
    return false;
}

function submitEdit(comment_id) {
    let newComment = $("#Event #comment_" + comment_id + " #editEventComment").val();
    
    let section = $("#Event #comment_" + comment_id + " #event_comment_section");
    var urlvariable = "/rest/updateComment";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Update comment REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + document.getElementById("event_id").innerHTML +
        '", "comment_id": "' + comment_id +
        '", "comment": "' + newComment +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't edit comment, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                submitEdit(comment_id);
                return;
            }
            return false;
        }
        let newComment = $("#Event #comment_" + comment_id + " #editEventComment").val();
        section.html(prevSectionContent);
        $("#Event #comment_" + comment_id + " #event_comment").html(newComment);
        $("#Event #comment_"+comment_id + " #delEditBtnsDiv").show();
        isEditing = false;

    };
    xmlhttp.send(ItemJSON);
}

//EDIT:
function EventBtnSwap() {
    var x = document.getElementById("editEventBtn");
    var y = document.getElementById("saveEventBtn");
    if (x.style.display === "none") {
        x.style.display = "block";
        y.style.display = "none";
    } else {
        x.style.display = "none";
        y.style.display = "block";
    }
}

function editEvent() {
    console.log("Now hiding edit button and showing save button");
    turnContentEvent(true);
    EventBtnSwap();
}

function saveEvent() {
    console.log("Now hiding save button and showing edit button");
    //needs to send request to server to save new element values
    //needs to turn the content editable off
    turnContentEvent(false);
    saveEventInfo();
    for (var i = 0; i < filedata.length; i++) {
        if (prevPics[i].src != document.getElementById("event_image_" + prevPics[i].id).src);
            getGCSUploadEventURL(i);
    }
    EventBtnSwap();
}

function updateEventInfoElements() {
    //console.log(infoElementsRef.length);
    for (i = 0; i < eventInfoElementsRef.length; i++) {
        eventInfoElements[i] = [];
        eventInfoElements[i] = [eventInfoElementsRef.eq(i).attr("id"), eventInfoElementsRef.eq(i).html()];
    }
    imgSrcPrev = $("#userImg").attr("src");
}

function turnContentEvent(on) {
    if (on) {
        eventInfoElementsRef = $("#Event .info");
        updateEventInfoElements();

        //Allow editable fields
        $('#Event .editable').each(function () {
            // console.log($(this.value));
            $(this).attr("contenteditable", "true");
            $(this).addClass("createEventFormInput");
        });

        //Change Event icon
        var profile = $("#Event #event_profile").html();
        var profile_html = 'Event visibility: <span class="lock" id="eventPrivacyLock" style = "margin-left:10px" ></span>'
        $("#Event #event_profile").html(profile_html);
        if (profile == "PUBLIC") {
            $(".lock").toggleClass('unlocked');
            EventVisibility = true;
        }
        $("#eventPrivacyLock").click(function () {
            $(this).toggleClass('unlocked');
            EventVisibility = !EventVisibility;
        });

        //Category:
        //Change category to list
        var prevRegion = document.getElementById("event_categoryRaw").innerHTML;
        let data = '<select id="eventCategorySelectable" style="color: dimgray; border-style: solid; border-color: white; border-width: 1px; border-radius: 12px; box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px; text-align:center; border-style:ridge; border-radius:5px; border-color:lightgray; max-width:100%; min-width:100%" placeholder="Category" value="'+prevRegion+'">'+
            '<option value="CUIDAR_DE_ANIMAIS">Animal Caring</option>'+
            '<option value="ENSINAR_IDIOMAS">Language Teaching</option>' +
            '<option value="ENSINAR_MUSICA">Music Teaching</option>' +
            '<option value="INICIATIVAS_AMBIENTAIS">Environmental Initiatives</option>' +
            '<option value="DESASTRES_AMBIENTAIS">Environmental Disasters</option>' +
            '<option value="COMUNICACAO_DIGITAL">Digital Comunication</option>' +
            '<option value="AUXILIO_DE_DOENTES">Aiding The Sick</option>' +
            '<option value="AJUDAR_PORTADORES_DE_DEFICIENCIA">Aiding the Disabled</option>' +
            '<option value="AJUDA_DESPORTIVA">Sports Aid</option>' +
            '<option value="AJUDA_EMPRESARIAL">Business Aid</option>' +
            '<option value="AJUDA_A_CRIANCAS">Aiding Children</option>' +
            '<option value="AJUDA_A_IDOSOS">Aiding the Elderly</option>' +
            '<option value="AJUDA_A_SEM_ABRIGO">Aiding the Homeless</option>' +
            '<option value="INTERNACIONAL">Aiding the Foreign</option>' +
            '<option value="PROTECAO_CIVIL">Civil Protection</option>' +
            '<option value="SOCIAL">Social</option>' +
            '<option value="RECICLAGEM">Recyling</option>' +
            '<option value="CONSTRUCAO">Construction</option>' +
            '</select>';
        $("#event_category_div").html(data);
        $('#eventCategorySelectable').val(prevRegion);
        $('#eventCategorySelectable').attr("placeholder", prevRegion);

        //dificulty:
        var prevDificulty = document.getElementById("event_dificulty").innerHTML;
        data = '<input class="createEventFormInput" onKeyDown="return false" type="number" min="1" max="5" value="' + prevDificulty + '" style="text-align:center; max-width:100%; min-width:100%" id="eventSelectDificulty" name="Event Dificulty" placeholder="1-5">'
        $("#event_dificulty_div").html(data);

        //Dates:
        var prevStart = document.getElementById("event_start_raw").innerHTML;
        console.log("BEFORE DATE START: "+prevStart);
        let prevDate = prevStart.substring(0, 10);
        console.log(prevDate);
        let prevTime = prevStart.substring(11, 16);
        console.log(prevTime);
        data = 'Start:<input class="createEventFormInput" type="date" style="max-width:100%" id="event_newStartDate" name="Event Date" placeholder="When will it happen" value="' + prevDate + '">' +
            '<input class="createEventFormInput" type="time" style="max-width:100%" id="event_newStartTime" name="Event Time" placeholder="At what time" value="' + prevTime + '">';
            
        $("#event_start_div").html(data);

        var prevEnd = document.getElementById("event_end_raw").innerHTML;
        console.log("BEFORE DATE END: " + prevEnd);
        prevDate = prevEnd.substring(0, 10);
        console.log(prevDate);
        prevTime = prevEnd.substring(11, 16);
        console.log(prevTime);
        data = 'End:<input class="createEventFormInput" type="date" style="max-width:100%" id="event_newEndDate" name="Event Date" placeholder="When will it happen" value="' + prevDate + '">' +
            '<input class="createEventFormInput" type="time" style="max-width:100%" id="event_newEndTime" name="Event Time" placeholder="At what time" value="' + prevTime + '">' +
            '<small style="display: block; margin-top: 5px; margin-bottom: 10px;" id="ev_dateMsg" name="Datemsg">Put your time correctly, no going back in time allowed here!</small>';
        $("#event_end_div").html(data);

        eventLoadFunction2();
      


        //MAPS:


        //Allow user to upload image
        prevPics = [];
        filedata = [];
        for (var x = 0; x < 7; x++) {
            $("#event_image_"+x).toggleClass("imgUpload");
            $("#Event #OpenImgUpload"+x).attr("style", "");
        }
    }
    else {

        //Disable editing
        $('#Event .editable').each(function () {
            $(this).attr("contenteditable", "false");
            $(this).removeClass("createEventFormInput");
            if ($(this).hasClass("url")) {
                let url = $(this).html();
                if (url.substring(0, 7) !== 'http://' || url.substring(0, 8) !== 'https://')
                    url = 'http://' + url;
                $(this).attr("href", url);
            }
        });

        //Disable editing for the event and display without icon
        var profile = $("#Event #event_profile").html();
        var profile_data;
        if (EventVisibility)
            profile_data = "PUBLIC";
        else
            profile_data = "PRIVATE";
        $("#Event #event_profile").html(profile_data);

        //Category:
        var prevRegion = getCategory($("#eventCategorySelectable").val());
        $('#event_categoryRaw').html($("#eventCategorySelectable").val());
        var data = '<p class="text-secondary mb-1 info" id="event_category" name="category">'+prevRegion+'</p>';
        $('#event_category_div').html(data);
       

        //dificulty
        var prevDificulty = document.getElementById("eventSelectDificulty").value;
        data = '<h6 name="difficulty" id="event_dificulty" class="mb-0 info" style="-webkit-text-stroke-width: 1px; -webkit-text-stroke-color: black; width: 100%; font-size: 150%">' + prevDificulty + '</h6>'
        $("#event_dificulty_div").html(data);
        switch (prevDificulty) {
            case "1": $("#event_dificulty").css("color", "green"); break;
            case "2": $("#event_dificulty").css("color", "lightgreen"); break;
            case "3": $("#event_dificulty").css("color", "yellow"); break;
            case "4": $("#event_dificulty").css("color", "orange"); break;
            case "5": $("#event_dificulty").css("color", "red"); break;
        }

        //dates:
        let prevDate = document.getElementById("event_newStartDate").value;
        //prevDate = prevDate.replaceAll("-", "/");
        console.log(prevDate);
        let prevTime = document.getElementById("event_newStartTime").value;
        console.log(prevTime);
        data = '<h6 id="event_start" style="width:100%" class="mb-0" name="start_date">Starts on: '+prevDate+' '+ prevTime+'</h6>';
        $("#event_start_div").html(data);
        document.getElementById("event_start_raw").innerHTML = prevDate + 'T' + prevTime + ':00.000Z';
        console.log("AFTER DATE START: "+document.getElementById("event_start_raw").innerHTML);

        prevDate = document.getElementById("event_newEndDate").value;
        //prevDate = prevDate.replaceAll("-", "/");
        console.log(prevDate);
        prevTime = document.getElementById("event_newEndTime").value;
        console.log(prevTime);
        data = '<h6 id="event_end" style="width:100%" class="mb-0" name="start_date">Ends on: ' + prevDate + ' ' + prevTime + '</h6>';
        $("#event_end_div").html(data);
        document.getElementById("event_end_raw").innerHTML = prevDate + 'T' + prevTime + ':00.000Z';
        console.log("AFTER DATE END: "+document.getElementById("event_end_raw").innerHTML);
       




        //MAPS
       

        //Unable user to upload image and update prev images:
        for (var x = 0; x < 7; x++) {
            $("#event_image_"+x).toggleClass("imgUpload");
            $("#Event #OpenImgUpload"+x).attr("style", "display:none");
        }
       


    }
    return false;
}

function saveEventInfo() {
    let eventID = $("#Event #event_id").html();
    var urlvariable = "/rest/updateEvent/attributes";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSONBeginning = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + eventID;
    var ItemJSONEnd = '"}';
    var ItemJSONInfo = "", infoName, infoPrev, infoNow, infoId, newInfo;

    for (i = 0; i < eventInfoElements.length; i++) {
        infoId = eventInfoElements[i][0];
        infoPrev = eventInfoElements[i][1];
        //console.log(infoId + " " + infoPrev);
        var element = $("#"+infoId);
        infoNow = element.html();
        infoName = element.attr("name");
        

        if (infoPrev != infoNow) {
            newInfo = '", "' + infoName + '": "' + infoNow;
            console.log(newInfo);
            ItemJSONInfo = ItemJSONInfo + newInfo;
        }
    }

    /*console.log(ItemJSONBeginning);
    console.log(ItemJSONInfo);
    console.log(ItemJSONEnd);*/

    if (ItemJSONInfo == "") {
        console.log("No changes made");
        return false;
    }

    var ItemJSON = ItemJSONBeginning + ItemJSONInfo + ItemJSONEnd;
    console.log(ItemJSON);
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200))
            return false;
        console.log("update request: " + xmlhttp.status);
        if (xmlhttp.status == 403) {
            if(tryAuthentication())
            saveEventInfo();
            return;
        }
        return false;
    }
    xmlhttp.send(ItemJSON);
}

//AUX:
function DateTimeListenerFunction2() {
    //inputTime = this.value;

    //start date
    minTimeCondition = $("#event_newStartTime").attr("min");
    minDateCondition = $("#event_newStartDate").attr("min");
    currTime = $("#event_newStartTime").val();
    currDate = $("#event_newStartDate").val();

    //console.log("Start Date:");
    //console.log("Inputs date: " + currDate + ", time:" + currTime + ".\nMins date: " + minDateCondition + ", time: " + minTimeCondition + "?");



    //end date
    endMinTimeCondition = currTime;
    endMinDateCondition = currDate;
    endCurrTime = $("#event_newEndTime").val();
    endCurrDate = $("#event_newEndDate").val();

    //console.log("End Date:");
    //console.log("Inputs date: " + endCurrDate + ", time:" + endCurrTime + ".\nMins date: " + endMinDateCondition + ", time: " + endMinTimeCondition + "?");

    //update end-date if the start-date interferes
    $('#event_newEndDate').prop('min', endMinDateCondition);
    $('#event_newEndTime').prop('min', endMinTimeCondition);

    if (endCurrDate < endMinDateCondition || (endMinDateCondition == endCurrDate && endCurrTime < endMinTimeCondition)) {
        console.log("Updated the end-date because the start-date surpassed it");

        $('#event_newEndDate').prop('value', currDate);

        $('#event_newEndTime').prop('value', currTime);
    }




    //start conditions
    if (currTime < minTimeCondition && currDate == minDateCondition) {
        console.log("Time and date violating the rules!")
        document.getElementById("ev_dateMsg").innerHTML = "Choose a starting date which takes place in the future";
        $("#ev_dateMsg").css("color", "red");
        // canSubmit = false;
        return false;
    }
    else {
        document.getElementById("ev_dateMsg").innerHTML = "The starting date is valid";
        $("#ev_dateMsg").css("color", "lightgreen");
        // canSubmit = true;
    }

    //end conditions
    if (endCurrTime < endMinTimeCondition && endCurrDate == endMinDateCondition) {
        console.log("Time and date violating the rules!")
        document.getElementById("ev_dateMsg").innerHTML = "Choose a end date which takes place after your start date";
        $("#ev_dateMsg").css("color", "red");
        // canSubmit = false;
        return false;
    }
    else {
        document.getElementById("ev_dateMsg").innerHTML = "The event schedule is valid";
        $("#ev_dateMsg").css("color", "lightgreen");
        //canSubmit = true;
    }
    return true;
}

function eventLoadFunction2() {
    var prevStart = Date(document.getElementById("event_start_raw").innerHTML);
    var prevEnd = Date(document.getElementById("event_end_raw").innerHTML);

    var now = new Date();
    now.setTime(now.getTime() - now.getTimezoneOffset() * 60 * 1000);
    // minimum date the user can choose, in this case now and in the future
    minDate = now.toISOString().substring(0, 10);
    //  console.log(minDate);
    //start
    $('#event_newStartDate').prop('min', minDate);
    minTime = now.toISOString().substring(11, 16);
    //  console.log(minTime);
    $('#event_newStartTime').prop('min', minTime);
    if (prevStart < now) {
       
        $('#event_newStartDate').prop('value', minDate);
        
        $('#event_newStartTime').prop('value', minTime);
        
    }
    
    now.setTime(now.getTime() + 15 * 60000);
    minTime = now.toISOString().substring(11, 16);

    //console.log(minTime);
    if (prevEnd < now) {
        $('#event_newEndDate').prop('min', minDate);
        $('#event_newEndDate').prop('value', minDate);
        $('#event_newEndTime').prop('min', minTime);
        $('#event_newEndTime').prop('value', minTime);
    }

    document.getElementById("event_newStartTime").addEventListener('input', function (evt) {
        DateTimeListenerFunction2()
    });
    document.getElementById("event_newStartDate").addEventListener('input', function (evt) {
        DateTimeListenerFunction2()
    });
    document.getElementById("event_newEndTime").addEventListener('input', function (evt) {
        DateTimeListenerFunction2()
    });
    document.getElementById("event_newEndDate").addEventListener('input', function (evt) {
        DateTimeListenerFunction2()
    });
}

//IMAGES:
//Image upload scripts begining:
//Script to redirect button click to file explorer input
function uploadEventImg(id) {
    $('#Event #imgupload'+id).trigger('click');
}
//Script to load the photo to the user image
var loadFileEvent = function (event, id) {
        if (event.target.files[0].size > 5000000) {
            alert("File is too big! 5MB Maximum");
            this.value = "";
            return false;
        };
    var image = document.getElementById("event_image_" + id);
    let existingPrevPic = prevPics.find(el => el.id == id);
    if (existingPrevPic)
        existingPrevPic.src = image.src;
    else
        prevPics.push({ id: id, src: image.src });
    imgfile = event.target.files[0];
    imgext = event.target.value.split('.')[1];
    console.log(imgext);
    imgblob = URL.createObjectURL(imgfile);
    image.src = imgblob;
    $("#event_image_" + id).css("display", "");
    if (document.getElementById("event_image_" + id + "_preview")) {
        document.getElementById("event_image_" + id + "_preview").src = imgblob;
        $("#event_image_" + id + "_preview").css("display", "");
    }

    let existingFileData = filedata.find(el => el.id == id);
    if (existingFileData)
        existingFileData.file = new Blob([imgfile]);
    else
        filedata.push({ file: new Blob([imgfile]), id: id });

};
//Script to ask for cloud url to backend
function getGCSUploadEventURL(i) {
    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/event/updatePicture"
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id +
        '", "pic_id": "' + filedata[i].id +
        '"}';


    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //UPLOAD IMG EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't get event upload url");
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                getGCSUploadEventURL(i);
                return;
            }
            return;
        }
        //success
        const attributes = JSON.parse(xmlhttp.responseText);
        sendEventFile(attributes.upload_url, filedata[i].file);
    }
    xmlhttp.send(ItemJSON);

}

//Script to convert image to byte array, used to send to cloud storage
function getBuffer(fileData) {
    return function (resolve) {
        var reader = new FileReader();
        reader.readAsArrayBuffer(fileData);
        reader.onload = function () {
            var arrayBuffer = reader.result
            var bytes = new Uint8Array(arrayBuffer);
            resolve(bytes);
        }
    }
}
//send file now in byte array to cloud storage
function sendEventFile(cloudURL, file) {
    console.log("Trying to send file to " + cloudURL);
    var promise = new Promise(getBuffer(file));
    // Wait for promise to be resolved, or log error.
    promise.then(function (data) {
        // Here you can pass the bytes to another function.
        console.log(data);

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("PUT", cloudURL, true);
        xmlhttp.onload = function (oEvent) {
            // Uploaded.
            console.log("Request status: " + xmlhttp.status);
        };
        xmlhttp.send(data);


    }).catch(function (err) {
        console.log('Error: ', err);
    });
}
//Image upload scripts end
function requestEventPictureGCS(url, img_id) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", url, true);
    xmlhttp.responseType = "blob";
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user image from GCS, message: " + xmlhttp.status);
            if (xmlhttp.status == 403) {
                if(tryAuthentication())
                requestEventPictureGCS(url, img_id);
                return;
            }
            return false;
        }
        // var blob = new Blob([xmlhttp.response]);
        var blob = xmlhttp.response;
        var eventpic = document.getElementById("event_image_"+img_id);
        var photo = URL.createObjectURL(blob);
        eventpic.src = photo;
        eventpic = $("#event_image_" + img_id);
        eventpic.css("display", "");
       

        if (document.getElementById("event_image_" + img_id + "_preview")) {
            eventpic = $("#event_image_" + img_id + "_preview");
            eventpic.css("display", "");
            document.getElementById("event_image_" + img_id + "_preview").src = photo;
        }

    };
    xmlhttp.send();

}

function htmlEncode(value) {
    return $('<div/>').text(value)
        .html();
}

function fillCodes() {
    let event_id = document.getElementById("event_id").innerHTML;
    var urlvariable = "/rest/event/presenceCode";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET COMMENTS EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200))
            return false;
        let string = JSON.parse(xmlhttp.responseText).code;
        let finalURL = 'https://chart.googleapis.com/chart?cht=qr&chl=' + htmlEncode(string) + '&chs=160x160&chld=L|0';
        $("#presenceEventCode").attr("src", finalURL);
    }
    xmlhttp.send(ItemJSON);

    urlvariable = "/rest/event/leaveCode";
    URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET COMMENTS EVENT REST URL
    var xmlhttp2 = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event_id + '"}';
    xmlhttp2.open("POST", URL, true);
    xmlhttp2.setRequestHeader("Content-Type", "application/json");
    xmlhttp2.onload = function (oEvent) {
        if (!(xmlhttp2.readyState == 4 && xmlhttp2.status == 200))
            return false;
        let string = JSON.parse(xmlhttp2.responseText).code;
        let finalURL = 'https://chart.googleapis.com/chart?cht=qr&chl=' + htmlEncode(string) + '&chs=160x160&chld=L|0';
        $("#leaveEventCode").attr("src", finalURL);
    }
    xmlhttp2.send(ItemJSON);

}