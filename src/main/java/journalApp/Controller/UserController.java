package journalApp.Controller;


import journalApp.Entity.User;
import journalApp.Repository.UserRepository;
import journalApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User newUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name= authentication.getName();
        User userInDb=userService.findByName(name);

        if (userInDb!=null){
            userInDb.setName(newUser.getName());
            userInDb.setPassword(newUser.getPassword());

            userService.saveNewUser(userInDb);
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(){
        Authentication authentication=SecurityContextHolder.createEmptyContext().getAuthentication();
        String name= authentication.getName();
        userService.deleteByName(name);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
