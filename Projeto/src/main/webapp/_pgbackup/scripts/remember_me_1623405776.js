const rmCheck = document.getElementById("rememberMe"),
    emailInput = document.getElementById("email");

if (localStorage.checkbox && localStorage.checkbox !== "") {
  rmCheck.setAttribute("checked", "checked");
  emailInput.value = localStorage.email;
} else {
  rmCheck.removeAttribute("checked");
  emailInput.value = "";
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