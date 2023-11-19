package org.example;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/time"})
public class TimezoneValidateFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String timezoneParam = httpRequest.getParameter("timezone");

        if (isValidTimezone(timezoneParam)) {
            if (timezoneParam != null && !timezoneParam.isEmpty()) {
                CookieHelper.addCookie(httpResponse, "lastTimezone", timezoneParam);
            }
            chain.doFilter(request, response);
        } else {
            httpResponse.setContentType("text/html");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("Invalid timezone");
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isValidTimezone(String timezone) {
        if (timezone != null) {
            if (timezone.equals("UTC")) {
                return true;
            } else {
                try {
                    int hoursOffset = Integer.parseInt(timezone.replace("UTC", "").trim());
                    return hoursOffset >= -12 && hoursOffset <= 14;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return true;
    }
}