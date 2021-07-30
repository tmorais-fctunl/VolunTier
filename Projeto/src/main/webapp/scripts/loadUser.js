let searchUserCursors = 0;

function loadUser(user_id) {
    if (!checkSession())
        return;
    $("body").css("cursor", "progress");
    var urlvariable = "/rest/user";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + user_id +
        '"}';
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Could not load user info, message: " + xmlhttp.status);
            $("body").css("cursor", "default");
            return false;
        }
        //Success
        const obj = JSON.parse(xmlhttp.responseText);
        if (obj.profile == "PRIVATE") {
            $("#user_website_section").hide();
            $("#user_information_section").hide();
        }
        else {
            $("#user_website_section").show();
            $("#user_information_section").show();
        }
        document.getElementById("user_username").innerHTML = obj.username;
        if (obj.full_name)
            document.getElementById("user_fullname").innerHTML = obj.full_name;
        document.getElementById("user_visibility").innerHTML = obj.profile;
        document.getElementById("user_email").innerHTML = obj.email;
        if (obj.region)
            document.getElementById("user_region").innerHTML = obj.region;
        if (obj.address)
            document.getElementById("user_address").innerHTML = obj.address;
        if (obj.mobile)
            document.getElementById("user_mobile").innerHTML = obj.mobile;
        if (obj.website) {
            fillRefs("user_website", obj.website);
        }
        if (obj.facebook) {
            fillRefs("user_facebook", obj.facebook);
        }
        if (obj.instagram) {
            fillRefs("user_instagram", obj.instagram);
        }
        if (obj.twitter) {
            fillRefs("user_twitter", obj.twitter);
        }
        //The rest fields

        //Load the urls
        $('#userContainer .editable').each(function () {
            $(this).attr("contenteditable", "false");
            if ($(this).hasClass("url")) {
                let url = $(this).html();
                if (url.substring(0, 7) !== 'http://' || url.substring(0, 8) !== 'https://')
                    url = 'http://' + url;
                $(this).attr("href", url);
            }
        });
        //request picture
        requestOtherUserPicture(obj.username);
        loadUserEvents(obj.email);
        loadUserParticipatingEvents(obj.email);
        loadUserTab(user_id);
        $("body").css("cursor", "default");
       
    }
    xmlhttp.send(ItemJSON);
    return false;
}

function requestOtherUserPictureGCS(url) {
    var xmlhttp = new XMLHttpRequest();
    var userpic = document.getElementById("user_userImg");
    xmlhttp.open("GET", url, true);
    xmlhttp.responseType = "blob";
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user image from GCS, message: " + xmlhttp.status);
            userpic.src = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
            return false;
        }
        // var blob = new Blob([xmlhttp.response]);
        var blob = xmlhttp.response;
        userpic.src = URL.createObjectURL(blob);
    };
    xmlhttp.send();

}

function requestOtherUserPicture(username) {
    if (!checkSession())
        return;
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
            var userpic = document.getElementById("user_userImg");
            userpic.src = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
            return false;
        }
        //Other wise...
        const obj = JSON.parse(xmlhttp.responseText);
        var cloudURL = obj.url;
        //trying to fetch GCS image
        requestOtherUserPictureGCS(cloudURL);
    }
    xmlhttp.send(ItemJSON);
}

function getUserEvents(username, callback) {
    if (!checkSession())
        return;
    var urlvariable = "/rest/user/events"
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + username +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user events, message: " + xmlhttp.status);
            return false;
        }
        const obj = JSON.parse(xmlhttp.responseText);
        callback(obj);

    }
    xmlhttp.send(ItemJSON);
}

function getUserParticipatingEvents(username, callback) {
    if (!checkSession())
        return;
    var urlvariable = "/rest/user/participatingEvents"
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + username +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load user participating events, message: " + xmlhttp.status);
            return false;
        }
        const obj = JSON.parse(xmlhttp.responseText);
        callback(obj);

    }
    xmlhttp.send(ItemJSON);
}

function loadUserEvents(username) {
    getUserEvents(username, function (obj) {
        let event_section = $("#user_events");
        event_section.empty();
        let events = obj.events;
        var event, content;
        for (i = 0; i < events.length; i++) {
            event = events[i];
            content = '<div class="row">' +
                '<a href="" id="user_event_' + event.event_id + '" onclick="return loadEvent(\'' + event.event_id + '\', false, true)">' + event.name + '</a></div>';
            event_section.append(content);
        }

    });

}

