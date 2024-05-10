package ru.artosoft.vcsvpl.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String name) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(name);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + name + " not found");
        }
        return UserDetailsImpl.build(user);
    }
}
