
var profileVisibility = false;
var infoElementsRef = document.getElementsByClassName("info");
var infoElements = [];
updateInfoElements();

function updateInfoElements() {
  console.log(infoElementsRef.length);
    for (i = 0; i < infoElementsRef.length; i++) {
        infoElements[i] = [];
        infoElements[i] = [infoElementsRef[i].getAttribute("id"), infoElementsRef[i].innerHTML];
    }
}

function editBtn() {
    console.log("Now hiding edit button and showing save button");
    //needs to turn the content editable on
    turnContent(true);
    profileBtnSwap();
}

function saveBtn() {
    console.log("Now hiding save button and showing edit button");
    //needs to send request to server to save new element values
    //needs to turn the content editable off
    turnContent(false);
    saveInfo();
    profileBtnSwap();
}

function profileBtnSwap() {
    var x = document.getElementById("editBtn");
    var y = document.getElementById("saveBtn");
    if (x.style.display === "none") {
        x.style.display = "block";
        y.style.display = "none";
    } else {
        x.style.display = "none";
        y.style.display = "block";
    }
}

//Content on = true for allowing editing, off = false for displaying fields without edition
function turnContent(on) {
    if (on) {
        infoElementsRef = document.getElementsByClassName("info");
        updateInfoElements();

        //Allow editable fields
        $('#profileContainer .editable').each(function () {
            // console.log($(this.value));
            $(this).attr("contenteditable", "true");
        });

        //Change profile icon
        var profile = document.getElementById("profile_visibility").innerHTML;
        var profile_html = 'Profile visibility: <pan class="lock" style = "margin-left:10px" ></span>'
        $('#profile_visibility').html(profile_html);
        if (profile == "PUBLIC") {
            $(".lock").toggleClass('unlocked');
            profileVisibility = true;
        }
        $(".lock").click(function () {
            $(this).toggleClass('unlocked');
            profileVisibility = !profileVisibility;
        });

        //Change region to list
        var prevRegion = document.getElementById("profile_region").innerHTML;
        $.get("contents/profile_region_list.html", function (data) {
            $('#profile_region_div').html(data);
        });
        if (prevRegion != "Country") {
            $.ajax({
                //We need to wait fror the page to load before changing any fields.
                complete: function () {
                    $('[name="country"]').val(prevRegion);
                    $('[name="country"]').attr("placeholder", prevRegion);
                }
            });
        }
    }
    else {

        //Disable editing
        $('#profileContainer .editable').each(function () {
            $(this).attr("contenteditable", "false");
        });

        //Disable editing for the profile and display without icon
        var profile = document.getElementById("profile_visibility");
        var profile_data;
        if (profileVisibility)
            profile_data = "PUBLIC";
        else
            profile_data = "PRIVATE";
        $('#profile_visibility').html(profile_data);

        //Disable region editon and display as plain text
        var prevRegion = $('[name="country"]').val();
        if (prevRegion == '')
            prevRegion = $('[name="country"]').attr("placeholder");
        var data = '<p class="text-muted font-size-sm editable info" id="profile_region" name="region">' + prevRegion + '</p>';
        $('#profile_region_div').html(data);

    }
    return false;
}




function saveInfo() {
    var urlvariable = "/rest/update/profile";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSONBeginning = '{"email": "' + userId +
        '", "token": "' + token;
    var ItemJSONEnd = '"}';
    var ItemJSONInfo = "", infoName, infoPrev, infoNow, infoId, newInfo;

    for (i = 0; i < infoElements.length; i++) {
        infoId = infoElements[i][0];
        infoPrev = infoElements[i][1];
        //console.log(infoId + " " + infoPrev);
        var element = document.getElementById(infoId);
        infoNow = element.innerHTML;
        infoName = element.getAttribute("name");

        if (infoPrev != infoNow) {
            newInfo = '", "' + infoName + '": "' + infoNow;
            console.log(newInfo);
            ItemJSONInfo = ItemJSONInfo + newInfo;
        }
    }

    /*console.log(ItemJSONBeginning);
    console.log(ItemJSONInfo);
    console.log(ItemJSONEnd);*/

    if (ItemJSONInfo == "") {
        console.log("No changes made");
        return false;
    }

    var ItemJSON = ItemJSONBeginning + ItemJSONInfo + ItemJSONEnd;


    console.log(ItemJSON);
    xmlhttp.send(ItemJSON);

    if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200))
        return false;
    console.log("update request: " + xmlhttp.status);

    /*Do token refresh or logout if it fails*/



}
