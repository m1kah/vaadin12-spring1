package fi.mika.vaadin.config;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SpringServlet extends VaadinServlet {
    private final ApplicationContext context;
    private final boolean forwardingEnforced = true;

    public SpringServlet(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        SpringVaadinServletService service = new SpringVaadinServletService(
                this,
                deploymentConfiguration,
                context);
        service.init();
        return service;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(wrapRequest(request), response);
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        if (forwardingEnforced && request.getPathInfo() == null) {
            /*
             * We need to apply a workaround in case of forwarding
             *
             * see https://jira.spring.io/browse/SPR-17457
             */
            return new ForwardingRequestWrapper(request);
        }
        return request;
    }

}
