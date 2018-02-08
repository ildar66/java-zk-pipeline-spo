package org.uit.director.servletutil;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class CharsetFilter implements Filter {
    public void init(final FilterConfig config) throws ServletException {
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain next)
            throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        try {
            next.doFilter(request, response);
        } catch (java.io.FileNotFoundException e) {
            //ignore it
            //письмо от Мохова 'fancybox надоел и другие проблемы'
        }
    }

    public void destroy() {
    }
}

