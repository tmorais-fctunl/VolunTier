//VAR FOR CURRENCY LB
var myClbRank;
var clb_cursor = 0;
var clbCursors = [];
var loadedClbRanks = 0;
//VAR FOR PARTICIPANTS LB
var myPlbRank;
var plb_cursor = 0;
var plbCursors = [];
var loadedPlbRanks = 0;
//VAR EVNT NOTIFICATIONS
var eventNotificationCursor = [];
var eventNotificationCursors = [];

var myAppRole = '' ;

function getStatistics() {
    var urlvariable = "/rest/getStats";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //STATS REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token + '"}';
    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load user info");
            return false;
        }
        //SUCCESS
        const obj = JSON.parse(xmlhttp.responseText);
        let statistics = $("#Statistics");
        var content;
        content = '<div class="container">' +
            '<div class="row">Total number of users: ' + obj.total_num_users + '</div>' +
            '<div class="row">Total number of events: ' + obj.total_num_events + '</div>' +
            '<div class="row">Total number of routes: ' + obj.total_num_routes + '</div>' +
            '<div class="row">Total number of causes: ' + obj.total_num_causes + '</div>' +
            '<div class="row">Total number of current event participations: ' + obj.num_participations_event + '</div>' +
            '<div class="row">Total number of event participations so far: ' + obj.total_num_participations_event + '</div>' +
            '<div class="row">Total number of current event presences: ' + obj.num_presences + '</div>' +
            '<div class="row">Total presence time: ' + obj.total_time_presences + '</div>' +
            '<div class="row">Total number of comments: ' + obj.total_comments + '</div>' +
            '<div class="row">Total number of current currency: ' + obj.total_current_currency + '</div>' +
            '<div class="row">Total number of currency held so far' + obj.total_alltime_currency + '</div>' +
            '<div class="row">Total number of donations ' + obj.total_donations + '</div>' +
            '<div class="row">Total number of currency donated ' + obj.total_donated + '</div>' +
            '<div class="row">Average number of users per event: ' + obj.users_per_event + '</div>' +
            '<div class="row">Average user presence time per user: ' + obj.presence_time_per_user + '</div>' +
            '<div class="row">Average user presence time per event: ' + obj.presence_time_per_event + '</div>' +
            '<div class="row">Average donations per cause: ' + obj.donated_per_cause + '</div>' +
            '<div class="row">Average donations so far: ' + obj.donations_average + '</div>' +
            '<div class="row">Average number of comments per presence time: ' + obj.comments_per_time + '</div>' +
            '<div class="row">Average number of comments per user: ' + obj.comments_per_user + '</div>' +
            '<div class="row">Average number of user presences over participations: ' + obj.presences_average + '</div>' +
            '<div class="row">Average number of user presences over participations so far: ' + obj.presences_average_total + '</div>' +
            '</div>';
        statistics.append(content);
    }
    xmlhttp.send(ItemJSON);

}

//Request user info
function requestUserInfo() {
    var urlvariable = "/rest/user";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + userId +
        '"}';
    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load user info");
            return false;
        }
        //Sucess
        //Get the content from the json response and fill in the spots with the correct info
        const obj = JSON.parse(xmlhttp.responseText);
        $.ajax({
            //We need to wait fror the page to load before changing any fields.
            complete: function () {
                document.getElementById("profile_username").innerHTML = obj.username;
                myAppRole = obj.role;
                if (myAppRole != "USER") {
                    //SHOW STATISTICS TAB
                    $("#statisticsBTN").show();
                    $("#Statistics").show();

                    getStatistics();


                }
                document.getElementById("user_tag").innerHTML = obj.username;
                $("#user_currency").append(obj.currentCurrency);
                document.getElementById("profile_fullname").innerHTML = obj.full_name;
                document.getElementById("profile_visibility").innerHTML = obj.profile;
                document.getElementById("profile_email").innerHTML = obj.email;
                if (obj.region)
                    document.getElementById("profile_region").innerHTML = obj.region;
                if (obj.address)
                    document.getElementById("profile_address").innerHTML = obj.address;
                if (obj.mobile)
                    document.getElementById("profile_mobile").innerHTML = obj.mobile;
                if (obj.website) {
                    fillRefs("profile_website", obj.website);
                }
                if (obj.facebook) {
                    fillRefs("profile_facebook", obj.facebook);
                }
                if (obj.instagram) {
                    fillRefs("profile_instagram", obj.instagram);
                }
                if (obj.twitter) {
                    fillRefs("profile_twitter", obj.twitter);
                }
                //The rest fields

                $('#profileContainer .editable').each(function () {
                    $(this).attr("contenteditable", "false");
                    if ($(this).hasClass("url")) {
                        let url = $(this).html();
                        if (url.substring(0, 7) !== 'http://' || url.substring(0, 8) !== 'https://')
                            url = 'http://' + url;
                        $(this).attr("href", url);
                    }
                });



                //request picture
                requestUserPicture(obj.username);
                loadProfileEvents(obj.email);
                loadProfileParticipatingEvents(obj.email);

                updateEditInputs();
            }
        });
    }
    xmlhttp.send(ItemJSON);
}

