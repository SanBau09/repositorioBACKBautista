package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Formato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormatoDao extends JpaRepository<Formato, Long> {
}
