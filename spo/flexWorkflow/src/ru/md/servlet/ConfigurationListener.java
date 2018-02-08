package ru.md.servlet;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Credit Committee application lifecycle listener.
 * @author Alexey Chalov
 */
public class ConfigurationListener implements ServletContextListener {
    protected static final Logger LOGGER = Logger.getLogger(ConfigurationListener.class.getName());
    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /* initialize Aspose.Words license */
        try {
            new com.aspose.words.License().setLicense(
                getClass().getResourceAsStream("/WEB-INF/Aspose.Words.lic")
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
