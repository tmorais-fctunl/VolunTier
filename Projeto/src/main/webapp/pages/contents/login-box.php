<form> 
    <div class="form-group"> 
        <div class="flex-nowrap input-group"> 
            <div class="input-group-prepend"> 
                <span class="bg-light input-group-text" id="addon-wrapping"> <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em"> 
</svg> </span> 
            </div>             
            <input type="text" class="form-control" placeholder="Email" aria-label="Email" aria-describedby="addon-wrapping" id="email" required=""/> 
        </div>         
    </div>     
    <div class="form-group"> 
        <div class="flex-nowrap input-group"> 
            <div class="input-group-prepend"> 
                <span class="bg-light input-group-text" id="addon-wrapping2"> <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em"> 
                        <g> 
                            <path fill="none" d="M0 0h24v24H0z"></path>                             
                            <path d="M10.758 11.828l7.849-7.849 1.414 1.414-1.414 1.415 2.474 2.474-1.414 1.415-2.475-2.475-1.414 1.414 2.121 2.121-1.414 1.415-2.121-2.122-2.192 2.192a5.002 5.002 0 0 1-7.708 6.294 5 5 0 0 1 6.294-7.708zm-.637 6.293A3 3 0 1 0 5.88 13.88a3 3 0 0 0 4.242 4.242z"></path>                             
                        </g>                         
                    </svg> </span> 
            </div>             
            <input type="password" class="form-control" placeholder="Password" aria-label="Password" aria-describedby="addon-wrapping" id="password" required=""/> 
        </div>         
    </div>     
    <div class="form-group form-check"> 
        <!--How to do this? Remember to search-->         
        <input type="checkbox" class="form-check-input" value="IsRememberMe" id="rememberMe"/> 
        <label class="form-check-label" for="rememberMe">Remember me</label>         
        <p> <!--Make sure the check mark makes the password visible--> <input type="checkbox" class="form-check-input" id="exampleCheck1" onclick="showPassword()"/> <label class="form-check-label" for="exampleCheck1">Show Password</label> </p> 
    </div>     
    <button type="submit" id="loginButton" class="btn btn-block btn-primary font-weight-bold pb-2 pl-3 pr-3 pt-2" onclick="return SignIn()" disabled="disabled">Sign In</button>     
    <!--Remember to do the forgot password form and password change page for the server to send-->     
    <p style="text-align: center; margin-top: 10px;"><a href="/pages/forgotpwd.html" id="forgotpwdLink" data-target="forgotpw-box">Forgot password?</a></p> 
    <p style="text-align: center; color: #4f859c;" id="result"></p> 
</form>