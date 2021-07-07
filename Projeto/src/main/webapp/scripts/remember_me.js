var rmCheck, emailInput;
function initiateRememberMe () {
  rmCheck = document.getElementById("rememberMe"),
  emailInput = document.getElementById("email");

  try {
      if (localStorage.checkbox && localStorage.checkbox !== "") {
        rmCheck.setAttribute("checked", "checked");
        emailInput.value = localStorage.emailCheckbox;
      } else {
        rmCheck.removeAttribute("checked");
        emailInput.value = "";
      }
  }
  catch (err) {
    console.log("Couldn't load inital \"remember me\" attributes");
  }
}
function isRememberMe() {
  if (rmCheck.checked && emailInput.value !== "") {
    localStorage.emailCheckbox = emailInput.value;
    localStorage.checkbox = rmCheck.value;
  } else {
    localStorage.emailCheckbox = "";
    localStorage.checkbox = "";
  }
}
