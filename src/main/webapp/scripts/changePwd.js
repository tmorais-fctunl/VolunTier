function changePwd() {
    $('body').css('cursor', 'progress');
    const params = new URLSearchParams(window.location.search);
    if (!params.has("t")) {
        window.location = "../pages/error/" + 404 + ".html";
        return false;
    }
    const token = params.get("t");
    var encryptedP = sha256(document.getElementById("password").value);
    var encryptedCP = sha256(document.getElementById("confirm_password").value);
    var urlvariable = "/rest/forgotpassword/change?t=" + token  ;
    var ItemJSON = '{"password": "' + encryptedP + '", "confirmation_password": "' + encryptedCP + '"}';
    var URL = "https://voluntier-312115.ew.r.appspot.com" + urlvariable;  //CHANGE PWD REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 204) {

        var timer = setTimeout(function () {
            $('body').css('cursor', 'default');
            document.getElementById("result").innerHTML = "Password successfully changed. Login with your new credentials";
        }, 2000);


    }
    else {
        //Put something like "login unsuccessful below the login button
        window.location = "../pages/error/"+xmlhttp.status+".html";
        $('body').css('cursor', 'default');
    }
    return false;
}