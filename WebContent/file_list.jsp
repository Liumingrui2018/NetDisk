<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	function deleteFile(filePath){
		$.get("FileHandlerServlet?op=delete&path="+filePath,function(){
			location.reload(true);
		});
	}
</script>
</head>
<body>
	<c:forEach var="file" items="${requestScope.files }">
		<div class="dirItem" title="${file.name}" onclick="deleteFile('')">
			<c:choose>
				<c:when test="${file.type=='file'}">
					<c:choose>
						<c:when test="${fn:substring(file.mime,0,indexOf('/')=='image' }">
							<img src="FileHandlerServlet?op=previewImg&path=${fn:substringAfter(file.path,sessionScope.userFileRoot) }"/>
						</c:when>
						<c:otherwise>
							<img src="img/fileIcon"/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${file.type=='directory' }">
					<img src="img/dirIcon"/>
				</c:when>
			</c:choose>
		</div>
	</c:forEach>
</body>
</html>