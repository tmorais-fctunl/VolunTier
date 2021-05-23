function updateRegisterInputs() {
	$(document).ready(function(){
		$('body').on("keyup",'#username, #password, #confirm_password, #email, #confirm_email', function(){
			if (allFilled()) $('#registerButton').removeAttr('disabled');
			else $('#registerButton').prop("disabled", true);
		});
	});
}

/*function allFilled() {
	var filled = true;
	$('#login-register-box input').each(function () {
		if ($(this).val() == '') filled = false;
	});
	return filled;
}*/
/*
function showPassword() {
	var x = document.getElementById("password");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}*/ //Maybe junto tudo num ficheiro