function fillRefs(element_id, link) {
    let website = document.getElementById(element_id);
    let url = link;
    website.innerHTML = url;
    if (url.substring(0, 7) !== 'http://' || url.substring(0, 8) !== 'https://')
        url = 'http://' + url;
    website.href = url;
}

function requestUserPictureGCS(url) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", url, true);
    xmlhttp.responseType = "blob";
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user image from GCS, message: " + xmlhttp.status);
            return false;
        }
        // var blob = new Blob([xmlhttp.response]);
        var blob = xmlhttp.response;
        var userpic = document.getElementById("userImg");

        userpic.src = URL.createObjectURL(blob);
    };
    xmlhttp.send();

}

function requestUserPicture(username) {
    var urlvariable = "/rest/picture/" + username;
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + token + '"}';
    xmlhttp.open("POST", URL, true);

    xmlhttp.setRequestHeader("Content-Type", "application/json");

    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user image, message: " + xmlhttp.status);
            if (tryAuthentication())
                requestUserPicture(username);
            return false;
        }
        //Other wise...
        const obj = JSON.parse(xmlhttp.responseText);
        var cloudURL = obj.url;
        var pic = obj.pic;
        var size = obj.size;


        document.getElementById("user64img").src = obj.pic;





        console.log(cloudURL);
        //trying to fetch GCS image
        requestUserPictureGCS(cloudURL);



    }
    xmlhttp.send(ItemJSON);
}

function loadProfileEvents(username) {
    getUserEvents(username, function (obj) {
        let event_section = $("#profile_events");
        let events = obj.events;
        var event, content;
        for (i = 0; i < events.length; i++) {
            event = events[i];
            content = '<div class="row">' +
                '<a href="" id="profile_event_' + event.event_id + '" onclick="return loadEvent(\'' + event.event_id + '\', false)">' + event.name + '</a></div>';
            event_section.append(content);
        }
        loadNotifications();

    });

}

function loadProfileParticipatingEvents(username) {
    getUserParticipatingEvents(username, function (obj) {
        let event_section = $("#profile_participating_events");
        let events = obj.events;
        var event, content;
        for (i = 0; i < events.length; i++) {
            event = events[i];
            content = '<div class="row">' +
                '<a href="" id="profile_participating_event_' + event.event_id + '" onclick="return loadEvent(\'' + event.event_id + '\', false)">' + event.name + '</a></div>';
            event_section.append(content);
        }

    });
}

function loadLeaderboards() {
    clbCursors.push(0);
    plbCursors.push(0);
    loadCurrencyLB(0);
    loadParticipationsLB(0);  
}

