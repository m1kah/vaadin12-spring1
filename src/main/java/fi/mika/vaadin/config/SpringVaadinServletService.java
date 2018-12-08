package fi.mika.vaadin.config;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.theme.AbstractTheme;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is simplified version from spring-vaadin project.
 */
public class SpringVaadinServletService extends VaadinServletService {
    private final transient ApplicationContext context;

    public SpringVaadinServletService(VaadinServlet servlet,
                                      DeploymentConfiguration deploymentConfiguration,
                                      ApplicationContext context) {
        super(servlet, deploymentConfiguration);
        this.context = context;
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> spiInstantiator = super.loadInstantiators();
        List<Instantiator> springInstantiators = context
                .getBeansOfType(Instantiator.class).values().stream()
                .filter(instantiator -> instantiator.init(this))
                .collect(Collectors.toList());
        if (spiInstantiator.isPresent() && !springInstantiators.isEmpty()) {
            throw new ServiceException(
                    "Cannot init VaadinService because there are multiple eligible "
                            + "instantiator implementations: Java SPI registered instantiator "
                            + spiInstantiator.get()
                            + " and Spring instantiator beans: "
                            + springInstantiators);
        }
        return spiInstantiator.isPresent() ? spiInstantiator
                : springInstantiators.stream().findFirst();
    }

    @Override
    public URL getStaticResource(String path) {
        URL resource = super.getStaticResource(path);
        if (resource == null) {
            resource = getResourceURL(path);
        }
        return resource;
    }

    @Override
    public URL getResource(String path, WebBrowser browser,
                           AbstractTheme theme) {
        URL resource = super.getResource(path, browser, theme);
        if (resource == null) {
            resource = getResourceURL(
                    getThemeResolvedPath(path, browser, theme));
        }
        return resource;
    }

    private URL getResourceURL(String path) {
        if (!isSpringBootConfigured()) {
            return null;
        }
        for (String prefix : context.getBean(
                org.springframework.boot.autoconfigure.web.ResourceProperties.class)
                .getStaticLocations()) {
            Resource resource = context.getResource(getFullPath(path, prefix));
            if (resource != null) {
                try {
                    return resource.getURL();
                } catch (IOException e) {
                    // NO-OP file was not found.
                }
            }
        }
        return null;
    }

    private String getFullPath(String path, String prefix) {
        if (prefix.endsWith("/") && path.startsWith("/")) {
            return prefix + path.substring(1);
        }
        return prefix + path;
    }

    private boolean isSpringBootConfigured() {
        String resourcePropertiesFQN = "org.springframework.boot.autoconfigure.web.ResourceProperties";
        if (isClassnameAvailable(resourcePropertiesFQN)) {
            return context.getBeanNamesForType(
                    org.springframework.boot.autoconfigure.web.ResourceProperties.class).length != 0;
        }
        return false;
    }

    private static boolean isClassnameAvailable(String clazzName) {
        try {
            Class.forName(clazzName, false,
                    SpringVaadinServletService.class.getClassLoader());
        } catch (LinkageError | ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public InputStream getResourceAsStream(String path, WebBrowser browser,
                                           AbstractTheme theme) {
        InputStream resourceAsStream = super
                .getResourceAsStream(path, browser, theme);
        if (resourceAsStream == null) {
            URL resourceURL = getResourceURL(
                    getThemeResolvedPath(path, browser, theme));
            if (resourceURL != null) {
                try {
                    resourceAsStream = resourceURL.openStream();
                } catch (IOException e) {
                    // NO-OP return null stream
                }
            }
        }
        return resourceAsStream;
    }

    private String getThemeResolvedPath(String url, WebBrowser browser,
                                        AbstractTheme theme) {
        String resourceUrl = resolveResource(url, browser);
        if (theme != null) {
            String themeUrl = theme.translateUrl(resourceUrl);
            if (!resourceUrl.equals(themeUrl)
                    && getResourceURL(themeUrl) != null) {
                return themeUrl;
            }
        }
        return resourceUrl;
    }
}
