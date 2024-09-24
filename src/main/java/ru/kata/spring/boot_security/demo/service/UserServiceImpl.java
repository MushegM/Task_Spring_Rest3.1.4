package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final  UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;



    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username+" not found in the database");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
    }


// ////////////////////////////////




    @Override
    @Transactional
    public void save(User user) {
        String password = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> byId = userRepository.findById(id);
        return Optional.ofNullable(byId.orElseThrow(UserNotFoundException::new));
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        if (!user.getPassword().isEmpty()) {
            String password = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(password);
        }
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


    //new
    @Override
    public User updateUser(Long id, User userDetails) {
        Optional<User> user = findById(id);
        if (!userDetails.getPassword().isEmpty()) {
            String password = bCryptPasswordEncoder.encode(userDetails.getPassword());
            userDetails.setPassword(password);
        }
        userConvertor(user.get(),userDetails);
       return userRepository.save(user.get());
    }

    private User userConvertor(User updateUser,User user) {
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setLastName(user.getLastName());
        updateUser.setEmail(user.getEmail());
        updateUser.setAge(user.getAge());
        updateUser.setPassword(user.getPassword());
        updateUser.setRoles(user.getRoles());
        return updateUser;
    }
}
