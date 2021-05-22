<form>
    <!-- Username-->
    <div class="form-group">
        <div class="flex-nowrap input-group">
            <div class="input-group-prepend">
                <span class="bg-light input-group-text" id="addon-wrapping"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em">
                   
                    </svg></span>
            </div>
            <input type="text" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="addon-wrapping" id="username" required=""/>
        </div>
    </div>
    
    <!-- email -->
    
    <div class="form-group">
        <div class="flex-nowrap input-group">
            <div class="input-group-prepend">
                <span class="bg-light input-group-text" id="addon-wrapping2"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em">
                     <g>
                            <path fill="none" d="M0 0h24v24H0z"></path>
                            <path d="M3 3h18a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm17 4.238l-7.928 7.1L4 7.216V19h16V7.238zM4.511 5l7.55 6.662L19.502 5H4.511z"></path>
                    </g>
                    </svg></span>
            </div>
            <input type="email" class="form-control" placeholder="Email" aria-label="Email" aria-describedby="addon-wrapping" id="email" required=""/>
        </div>
    </div>
    
    
    <!-- email confirmation-->
    
    <div class="form-group">
        <div class="flex-nowrap input-group">
            <div class="input-group-prepend">
                <span class="bg-light input-group-text" id="addon-wrapping2"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em">
                     <g>
                            <path fill="none" d="M0 0h24v24H0z"></path>
                            <path d="M3 3h18a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm17 4.238l-7.928 7.1L4 7.216V19h16V7.238zM4.511 5l7.55 6.662L19.502 5H4.511z"></path>
                    </g>
                    </svg></span>
            </div>
            <input type="email" onpaste="return false;" ondrop="return false;" autocomplete="off" class="form-control" placeholder="Confirm Email" aria-label="Confirm Email" aria-describedby="addon-wrapping" id="confirm_email" required=""/>
        </div>
    </div>
    
    <!--password-->
    <div class="form-group">
        <div class="flex-nowrap input-group">
            <div class="input-group-prepend">
                <span class="bg-light input-group-text" id="addon-wrapping2"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em">
                    <g>
                            <path fill="none" d="M0 0h24v24H0z"></path>
                            <path d="M10.758 11.828l7.849-7.849 1.414 1.414-1.414 1.415 2.474 2.474-1.414 1.415-2.475-2.475-1.414 1.414 2.121 2.121-1.414 1.415-2.121-2.122-2.192 2.192a5.002 5.002 0 0 1-7.708 6.294 5 5 0 0 1 6.294-7.708zm-.637 6.293A3 3 0 1 0 5.88 13.88a3 3 0 0 0 4.242 4.242z"></path>
                        </g>
                    </svg></span>
            </div>
            <input type="password" class="form-control" placeholder="Password" aria-label="Password" aria-describedby="addon-wrapping" id="password" required=""/>
        </div>
    </div>
    
    <!--password confirmation-->
    
    <div class="form-group">
        <div class="flex-nowrap input-group">
            <div class="input-group-prepend">
                <span class="bg-light input-group-text" id="addon-wrapping2"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="1em" height="1em">
                    <g>
                            <path fill="none" d="M0 0h24v24H0z"></path>
                            <path d="M10.758 11.828l7.849-7.849 1.414 1.414-1.414 1.415 2.474 2.474-1.414 1.415-2.475-2.475-1.414 1.414 2.121 2.121-1.414 1.415-2.121-2.122-2.192 2.192a5.002 5.002 0 0 1-7.708 6.294 5 5 0 0 1 6.294-7.708zm-.637 6.293A3 3 0 1 0 5.88 13.88a3 3 0 0 0 4.242 4.242z"></path>
                        </g>
                    </svg></span>
            </div>
            <input type="password" onpaste="return false;" ondrop="return false;" autocomplete="off" class="form-control" placeholder="Confirm Password" aria-label="Confirm Password" aria-describedby="addon-wrapping" id="confirm_password" required=""/>
        </div>
    </div>
    
    <button type="submit" id="registerButton" class="btn btn-block btn-primary font-weight-bold pb-2 pl-3 pr-3 pt-2" onclick="return register()" disabled="disabled">Register</button>
    <p style="text-align: center; color: #4f859c;" id="result"></p>
</form>