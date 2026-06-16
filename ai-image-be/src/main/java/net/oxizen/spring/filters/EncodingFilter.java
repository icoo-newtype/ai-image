package net.oxizen.spring.filters;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

	private String encoding;

	public void init(FilterConfig config) {
		encoding = config.getInitParameter("requestEncoding");
		if (encoding == null) {
			encoding = "UTF-8";
		}
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		if (null == servletRequest.getCharacterEncoding()) {
			servletRequest.setCharacterEncoding(encoding);
		}
		servletResponse.setCharacterEncoding(encoding);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
	}
}