function loadUserParticipatingEvents(username) {
    getUserParticipatingEvents(username, function (obj) {
        let event_section = $("#user_participating_events");
        event_section.empty();
        let events = obj.events;
        var event, content;
        for (i = 0; i < events.length; i++) {
            event = events[i];
            content = '<div class="row">' +
                '<a href="" id="user_participating_event_' + event.event_id + '" onclick="return loadEvent(\'' + event.event_id + '\', false, true)">' + event.name + '</a></div>';
            event_section.append(content);
        }

    });
}

function loadSearchUsers(cursor) {
    if (cursor == -1)
        return;
    let user = $("#searchUserInput").val();
    let loaded = $("#loadedSearchUsers");
    if (cursor == 0)
        loaded.empty();

    //get users and paste them with options
    var urlvariable = "/rest/search?q=" + user;
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //search REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token;
    if (cursor != 0)
        ItemJSON = ItemJSON + '", "cursor": ["' + cursor[0] +'","'+cursor[1]+'"]}';
    else
        ItemJSON = ItemJSON + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            console.log("Couldn't load users, message: " + xmlhttp.status);
            return false;
        }
        const obj = JSON.parse(xmlhttp.responseText);
        let results = obj.results;
        



        var content;

        for (var i = 0; i < obj.users.length; i++) {
            content = '<div id="searched_user_'+obj.users[i].email+'" class="createEventFormInput" style="text-align:center; width:100%">';
            if (obj.users[i].pic_64 != null)
                content = content + '<img src="' + obj.users[i].pic_64 + '" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
            else
                content = content + '<img src="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png" class="rounded-circle userImg" height="20" width="20" style="display: inline-block; margin-left: 5px">';
            content = content + '<span style="display:inline-block; margin-left:5px"><a style="color: green; margin-left:4px" href="" onclick="return loadUser(\'' + obj.users[i].email + '\')">' + obj.users[i].username + '</a></span>';
            if (obj.users[i].account == "ACTIVE")
                content = content + '<span id="user_ban" style="margin-left:5px; color:red">Ban <i style="" onclick="return ban(\'' + obj.users[i].email + '\')" class="fa fa-ban likeBtn" aria-hidden="true"></i></span>';
            else {
                content = content + '<span id="user_ban" style="margin-left:5px; color:red">BANNED</span>'
                loaded.append(content);
                continue;
            }
            if (obj.users[i].state == "ENABLED")
                content = content + '<span id="user_enable" style="margin-left:5px; color:blue">Disable <i style="" onclick="return changeState(\'' + obj.users[i].email + '\',false)" class="fa fa-wheelchair likeBtn" aria-hidden="true"></i></span>';
            else
                content = content + '<span id="user_enable" style="margin-left:5px; color:lightblue">Enable <i style="" onclick="return changeState(\'' + obj.users[i].email + '\', true)" class="fa fa-wheelchair likeBtn" aria-hidden="true"></i></span>';
            if (obj.users[i].role == null)
                content = content + '<span id="user_role" style="margin-left:5px; color:green">Change Role <i style="" onclick="return changeRole(\'' + obj.users[i].email + '\')" class="fa fa-shield likeBtn" aria-hidden="true"></i> ' + 'USER' + '</span>';
            else
                content = content + '<span id="user_role" style="margin-left:5px; color:green">Change Role <i style="" onclick="return changeRole(\'' + obj.users[i].email + '\')" class="fa fa-shield likeBtn" aria-hidden="true"></i> ' + obj.users[i].role + '</span>';
            loaded.append(content);
        }

        if (results != "NO_MORE_RESULTS") {
            searchUserCursors = obj.cursor;
            $("#showmoreloadedusers").show();
        }
        else {
            searchUserCursors = -1;
            loaded.append("<p>No More Results</p>");
            $("#showmoreloadedusers").hide();
        }

    }
    xmlhttp.send(ItemJSON);
}
function roamLoadSearchUsers() {
    if (searchUserCursors != -1)
        loadSearchUsers(searchUserCursors);
}

function ban(user) {
    let confirmMsg = $("#confirmMsgSearchUser");
    confirmMsg.empty();
    confirmMsg.append('Are you sure you wish to ban? <button onclick="return confirmban(\''+user+'\')">Yes</button> <button onclick="return cancelmsg()">No</button>');
}

function unban(user) {
    let confirmMsg = $("#confirmMsgSearchUser");
    confirmMsg.empty();
    confirmMsg.append('Are you sure you wish to unban? <button onclick="return confirmState(\''+user+'\',true)">Yes</button> <button onclick="return cancelmsg()">No</button>')
}

function cancelmsg() {
    let confirmMsg = $("#confirmMsgSearchUser");
    confirmMsg.empty();
}

