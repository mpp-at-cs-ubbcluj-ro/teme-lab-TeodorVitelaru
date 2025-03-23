package ro.mpp2024.repo;

import ro.mpp2024.domain.User;

public interface UserRepo extends Repository<Long, User> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