function loadCurrencyLB(cursor) {
    var urlvariable = "/rest/totalCurrencyRank";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //CURRENCY LB REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token;
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';

    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load currency leaderboard info");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        let results = attributes.results;
        let users = attributes.users;
        if (results != "NO_MORE_RESULTS")
            clbCursors.push(attributes.cursor);

        console.log("I have acquired the users, and the number of users is: " + users.length);
        console.log("Next cursor is: " + attributes.cursor);
        if (users.length == 0) {
            roamCLB(true);
            return false;
        }
        let clbGlobalSection = $("#Leaderboard #leaderboard_currency #global");
        let clbSelfSection = $("#Leaderboard #leaderboard_currency #self");
        var content;

        if (attributes.current_user) {
            myClbRank = attributes.current_user.rank;
            content = '<div class="createEventFormInput" style="background-color: lightgreen; text-align:center; text-overflow:ellipsis; width:250px">' + myClbRank + ' ';
            if (attributes.current_user.pic_64)
                content = content + '<img src="' + attributes.current_user.pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
            content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + attributes.current_user.email + '\')">' + attributes.current_user.username + '</a></span>' +
                '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:green; text-shadow:none" class="fa fa-money" aria-hidden="true"></i> ' + attributes.current_user.score + '</span>' +
                '</div>';
            clbSelfSection.append(content);
        }

        for (var i = 0; i < users.length; i++) {
            loadedClbRanks++;
            if (loadedClbRanks == myClbRank) {
                content = '<div class="createEventFormInput" style="background-color: lightgreen; text-align:center; text-overflow:ellipsis; width:250px"> '+loadedClbRanks+' ';
                if (attributes.users[i].pic_64)
                    content = content + '<img src="' + attributes.users[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + attributes.users[i].email + '\')">' + attributes.users[i].username + '</a></span>' +
                    '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:green; text-shadow:none" class="fa fa-money" aria-hidden="true"></i> ' + attributes.users[i].score + '</span>' +
                    '</div><br>';
                clbGlobalSection.append(content);
            }
            else {
                content = '<div class="createEventFormInput" style="text-align:center; text-overflow:ellipsis; width:250px">' + loadedClbRanks + ' ';
                if (users[i].pic_64)
                    content = content + '<img src="' + users[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: black; margin-left:4px" href="" onclick="return loadUser(\'' + users[i].email + '\')">' + users[i].username + '</a></span>' +
                    '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:green; text-shadow:none" class="fa fa-money" aria-hidden="true"></i> ' + users[i].score + '</span>' +
                    '</div><br>';
                clbGlobalSection.append(content);
            }
        }
        if ($("#Leaderboard #leaderboard_currency #global").height() < 800) {
            roamCLB(true);
        }
       
        
    }
    xmlhttp.send(ItemJSON);

}

function roamCLB(next) {
    console.log("Roam activated, currencyCursors length: " + clbCursors.length);
    console.log("before cursor: " + clb_cursor);
    if (next && (clb_cursor + 1 < clbCursors.length)) {
        clb_cursor++;
        loadCurrencyLB(clbCursors[clb_cursor]);
    }
    
}

function loadParticipationsLB(cursor) {
    var urlvariable = "/rest/presencesRank";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //CURRENCY LB REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token;
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';

    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load participations leaderboard info");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        let results = attributes.results;
        let users = attributes.users;
        if (results != "NO_MORE_RESULTS")
            plbCursors.push(attributes.cursor);

        console.log("I have acquired the users, and the number of users is: " + users.length);
        console.log("Next cursor is: " + attributes.cursor);
        if (users.length == 0) {
            roamPLB(true);
            return false;
        }
        let plbGlobalSection = $("#Leaderboard #leaderboard_participations #global");
        let plbSelfSection = $("#Leaderboard #leaderboard_participations #self");
        var content;

        if (attributes.current_user) {
            myPlbRank = attributes.current_user.rank;
            content = '<div class="createEventFormInput" style="background-color: lightgreen; text-align:center; text-overflow:ellipsis; width:250px">' + myPlbRank + ' ';
            if (attributes.current_user.pic_64)
                content = content + '<img src="' + attributes.current_user.pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
            content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + attributes.current_user.email + '\')">' + attributes.current_user.username + '</a></span>' +
                '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:blue; text-shadow:none" class="fa fa-users" aria-hidden="true"></i> ' + attributes.current_user.score + '</span>' +
                '</div>';
            plbSelfSection.append(content);
        }

        for (var i = 0; i < users.length; i++) {
            loadedPlbRanks++;
            if (loadedPlbRanks == myPlbRank) {
                content = '<div class="createEventFormInput" style="background-color: lightgreen; text-align:center; text-overflow:ellipsis; width:250px"> ' + loadedPlbRanks + ' ';
                if (attributes.users[i].pic_64)
                    content = content + '<img src="' + attributes.users[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + attributes.users[i].email + '\')">' + attributes.users[i].username + '</a></span>' +
                    '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:blue; text-shadow:none" class="fa fa-users" aria-hidden="true"></i> ' + attributes.users[i].score + '</span>' +
                    '</div><br>';
                plbGlobalSection.append(content);
            }
            else {
                content = '<div class="createEventFormInput" style="text-align:center; text-overflow:ellipsis; width:250px">' + loadedPlbRanks + ' ';
                if (users[i].pic_64)
                    content = content + '<img src="' + users[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: black; margin-left:4px" href="" onclick="return loadUser(\'' + users[i].email + '\')">' + users[i].username + '</a></span>' +
                    '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;"><i style="color:blue; text-shadow:none" class="fa fa-users" aria-hidden="true"></i> ' + users[i].score + '</span>' +
                    '</div><br>';
                plbGlobalSection.append(content);
            }
        }
        if ($("#Leaderboard #leaderboard_participations #global").height() < 800) {
            roamPLB(true);
        }


    }
    xmlhttp.send(ItemJSON);
}

function roamPLB(next) {
    console.log("Roam activated, currencyCursors length: " + plbCursors.length);
    console.log("before cursor: " + plb_cursor);
    if (next && (plb_cursor + 1 < plbCursors.length)) {
        plb_cursor++;
        loadParticipationsLB(plbCursors[plb_cursor]);
    }
}

function loadNotifications() {
    let myEventIds = [];
    let events = $("#Profile #profile_events .row a");
    events.each(function () {
        let id = $(this).attr("id");
        id = id.split("profile_event_")[1];
        myEventIds.push(id);
    });
    let notificationSector = $("#notifications_container");
    var content;
    //Add card for each event
    for (var i = 0; i < myEventIds.length; i++) {
        content = '<div class="createEventFormInput" id="event_' + myEventIds[i] + '_notifications" style="text-align:center; max-height:200px; overflow:auto">' +
        '<h5 style="text-align:center">'+$("#profile_event_"+myEventIds[i]).html()+'</h5>' +
        '</div><br>';
        notificationSector.append(content);
    }
    
    for (var i = 0; i < myEventIds.length; i++) {
        eventNotificationCursor[i] = 0;
        eventNotificationCursors[i] = [];
        eventNotificationCursors[i].push(0);
        loadEventNofication(myEventIds[i], 0, i);
    }

    for (var i = 0; i < myEventIds.length; i++) {
        let section = "#event_" + myEventIds[i] + "_notifications";
        let id = myEventIds[i];
        let pos = i;
        $(section).on('scroll', function (eventId) {
            if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                //console.log(id);
                roamEventNotifications(true, id, pos);
            }
        });
    }
}

function loadEventNofication(event, cursor, arrayPosition) {
    var urlvariable = "/rest/getRequests";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //REQUESTS REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + event;
    if (cursor != 0) {
        ItemJSON = ItemJSON + '", "cursor": "' + cursor;
    }
    ItemJSON = ItemJSON + '"}';
    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load event requests info");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        let results = attributes.results;
        let users = attributes.participants;
        let eventSection = $("#event_" + event + "_notifications");
        if (results != "NO_MORE_RESULTS")
            eventNotificationCursors[arrayPosition].push(attributes.cursor);
        if (cursor == 0 && results == "NO_MORE_RESULTS" && users.length == 0)
            eventSection.append('<h7 style="text-align:center; color:red">No Requests</h7>')

        console.log("I have acquired the users, and the number of users is: " + users.length);
        console.log("Next cursor is: " + attributes.cursor);
        if (users.length == 0) {
            roamEventNotifications(true, event, arrayPosition);
            return false;
        }
        
        var content;

        for (var i = 0; i < users.length; i++) {
                content = '<div class="createEventFormInput row" style="text-align:center; background-color:white; width:50%; margin-left:25%; margin-right:25%; margin-top:5px; margin-bottom:5px"><div style="text-align:center; margin-top:5px" class="col-sm-6">';
                if (users[i].pic)
                    content = content + '<img src="' + users[i].pic + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px; margin-bottom:5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: black; margin-left:4px" href="" onclick="return loadUser(\'' + users[i].email + '\')">' + users[i].username + '</a></span></div>' +
                '<div style="margin-top:2px" class="col-sm-6"><button class="btn btn-primary" style="width:50px; height:25px; margin-left:5px;background-color:green; text-align:center; font-size:0.7em; color:white; padding:0" onclick="acceptRequest(this,\'' + event + '\', \'' + users[i].email + '\')">Accept</button>' +
                '<button class="btn btn-primary" style="width:50px; height:25px; margin-left:5px;background-color:#eead2d; text-align:center; font-size:0.7em; color:white; padding:0" onclick="declineRequest(this,\'' + event + '\', \'' + users[i].email + '\')">Decline</button>' +
                '</div></div>';
                eventSection.append(content);
            
        }
        if (eventSection.height() < 200) {
            roamEventNotifications(true, event, arrayPosition);
        }


    }
    xmlhttp.send(ItemJSON);
}

function roamEventNotifications(next, event, arrayPosition) {
    console.log("Roam activated, currencyCursors length: " + eventNotificationCursors[arrayPosition].length);
    console.log("before cursor: " + eventNotificationCursor[arrayPosition]);
    if (next && (eventNotificationCursor[arrayPosition] + 1 < eventNotificationCursors[arrayPosition].length)) {
        eventNotificationCursor[arrayPosition]++;
        loadEventNotification(event, eventNotificationsCursor[eventNotificationCursor[arrayPosition]], arrayPosition);
    }

}

function acceptRequest(btn, event, user) {
    var urlvariable = "/rest/acceptRequest";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //ACCEPT REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token" : "' + token +
        '", "event_id": "' + event +
        '", "participant": "' + user +
        '"}';
    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Could not accept user request, refresh and try again");
            return false;
        }
        //Sucess
        $(btn).closest(".row").remove();
        alert("User successfully accepted");
    }
    xmlhttp.send(ItemJSON);
}
function declineRequest(btn, event, user) {
    var urlvariable = "/rest/declineRequest";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //DECLINE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token" : "' + token +
        '", "event_id": "' + event +
        '", "participant": "' + user +
        '"}';
    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Could not decline user request, refresh and try again");
            return false;
        }
        //Sucess
        $(btn).closest(".row").remove();
        alert("User successfully declined");
    }
    xmlhttp.send(ItemJSON);
}

