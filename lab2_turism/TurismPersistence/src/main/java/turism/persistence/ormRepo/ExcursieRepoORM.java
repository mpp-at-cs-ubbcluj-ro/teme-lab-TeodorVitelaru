package turism.persistence.ormRepo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import turism.model.Excursie;
import turism.persistence.ExcursieRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ExcursieRepoORM implements ExcursieRepo {
    private static final Logger logger = LogManager.getLogger();
    private final SessionFactory sessionFactory;

    public ExcursieRepoORM(SessionFactory sessionFactory) {
        logger.info("Initializing ExcursieRepoHibernate");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Excursie> findByDestinationAndDate(String obiectiv, LocalDateTime date1, LocalDateTime date2) {
        logger.traceEntry("finding excursii with destination {} and date between {} and {}", obiectiv, date1, date2);

        List<Excursie> excursii = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query<Excursie> query = session.createQuery(
                        "from Excursie where obiectiv = :obiectiv and dataPlecarii between :date1 and :date2",
                        Excursie.class
                );
                query.setParameter("obiectiv", obiectiv);
                query.setParameter("date1", date1);
                query.setParameter("date2", date2);

                excursii = query.list();

                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit(excursii);
        return excursii;
    }

    @Override
    public Optional<Excursie> findOne(Long id) {
        logger.traceEntry("finding excursie with id {}", id);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Excursie excursie = session.get(Excursie.class, id);

                tx.commit();
                logger.traceExit(excursie);
                return Optional.ofNullable(excursie);
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit("null");
        return Optional.empty();
    }

    @Override
    public Iterable<Excursie> findAll() {
        logger.traceEntry("finding all excursii");

        List<Excursie> excursii = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                excursii = session.createQuery("from Excursie", Excursie.class).list();

                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit(excursii);
        return excursii;
    }

    @Override
    public Optional<Excursie> save(Excursie entity) {
        logger.traceEntry("saving excursie {}", entity);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                session.save(entity);

                tx.commit();
                logger.traceExit(entity);
                return Optional.of(entity);
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit("null");
        return Optional.empty();
    }

    @Override
    public Optional<Excursie> delete(Long id) {
        logger.traceEntry("deleting excursie with id {}", id);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Excursie excursie = session.get(Excursie.class, id);
                if (excursie != null) {
                    session.delete(excursie);
                    tx.commit();
                    logger.traceExit(excursie);
                    return Optional.of(excursie);
                }

                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit("null");
        return Optional.empty();
    }

    @Override
    public Optional<Excursie> update(Excursie entity) {
        logger.traceEntry("updating excursie {}", entity);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                session.update(entity);

                tx.commit();
                logger.traceExit(entity);
                return Optional.of(entity);
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(ex);
                System.out.println("Error DB " + ex);
            }
        }

        logger.traceExit("null");
        return Optional.empty();
    }
}