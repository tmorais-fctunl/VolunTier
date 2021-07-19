var tabStack = [];
var openedTab;
var tabPrev;
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
    tabstack = [];
    openedTab = document.getElementById(tab);
    tabPrev = openedTab.id;
 
}

// Get the element with id="defaultOpen" and click on it
document.getElementById("defaultOpen").click();


function loadEventTab(id) {
    openedTab.style.display = "none";
    document.getElementById(tabPrev).style.display = "none";
    document.getElementById("Event").style.display = "block";
    tabStack.push({ Tab: "Event", id: id });
    tabPrev = "Event";
}

function closeEventTab() {
    document.getElementById("Event").style.display = "none";
    tabStack.pop();
    let openPrev;
    if (tabStack.length == 0) 
        openedTab.style.display = "block";
    else {
        openPrev = tabStack.pop();
        let tab = openPrev.Tab;
        document.getElementById(openPrev.Tab).style.display = "block";
        if (tab == "Event")
            loadEvent(openPrev.id);
        else if (tab == "User")
            loadUser(openPrev.id);
    }
}

function loadUserTab(id) {
    openedTab.style.display = "none";
    document.getElementById(tabPrev).style.display = "none";
    document.getElementById("User").style.display = "block";
    tabStack.push({ Tab: "User", id: id });
    tabPrev = "User";

}

function closeUserTab() {
    document.getElementById("User").style.display = "none";
    tabStack.pop();
    let openPrev;
    if (tabStack.length == 0)
        openedTab.style.display = "block";
    else {
        openPrev = tabStack.pop();
        let tab = openPrev.Tab;
        document.getElementById(openPrev.Tab).style.display = "block";
        if (tab == "Event")
            loadEvent(openPrev.id);
        else if (tab == "User")
            loadUser(openPrev.id);
    }
}




