package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Usuario;

public interface IUsuarioService {

    public Usuario findByUsername(String username);

}
