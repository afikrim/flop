package com.github.afikrim.flop.config;

import com.github.afikrim.flop.users.details.UserDetailService;
import com.github.afikrim.flop.utils.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private JwtFilter jwtFilter;

    public UserDetailService getUserDetailService() {
        return userDetailService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()//
                .disable()//
                .authorizeRequests()//
                .antMatchers(HttpMethod.GET, "/").permitAll()//
                .antMatchers(HttpMethod.POST, "/v1/auth/login", "/v1/auth/register").permitAll()//
                .antMatchers(HttpMethod.GET, "/v1/users/**", "/v1/wallets").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")//
                .antMatchers(HttpMethod.POST, "/v1/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")//
                .antMatchers(HttpMethod.PUT, "/v1/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")//
                .antMatchers(HttpMethod.DELETE, "/v1/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")//
                .antMatchers(HttpMethod.GET, "/v1/users", "/v1/system-wallets/**").hasAnyAuthority("ROLE_ADMIN")//
                .antMatchers(HttpMethod.POST, "/v1/wallets/**", "/v1/system-wallets/**").hasAnyAuthority("ROLE_ADMIN")//
                .antMatchers(HttpMethod.PUT, "/v1/wallets/**", "/v1/system-wallets/**").hasAnyAuthority("ROLE_ADMIN")//
                .antMatchers(HttpMethod.DELETE, "/v1/wallets/**", "/v1/system-wallets/**").hasAnyAuthority("ROLE_ADMIN")//
                .anyRequest()//
                .authenticated()//
                .and()//
                .exceptionHandling()//
                .and()//
                .sessionManagement()//
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
