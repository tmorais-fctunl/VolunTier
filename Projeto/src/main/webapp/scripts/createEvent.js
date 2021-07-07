
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
  '", "location": ["'+location[0]+'","'+location[1]+'"]' +
  ', "start_date": "' + now.toISOString() +
  '", "end_date": "' + end.toISOString() +
  '"}';
  console.log(ItemJSON);
  var URL = "https://voluntier-317915.appspot.com" + urlvariable;  //LOGIN REST URL
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open("POST", URL, false);
  xmlhttp.setRequestHeader("Content-Type", "application/json");
  xmlhttp.send(ItemJSON);
  if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
    const obj = JSON.parse(xmlhttp.responseText);
    alert("Create event: SUCCESS. Id: "+obj.event_id);

    jQuery.ajax({
        url: "../pages/contents/eventCreatedSuccess.html",
        success: function (data) {
            $('#sidebar_content').html(data);
        },
        async: false,
        complete: function() {
          document.getElementById("successId").innerHTML = "Event successfuly created under ID: "+obj.event_id;
        }
    });

    return false;

  }
  else {
    alert("Create event: UNSUCCESS");
    return false;
  }
}
