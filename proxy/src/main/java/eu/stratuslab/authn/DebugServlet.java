package eu.stratuslab.authn;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
 
public class DebugServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

	PrintWriter writer = response.getWriter();
        writer.println("<h1>DebugServlet</h1>");
        writer.println("<p>session=" + request.getSession(true).getId());
	writer.println("<p>authtype=" + request.getAuthType());
	writer.println("<p>contextpath=" + request.getContextPath());
	writer.println("<p>principal=" + request.getUserPrincipal());
        writer.println("<p>remoteuser=" + request.getRemoteUser());
	writer.println("<p>cloud-access=" + request.isUserInRole("cloud-access"));

	writer.println("<p>cookies\n<ul>");
	Cookie[] cookies = request.getCookies();
	if (cookies!=null) {
	    for (Cookie cookie : cookies) {
		writer.println("<li> " + formatCookie(cookie));
	    }
	}
	writer.println("</ul>");

	writer.println(formatHeaders(request));
	writer.println(formatAttributes(request));
    }

    public static String formatCookie(Cookie cookie) {
	StringBuilder sb = new StringBuilder();
	sb.append(cookie.getName() + " : ");
	sb.append(cookie.getDomain() + " : ");
	sb.append(cookie.getMaxAge() + " : ");
	sb.append(cookie.getSecure() + " : ");
	sb.append(cookie.getValue() + " : ");
	return sb.toString();
    }

    public static String formatHeaders(HttpServletRequest request) {
	StringBuilder sb = new StringBuilder();
	sb.append("<p>headers\n<ul>\n");
	Enumeration<String> names = request.getHeaderNames();
	while (names.hasMoreElements()) {
	    String name = names.nextElement();
	    String value = request.getHeader(name);
	    sb.append("<li> " + name + " = " + value + "\n");
	}
	sb.append("</ul>");
	return sb.toString();
    }

    public static String formatAttributes(HttpServletRequest request) {
	StringBuilder sb = new StringBuilder();
	sb.append("<p>attributes\n<ul>\n");
	Enumeration<String> names = request.getAttributeNames();
	while (names.hasMoreElements()) {
	    String name = names.nextElement();
	    Object value = request.getAttribute(name);
	    value = (value!=null) ? value : "null";
	    sb.append("<li> " + name + " = " + value.toString() + "\n");
	}
	sb.append("</ul>");
	return sb.toString();
    }
}
