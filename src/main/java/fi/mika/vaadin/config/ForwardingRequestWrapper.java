package fi.mika.vaadin.config;

import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Sets pathInfo for requests. See vaadin-spring github for more details.
 */
public class ForwardingRequestWrapper extends HttpServletRequestWrapper {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public ForwardingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getServletPath() {
        String pathInfo = super.getPathInfo();
        if (pathInfo == null) {
            return "";
        } else {
            return super.getServletPath();
        }
    }

    @Override
    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        if (pathInfo == null) {
            pathInfo = urlPathHelper.getPathWithinServletMapping(this);
        }
        return pathInfo;
    }
}
