package journalApp.Controller;

import journalApp.Entity.User;
import journalApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Ok";
    }

    @PostMapping("/create-User")
    public void createUser(@RequestBody User user){
        userService.saveNewUser(user);
    }

}
