function loadUser(user_id) {
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
            return false;
        }
        //Success:
        const obj = JSON.parse(xmlhttp.responseText);
        document.getElementById("user_username").innerHTML = obj.username;
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
    }
    xmlhttp.send(ItemJSON); 
}

function requestOtherUserPictureGCS(url) {
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
        var userpic = document.getElementById("user_userImg");
        userpic.src = URL.createObjectURL(blob);
    };
    xmlhttp.send();

}

function requestOtherUserPicture(username) {
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