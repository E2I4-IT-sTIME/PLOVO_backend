package com.kb_hackathon.plovo.config;

import com.kb_hackathon.plovo.config.jwt.CustomAuthenticationEntryPoint;
import com.kb_hackathon.plovo.config.jwt.JwtAuthorizationFilter;
import com.kb_hackathon.plovo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilter())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**")
                .access("hasRole('ROLE_USER')")
                .anyRequest().permitAll()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .build();
    }

    public class CustomFilter extends AbstractHttpConfigurer<CustomFilter, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsFilter)
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
//                    .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, userRepository), UsernamePasswordAuthenticationFilter.class);
        }
    }
}
