package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private final TemplateEngine templateEngine;

    public TimeServlet() {
        templateEngine = new TemplateEngine();

        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(templateEngine.getTemplateResolvers().size());
        templateResolver.setCacheable(false);
        templateEngine.addTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String timezoneParam = request.getParameter("timezone");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'X");

        String lastTimezone = CookieHelper.getCookieValue(request, "lastTimezone");
        if (lastTimezone != null && !lastTimezone.trim().isEmpty()) {
            try {
                int hoursOffset = Integer.parseInt(lastTimezone.replace("UTC", "").trim());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + (hoursOffset >= 0 ? "+" : "") + hoursOffset));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
        } else if (timezoneParam != null && !timezoneParam.trim().isEmpty()) {
            try {
                int hoursOffset = Integer.parseInt(timezoneParam.replace("UTC", "").trim());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + (hoursOffset >= 0 ? "+" : "") + hoursOffset));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
        } else {
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        String currentTime = dateFormat.format(new Date());

        WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        ctx.setVariable("currentTime", currentTime);

        templateEngine.process("time_template", ctx, response.getWriter());
    }
}