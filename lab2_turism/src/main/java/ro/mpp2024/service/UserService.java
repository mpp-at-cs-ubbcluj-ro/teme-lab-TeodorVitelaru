package ro.mpp2024.service;

import ro.mpp2024.domain.User;
import ro.mpp2024.repo.UserRepo;

public class UserService {
    private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User findUser(String username, String password) {
        try{
            return userRepo.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            System.out.println("Error finding user " + e);
        }
        return null;
    }
}
