package com.lmr.netdisk.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lmr.netdisk.dao.UserFileDao;
import com.lmr.netdisk.model.NetFile;
import com.lmr.netdisk.model.NetFilePropertyFilter;
import com.lmr.netdisk.model.User;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.filters.MappingPropertyFilter;

/**
 * Servlet implementation class FileHandlerServlet
 */
@WebServlet(urlPatterns = { "/FileHandlerServlet" }, name = "fileHandler")
public class FileHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserFileDao fileDao;
	private String filesRoot;
	private JsonConfig jsonConfig;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileHandlerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		String op = request.getParameter("op");
		HttpSession session = request.getSession();
		String userName = ((User) session.getAttribute("user")).getName();// 以用户名作为用户文件的根目录
		fileDao.setUserName(userName);
		// 解析op命令参数
		switch (op) {
		case "list": {
			String pathPara = request.getParameter("path");
			if (pathPara == null) {
				return;
			}
			pathPara = URLDecoder.decode(pathPara, "utf-8");
			List<NetFile> dirContent = fileDao
					.listDir(Paths.get(filesRoot, userName, URLDecoder.decode(pathPara, "utf-8")));
			JSONArray array = JSONArray.fromObject(dirContent,jsonConfig);
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setIntHeader("Expires", 0);
			response.getWriter().print(array);
			break;
		}
		case "upload": {
			String uploadLocation = request.getParameter("path");
			if (uploadLocation == null)
				throw new ServletException("请求参数path必须存在");
			uploadLocation = URLDecoder.decode(uploadLocation, "utf-8");
			DiskFileItemFactory factory = new DiskFileItemFactory(10 * 1024, new File(System.getenv("temp")));
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			try {
				List<FileItem> fileItemList = fileUpload.parseRequest(request);
				for (FileItem fileItem : fileItemList) {
					String fileName = fileItem.getName();
					Path uploadFile = Paths.get(filesRoot, userName, uploadLocation, fileName.substring(fileName.lastIndexOf('\\')+1/*此处里的目的是过滤掉fileName可能含有的路径信息*/));
					// TODO 检查文件是否存在，如果存在，则提示用户是否覆盖
					fileItem.write(uploadFile.toFile());
				}
			} catch (FileUploadException e) {
				throw new ServletException(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "download": {
			String pathPara = request.getParameter("path");
			if (pathPara == null) {
				return;
			}
			pathPara = URLDecoder.decode(pathPara, "utf-8");
			Path path = Paths.get(filesRoot, userName, URLDecoder.decode(pathPara, "utf-8"));
			if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				return;
			}
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + URLEncoder.encode(path.getFileName().toString(), "utf-8") + "\"");
			response.setContentType(this.getServletContext().getMimeType(path.getFileName().toString()));
			InputStream input = Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS);
			OutputStream output = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) > 0) {
				output.write(buffer, 0, len);
			}
			break;
		}
		case "delete": {
			String[] pathes = request.getParameterValues("path");
			if (pathes == null)
				return;
			for (int index = 0; index < pathes.length; index++) {
				pathes[index] = URLDecoder.decode(pathes[index], "utf-8");
			}
			for (String pathStr : pathes) {
				Path path = Paths.get(filesRoot, userName, pathStr);
				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					fileDao.deleteDir(path);
				} else {
					fileDao.deleteFile(path);
				}
			}
			break;
		}
		case "previewImg": {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setIntHeader("Expires", 0);
			String imgPathStr = request.getParameter("path");
			if (imgPathStr == null) {
				throw new ServletException("path请求参数不存在");
			}
			imgPathStr = URLDecoder.decode(imgPathStr, "utf-8");
			Path imgPath = Paths.get(filesRoot, userName, imgPathStr);
			if (!Files.exists(imgPath, LinkOption.NOFOLLOW_LINKS)) {
				response.setStatus(404);
				return;
			}
			try (InputStream input = Files.newInputStream(imgPath, LinkOption.NOFOLLOW_LINKS)) {
				response.setContentType(getServletContext().getMimeType(imgPathStr));
				OutputStream output = response.getOutputStream();
				byte[] buffer = new byte[1000];
				int len;
				while ((len = input.read(buffer)) != -1) {
					output.write(buffer, 0, len);
				}
			}
			break;
		}
		case "createDir": {
			String path = request.getParameter("path");
			if (path == null)
				return;
			String dirStr = URLDecoder.decode(path, "utf-8");
			Path dir = Paths.get(filesRoot, userName, dirStr);
			fileDao.createDir(dir);
			break;
		}
		case "queryByMimeHead":{
			String mimeHead = request.getParameter("mimeHead");
			if(mimeHead==null||mimeHead.equals("")) {
				throw new ServletException("缺少mimeHead请求参数或者mimeHead值为空");
			}
			List<NetFile> files = fileDao.queryByMimeHead(mimeHead,Paths.get(filesRoot, userName));
			JSONArray array = JSONArray.fromObject(files,jsonConfig);
			response.getWriter().print(array);
			break;
		}
		default:
			throw new ServletException("参数op的值错误：op=" + op);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		doGet(request, response);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		jsonConfig = new JsonConfig();
		jsonConfig.setJsonPropertyFilter(new NetFilePropertyFilter());
		fileDao = new UserFileDao(this.getServletContext());
		filesRoot = this.getServletContext().getInitParameter("filesRoot");// 所用用户主目录的父目录
		Path rootPath = Paths.get(filesRoot);
		if (!Files.exists(rootPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(rootPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			if(!Files.isDirectory(rootPath, LinkOption.NOFOLLOW_LINKS)) {
				throw new ServletException("指定的存储用户数据的目录不是目录："+rootPath);
			}//注：当指定的数据目录不是一个目录时，不应该去删除这个文件并创建目录，而是应该抛出异常来提醒开发者重新配置数据目录。
		}
	}

}
