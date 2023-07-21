package com.example.demo.config;


import com.example.demo.security.CustomOAuth2UserService;
import com.example.demo.security.DatabaseLoginSuccessHandler;
import com.example.demo.security.OAuthLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//import jakarta.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SpringSercurity {

    @Autowired
    private UserDetailsService userDetailsService;

//    @Autowired
//    private DataSource;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .requestMatchers("/forgot_password").permitAll()
                                .requestMatchers("/reset_password").permitAll()

                                .requestMatchers("/static/**").permitAll()
                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")

                                .requestMatchers("/product/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                                .requestMatchers("/product/edit/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                                .requestMatchers("/product/delete/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/product/save").hasAuthority("ROLE_ADMIN")

                ).formLogin(form -> {

            try {
                form.permitAll()

                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/users")
                        .usernameParameter("email")
                        .passwordParameter("pass")
                        .successHandler(databaseLoginSuccessHandler)
                        .permitAll()

                        .and()
                        .rememberMe()

                        //save cookie
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // time 7 day
                        .key("AbcdefghiJklmNoPqRstUvXyz");


                //save database
//                                .tokenRepository(p)


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ).oauth2Login((oauth2) ->
                oauth2
                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/users")
                        .permitAll()
                        .userInfoEndpoint()
                            .userService(oAuth2UserService)
                        .and()
                        .successHandler(oauthLoginSuccessHandler)

        ).logout(
                logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
        );
        return http.build();
    }

//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
//        tokenRepo.setDataSource(dataSource);
//        return tokenRepo;
//    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private OAuthLoginSuccessHandler oauthLoginSuccessHandler;

    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
}
