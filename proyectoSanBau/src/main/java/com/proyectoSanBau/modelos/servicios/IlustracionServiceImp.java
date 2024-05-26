package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.IIlustrationDao;
import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IlustracionServiceImp implements IIlustracionService{

    @Autowired
    private IIlustrationDao ilustracionDao;

    @Override
    @Transactional
    public Ilustracion saveIlustracion(Ilustracion ilustracion) {
        return ilustracionDao.save(ilustracion);
    }

    @Transactional
    @Override
    public void deleteIlu(Long id) {
        ilustracionDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Ilustracion> findAll() {
        Set<Ilustracion> hashSet = new HashSet<>(ilustracionDao.findAll());
        return hashSet;
    }

    @Override
    @Transactional(readOnly = true)
    public Ilustracion findByIdIlustracion(Long id) {
        return ilustracionDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Categoria> findAllCategorias() {return ilustracionDao.findAllCategorias();
    }
}
