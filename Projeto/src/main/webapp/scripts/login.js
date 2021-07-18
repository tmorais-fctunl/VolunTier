function login() {
    $('body').addClass('waiting');
    var urlvariable = "/rest/login";
    var encryptedP = sha512(document.getElementById("password").value);
    var ItemJSON = '{"user": "' + document.getElementById("email").value + '", "password": "' + encryptedP + '"}';
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LOGIN REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

        const obj = JSON.parse(xmlhttp.responseText);

        localStorage.setItem('jwt', obj.accessToken);
        localStorage.setItem('jwrt', obj.refreshToken);
        localStorage.setItem("email", obj.email);
        localStorage.setItem("jwt_creation_date", obj.creationDate)
        localStorage.setItem("jwt_expiration_date", obj.expirationDate)
        localStorage.setItem("jwrt_expiration_date", obj.refresh_expirationDate)
        localStorage.setItem("username", obj.username);


            $('body').removeClass('waiting');
            document.getElementById("result").innerHTML = "Login successful";
            console.log("Login successful");
            $.ajax({
                //We need to wait fror the page to load before changing any fields.
                complete: function () {
                  window.location = "../pages/App.html";
                  return false;
                }
            });
          /*  window.location = "../pages/App.html";
            return false;*/

    }
    else {
        $('body').removeClass('waiting');
        switch (xmlhttp.status) {
            case 403:
                document.getElementById("result").innerHTML = "Login unsuccessful";
                break;
            case 500:
                document.getElementById("result").innerHTML = "Server error, try later";
                break;
            case 400:
                document.getElementById("result").innerHTML = "Check requirements";
                break;
            default: break;
        }
    }
    return false;


}
