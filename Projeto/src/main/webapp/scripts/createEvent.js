var participant_cursor;
var participantCursors = [];
var comments_cursor;
var commentsCursors = [];
var isEditing = false;
let prevSectionContent = '';
var isDeleting = false;
var event_capacity = 0;
var event_num_participants = 0;
var permissions = false;



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
  '", "location": ["'+location[0]+'","'+location[1]+'"]' +
  ', "start_date": "' + now.toISOString() +
  '", "end_date": "' + end.toISOString() +
  '", "category": "' + $("#eventCategory").val() +
  '", "profile": "' + $("#eventPrivacyLock").val() +
  '"}';
  console.log(ItemJSON);
  var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //ADD EVENT REST URL
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open("POST", URL, false);
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
            return false;
        }
    }
    xmlhttp.send(ItemJSON);
}

function fillEventAttributes(attributes) {

    document.getElementById("event_id").innerHTML = attributes.event_id;
    document.getElementById("event_name").innerHTML = attributes.name;
    document.getElementById("event_profile").innerHTML = attributes.profile;
    document.getElementById("event_joined_capacity").innerHTML = attributes.num_participants + "/" + attributes.capacity;

    if (attributes.status == "OWNER")
        document.getElementById("editEventBtn").style.display = "block";
    else
        document.getElementById("editEventBtn").style.display = "none";

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
    var createdOn = new Date(attributes.creation_date);
    start = start.getDate() + "/" + start.getMonth() + "/" + start.getFullYear() + " " + start.getHours() + ":" + start.getMinutes();
    end = end.getDate() + "/" + end.getMonth() + "/" + end.getFullYear() + " " + end.getHours() + ":" + end.getMinutes();
    createdOn = createdOn.getDate() + "/" + createdOn.getMonth() + "/" + createdOn.getFullYear() + " " + createdOn.getHours() + ":" + createdOn.getMinutes();
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
    console.log("Are you allowed to comment: " + (attributes.status == "PARTICIPANT" || attributes.status == "OWNER"));
    newCommentVerification(attributes.status == "PARTICIPANT" || attributes.status == "OWNER" || attributes.status == "MOD");
    handleComments();
   
    //Photos:
    
    //The rest
 


}
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

function handleParticipants() {
    let event_id = document.getElementById("event_id").innerHTML;
    let participantElement = $("#event_participants");
    participantElement.empty();
    participant_cursor = 0;
    participantCursors = [];
    participantCursors.push(0);
    fillEventParticipants(event_id, participant_cursor);
}

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
    let button = $("#joinBtn");
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
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);

        //Fill and update the participants
        let participants = attributes.participants;
        let content = '<div id="event_participants_page_' + participant_cursor + '" style="display:inline-block; margin-left: 5px">';
        console.log("Participants page: " + participant_cursor + " with " + participants.length + " participants");
        for (i = 0; i < participants.length; i++) {
            content = content.concat('<span style="display:none">' + participants[i].email + '</span>');
            if (participants[i].pic != '')
                content = content.concat('<img src="' + participants[i].pic + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">');
            if (participants[i].role == "OWNER") {
                let creator = $("#event_creator");
                creator.attr("onclick", "return loadUser(\'"+participants[i].email+"\')")
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
            participantCursors.push(attributes.cursor);
    };
    xmlhttp.send(ItemJSON);




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
    console.log("activated fill with cursor: "+cursor+" 'page' "+ comments_cursor);
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
            alert("Couldn't load event comments, message: " + xmlhttp.status);
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
            if (true)
                likes = '<span id="event_likes" style="margin-left:5px; color:green"><i onclick="return likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i>' + " " + comments[i].likes + '</span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'
            else
                likes = '<span id="event_likes" style="margin-left:5px; color:blue"><i onclick="return likeDislikeComment(\'' + comments[i].comment_id + '\')" class="fa fa-thumbs-o-up likeBtn" aria-hidden="true"></i>' + " " + comments[i].likes + '</span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>'
                

            
            commentElement.append(
            '<div id="comment_'+comments[i].comment_id+'">'+
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
        handleLikes(event_id);
    };
    xmlhttp.send(ItemJSON);




}

function handleLikes(event_id) {
    let comments = $("#event_comments").children();
    var commentid;
    for (i = 0; i < comments.length; i++) {
        commentid = comments[i].id;
        let like = $("#"+commentid+" #event_likes i");
        like.css("color","blue");
        console.log(like);
    }
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
                        '<span id="event_user" style="margin-left:5px"><a href="">' + document.getElementById('user_tag') + '</a><span>' +
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
                        '<span  id="event_likes" style="margin-left:5px; color:blue">' + "Likes " + '0' + '</span><div id="delEditBtnsDiv"' + deleteEditButtons + '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';

        
           
          





        $("#event_comments").append(commentElement);
        $("#event_comments").append(comments);

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
  
    let commentId = "#comment_" + comment_id;
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
    let commentId = "#comment_" + comment_id;
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
    let commentId = "#comment_" + comment_id;
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
    return false;
}

function submitEdit(comment_id) {
    let newComment = $("#comment_" + comment_id + " #editEventComment").val();
    
    let section = $("#comment_" + comment_id + " #event_comment_section");
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
            return false;
        }
        let newComment = $("#comment_" + comment_id + " #editEventComment").val();
        section.html(prevSectionContent);
        $("#comment_" + comment_id + " #event_comment").html(newComment);
        $("#comment_"+comment_id + " #delEditBtnsDiv").show();
        isEditing = false;

    };
    xmlhttp.send(ItemJSON);
}

function editEvent() {
    $('#Event .editable').each(function () {
        // console.log($(this.value));
        $(this).attr("contenteditable", "true");
    });
}