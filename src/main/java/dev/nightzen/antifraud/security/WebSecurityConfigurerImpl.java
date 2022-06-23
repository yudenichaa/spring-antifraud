package dev.nightzen.antifraud.security;

import dev.nightzen.antifraud.constants.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .mvcMatchers("/h2/**")
                .permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/auth/user")
                .permitAll()
                .mvcMatchers("/actuator/shutdown")
                .permitAll()
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/**")
                .hasAuthority(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.GET, "/api/auth/list")
                .hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction")
                .hasAuthority(UserRole.MERCHANT.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role")
                .hasAuthority(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access")
                .hasAuthority(UserRole.ADMINISTRATOR.name())
                .mvcMatchers("/api/antifraud/suspicious-ip/**")
                .hasAuthority(UserRole.SUPPORT.name())
                .mvcMatchers("/api/antifraud/stolencard/**")
                .hasAuthority(UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET,"/api/antifraud/history/**")
                .hasAuthority(UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction/**")
                .hasAuthority(UserRole.SUPPORT.name())
                .mvcMatchers("/**")
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
