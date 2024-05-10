package ru.artosoft.vcsvpl.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;
    private BCryptPasswordEncoder encoder(){ return new BCryptPasswordEncoder();}
    public boolean saveUser(UserEntity user){
        UserEntity userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setPassword(encoder().encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }

    public boolean existsByName(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isValidUsername(String username) {
        String pattern = "^[a-zA-Z0-9]+$";
        return username.matches(pattern);
    }


    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }
}
