window.onload = function () {
	updateRequestChangePwdInputs();
};

function updateRequestChangePwdInputs() {
	$('#email').bind('keyup', function () {
		if (allFilled()) $('#requestChangeBtn').removeAttr('disabled');
		else $('#requestChangeBtn').prop("disabled", true);
	});
}

function allFilled() {
	var filled = true;
	$('#requestChangePwdBox input').each(function () {
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

//ADD something to make sure email is valid type email