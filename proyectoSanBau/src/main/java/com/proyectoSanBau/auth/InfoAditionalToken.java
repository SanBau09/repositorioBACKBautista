package com.proyectoSanBau.auth;

import com.proyectoSanBau.modelos.entidades.Usuario;
import com.proyectoSanBau.modelos.servicios.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoAditionalToken implements TokenEnhancer {

    @Autowired
    private IUsuarioService usuarioService;

    /**
     * Agrega información adicional al token OAuth2.
     *
     * @param accessToken el token de acceso OAuth2
     * @param authentication la autenticación OAuth2
     * @return el token de acceso OAuth2 con información adicional
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        Map<String, Object> info = new HashMap<>();
        info.put("infoAdicional", "lalala".concat(authentication.getName()));
        info.put("nombre", usuario.getNombre());
        info.put("apellidos", usuario.getApellidos());
        info.put("email", usuario.getEmail());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

        return accessToken;
    }
}
