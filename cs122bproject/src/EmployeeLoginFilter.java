

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/EmployeeLoginFilter")
public class EmployeeLoginFilter implements Filter {

    /**
     * Default constructor. 
     */
    public EmployeeLoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		System.out.println("LoginFilter: " + httpRequest.getRequestURI());
		
		// check if this URL is allowed to access without logging in
		if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
			// keep default action: pass along the filter chain
			chain.doFilter(request, response);
			return;
		}
		
		// redirect to login page if the "user" attribute doesn't exist in session
		if (httpRequest.getSession().getAttribute("user") == null) {
			httpResponse.sendRedirect("/cs122bproject/EmployeeLogin.html");
			return;
		} else {
			chain.doFilter(request, response);
			return;
		}
	}
	
	// Setup your own rules here to allow accessing some resources without logging in
	// Always allow your own login related requests(html, js, servlet, etc..)
	// You might also want to allow some CSS files, etc..
	private boolean isUrlAllowedWithoutLogin(String requestURI) {
		requestURI = requestURI.toLowerCase();
		
		if (requestURI.endsWith("login.html") || requestURI.endsWith("login.js") 
				|| requestURI.endsWith("login") || requestURI.endsWith("login.css")
				|| requestURI.endsWith("styles.css") || requestURI.endsWith("avatar.png")
				|| requestURI.endsWith("EmployeeLogin.html") || requestURI.endsWith("EmployeeLogin.css")) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
