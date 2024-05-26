package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ICategoriaDao extends JpaRepository<Categoria, Long> {

    @Query("from Ilustracion")
    public Set<Ilustracion> findAllIlustraccionesByCategoria();
}
