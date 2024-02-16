package com.web.store.security;

import com.web.store.security.filter.JwtAuthenticationFilter;
import com.web.store.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(requests -> requests
                        .antMatchers(HttpMethod.GET, "/user", "/user/{id}", "/user/search", "/user/search/url").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/user/create").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/user/create/admin").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/user/update/{id}").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/user/delete/{id}").hasRole("ADMIN")
                        //
                        .antMatchers(HttpMethod.GET, "/role", "/role/{id}").hasAnyRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/role/create", "/role/search").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/role/update/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/role/delete/{id}").hasRole("ADMIN")
                        //
                        .antMatchers(HttpMethod.GET, "/product", "/product/{id}", "/product/search", "/product/search/url").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/product/create", "/product/create-with-photo").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/product/update/{id}", "/product/update-with-photo/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/product/delete/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/product/uploads/image/{id}").hasRole("ADMIN")
                        //
                        .antMatchers(HttpMethod.GET, "/shoppingCart", "/shoppingCart/{id}").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/shoppingCart/create").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/shoppingCart/update/{id}").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/shoppingCart/delete/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/shoppingCart/{id}/totalValue", "/shoppingCart/invoice/{id}").hasAnyRole("USER", "ADMIN")
                        //
                        .antMatchers(HttpMethod.GET, "/brand", "/brand/{id}").hasAnyRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/brand/create", "/brand/create-with-photo").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/brand/update/{id}", "/brand/update-with-photo/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/brand/delete/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/brand/uploads/image/{id}").hasRole("ADMIN")
                        //
                        .antMatchers(HttpMethod.GET, "/cartItem", "/cartItem/{id}").hasAnyRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/cartItem/create").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/cartItem/update/{id}").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/cartItem/delete/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(csrf -> {
                    try {
                        csrf.disable()
                                .headers(headers -> headers.frameOptions().disable());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.accessDeniedHandler(new CustomAccessDeniedHandler()));
    }

    //

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return corsBean;
    }

}