function changeState(user, enable) {
    let confirmMsg = $("#confirmMsgSearchUser");
    confirmMsg.empty();
    if (enable)
        confirmMsg.append('Are you sure you wish to enable the user? <button onclick="return confirmState(\''+user+'\',true)">Yes</button> <button onclick="return cancelmsg()">No</button>')
    else
        confirmMsg.append('Are you sure you wish to disable the user? <button onclick="return confirmState(\''+user+'\',false)">Yes</button> <button onclick="return cancelmsg()">No</button>')
}

function confirmban(userid) {
    let user = userid.replace(/\./g, '\\.');
    user = user.replace(/\@/g, '\\@');
    var urlvariable = "/rest/update/remove";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //confirm ban REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + userid + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't ban user, message: " + xmlhttp.status);
            return false;
        }
        alert("User successfuly banned!");
        let confirmMsg = $("#confirmMsgSearchUser");
        confirmMsg.empty();
        let userSection = $("#searched_user_" + user).remove();
        //userSection.replaceWith('<span id="user_ban" style="margin-left:5px; color:green"> Unban<i style="" onclick ="return unban(\'' + userid + '\')" class= "fa fa-ban likeBtn" aria-hidden="true"></i></span>');

        return true;
    }
    xmlhttp.send(ItemJSON);
}

function confirmState(userid, enable) {
    let user = userid.replace(/\./g, '\\.');
    user = user.replace(/\@/g, '\\@');
    let state;
    if (enable)
        state = "ENABLED";
    else
        state = "BANNED";
    var urlvariable = "/rest/update/state";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //confirm state REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + userid +
        '", "state": "' + state + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't change state of the user, message: " + xmlhttp.status);
            return false;
        }
        alert("User state successfuly changed!");
        let confirmMsg = $("#confirmMsgSearchUser");
        confirmMsg.empty();
        let userSection2 = $("#searched_user_" + user + " #user_enable");
        if (enable) {
            let userSection = $("#searched_user_" + user + " #user_ban");
            userSection.replaceWith('< span id = "user_ban" style = "margin-left:5px; color:red" > Ban < i style = "" onclick = "return ban(\'' + userid + '\')" class= "fa fa-ban likeBtn" aria - hidden="true" ></i ></span >');
            userSection2.replaceWith('<span id="user_enable" style="margin-left:5px; color:blue">Disable <i style="" onclick="return changeState(\'' + userid + '\', false)" class="fa fa-wheelchair likeBtn" aria-hidden="true"></i></span>');
        }
        else
            userSection2.replaceWith('<span id="user_enable" style="margin-left:5px; color:lightblue">Enable <i style="" onclick="return changeState(\'' + userid + '\', true)" class="fa fa-wheelchair likeBtn" aria-hidden="true"></i></span>');

        
        


        return true;
    }
    xmlhttp.send(ItemJSON);
}

function changeRole(user) {
    let confirmMsg = $("#confirmMsgSearchUser");
    confirmMsg.empty();
    if (myAppRole == "SU") {
        confirmMsg.append('Change role to GA <button onclick="return confirmRole(\'' + user + '\',\'GA\')">Change to GA</button><br><br>');
        confirmMsg.append('Change role to GBO <button onclick="return confirmRole(\'' + user + '\',\'GBO\')">Change to GBO</button><br><br>');
        confirmMsg.append('Change role to GA <button onclick="return confirmRole(\'' + user + '\',\'USER\')">Change to USER</button>');
    }
    else if (myAppRole == "GA") {
        confirmMsg.append('Change role to GBO <button onclick="return confirmRole(\'' + user + '\',\'GBO\')">Change to GBO</button><br><br>');
        confirmMsg.append('Change role to GA <button onclick="return confirmRole(\'' + user + '\',\'USER\')">Change to USER</button>');
    }
    else if (myAppRole == "GBO") {
        confirmMsg.append('ahahah you can\'t change a damn thing sucks to be you <button onclick="return cancelmsg()">Back :(</button>');
    }
}

function confirmRole(userid, role){
    let user = userid.replace(/\./g, '\\.');
    user = user.replace(/\@/g, '\\@');
    var urlvariable = "/rest/update/role";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //change role state REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "target": "' + userid +
        '", "role": "' + role + '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 204)) {
            alert("Couldn't change role of the user, message: " + xmlhttp.status);
            return false;
        }
        alert("User role successfuly changed!");
        let confirmMsg = $("#confirmMsgSearchUser");
        confirmMsg.empty();
        let userSection2 = $("#searched_user_" + user + " #user_role");
        userSection2.replaceWith('<span id="user_role" style="margin-left:5px; color:green">Change Role <i style="" onclick="return changeRole(\'' + userid + '\')" class="fa fa-shield likeBtn" aria-hidden="true"></i> ' + role + '</span>');
        return true;
    }
    xmlhttp.send(ItemJSON);
}
