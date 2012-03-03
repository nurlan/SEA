package kz.edu.sdu.sea.apps.web.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class RenderToResponse
{
	public static final String INDEX_PAGE = "/index.jsp";
	
	public static final String SEARCH_RESULT_PAGE = "/SearchResult.jsp";
	
	public static final String INDEX_URI = "/";
		
	public static void renderPage( String path, HttpServletRequest req, HttpServletResponse resp )
	throws IOException, ServletException
	{
		RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
		if(requestDispatcher==null) throw new ServletException("Request Dispacher Not Found:"+path);
		requestDispatcher.forward(req,resp);
	}
	
	public static void redirectPage(String path, HttpServletRequest req, HttpServletResponse resp )
	throws IOException, ServletException
	{
		resp.sendRedirect(path);
	}
}