//FROM USER John Doe @ STACK OVERFLOW 25/07/2021
//https://stackoverflow.com/questions/12934720/how-to-increment-decrement-hex-color-values-with-javascript-jquery
function gradient(startColor, endColor, steps) {
    var start = {
        'Hex': startColor,
        'R': parseInt(startColor.slice(1, 3), 16),
        'G': parseInt(startColor.slice(3, 5), 16),
        'B': parseInt(startColor.slice(5, 7), 16)
    }
    var end = {
        'Hex': endColor,
        'R': parseInt(endColor.slice(1, 3), 16),
        'G': parseInt(endColor.slice(3, 5), 16),
        'B': parseInt(endColor.slice(5, 7), 16)
    }
    diffR = end['R'] - start['R'];
    diffG = end['G'] - start['G'];
    diffB = end['B'] - start['B'];

    stepsHex = new Array();
    stepsR = new Array();
    stepsG = new Array();
    stepsB = new Array();

    for (var i = 0; i <= steps; i++) {
        stepsR[i] = start['R'] + ((diffR / steps) * i);
        stepsG[i] = start['G'] + ((diffG / steps) * i);
        stepsB[i] = start['B'] + ((diffB / steps) * i);

        let red = Math.round(stepsR[i]).toString(16);
        if (red == '0')
            red = '00';
        let blue = Math.round(stepsB[i]).toString(16);
        if (blue == '0')
            blue = '00';
        let green = Math.round(stepsG[i]).toString(16);
        if (green == '0')
            green = '00';
        stepsHex[i] = '#' + red + '' + green + '' + blue;
    }
    return stepsHex;

}

