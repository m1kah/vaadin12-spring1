package fi.mika.vaadin.config;

import org.springframework.context.ApplicationContext;

public final class Spring {
    private static ApplicationContext appContext;

    private Spring() {}

    static void setApplicationContext(ApplicationContext context) {
        Spring.appContext = context;
    }

    public static <T> T bean(Class<T> beanType) {
        return appContext.getBean(beanType);
    }
}
