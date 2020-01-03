<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" href="css/index.css"/>
		<script type="text/javascript" src="js/register.js"></script>
		<title>账号注册</title>
	</head>
	<body>
		<div class="middle">

			<c:if test="${not empty requestScope.errorMessage }">
				<script type="text/javascript">
					window.alert('${requestScope.errorMessage}');
				</script>
			</c:if>                        
		    <form id="form" action="register" method="post"><%--form的action属性由用户的具体操作决定--%>
		    	<table vspace="5" id="login">
		    		<tr>
		        		<td>账号</td>
		        		<td><input id="account" type="text" name="name" onblur="checkAccount(this.value)"/><em>*</em></td>
		        		<td><scan id="accountError" class="errorField"></scan></td>
		            </tr>
		            <tr>
		                <td>密码</td>
		                <td><input id="password" type="password" name="password" onblur="checkPassword(this.value)"/><em>*</em></td>
		                <td><scan id="passwordError" class="errorField"></scan></td>
		            </tr>
		            <tr>
		            	<td>确认密码</td>
		            	<td><input id="confirmPwd" type="password" name="confirmPwd" onblur="checkConfirmPwd()"/><em>*</em></td>
		            	<td><scan id="confirmError" class="errorField"></scan></td>
		            </tr>
		         </table>
		         <p><button type="button" onClick="register()">注册</button></p>
		     </form>
	    </div>
	</body>
</html>