function loadDonations() {
    let donationsSection = $("#donations_container");
    console.log(donationsSection);
    var urlvariable = "/rest/causes/get/all";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //CURRENCY LB REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token + '"}';

    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Could not load donations info");
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        var content;
        let causes = attributes.causes;
        let gradientvar = gradient("#f00000","#00d000", 5);
        
   

        for (var i = 0; i < causes.length; i++) {
            let goalparts = (causes[i].goal/5);
            let raisedColorQfc = (causes[i].raised / goalparts);
            let goalColor = Math.round(raisedColorQfc);
            let colorGoal = gradientvar[goalColor];
         
            content =
                '<div class="row createEventFormInput" style="max-height:500px; background-color:white;" id="donation_'+ causes[i].id +'_section">' +
                    '<div class="col-4" style="margin-top:5px; margin-bottom:5px; max-height:490px; overflow:auto;" id="donation_images">' +
                        
                    '</div> ' +
                    '<div class="col-8" style="text-align: center; margin-top:5px; margin-bottom:5px; max-height:490px; overflow:auto">' +
                        '<span id="donation_id" style="display:none">' + causes[i].id + '</span>' +
                        '<span style="font-size: 150%; color:yellow; text-shadow: -1px 0 1px gray, 0 1px 1px gray, 1px 0 1px gray, 0 -1px 1px gray;">' + causes[i].name + ' </span><span style="display:inline-block"> ' + 'by </span><span style="font-size: 150%; color: yellow; text-shadow: -1px 0 1px gray, 0 1px 1px gray, 1px 0 1px gray, 0 -1px 1px gray;"> ' + causes[i].company_name + '</span>' +
                        '<p id="raised" style="font-size: 150%; color: '+colorGoal+'">' + "Our goal: " + causes[i].raised + "/" + causes[i].goal + '</p>' +
                        '<p style="white-space: pre-wrap;">' + causes[i].description + '</p>' +
                        '<p id="num_donations">' + "Number of donations so far: " + causes[i].num_donations + '</p>' +
                        '<p><a id="cause_site">' + "Visit: " + causes[i].website + '</a></p>' +
                        '<input class="createEventFormInput" id="cause_'+causes[i].id+'_donate_ammount_input" type="number" min=1 max=1000000 value=1 placeholder="How much?" style="width:120px">' +
                        '<button class="btn createEventFormInput" style="margin-left: 5px;background-color: lightgray;color:black; padding:2px; font-size:100%" disabled=false onclick="donateToCause(this,\'' + causes[i].id + '\')" >Donate</button>' +
                        '<br>' +
                        '<br>' +
                        '<h6 style="text-aligned" >Recent Donators</h6>' +
                        '<div id="event_donators" class="createEventFormInput" style="overflow:auto; max-height:150px"></div>' +
                        '<br>' +
                    '</div> ' +
                '</div>' +
                '<br>';
            donationsSection.append(content);

            let website = $("#donation_" + causes[i].id + "_section #cause_site");
            let url = causes[i].website;
            if (url.substring(0, 7) !== 'http://' || url.substring(0, 8) !== 'https://')
                url = 'http://' + url;
            website.attr("href",url);

            
            
            let pics = causes[i].pics;
            for (var j = 0; j < pics.length; j++)
                loadDonationPhoto(causes[i].id, pics[j].dwld_url.url);
            loadDonationDonators(causes[i].id);
        }
    }
    xmlhttp.send(ItemJSON);

}

