//Request user info
function requestUserInfo() {
    var urlvariable = "/rest/user";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + token + '"}';
    xmlhttp.send(ItemJSON);

    if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200))
        return false;

    /*
     * Do token refresh or logout if it fails*/


    //Get the content from the json response and fill in the spots with the correct info
    const obj = JSON.parse(xmlhttp.responseText);
    $.ajax({
        //We need to wait fror the page to load before changing any fields.
        complete: function () {
            document.getElementById("profile_username").innerHTML = obj.username;
            document.getElementById("user_tag").innerHTML = obj.username;
            document.getElementById("profile_fullname").innerHTML = obj.full_name;
            document.getElementById("profile_visibility").innerHTML = obj.profile;
            document.getElementById("profile_email").innerHTML = obj.email;
            if (obj.region)
                document.getElementById("profile_region").innerHTML = obj.region;
            if (obj.address)
                document.getElementById("profile_address").innerHTML = obj.address;
            if (obj.mobile)
                document.getElementById("profile_mobile").innerHTML = obj.mobile;
            if (obj.website)
                document.getElementById("profile_website").innerHTML = obj.website;
            if (obj.facebook)
                document.getElementById("profile_facebook").innerHTML = obj.facebook;
            if (obj.instagram)
                document.getElementById("profile_instagram").innerHTML = obj.instagram;
            if (obj.twitter)
                document.getElementById("profile_twitter").innerHTML = obj.twitter;
            //The rest fields



            //request picture
            requestUserPicture(obj.username);
        }
    });
}

function requestUserPictureGCS(url) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", url, true);
    xmlhttp.responseType = "blob";
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load user image from GCS, message: " + xmlhttp.status);
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
    var urlvariable = "/rest/picture/"+username;
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + token + '"}';
    xmlhttp.open("POST", URL, true);
    
    xmlhttp.setRequestHeader("Content-Type", "application/json");

    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load user image, message: " + xmlhttp.status);
            return false;
        }
        //Other wise...
        const obj = JSON.parse(xmlhttp.responseText);
        var cloudURL = obj.url;
        var pic = obj.pic; 
        var size = obj.size;
        //Can't do nothing with pic right now

        //TODO


        console.log(cloudURL);
        //trying to fetch GCS image
        requestUserPictureGCS(cloudURL);
        


    }
    xmlhttp.send(ItemJSON);
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
}
