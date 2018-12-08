package fi.mika.vaadin.config;

import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ForwardingRequestWrapper extends HttpServletRequestWrapper {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public ForwardingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getServletPath() {
        String pathInfo = super.getPathInfo();
        if (pathInfo == null) {
            // the path where a ServletForwardingController is registered is not
            // a real servlet path
            return "";
        } else {
            return super.getServletPath();
        }
    }

    @Override
    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        if (pathInfo == null) {
            // this uses getServletPath() and should work both with and without
            // clearServletPath
            pathInfo = urlPathHelper.getPathWithinServletMapping(this);
        }
        return pathInfo;
    }
}