function loadDonationDonators(causeid) {
    let section = $("#donation_" + causeid + "_section #event_donators");
    let nextCursor = 0;
    let currentCursor = 0;
    var urlvariable = "/rest/causes/donators";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //DONATE REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");

    do {
        xmlhttp.open("POST", URL, false);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        currentCursor = nextCursor;
        var ItemJSON = '{"email": "' + userId +
            '", "token": "' + token +
            '", "cause_id": "' + causeid;
        if (currentCursor != 0)
            ItemJSON = ItemJSON + '", "cursor": "' + currentCursor;
        ItemJSON = ItemJSON + '"}';

        xmlhttp.onload = function () {
            if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
                console.log("Couldn't load donators...");
                return false;
            }
            const attributes = JSON.parse(xmlhttp.responseText);
            let donators = attributes.donators;
            if (attributes.results != "NO_MORE_RESULTS")
                nextCursor = attributes.cursor;
            var content;
            for (var i = 0; i < donators.length; i++) {
                let date = new Date(donators[i].timestamp);
                let hour = ("0" + date.getHours()).slice(-2);
                let min = ("0" + date.getMinutes()).slice(-2)
                date = date.getDate() + "/" + (parseInt(date.getMonth()) + 1) + "/" + date.getFullYear() + " " + hour + ":" + min;
                content = '<div style="display:block">';
                if (donators[i].pic_64)
                    content = content + '<img src="' + donators[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px; margin-bottom:5px">';
                content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: black; margin-left:4px" href="" onclick="return loadUser(\'' + donators[i].email + '\')">' + donators[i].username + '</a></span>' +
                    '<span style="display:inline-block; margin-left:5px; color:lightgray">' + date + '</span>' +
                    '<span style="display:inline-block; margin-left:5px; color:yellow; text-shadow: -1px 0 1px gray, 0 1px 1px gray, 1px 0 1px gray, 0 -1px 1px gray;">' + donators[i].donation + '</span></div><br>';
                section.append(content);
            }

        }
        xmlhttp.send(ItemJSON);
    }
    while (currentCursor!=nextCursor)
}

function donateToCause(btn, cause_id) {
    let input = $(btn).siblings("input");
    let val = input.val();
    var urlvariable = "/rest/causes/donate";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //DONATE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "cause_id": "' + cause_id +
        '", "amount": "' + val +
        '"}';

    xmlhttp.onload = function () {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Could not donate :( "+xmlhttp.status);
            return false;
        }
        //SUCCESS:
        let prevCurrency = $("#user_currency").text();
        let newCurrency = parseInt(prevCurrency) - val;
        $("#user_currency").text(newCurrency);
        let numDonations = $(btn).siblings("#num_donations");
        let prevDonations = numDonations.html().split(": ")[1];
        numDonations.html(numDonations.html().split(": ")[0] + ": " + (parseInt(prevDonations) + 1));

        let numRaised = $(btn).siblings("#raised");
        let prevRaised = numRaised.html().split("/")[0].split("Our goal: ")[1];
        let goal = numRaised.html().split("/")[1];
        numRaised.html("Our goal: " + (parseInt(prevRaised) + parseInt(val)) + "/" + goal);
        alert("Thank you for your donation!");
    }
    xmlhttp.send(ItemJSON);
}

function loadDonationPhoto(causeid, url) {
    let section = $("#donation_" + causeid + "_section #donation_images");
    let img = $("#donation_" + causeid + "_section img");
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", url, true);
    xmlhttp.responseType = "blob";
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load donation image from GCS, message: " + xmlhttp.status);
            return false;
        }
        // var blob = new Blob([xmlhttp.response]);
        var blob = xmlhttp.response;
        let content = '<img style="margin-top:2.5%; margin-bottom:2.5%; border-style:solid; border-width:3px; box-shadow: 0 0 50px #ccc;" width="95%" src="' + URL.createObjectURL(blob)+'">';
        section.append(content);
    };
    xmlhttp.send();

}



