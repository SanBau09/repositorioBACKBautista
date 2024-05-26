package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import com.proyectoSanBau.modelos.entidades.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface IIlustrationDao extends JpaRepository<Ilustracion, Long> {

    @Query("from Categoria")
    public Set<Categoria> findAllCategorias();
}
