
function createEventRequest() {

//Just check if the date-time are valid
  var now = new Date();
  now.setTime(now.getTime()-now.getTimezoneOffset()*60*1000);
  minDate = now.toISOString().substring(0,10);
  console.log(minDate);
  $('#evDate').prop('min', minDate);
  minTime = now.toISOString().substring(11,16);
  console.log(minTime);
  $('#evTime').prop('min', minTime);

  DateTimeListenerFunction();

  if (!canSubmit) {
    alert("Can not create the event until all conditions are met.");
    return false;
  }

  var urlvariable = "/rest/addEvent";
  var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
  var location = $("#evLoc").val().split(",");
  var now = new Date($("#evDate").val()+"T"+$("#evTime").val());
  var end = new Date($("#evDateEnd").val()+"T"+$("#evTimeEnd").val());
  var ItemJSON = '{"email": "' + userId +
  '", "token": "' + token +
  '", "event_name": "' + $("#evName").val() +
  '", "description": "' + $("#evDesc").val() +  
  '", "location": ["'+location[0]+'","'+location[1]+'"]' +
  ', "start_date": "' + now.toISOString() +
  '", "end_date": "' + end.toISOString() +
  '", "category": "' + $("#eventCategory").val() +
  '", "profile": "' + $("#eventPrivacyLock").val() +
  '"}';
  console.log(ItemJSON);
  var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //ADD EVENT REST URL
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open("POST", URL, false);
  xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            const obj = JSON.parse(xmlhttp.responseText);
            alert("Create event: SUCCESS. Id: " + obj.event_id);
            // localStorage.setItem(obj.event_id, ItemJSON);
            loadEvent(obj.event_id, true);
            return false;

        }
        else {
            alert("Create event: UNSUCCESS");
            return false;
        }
    }
    xmlhttp.send(ItemJSON);
}

function fillEventAttributes(attributes) {

    
    document.getElementById("event_name").innerHTML = attributes.name;
    document.getElementById("event_profile").innerHTML = attributes.profile;
    document.getElementById("event_joined_capacity").innerHTML = attributes.num_participants +"/"+attributes.capacity;
    document.getElementById("event_creator").innerHTML = attributes.owner_email;
    //Remove previous markers and add marker on preview of event


    //Address


    //date:
    var start = new Date(attributes.start_date);
    var end = new Date(attributes.end_date);
    var createdOn = new Date(attributes.creation_date);
    start = start.getDate() + "/" + start.getMonth() + "/" + start.getFullYear() + " " + start.getHours() + ":" + start.getMinutes();
    end = end.getDate() + "/" + end.getMonth() + "/" + end.getFullYear() + " " + end.getHours() + ":" + end.getMinutes();
    createdOn = createdOn.getDate() + "/" + createdOn.getMonth() + "/" + createdOn.getFullYear() + " " + createdOn.getHours() + ":" + createdOn.getMinutes();
    document.getElementById("event_start_end").innerHTML = "Created on: "+createdOn+" Starts on: "+start+" Ends on: "+end;
    document.getElementById("event_description").innerHTML = attributes.description;
    //Participants:
    //Comments:
    //Photos:
    
    //The rest
 


}

function getEvent(eventID, callback) {

    var urlvariable = "/rest/getEvent";
    var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //GET EVENT REST URL
    var xmlhttp = new XMLHttpRequest();
    var userId = localStorage.getItem("email"), token = localStorage.getItem("jwt");
    var ItemJSON = '{"email": "' + userId +
        '", "token": "' + token +
        '", "event_id": "' + eventID +
        '"}';
    xmlhttp.open("POST", URL, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json");
    xmlhttp.onload = function (oEvent) {
        if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) {
            alert("Couldn't load event info, message: " + xmlhttp.status);
            return false;
        }
        const attributes = JSON.parse(xmlhttp.responseText);
        callback(attributes);

    };
    xmlhttp.send(ItemJSON);
}

//CreateInMap is a flag to determine if it should create the marker in the maps window
function loadEvent(eventID, createInMap) {
    getEvent(eventID, function (data) {
        if (!data) {
            alert("Could not load event");
        }
        loadEventTab();
        fillEventAttributes(data);
        if (createInMap)
            loadEventMiniature(data);
    });
}
