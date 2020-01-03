<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://netdisk.lmr.com/jsp/functions" prefix="myfn" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" href="css/index.css"/>
		<script type="text/javascript" src="js/index.js"></script>
		<title>网盘登录</title>
	</head>
	<body>
		<div class="middle">
			<c:if test="${not empty requestScope.errorMessage }">
				<script type="text/javascript">
						window.alert('${requestScope.errorMessage}');
				</script>
			</c:if>
		    <form id="form" action="login" method="post"><%--form的action属性由用户的具体操作决定--%>
		    	<table vspace="5" id="login">
		    		<tr>
		        		<td>账号</td>
		        		<td><input id="account" type="text" name="name" value="${myfn:decodeURI(cookie.userName.value)}" onblur="checkAccount(this.value)"/><em>*</em></td>
		        		<td><scan id="accountError" class="errorField"></scan></td>
		            </tr>
		            <tr>
		                <td>密码</td>
		                <td><input id="password" type="password" name="password" value="${cookie.password.value}" onblur="checkPassword(this)"/><em>*</em></td>
		                <td><scan id="passwordError" class="errorField"></scan></td>
		            </tr>
		         </table>
		         <p><button type="button" onClick="login()">登 录</button></p>
		         <p><a href="register.jsp">注册</a></p>
		     </form>
	    </div>
	</body>
</html>