window.onload = function () {
    // For debugging purposes: loadContents();
    if (!tryAuthentication()) {
        loadContent("appTab", "../pages/contents/logoutTab.html");
        return false;
    }
    loadContents();
    loadContent("appTab", "../pages/contents/loggedTab.html");

    requestUserInfo();
    loadLeaderboards();
    loadDonations();
    


    //For event tab
    $('#event_comments').on('scroll', function () {
        if ($(this).scrollTop() +
            $(this).innerHeight() >=
            $(this)[0].scrollHeight) {
            roamComments(true);
        }
    });
    $('body').on("keyup", '#newEventComment', function () {
        if ($(this).val() != "") $('#Event #submitCommentBtn').removeAttr('disabled');
        else $('#Event #submitCommentBtn').prop("disabled", true);
    });

    //for route tab
    $('#route_comments').on('scroll', function () {
        if ($(this).scrollTop() +
            $(this).innerHeight() >=
            $(this)[0].scrollHeight) {
            route_roamComments(true);
        }
    });

    $('#Leaderboard #leaderboard_currency #global').on('scroll', function () {
        if ($(this).scrollTop() +
            $(this).innerHeight() >=
            $(this)[0].scrollHeight) {
            roamCLB(true);
        }
    });

    $('#Leaderboard #leaderboard_participations #global').on('scroll', function () {
        if ($(this).scrollTop() +
            $(this).innerHeight() >=
            $(this)[0].scrollHeight) {
            roamPLB(true);
        }
    });


    $('body').on("keyup", '#newRouteComment', function () {
        if ($(this).val() != "") $('#Route #submitCommentBtn').removeAttr('disabled');
        else $('#Route #submitCommentBtn').prop("disabled", true);
    });

    //Donations
    $('body').ready(function () {
        $('body #Donations').on("keyup change", 'input', function () {
            if ($(this).val().length == 0 || $(this).val() > parseInt($("#user_currency").text()))
                $(this).siblings("button").prop("disabled", true);
            else
                $(this).siblings("button").removeAttr('disabled');
        });
    });


    //loadUser("tiagoap99morais@gmail.com");
}
