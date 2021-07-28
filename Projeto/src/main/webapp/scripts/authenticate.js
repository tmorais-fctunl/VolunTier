//Check if there's any data that implies a signed in user

function tryAuthentication() {

    if (!("email" in localStorage)) {
        console.log("User not in storage");
        clearLoggedInfo();
        return false;
    }
    console.log("User in storage");
    //Authenticate user
   var authenticated = authenticate(localStorage.getItem("email"), localStorage.getItem("jwt"));

    if (authenticated) {
        console.log("validated user");
        return true;
    }
    console.log("User not validated");
    var expDate = new Date(parseInt(localStorage.getItem("jwt_expiration_date")));
    var currDate = new Date();
    console.log("Exp: " + expDate);
    console.log("Curr: " + currDate);
    var expired = expDate < currDate;
    if (!expired) {
        console.log("jwt not expired, incorrect token");
        clearLoggedInfo();
        return false;
    }
    console.log("jwt expired");
    var expRefDate = new Date(parseInt(localStorage.getItem("jwrt_expiration_date")));
    expired = expRefDate < currDate;
    if (expired) {
        console.log("jwrt expired");
        clearLoggedInfo();
        return false;
    }
    if (!refreshToken(localStorage.getItem("email"), localStorage.getItem("jwrt"))) {
        console.log("jwrt could not be refreshed");
        clearLoggedInfo();
        return false;
    }
    console.log("jwrt refreshed and validated");
    return true;
}

//Returns true if it successfuly refreshed token
function refreshToken(userId, rtoken) {
    var urlvariable = "/rest/refresh";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //REFRESH REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + rtoken + '"}';
    xmlhttp.send(ItemJSON);

    if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200))
        return false;   

    const obj = JSON.parse(xmlhttp.responseText);
    localStorage.setItem('jwt', obj.accessToken);
    localStorage.setItem('jwrt', obj.refreshToken);
    localStorage.setItem("jwt_creation_date", obj.creationDate)
    localStorage.setItem("jwt_expiration_date", obj.expirationDate)
    localStorage.setItem("jwrt_expiration_date", obj.refresh_expirationDate)
    //localStorage.setItem("username", obj.username)
    return true;

}

//Returns true if was successful to authenticate
function authenticate(userId, token) {
    var urlvariable = "/rest/validate";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //VALIDATE REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var ItemJSON = '{"email": "' + userId + '", "token": "' + token + '"}';
    xmlhttp.send(ItemJSON);
    return (xmlhttp.readyState == 4 && xmlhttp.status == 204);
}

function clearLoggedInfo() {
    localStorage.removeItem('email');
    localStorage.removeItem('jwt');
    localStorage.removeItem('jwrt');
    localStorage.removeItem("jwt_creation_date");
    localStorage.removeItem("jwt_expiration_date");
    localStorage.removeItem("jwrt_expiration_date");
    localStorage.removeItem('username');

}

function checkSession() {
    //ver now >= exp
    var now = new Date();
    let exp = localStorage.getItem("jwt_expiration_date");
    var expDate = new Date(parseInt(exp));
    let jwrtExp = new Date(parseInt(localStorage.getItem("jwrt_expiration_date")));
    if ((now >= expDate)) {
        if (now < jwrtExp) {
            if (refreshToken(localStorage.getItem("email"), localStorage.getItem("jwrt")))
                return true;
            else {
                //Return to login
                alert("Session has expired. You will be redirected to the login page.");
                clearLoggedInfo();
                window.location = "../pages/index.html";
                return false;
            }
        }
        else {
            //Return to login
            alert("Session has expired. You will be redirected to the login page.");
            clearLoggedInfo();
            window.location = "../pages/index.html";
            return false;
        }
    }
    return true;
}