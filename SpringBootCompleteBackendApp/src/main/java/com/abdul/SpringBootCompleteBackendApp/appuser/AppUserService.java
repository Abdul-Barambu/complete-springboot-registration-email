package com.abdul.SpringBootCompleteBackendApp.appuser;

import com.abdul.SpringBootCompleteBackendApp.registration.token.ConfirmationToken;
import com.abdul.SpringBootCompleteBackendApp.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email does not exist"));
    }

    public String signUpUser(AppUser appUser){
        boolean userExist = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if(userExist){
            throw new IllegalStateException("Email Already Taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

//        send confirmation token

//        create a UUID token and make it string
        String token = UUID.randomUUID().toString();

//        create object of confirmationToken
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO: Send Email

        return token;
    }

    public int enableAppUser(String email){
        return appUserRepository.enableAppUser(email);
    }
}
