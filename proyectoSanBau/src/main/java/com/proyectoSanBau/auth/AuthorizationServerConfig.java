package com.proyectoSanBau.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private InfoAditionalToken infoAditionalToken;

    /**
     * Configura la seguridad del servidor de autorización.
     *
     * @param security el configurador de seguridad del servidor de autorización
     * @throws Exception en caso de error en la configuración
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * Configura los detalles del cliente.
     *
     * @param clients el configurador de detalles del cliente
     * @throws Exception en caso de error en la configuración
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("angularApp")
                .secret(passwordEncoder.encode("1234"))
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(7200)
                .refreshTokenValiditySeconds(7200);
    }

    /**
     * Configura los endpoints del servidor de autorización.
     *
     * @param endpoints el configurador de endpoints del servidor de autorización
     * @throws Exception en caso de error en la configuración
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAditionalToken, accesTokenConverter()));

        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(accesTokenConverter())
                .tokenEnhancer(tokenEnhancerChain);
    }

    /**
     * Define un bean de JwtTokenStore que utiliza el convertidor de tokens JWT.
     *
     * @return una instancia de JwtTokenStore
     */
    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accesTokenConverter());
    }

    /**
     * Define un bean de JwtAccessTokenConverter con la clave de firma configurada.
     *
     * @return una instancia de JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter accesTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(JwtConfig.SECRET_KEY);
        return jwtAccessTokenConverter;
    }
}
