package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Venta;

import java.util.List;

public interface IVentaService {

    public Venta saveVenta(Venta venta);

    public List<Venta> findAll();
}
