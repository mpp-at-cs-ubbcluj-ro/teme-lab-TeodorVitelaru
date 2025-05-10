package turism.persistence;


import turism.model.User;

public interface UserRepo extends Repository<Long, User> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
