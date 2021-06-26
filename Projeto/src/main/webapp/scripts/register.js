function register() {
    $('body').css('cursor', 'progress');
    var urlvariable = "/rest/register";

    if (!match()) {
    	$('body').css('cursor', 'default');
        return false;
    }

    var encryptedP = sha256(document.getElementById("password").value);
    //var encryptedCP = sha256(document.getElementById("confirm_password").value);
    var ItemJSON;
    ItemJSON = '{"username": "' + document.getElementById("username").value + '", "password": "' + encryptedP + '", "email":"' + document.getElementById("email").value + '"}';


    URL = "https://voluntier-317915.appspot.com" + urlvariable;  //Your URL

    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 204) {
        //Load the page informing the user to check his/her email
        $('body').css('cursor', 'default');
        window.location = "../pages/registerInfo.html";
    }
    else {
        //Display something along the lines of "Registration unsuccessful", try to get cases for 403, 400, etc
        $('body').css('cursor', 'default');
        switch (xmlhttp.status) {
            case 403:
                document.getElementById("result").innerHTML = "Username already in use";
                break;
            case 500:
                document.getElementById("result").innerHTML = "Something went wrong on our side, please try again at a later time";
                break;
            case 400:
                document.getElementById("result").innerHTML = "Make sure you're using valid characters (UTF-8)";
                break;
            default: break;
        }     
    }
    return false;

    //Add something that matches the pwd and the confpwd
}

function match() {
    var pw = document.getElementById("password").value;
    var cpwd = document.getElementById("confirm_password").value;
    if (pw != cpwd) {
        document.getElementById("result").innerHTML = "Passwords don't match";
        return false;
    }

    var e = document.getElementById("email").value;
    var ce = document.getElementById("confirm_email").value;
    if (e != ce) {
        document.getElementById("result").innerHTML = "Emails don't match";
        return false;
    }
    return true;
}