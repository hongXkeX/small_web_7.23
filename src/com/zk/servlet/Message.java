package com.zk.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
                                                         
public class Message extends HttpServlet{
	
//	public static Map<String, String> users = new HashMap<String,String>();
	public static Set<String> userskeys = new HashSet<String>() { };
	
	Connection conn = null;
	ResultSet rs = null;
	ResultSet rs2 = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("执行get方法");
		
		//接收用户传来的账号信息
		String method = req.getParameter("method");
		switch (method) {
		case "login":
			login(req, resp);
			break;
		case "register":
			register(req, resp);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 账号匹配验证，用接收到的账号信息和数据库中的账号信息进行匹配
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String name = req.getParameter("username")==null?"":req.getParameter("username");
		String pass = req.getParameter("password")==null?"":req.getParameter("password");
        try{
            // 第一步 先加载JDBC的驱动类
            Class.forName("com.mysql.jdbc.Driver");
            // 第二步 提供JDBC连接的url
            String url = "jdbc:mysql://localhost:3306/itcast";
            String username = "root";
            String password = "root";
            // 第三步 创建数据库的连接
            conn = DriverManager.getConnection(url, username, password);
            // 第四步 创建一个statement对象
            String sql = "select * from hello where username = ? and password = ?";
            pstmt = conn.prepareStatement(sql);
            // 第五步 执行sql语句
            pstmt.setString(1, name);
            pstmt.setString(2, pass);
            rs = pstmt.executeQuery();
            
            if(rs.next()) {  
            	String sessionname = req.getSession().getAttribute("name")==null?"":(String) req.getSession().getAttribute("name");
            	if( userskeys.contains(name) ){
    				req.setAttribute("mess","登陆失败，该用户已经登录过系统，正在返回，请重新登陆。");
    				req.setAttribute("url","index.jsp");
    				req.getRequestDispatcher("user_status.jsp").forward(req, resp);
    			}else{
    				req.getSession().setAttribute("name", name);
                	userskeys.add(name);
                	//将页面转发到login_ok.jsp,并且携带requset和response对象信息
                	req.setAttribute("mess","登陆成功,即将转向首页");
    				req.setAttribute("url","main.jsp");
    				req.getRequestDispatcher("user_status.jsp").forward(req, resp);
    			}
            }else{
        		req.setAttribute("mess","登陆失败，用户名或密码错误，正在返回，请重新登陆。");
        		req.setAttribute("url","index.jsp");
        		req.getRequestDispatcher("user_status.jsp").forward(req, resp);
            } 
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("ClassNotFoundException");
            // 还要进行写入异常操作
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException");
        }finally{
            try{
                // 第七步 关闭JDBC对象
                if(rs != null) {
                    rs.close();
                }
                if(pstmt != null) {
                    pstmt.close();
                }
                if(conn != null) {
                    conn.close();
                }
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String name = req.getParameter("username")==null?"":req.getParameter("username");
		String pass = req.getParameter("password")==null?"":req.getParameter("password");
		rs2 = null;
		
		try{
			// 第一步 先加载JDBC的驱动类
            Class.forName("com.mysql.jdbc.Driver");
            // 第二步 提供JDBC连接的url
            String url = "jdbc:mysql://localhost:3306/itcast";
            String username = "root";
            String password = "root";
            // 第三步 创建数据库的连接
            conn = DriverManager.getConnection(url, username, password);
            // 第四步 创建一个statement对象
            String sql = "insert into hello values(?,?)";
            String sql2 = "select * from hello where username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt2 = conn.prepareStatement(sql2);
            // 第五步 执行sql语句
            pstmt.setString(1, name);
            pstmt.setString(2, pass);
            pstmt2.setString(1, name);
           
            rs2 = pstmt2.executeQuery();
            
			if(name.equals("")||pass.equals("")){
				req.setAttribute("mess","注册失败,请输入正确的用户名和密码。");
				req.setAttribute("url","register.jsp");
				req.getRequestDispatcher("user_status.jsp").forward(req, resp);
			}else if(rs2.next()){
				req.setAttribute("mess","注册失败,用户名已被占用。");
				req.setAttribute("url","register.jsp");
				req.getRequestDispatcher("user_status.jsp").forward(req, resp);
			}else{
				pstmt.executeUpdate();
				req.setAttribute("mess","注册成功，即将转向登陆页面");
				req.setAttribute("url","index.jsp");
				req.getRequestDispatcher("user_status.jsp").forward(req, resp);
			}
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("ClassNotFoundException");
            // 还要进行写入日志等操作 所以要分开两个异常类别 都要 catch 
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException");
        }finally{
            try{
                // 第六步 关闭JDBC对象
                if(pstmt != null) {
                    pstmt.close();
                }
                if(pstmt2 != null) {
                	pstmt2.close();
                }
                if(conn != null) {
                    conn.close();
                }
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
	}	
}

