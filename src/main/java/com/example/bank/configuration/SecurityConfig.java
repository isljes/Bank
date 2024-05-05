package com.example.bank.configuration;

import com.example.bank.security.AuthSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception{
        http

                .csrf(Customizer.withDefaults())
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests(authorize->{
                    authorize.requestMatchers("/registration","/login","/forgot-password/**").hasRole("ANONYMOUS");
                    authorize.requestMatchers("/css/**","/js/**","/test/**").permitAll();

                    authorize.anyRequest().authenticated();
                })

                .formLogin(form->
                    form.loginPage("/login").successHandler(authenticationSuccessHandler()))
                .logout(logout->{
                    logout.logoutSuccessUrl("/login")
                            .deleteCookies("SESSION")
                            .clearAuthentication(true)
                            .invalidateHttpSession(true);
                })
                .rememberMe(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new AuthSuccessHandler();
    }

}
