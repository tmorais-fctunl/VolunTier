function logout() {
    clearLoggedInfo();
    window.location = "../pages/index.html";
    return false;
}
function clearLoggedInfo() {
    localStorage.removeItem('email');
    localStorage.removeItem('jwt');
    localStorage.removeItem('jwrt');
    localStorage.removeItem("jwt_creation_date");
    localStorage.removeItem("jwt_expiration_date");
    localStorage.removeItem("jwrt_expiration_date");
}