function login() {
    $('body').css('cursor', 'progress');
    var urlvariable = "/rest/login";
    var encryptedP = sha256(document.getElementById("password").value);
    var ItemJSON = '{"user_id": "' + document.getElementById("username").value + '", "password": "' + encryptedP + '"}';
    var URL = "https://voluntier-312115.ew.r.appspot.com" + urlvariable;  //LOGIN REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

        const obj = JSON.parse(xmlhttp.responseText);

        localStorage.setItem('jwt', obj.accessToken);
        localStorage.setItem('jwrt', obj.refreshToken);
        localStorage.setItem("user_id", obj.user_id);
        localStorage.setItem("jwt_creation_date", obj.creationDate)
        localStorage.setItem("jwt_expiration_date", obj.expirationDate)
        localStorage.setItem("jwrt_expiration_date", obj.refresh_expirationDate)

        var timer = setTimeout(function () {
            $('body').css('cursor', 'default');
            document.getElementById("result").innerHTML = "Login successful";
            window.location = "../pages/App.html";
        }, 2000);


    }
    else {
        $('body').css('cursor', 'default');
        switch (xmlhttp.status) {
            case 403:
                document.getElementById("result").innerHTML = "Login unsuccessful";
                break;
            case 500:
                document.getElementById("result").innerHTML = "Something went wrong on our side, please try again at a later time";
                break;
            case 400:
                document.getElementById("result").innerHTML = "Make sure you're using valid characters (UTF-8)";
        }     
    }
    return false;


}