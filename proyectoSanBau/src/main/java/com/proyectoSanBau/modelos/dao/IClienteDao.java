package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface IClienteDao extends JpaRepository <Cliente, Long> {
    @Query("from Pais")
    public List<Pais> findAllPaises();
}
