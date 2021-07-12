
var profileVisibility = false;
var infoElementsRef = document.getElementsByClassName("info");
var infoElements = [];
updateInfoElements();
var imgfile, imgblob, imgext, imgSrcPrev;
var fileData;
/*
 * Fazer o seguinte:
 * Carregamos info do user, se nao tiver img, imgsrc fica igual à default e carrega na página. 
 * Caso haja, carregamos a img
 * Para saber se a imagem foi mudada, ou temos um booleano, ou comparamos novo src com antigo (imgsrc)
 */

//Image upload scripts begining:
//Script to redirect button click to file explorer input
function uploadImg() {
    $('#imgupload').trigger('click');
}
//Script to load the photo to the user image
var loadFile = function (event) { 
    var image = document.getElementById('userImg');
    imgfile = event.target.files[0];
    imgext = event.target.value.split('.')[1];
    console.log(imgext);
    imgblob = URL.createObjectURL(imgfile);
    image.src = imgblob; 

    fileData = new Blob([imgfile]);
};
//Script to put image into base64 format and call send to backend
function saveImage() {

   var blobToBase64 = function (blob, callback) {
        var reader = new FileReader();
        reader.onload = function () {
            var dataUrl = reader.result;
            var base64 = dataUrl.split(',')[1];
            callback(base64);
        };
        reader.readAsDataURL(blob);
    };

    const img = new Image();
    img.src = imgblob;
    img.onload = function (ev) {

        URL.revokeObjectURL(imgblob); //release memory
        //Do whatever I need here:
        const newWidth = 200, newHeight = 200;
        const canvas = document.createElement('canvas');
        canvas.width = newWidth;
        canvas.height = newHeight;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, newWidth, newHeight);

        canvas.toBlob(function (blob) {
            blobToBase64(blob, function (base64) {
                //console.log(base64);
                saveUserPhoto(base64);
            });
        }, 'image/'+imgext, 1);
    }  
}
//Script to send to backend and call send to cloud storage
function saveUserPhoto(base64) {
    var urlvariable = "/rest/update/picture";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LookUp REST URL
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", URL, false);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "data": "' + 'data:image/'+imgext+';base64,' + base64 + '"}';
    xmlhttp.send(ItemJSON);
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
        const obj = JSON.parse(xmlhttp.responseText);
        let cloudURL = obj.url;
        sendFile(cloudURL, fileData);
        return false;
    }
    console.log("update request: " + xmlhttp.status);
}
//Script to convert image to byte array, used to send to cloud storage
function getBuffer(fileData) {
    return function (resolve) {
        var reader = new FileReader();
        reader.readAsArrayBuffer(fileData);
        reader.onload = function () {
            var arrayBuffer = reader.result
            var bytes = new Uint8Array(arrayBuffer);
            resolve(bytes);
        }
    }
}
//send file now in byte array to cloud storage
function sendFile(cloudURL, file) {
    console.log("Trying to send file to " + cloudURL);
    var promise = new Promise(getBuffer(file));
    // Wait for promise to be resolved, or log error.
    promise.then(function (data) {
        // Here you can pass the bytes to another function.
        console.log(data);

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("PUT", cloudURL, true);
        xmlhttp.onload = function (oEvent) {
            // Uploaded.
            console.log("Request status: " + xmlhttp.status);
        };
        xmlhttp.send(data);


    }).catch(function (err) {
        console.log('Error: ', err);
    });
}
//Image upload scripts end

function updateInfoElements() {
  //console.log(infoElementsRef.length);
    for (i = 0; i < infoElementsRef.length; i++) {
        infoElements[i] = [];
        infoElements[i] = [infoElementsRef[i].getAttribute("id"), infoElementsRef[i].innerHTML];
    }
    imgSrcPrev = $("#userImg").attr("src");
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
    if (imgSrcPrev != $("#userImg").attr("src")) {
        saveImage();
    }
    else console.log("User Image not changed.")
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
        var profile_html = 'Profile visibility: <span class="lock" id="profilePrivacyLock" style = "margin-left:10px" ></span>'
        $('#profile_visibility').html(profile_html);
        if (profile == "PUBLIC") {
            $(".lock").toggleClass('unlocked');
            profileVisibility = true;
        }
        $("#profilePrivacyLock").click(function () {
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
        //Allow user to upload image
        $("#userImg").toggleClass("imgUpload");
        $("#OpenImgUpload").attr("style", "");
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

        //Unable user to upload image
        $("#userImg").toggleClass("imgUpload");
        $("#OpenImgUpload").attr("style", "display: none");
        

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
        '", "token": "' + token +
        '", "target": "' + userId;
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






