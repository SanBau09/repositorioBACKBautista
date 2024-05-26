package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Articulo;
import com.proyectoSanBau.modelos.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface IArticuloDao extends JpaRepository<Articulo, Long> {

    @Query("from Categoria")
    public Set<Categoria> findAllCategorias();
}
