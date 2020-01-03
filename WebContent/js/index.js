// JavaScript Document
function isEmpty(input){
	if(input.value==""){
		return true;
	}else{
		return false;
	}		
}
function checkAccount(name){
	var accountError = document.getElementById('accountError');
	if(name==""||name==null){
		accountError.innerHTML = "请输入用户名";
		return;
	}
	var xmlhttp;
	if(window.XMLHttpRequest){
		xmlhttp = new XMLHttpRequest();
	}else{
		xmlhttp = new ActiveXObject("Microsoft.XMLHttpRequest");
	}
	xmlhttp.open("post","existAccount",true);
	xmlhttp.onreadystatechange = function(){
		if(xmlhttp.readyState==4&&xmlhttp.status==200){
			var exist = xmlhttp.responseText;
			if(exist=="false"){
				accountError.innerHTML = "用户不存在";
			}else{
				accountError.innerHTML = "";
			}
		}
	};
	//TODO 设置Content-Type请求头
	xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	xmlhttp.send("name="+name);
}
function checkPassword(passwordInput){
	var passwordError = document.getElementById("passwordError");
	if(isEmpty(passwordInput)){
		passwordError.innerHTML = "请输入密码";
	}else{
		passwordError.innerHTML = "";
	}
}
function login(){
	var accountInput = document.getElementById("account");
	var passwordInput = document.getElementById("password");
	var inputSuccess = true;//用来判断用户输入是否成功
	
	if(isEmpty(accountInput)){
		var accountError = document.getElementById("accountError");
		accountError.innerHTML="必须填写账号";
		inputSuccess = false;
	}
	if(isEmpty(passwordInput)){
		var passwordError = document.getElementById("passwordError");
		passwordError.innerHTML="必须填写密码";
		inputSuccess = false;
	}
	if(!inputSuccess){
		return;
	}
	var form = document.getElementById("form");
	form.submit();
}