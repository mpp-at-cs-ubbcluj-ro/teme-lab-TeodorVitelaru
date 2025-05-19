package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtils {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Creează un ServiceRegistry
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure() // va folosi hibernate.cfg.xml din classpath
                        .build();

                // Creează SessionFactory folosind ServiceRegistry
                try {
                    sessionFactory = new MetadataSources(registry)
                            .buildMetadata()
                            .buildSessionFactory();
                    logger.info("SessionFactory created successfully");
                }
                catch (Exception e) {
                    logger.error("Exception in creating SessionFactory: " + e);
                    StandardServiceRegistryBuilder.destroy(registry);
                    throw e;
                }
            }
            catch (Exception e) {
                logger.error("Exception in creating StandardServiceRegistry: " + e);
                throw e;
            }
        }
        return sessionFactory;
    }
}