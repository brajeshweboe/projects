<section class="login-page">

<div class="logo"><img src="/erpTheme/images/erp-logo.png" alt=""></div>
<div class="login-box">
<h3>Sign In</h3>

<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
<div class="form-group">
<label>Username</label>
<input type="text" name="USERNAME" class="form-control" value="${username!}" placeholder="Enter username">
</div>

<div class="form-group">
<label>Password</label>
<input type="password" name="PASSWORD" class="form-control" placeholder="Enter Password">
</div>

<div class="form-group">
<div class="row">
<div class="col-sm-6 col-xs-6"><input type="checkbox" name=""> Remember me</div>
<div class="col-sm-6 col-xs-6 text-right"><a href="#model">Forgot pwd?</a></div>
</div>
</div>

<input type="submit" name="" class="btn btn-danger" value="Login">
</form>

</div>
</section>
