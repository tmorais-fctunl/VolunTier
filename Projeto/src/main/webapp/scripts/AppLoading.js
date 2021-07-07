window.onload = function () {
    // For debugging purposes: loadContents();
    if (!tryAuthentication()) {
        loadContent("appTab", "../pages/contents/logoutTab.html");
        return false;
    }
    loadContents();
    loadContent("appTab", "../pages/contents/loggedTab.html");

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



        }
    });
}
