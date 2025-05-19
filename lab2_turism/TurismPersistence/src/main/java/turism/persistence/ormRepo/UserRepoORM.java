package turism.persistence.ormRepo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import turism.model.User;
import turism.persistence.UserRepo;

import java.util.List;
import java.util.Optional;

public class UserRepoORM implements UserRepo {

    private final SessionFactory sessionFactory;

    public UserRepoORM(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery("FROM User WHERE username = :username AND password = :password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
        }
    }

    @Override
    public Optional<User> findOne(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        }
    }

    @Override
    public Iterable<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            return users;
        }
    }

    @Override
    public Optional<User> save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Long id = (Long) session.save(user);
            tx.commit();
            user.setId(id);
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                tx.commit();
                return Optional.of(user);
            }
            tx.rollback();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> update(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User existing = session.find(User.class, user.getId());
            if (existing != null) {
                existing.setUsername(user.getUsername());
                existing.setPassword(user.getPassword());
                session.merge(existing);
                tx.commit();
                return Optional.empty();
            }
            tx.rollback();
            return Optional.of(user);
        }
    }
}
