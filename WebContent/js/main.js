/**
 * 针对main.jsp的JavaScript代码
 */
 var dirContents;//当前目录的内容
 var currentPath="";//当前的访问的路径
 var previousPath="";//上一个访问的路径
 var dirItemSelected;//被选择的目录项
 /**
  * 列出目录dir中的所有项
  * @param dir 要访问的目录
  */
 function list(dir){
	 //准备ajax请求对象
	 var xmlhttp;
	 if(window.XMLHttpRequest){
	 	xmlhttp = new XMLHttpRequest();
	 }else{
		xmlhttp = new ActiveXObject('Microsoft.XMLHttpRequest');
	 }
	 //发送请求
	 var url = encodeURI(encodeURI('FileHandlerServlet?op=list&path='+dir));
	 xmlhttp.open("get",url,true);
	 xmlhttp.onreadystatechange = function(){
		 if(xmlhttp.readyState==4&&xmlhttp.status==200){
			 dirContents = eval("("+xmlhttp.responseText+")");
			 if(dir!=currentPath){
				 previousPath = currentPath;
				 currentPath = dir;//更新当前的访问路径
			 }
			 $(".content").html("");
			 for(index in dirContents){
				 var itemDiv = $("<div class='dirItem' title='"+dirContents[index].name+"'></div>");
				 itemDiv.attr("id",index.toString());
				 var dirItemImg = $("<img/>");
				 var dirItemName = $("<p class='dirItemName'></p>");
				 
				 if(dirContents[index].type=="file"){
					 var mime = dirContents[index].mime;
					 var fileType = mime.substring(0,mime.indexOf("/"));
					 switch(fileType){
					 case "image":dirItemImg.attr("src",encodeURI(encodeURI("FileHandlerServlet?op=previewImg&path="+dir+"/"+dirContents[index].name)));break;
					 default:dirItemImg.attr("src","img/fileIcon.png");
					 }
					 dirItemImg.dblclick(function(){
						 var index = $(this).parent().attr("id");
						 var url = encodeURI(encodeURI("FileHandlerServlet?op=download&path="+currentPath+"/"+dirContents[index].name));
						 location.assign(url);
					 });
				 }else if(dirContents[index].type=="directory"){
					 dirItemImg.attr("src","img/dirIcon.png");
					 dirItemImg.dblclick(function(){
						 var index = $(this).parent().attr("id");
						 list(currentPath+"/"+dirContents[index].name);
					 });
				 }
				 dirItemImg.click(function(){
					 var dirItem = $(this).parent();
					 var index = dirItem.attr("id");
					 dirItemSelected = dirContents[index];
					 dirItem.addClass("selected");
					 dirItem.siblings().removeClass("selected");
				 });
				 dirItemName.text(dirContents[index].name);
				 itemDiv.append(dirItemImg);
				 itemDiv.append(dirItemName);
				 $(".content").append(itemDiv);
			 }
			 showLocation();
		 }
	 };
	 xmlhttp.send();
 }
 function refreshList(){
	 list(currentPath);
 }
 /**
  * 返回到上一个访问的目录
  */
 function goBack(){
	 list(previousPath);
 }
 /**
  * 实现上传文件功能
  * @param fileName 要上传的文件名
  */
 function uploadFile(){
	 var xmlhttp;
	 if(window.XMLHttpRequest){
	 	xmlhttp = new XMLHttpRequest();
	 }else{
		xmlhttp = new ActiveXObject('Microsoft.XMLHttpRequest');
	 }
	 xmlhttp.open("post",encodeURI(encodeURI('FileHandlerServlet?op=upload&path='+currentPath)),true);
	 xmlhttp.onreadystatechange = function(){
		 if(xmlhttp.readyState==4&&xmlhttp.status==200){
			 $("#progressBar").remove();
			 refreshList();
		 }
	 }
	 var formData = new FormData($("#uploadForm")[0]);
	 //显示上传进度
	 var progress = $("<div id='progressBar'><p>正在上传</p></div>");
	 progress.css({
		 position:"absolute",
		 top:"50%",
		 left:"50%",
		 width:"10em",
		 height:"20px",
		 padding:"0 10px",
		 "margin-left":"-5em",
		 "margin-top":"-20px",
		 background:"rgba(0,0,0,0.6)",
		 color:"white",
		 "text-align":"center"
		 
	 });
	 $("body").append(progress);
	 //上传文件
	 xmlhttp.send(formData);
 }
 /**
  * 列出当前目录的上一级目录的内容
  */
 function up(){
	 var pathItems = currentPath.split("/");
	 var lastIndex;
	 var lastItem = pathItems[pathItems.length-1];
	 var parentPath = currentPath.substring(0,currentPath.length-(lastItem.length+1));
	 list(parentPath);
 }
 function showLocation(){
	 var pathItems = currentPath.split("/");
	 var navUl = $("ul.locationNav");
	 navUl.html("<li>所有文件</li>");
	 for(var index in pathItems){
		var li = $("<li></li>");
		li.html(pathItems[index]);
		navUl.append(li);
	 }
 }
 function deleteFileOrDir(toDelete){
	 if(toDelete==null||toDelete==""){
		 alert("请选择要删除的文件或文件夹！");
		 return;
	 }
	 var url = "FileHandlerServlet";
	 $.post(url,{
		 op:"delete",
		 path:toDelete
	 },function(data,status){
		 refreshList();
	 });
 }
 function createDir(){
	 var dirName = prompt('请输入文件夹的名字');
	 if(dirName==null||dirName=="")
		 return;
	 var dirPath = currentPath+"/"+dirName;
	 var url = "FileHandlerServlet";
	 $.post(url,{
		 op:"createDir",
		 path:dirPath
	 },function(data,status){
		 refreshList();
	 });
 }
 function listByMimeHead(mimeHead){//TODO 待实现
	 $.get("FileHandlerServlet?op=queryByMimeHead&mimeHead="+mimeHead,function(data){
		 dirContents = eval("("+data+")");
		 $(".content").html("");
		 for(index in dirContents){
			 var itemDiv = $("<div class='dirItem' title='"+dirContents[index].name+"'></div>");
			 itemDiv.attr("id",index.toString());
			 var dirItemImg = $("<img/>");
			 var dirItemName = $("<p class='dirItemName'></p>");
			 
			 var mime = dirContents[index].mime;
			 var fileType = mime.substring(0,mime.indexOf("/"));
			 switch(fileType){
			 case "image":
				 dirItemImg.attr("src",encodeURI(encodeURI("FileHandlerServlet?op=previewImg&path="+dirContents[index].virtualPath)));break;
				 default:dirItemImg.attr("src","img/fileIcon.png");
			 }
			 dirItemImg.dblclick(function(){
				 var index = $(this).parent().attr("id");
				 var url = encodeURI(encodeURI("FileHandlerServlet?op=download&path="+dirContents[index].virtualPath));
				 location.assign(url);
			 });	 
			 dirItemImg.click(function(){
				 var dirItem = $(this).parent();
				 var index = dirItem.attr("id");
				 dirItemSelected = dirContents[index];
				 dirItem.addClass("selected");
				 dirItem.siblings().removeClass("selected");
			 });
			 dirItemName.text(dirContents[index].name);
			 itemDiv.append(dirItemImg);
			 itemDiv.append(dirItemName);
			 $(".content").append(itemDiv);
		 }
		 
	 })
 }
 