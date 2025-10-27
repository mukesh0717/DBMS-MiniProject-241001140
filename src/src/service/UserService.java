package src.service;

import src.dao.UserDAO;
import src.model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public boolean registerUser(User user) {
        return userDAO.registerUser(user);
    }

    public User login(String email, String password) {
        return userDAO.login(email, password);
    }

    public boolean removeStudent(String email) {
        return userDAO.removeStudent(email);
    }
}
