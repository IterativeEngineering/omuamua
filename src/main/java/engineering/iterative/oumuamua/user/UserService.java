package engineering.iterative.oumuamua.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService {
    
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Reading all users from database");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.info("Reading user with id {} from database", id);
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(User user) {
        log.info("Creating new user in database: {}", user.getName());
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(Long id, User userDetails) {
        log.info("Updating user with id {} in database", id);

        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userDetails.getName());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        log.info("Deleting user with id {} from database", id);

        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}
