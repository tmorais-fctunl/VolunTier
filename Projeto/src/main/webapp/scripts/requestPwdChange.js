function requestChangePwd() {
    $('body').css('cursor', 'progress');
    var urlvariable = "/rest/forgotpassword";
    var ItemJSON = '{"email": "' + document.getElementById("email").value + '"}';
    var URL = "https://voluntier-317915.appspot.com/" + urlvariable;  //FORGOT PWD REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.send(ItemJSON);
    document.getElementById("result").innerHTML = "If the credentials match you will receive an email to change your password.";
    $('body').css('cursor', 'default');
    return false;
}