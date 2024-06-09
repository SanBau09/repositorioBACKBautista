package com.proyectoSanBau.modelos.servicios;


import com.proyectoSanBau.modelos.dao.IVentaDao;
import com.proyectoSanBau.modelos.entidades.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class VentaServiceImp implements IVentaService{
    @Autowired
    private IVentaDao ventaDao;


    @Override
    @Transactional
    public Venta saveVenta(Venta venta) {
        return ventaDao.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return (List<Venta>) ventaDao.findAll();
    }
}
