package org.example.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 10532
 */
@Component
public class WebFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader("trace-id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
            httpRequest = new CustomHttpServletRequestWrapper(httpRequest, "trace-id", traceId);
        }
        chain.doFilter(httpRequest, response);
    }


    private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final String headerName;
        private final String headerValue;

        public CustomHttpServletRequestWrapper(HttpServletRequest request, String headerName, String headerValue) {
            super(request);
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public String getHeader(String name) {
            if (headerName.equalsIgnoreCase(name)) {
                return headerValue;
            }
            return super.getHeader(name);
        }
    }

}
