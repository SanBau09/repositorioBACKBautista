package com.proyectoSanBau.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configura la seguridad de la aplicación Spring Boot.
 * Define autenticación de usuarios, manejo de sesiones y seguridad de solicitudes.
 */
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService usuarioService;

    /**
     * Proporciona una instancia de BCryptPasswordEncoder para encriptar contraseñas.
     * @return una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public static BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el AuthenticationManagerBuilder con el servicio de usuario y el codificador de contraseñas.
     * @param auth el AuthenticationManagerBuilder.
     * @throws Exception en caso de error en la configuración.
     */
    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder());
    }
    /**
     * Proporciona una instancia de AuthenticationManager.
     * @return una instancia de AuthenticationManager.
     * @throws Exception en caso de error en la configuración.
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }
    /**
     * Configura la seguridad HTTP para la aplicación.
     * @param http el HttpSecurity.
     * @throws Exception en caso de error en la configuración.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()  //se quita la proteccion csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //como vamos a trabajar en Angular con token, no son necesarias las sesiones
    }

}
