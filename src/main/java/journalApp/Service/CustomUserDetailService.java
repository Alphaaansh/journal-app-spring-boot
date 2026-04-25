package journalApp.Service;

import journalApp.Entity.User;
import journalApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByName(username );

        if (user !=null){
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getName())
                    .password(user.getPassword())
                    .authorities(user.getRoles().stream()
                            .map(role ->new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList()))
                    .build();
        }
        System.out.println("User Not Found : "+username);
        throw new UsernameNotFoundException("User Not Found : "+username);
    }
}
