package com.lmr.netdisk.servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lmr.netdisk.dao.UserDao;
import com.lmr.netdisk.model.User;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet({"/login","/register","/changePassword","/existAccount"})
public class UserManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserManageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String servletPath = request.getServletPath();
		switch(servletPath) {
		case "/login":
		{
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			if(name==null||password==null) {
				throw new ServletException("URL中缺少请求参数name和password");
			}
			name = URLDecoder.decode(name, "utf-8");
			User user = userDao.login(name, password);
			if(user!=null) {
				HttpSession session = request.getSession();
				
				Cookie cookie1 = new Cookie("userName", URLEncoder.encode(user.getName(), "utf-8"));
				Cookie cookie2 = new Cookie("password", user.getPassword());
				cookie1.setMaxAge(60*60);//保存cookie一个小时
				cookie2.setMaxAge(60*60);
				cookie1.setPath(getServletContext().getContextPath());
				cookie2.setPath(getServletContext().getContextPath());
				
				session.setAttribute("user", user);
				session.setAttribute("userFileRoot", Paths.get(getServletContext().getInitParameter("fileRoot"),user.getName()).toString());
				response.addCookie(cookie1);
				response.addCookie(cookie2);
				response.sendRedirect("main.jsp");
			}else {
				//Send error message
				request.setAttribute("errorMessage", "登录失败：用户名或密码错误");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
		}
		break;
		case "/register":
		{
			String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
			String password = request.getParameter("password");
			User user = new User();
			user.setName(name);
			user.setPassword(password);
			if(userDao.existUser(user.getName())) {
				response.getWriter().print("用户已存在，请输入其他的用户名<a href='register.jsp'>返回</a>");
			}else {
				userDao.registerUser(user);
				response.sendRedirect("registerSuccess.jsp");
			}
		}break;
		case "/changePassword":{
			String newPassword = request.getParameter("newPassword");
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			if(user!=null) {
				String name = user.getName();
				userDao.changePassword(name, newPassword);
			}
			break;
		}
		case "/existAccount":{
			String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
			boolean exist = userDao.existUser(name);
			response.getWriter().print(exist);
		}
		}
		
//		User user = userDao.login(name, password);
//		if(user!=null) {
//			HttpSession session = request.getSession();
//			session.setAttribute("user", user);
//			response.sendRedirect("/main.jsp");
//		}else {
//			//Send error message
//			request.setAttribute("errorMessage", "登录失败");
//			request.getRequestDispatcher("/index.jsp").forward(request, response);
//		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		ServletContext context = getServletContext();
		String databaseUrl = context.getInitParameter("dbUrl");
		String userName = context.getInitParameter("dbUser");
		String password = context.getInitParameter("password");
		try {
			userDao = new UserDao(databaseUrl,userName,password,context);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new ServletException(e);
		}
	}
}
