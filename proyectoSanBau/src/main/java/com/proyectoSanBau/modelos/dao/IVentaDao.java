package com.proyectoSanBau.modelos.dao;

import com.proyectoSanBau.modelos.entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVentaDao extends JpaRepository<Venta, Long> {
}
