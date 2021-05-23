function logout() {
    localStorage.clear();
    window.location = "../pages/index.html";
    return false;
}