window.onload = function onLoad() {

    //Check if there's any data that implies a signed in user
    if ("email" in localStorage) {
        console.log("User in storage");
        //Authenticate user
        var authenticated = authenticate(localStorage.getItem("email"), localStorage.getItem("jwt"));

        if (authenticated) {
            console.log("validated user");
            /*document.getElementById("introductionID").innerHTML =
                '<h3 class="u-align-center u-custom-font u-font-georgia u-text u-text-1">Logged in! ' + localStorage.getItem("user_id") + '</h3>';*/
            window.location = "Map.html"
            // return;
        }
        console.log("User not validated");


        var expDate = new Date(localStorage.getItem("jwt_expiration_date"));
        var currDate = new Date();
        var expired = expDate < currDate;
        if (expired) {
            console.log("jwt expired");
            var expRefDate = new Date(localStorage.getItem("jwrt_expiration_date"));
            expired = expRefDate < currDate;
            if (expired) {
                console.log("jwrt expired");
                localStorage.clear();
                return;
            }
            if (refreshToken(localStorage.getItem("email"), localStorage.getItem("jwrt"))) {
                console.log("jwrt refreshed and validated");
                /* document.getElementById("introductionID").innerHTML =
                     '<h3 class="u-align-center u-custom-font u-font-georgia u-text u-text-1">Logged in! ' + localStorage.getItem("user_id") + '</h3>';*/
                window.location = "Map.html"
                //return;
            }
            else {
                console.log("jwrt could not be refreshed");
                localStorage.clear();
                return;
            }
        }
        else {
            console.log("jwt not expired, incorrect token");
            return;
        }

    }
    else {
        console.log("User not in storage");
        return;
    }
}

//Returns true if it successfuly refreshed token
function refreshToken(userId, rtoken) {
    var urlvariable = "/rest/refresh";
    var URL = "https://voluntier-312115.ew.r.appspot.com" + urlvariable;  //REFRESH REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + rtoken + '"}';
    xmlhttp.send(ItemJSON);

    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
        const obj = JSON.parse(xmlhttp.responseText);
        localStorage.setItem('jwt', obj.accessToken);
        localStorage.setItem('jwrt', obj.refreshToken);
        localStorage.setItem("jwt_creation_date", creationDate)
        localStorage.setItem("jwt_expiration_date", expirationDate)
        localStorage.setItem("jwrt_expiration_date", refresh_expirationDate)
        return true;
    }
    else return false;
}

//Returns true if was successful to authenticate
function authenticate(userId, token) {
    var urlvariable = "/rest/validate";
    var URL = "https://voluntier-312115.ew.r.appspot.com" + urlvariable;  //VALIDATE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + token + '"}';
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 204)
        return true;
    else return false;
}