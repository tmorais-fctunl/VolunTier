
window.onload = function () {
	updateChangePwdInputs();
};

function updateChangePwdInputs() {
	$('#password, #confirm_password').bind('keyup', function () {
		if (allFilled()) $('#changePwdBtn').removeAttr('disabled');
		else $('#changePwdBtn').prop("disabled", true);
	});
}

function allFilled() {
	var filled = true;
	$('#changePwdBox input').each(function () {
		if ($(this).val() == '') filled = false;
	});
	return filled;;
}
/*
function showPassword() {
	var x = document.getElementById("password");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}*/ //Maybe junto tudo num ficheiro