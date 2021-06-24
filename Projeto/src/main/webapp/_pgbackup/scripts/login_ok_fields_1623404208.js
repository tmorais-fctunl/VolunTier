function updateLoginInputs() {
	$(document).ready(function(){
		$('body').on("keyup",'#username, #password', function(){
        	if (allFilled()) $('#loginButton').removeAttr('disabled');
			else $('#loginButton').prop("disabled", true);
   		});
	});
}

function allFilled() {
	var filled = true;
	$('#login-register-box input').each(function () {
		if ($(this).val() == '') filled = false;
	});
	return filled;
}

function showPassword() {
	var x = document.getElementById("password");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}
updateLoginInputs();


