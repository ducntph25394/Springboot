package com.example.demo.service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.AuthenticationProvider;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

        private UserRepository userRepository;
        private RoleRepository roleRepository;
        private PasswordEncoder passwordEncoder;

        public UserServiceImpl(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void saveUser(UserDto userDto) {
            User user = new User();
            user.setName(userDto.getFirstName() + " " + userDto.getLastName());
            user.setEmail(userDto.getEmail());

            //encrypt the password once we integrate spring security
            //user.setPassword(userDto.getPassword());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            Role role = roleRepository.findByName("ROLE_USER");
            if(role == null){
                role = checkRoleExist();
            }
            user.setRoles(Arrays.asList(role));
            userRepository.save(user);
        }

        @Override
        public User findByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        @Override
        public List<UserDto> findAllUsers() {
            List<User> users = userRepository.findAll();
            return users.stream().map((user) -> convertEntityToDto(user))
                    .collect(Collectors.toList());
        }


    private UserDto convertEntityToDto(User user){
            UserDto userDto = new UserDto();
            String[] name = user.getName().split(" ");
            userDto.setFirstName(name[0]);
            userDto.setLastName(name[1]);
            userDto.setEmail(user.getEmail());
            return userDto;
        }

        private Role checkRoleExist() {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        }

        public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
            User customer = userRepository.findByEmail(email);
            if (customer != null) {
                customer.setResetPasswordToken(token);
                userRepository.save(customer);
            } else {
                throw new CustomerNotFoundException("Could not find any customer with the email " + email);
            }
        }

        public User getByResetPasswordToken(String token) {
            return userRepository.findByResetPasswordToken(token);
        }

        public void updatePassword(User customer, String newPassword) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);
            customer.setPassword(encodedPassword);

            customer.setResetPasswordToken(null);
            userRepository.save(customer);
        }


    @Override
    public void updateAuthenticationType(String email, String oauth2ClientName) {
        AuthenticationProvider authType = AuthenticationProvider.valueOf(oauth2ClientName.toUpperCase());
        userRepository.updateAuthenticationProvider(email, authType);
        System.out.println("Updated user's authentication type to " + authType);
    }

}
