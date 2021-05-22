$(document).ready(function () {
                //Set trigger and container variables
                var trigger = $("#login-registerLink"), container = $("#login-register-box");
                var login = true;

                //fire on click
                trigger.on('click', function () {

                    //Set $this for re-use 
                    var $this = $(this);
                    target = $this.data('target');

                    //load target page into the container
                    container.load("./contents/"+target + '.php');
                    $.ajax({
                        complete: function () {
                            if (login) {
                                $this.data('target', 'login-box');
                                $this.text("Have an account? Log in");
                                updateRegisterInputs();
                            }
                            else {
                                $this.data('target', 'register-box');
                                $this.text("Don't have an account? Register");
                                updateLoginInputs();
                            }
                            login = !login;
                        }
                    });
                    //Stop normal link behaviour

                    return false;
                });

            });