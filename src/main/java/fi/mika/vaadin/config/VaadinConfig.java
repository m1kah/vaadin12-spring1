package fi.mika.vaadin.config;

import com.vaadin.flow.server.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class VaadinConfig {
    @Value("${app.vaadinContextPath}")
    String vaadinPath;

    @Value("${app.vaadinPackages}")
    List<String> vaadinPackages;

    @Bean
    ServletContextInitializer contextInitializer(WebApplicationContext context) {
        return new VaadinServletContextInitializer(context, vaadinPackages);
    }

    @Bean
    ServletRegistrationBean vaadinServlet(WebApplicationContext context) {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new SpringServlet(context),
                vaadinPath
        );
        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.SERVLET_PARAMETER_PUSH_URL, "context://*");
        registration.setInitParameters(parameters);
        registration.setName(ClassUtils.getShortNameAsProperty(SpringServlet.class));
        return registration;
    }

    @Bean
    public SimpleUrlHandlerMapping vaadinRootMapping(Controller vaadinForwardingController) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        mapping.setUrlMap(Collections.singletonMap("/*", vaadinForwardingController));
        return mapping;
    }

    @Bean
    public Controller vaadinForwardingController() {
        ServletForwardingController controller = new ServletForwardingController();
        controller.setServletName(ClassUtils.getShortNameAsProperty(SpringServlet.class));
        return controller;
    }

}
