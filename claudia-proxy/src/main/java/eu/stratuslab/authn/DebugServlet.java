/*
 Created as part of the StratusLab project (http://stratuslab.eu),
 co-funded by the European Commission under the Grant Agreement
 INSFO-RI-261552.

 Copyright (c) 2010, Centre Nationale de la Recherche Scientifique

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package eu.stratuslab.authn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DebugServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();
        writer.println("<h1>DebugServlet</h1>");
        writer.println("<p>session=" + request.getSession(true).getId());
        writer.println("<p>authtype=" + request.getAuthType());
        writer.println("<p>contextpath=" + request.getContextPath());
        writer.println("<p>principal=" + request.getUserPrincipal());
        writer.println("<p>remoteuser=" + request.getRemoteUser());
        writer.println("<p>cloud-access="
                + request.isUserInRole("cloud-access"));

        writer.println("<p>cookies\n<ul>");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                writer.println("<li> " + formatCookie(cookie));
            }
        }
        writer.println("</ul>");

        writer.println(formatHeaders(request));
        writer.println(formatAttributes(request));
    }

    public static String formatCookie(Cookie cookie) {
        return String.format("%s : %s : %s : %s : %s ", cookie.getName(),
                cookie.getDomain(), cookie.getMaxAge(), cookie.getSecure(),
                cookie.getValue());
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public static String formatAttributes(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>attributes\n<ul>\n");
        Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object value = request.getAttribute(name);
            value = (value != null) ? value : "null";
            sb.append("<li> " + name + " = " + value.toString() + "\n");
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
