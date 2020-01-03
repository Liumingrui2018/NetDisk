/**
 * 
 */
var inputSuccess = true;//用来判断用户输入是否成功

function isEmpty(input){
	if(input.value==""){
		return true;
	}else{
		return false;
	}		
}
/**
检查输入的账号是否合法
@param name 账号
*/
function checkAccount(name){
	var accountError = document.getElementById("accountError");
	if(name==null||name==""){
		accountError.innerHTML = "请输入账号";
		return;
	}
	var xmlhttp;
	if(window.XMLHttpRequest){
		xmlhttp = new XMLHttpRequest();
	}else{
		xmlhttp = new ActiveXObject("Microsoft.XMLHttpRequest");
	}
	xmlhttp.open("post","existAccount",true);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xmlhttp.onreadystatechange = function(){
		if(xmlhttp.readyState==4&&xmlhttp.status==200){
			var response = xmlhttp.responseText;
			if(response=="true"){
				accountError.innerHTML = "该用户已被注册";
				inputSuccess = false;
			}else{
				accountError.innerHTML = "";
			}
		}
	};
	xmlhttp.send("name="+name);
}
function checkPassword(password){
	var passwordError = document.getElementById("passwordError");
	if(password==null||password==""){
		passwordError.innerHTML = "请输入密码";
	}
}
/**
 * 判断输入的密码与确认密码是否相等
 * @returns 如果输入的密码与确认密码相等，返回true
 */
function checkConfirmPwd(){
	var password = document.getElementById("password").value;
	var confirmPwd = document.getElementById("confirmPwd").value;
	var equal = password==confirmPwd;
	if(!equal){
		confirmError.innerHTML = "确认密码与输入输入密码不一致";
		inputSuccess = false;
	}else{
		confirmError.innerHTML = "";
		inputSuccess = true;
	}
	return equal;
}
/**
注册账号的函数
*/
function register(){
	var accountInput = document.getElementById("account");
	var passwordInput = document.getElementById("password");
	var confirmInput = document.getElementById("confirmPwd");
	
	var accountError = document.getElementById("accountError");
	var passwordError = document.getElementById("passwordError");
	var confirmError = document.getElementById("confirmError");
	
	if(isEmpty(accountInput)){
		accountError.innerHTML="必须填写账号";
		inputSuccess = false;
	}else{
		checkAccount(accountInput.value);
	}
	if(isEmpty(passwordInput)){
		passwordError.innerHTML="必须填写密码";
		inputSuccess = false;
	}
	if(passwordInput.value!=confirmInput.value){
		confirmError.innerHTML = "确认密码与输入输入密码不一致";
		inputSuccess = false;
	}
	if(!inputSuccess){
		return;
	}
	var form = document.getElementById("form");
	form.submit();
}