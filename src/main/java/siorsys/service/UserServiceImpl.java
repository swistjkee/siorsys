package siorsys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import siorsys.dao.UserDao;
import siorsys.model.User;

import java.util.List;

@Service("userManager")
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public void createUser(User user) {
        userDao.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public User getUserByLogin(String login) {
        return userDao.getUserByLogin(login);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}
