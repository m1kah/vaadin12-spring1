package fi.mika.vaadin.config;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.startup.AbstractRouteRegistryInitializer;
import com.vaadin.flow.server.startup.RouteRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public class VaadinServletContextInitializer implements ServletContextInitializer {
    private static final Logger log = LoggerFactory.getLogger(VaadinServletContextInitializer.class);
    private final WebApplicationContext appContext;

    public VaadinServletContextInitializer(WebApplicationContext appContext) {
        this.appContext = appContext;
        Spring.setApplicationContext(appContext);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        RouteRegistry registry = RouteRegistry.getInstance(servletContext);
        if (!registry.navigationTargetsInitialized()) {
            servletContext.addListener(new RouteServletContextListener());
        }
    }

    private class RouteServletContextListener extends AbstractRouteRegistryInitializer implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent event) {
            RouteRegistry registry = RouteRegistry.getInstance(event.getServletContext());
            if (registry.navigationTargetsInitialized()) {
                return;
            }
            List<Class<?>> routeClasses = findByAnnotation(
                    getRoutePackages(),
                    Route.class,
                    RouteAlias.class)
                    .collect(Collectors.toList());

            for (Class<?> routeClass : routeClasses) {
                Route route = routeClass.getAnnotation(Route.class);
                if (route != null) {
                    log.info("Registering Vaadin route: {}", route.value());
                }
                RouteAlias routeAlias = routeClass.getAnnotation(RouteAlias.class);
                if (routeAlias != null) {
                    log.info("Registering Vaadin route alias: {}", routeAlias.value());
                }
            }
            Set<Class<? extends Component>> navigationTargets =
                    validateRouteClasses(routeClasses.stream());

            try {
                registry.setNavigationTargets(navigationTargets);
            } catch (InvalidRouteConfigurationException e) {
                throw new IllegalStateException("Failed to set navigation targets", e);
            }
            registry.setPwaConfigurationClass(validatePwaClass(routeClasses.stream()));
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {

        }
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<?>> findByAnnotation(
            Collection<String> packages,
            Class<? extends Annotation>... annotations) {
        return findByAnnotation(packages, Stream.of(annotations));
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<?>> findByAnnotation(
            Collection<String> packages,
            Stream<Class<? extends Annotation>> annotations) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);
        scanner.setResourceLoader(appContext);
        annotations.forEach(annotation -> scanner
                .addIncludeFilter(new AnnotationTypeFilter(annotation)));

        return packages.stream().map(scanner::findCandidateComponents)
                .flatMap(Collection::stream).map(this::getBeanClass);
    }

    private Stream<Class<?>> findBySuperType(Collection<String> packages,
                                             Class<?> type) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);
        scanner.setResourceLoader(appContext);
        scanner.addIncludeFilter(new AssignableTypeFilter(type));

        return packages.stream().map(scanner::findCandidateComponents)
                .flatMap(set -> set.stream()).map(this::getBeanClass);
    }

    private Class<?> getBeanClass(BeanDefinition beanDefinition) {
        AbstractBeanDefinition definition = (AbstractBeanDefinition) beanDefinition;
        Class<?> beanClass;
        if (definition.hasBeanClass()) {
            beanClass = definition.getBeanClass();
        } else {
            try {
                beanClass = definition
                        .resolveBeanClass(appContext.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return beanClass;
    }

    private Collection<String> getRoutePackages() {
        return getDefaultPackages();
    }

    private Collection<String> getCustomElementPackages() {
        return getDefaultPackages();
    }

    private Collection<String> getVerifiableAnnotationPackages() {
        return getDefaultPackages();
    }

    private Collection<String> getErrorParameterPackages() {
        return Stream
                .concat(Stream
                                .of(HasErrorParameter.class.getPackage().getName()),
                        getDefaultPackages().stream())
                .collect(Collectors.toSet());
    }

    private List<String> getDefaultPackages() {
        return singletonList("fi.mika.vaadin.car");
    }
}
