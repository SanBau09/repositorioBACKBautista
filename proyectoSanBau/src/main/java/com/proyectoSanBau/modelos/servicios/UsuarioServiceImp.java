package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.IUsuarioDao;
import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import com.proyectoSanBau.modelos.entidades.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImp implements UserDetailsService, IUsuarioService {

    private Logger logger = LoggerFactory.getLogger(UsuarioServiceImp.class);

    @Autowired
    private IUsuarioDao usuarioDao;

    /**
     * Carga un usuario por su nombre de usuario para la autenticación.
     *
     * @param username El nombre de usuario del usuario que se desea cargar.
     * @return UserDetails Un objeto UserDetails que representa al usuario cargado.
     * @throws UsernameNotFoundException Si el usuario no puede ser encontrado en el sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar el usuario en la base de datos por su nombre de usuario
        Usuario usuario = usuarioDao.findByUsername(username);

        // Verificar si el usuario existe
        if(usuario == null){
            logger.error("Error en el login: el usuario '"+ username + "' no existe en el sistema!");
            throw new UsernameNotFoundException("Error en el login: el usuario '"+ username + "' no existe en el sistema!");
        }
        // Obtener los roles del usuario y convertirlos en GrantedAuthority
        List<GrantedAuthority> authorities = usuario.getRoles()
                .stream().
                map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                // Registrar cada rol para fines de seguimiento
                .peek(authority ->logger.info("Rol: " + authority.getAuthority()))
                .collect(Collectors.toList());

        // Crear y devolver un objeto UserDetails que representa al usuario cargado
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(),true, true, true, authorities);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioDao.findByUsername(username);
    }

    @Override
    @Transactional
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioDao.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioDao.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pais> findAllPaises() {
        return usuarioDao.findAllPaises();
    }
}
