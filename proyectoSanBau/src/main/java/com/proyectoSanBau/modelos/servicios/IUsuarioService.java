package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import com.proyectoSanBau.modelos.entidades.Usuario;

import java.util.List;

public interface IUsuarioService {

    public Usuario findByUsername(String username);

    public Usuario saveUsuario(Usuario usuario);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    public List<Pais> findAllPaises();

    public Usuario findByIdUsuario(Long id);


}
