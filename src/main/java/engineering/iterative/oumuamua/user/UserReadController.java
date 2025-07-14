package engineering.iterative.oumuamua.user;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Log4j2
public class UserReadController {

    private final UserService userService;

    @Autowired
    public UserReadController(UserService userService) {
        this.userService = userService;
    }

    // GET all users (read operation)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("REST request to get all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET user by ID (read operation)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("REST request to get user with id {}", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
