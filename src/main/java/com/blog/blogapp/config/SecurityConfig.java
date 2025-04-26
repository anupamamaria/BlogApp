package com.blog.blogapp.config;

import com.blog.blogapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/posts/create").hasAnyRole("ADMIN", "AUTHOR")
                                .requestMatchers("/posts/{id}/edit").hasAnyRole("ADMIN", "AUTHOR")
                                .requestMatchers("/posts/{id}/delete").hasAnyRole("ADMIN", "AUTHOR")
                                .requestMatchers(HttpMethod.POST, "/posts").hasAnyRole("ADMIN", "AUTHOR")
                                .requestMatchers("/comments/add").permitAll()
                                .requestMatchers("/", "/posts/**", "/register", "/css/**").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/author/**").hasRole("AUTHOR")
                                .anyRequest().authenticated()
                )
//                .formLogin(form -> form
//                        .loginPage("/login").permitAll()
//                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/posts", true)
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace(); // Logs why login failed
                            response.sendRedirect("/login?error=true"); // Redirects to your login page with error param
                        })
                        .permitAll()
                )



//                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                        .logoutUrl("/logout")
                .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                .permitAll()
    );

        return http.build();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}

