<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>网盘主页</title>
    <link rel="stylesheet" href="css/main.css"/>
    <script type="text/javascript" src="js/jquery-3.3.1.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    <script type="text/javascript">
    	$(document).ready(function(e) {
            list("");//列出当前用户主目录下的文件和目录
        });
    </script>
</head>

<body>
<div class="container">
  <div class="header">
  	<a href="#"><img src="img/logo.png" alt="" name="Insert_logo" width="174" height="37" id="Insert_logo" style="background-color: #C6D580" /></a>
  	<div id="userInfo">
  		用户：${user.name}
  	</div>
  </div>
  <div class="navBar">
  	<ul class="locationNav">
  	</ul>
  	<div class="fileControls">
	  	<button onclick="$(':file').click()">上传文件</button>
	  	<button onclick="goBack()">返回</button>
	  	<button onclick="up()">上一级目录</button>
	  	<button onclick="createDir()">新建文件夹</button>
	  	<button onclick="deleteFileOrDir(dirItemSelected.virtualPath)">删除</button>
	  	<form id='uploadForm' enctype='multipart/form-data' method='post' hidden='hidden'>
	  		<input type='file' name='file' multiple='multiple' onchange="uploadFile()"/>
	  	</form>
  	</div>
  </div>
  <div class="sidebar1">
    <ul class="nav">
      <li><a href="javascript:list('')">所有文件</a></li>
      <li><a href="javascript:listByMimeHead('image')">图片</a></li>
      <li><a href="javascript:listByMimeHead('vedio')">视频</a></li>
      <li><a href="#">文档</a></li>
      <li><a href="#">回收站</a></li>
    </ul>
  </div>
  <div class="content">
  </div>
  <div class="footer">
    <p>&copy;百度科技有限公司</p>
  </div>
</div>
</body>
</html>