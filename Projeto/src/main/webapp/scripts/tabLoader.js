function openTab(evt, tab) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tab).style.display = "block";
    evt.currentTarget.className += " active";
}

// Get the element with id="defaultOpen" and click on it
document.getElementById("defaultOpen").click();

var openedTab;
function loadEventTab() {
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        if (tabcontent[i].style.display == "block") {
            console.log(tabcontent[i].id);
            if (tabcontent[i].id == "User")
                openedTab = tabcontent[0];
            else
                openedTab = tabcontent[i];
        }
    }
    openedTab.style.display = "none";
    document.getElementById("Event").style.display = "block";

}

function closeEventTab() {
    document.getElementById("Event").style.display = "none";
    openedTab.style.display = "block";
}

function loadUserTab() {
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        if (tabcontent[i].style.display == "block") {
            console.log(tabcontent[i].id);
            openedTab = tabcontent[i];
        }
    }
    openedTab.style.display = "none";
    document.getElementById("User").style.display = "block";

}

function closeUserTab() {
    document.getElementById("User").style.display = "none";
    openedTab.style.display = "block";
}




