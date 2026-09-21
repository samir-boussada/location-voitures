package com.locationvoitures.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filtreSecurite(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)auth.requestMatchers(new RequestMatcher[]{AntPathRequestMatcher.antMatcher((String)"/javax.faces.resource/**"), AntPathRequestMatcher.antMatcher((String)"/jakarta.faces.resource/**"), AntPathRequestMatcher.antMatcher((String)"/resources/**"), AntPathRequestMatcher.antMatcher((String)"/images/**"), AntPathRequestMatcher.antMatcher((String)"/login.html"), AntPathRequestMatcher.antMatcher((String)"/registration.html"), AntPathRequestMatcher.antMatcher((String)"/inscription")})).permitAll().anyRequest()).authenticated()).formLogin(form -> ((FormLoginConfigurer)((FormLoginConfigurer)((FormLoginConfigurer)form.loginPage("/login.html").loginProcessingUrl("/login")).failureUrl("/login.html?error")).defaultSuccessUrl("/", true)).permitAll()).logout(logout -> logout.permitAll());
        return (SecurityFilterChain)http.build();
    }

    @Bean
    public FilterRegistrationBean<LoginPageRedirectFilter> filtreLogin() {
        FilterRegistrationBean enregistrement = new FilterRegistrationBean();
        enregistrement.setFilter((Filter)new LoginPageRedirectFilter());
        enregistrement.addUrlPatterns(new String[]{"/login.html"});
        enregistrement.setOrder(2147483547);
        return enregistrement;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class LoginPageRedirectFilter
    extends OncePerRequestFilter {
        protected void doFilterInternal(HttpServletRequest requete, HttpServletResponse reponse, FilterChain chaine) throws ServletException, IOException {
            boolean connecte;
            Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
            boolean bl = connecte = authentification != null && authentification.isAuthenticated() && !(authentification instanceof AnonymousAuthenticationToken);
            if (connecte && "GET".equalsIgnoreCase(requete.getMethod())) {
                reponse.sendRedirect(requete.getContextPath() + "/");
                return;
            }
            chaine.doFilter((ServletRequest)requete, (ServletResponse)reponse);
        }
    }
}