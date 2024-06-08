package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.ICategoriaDao;
import com.proyectoSanBau.modelos.dao.IFormatoDao;
import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Formato;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FormatoServiceImp implements IFormatoService {
    @Autowired
    private IFormatoDao formatoDao;

    @Override
    @Transactional
    public Formato saveFormato(Formato formato) {return formatoDao.save(formato);}

    @Override
    @Transactional(readOnly = true)
    public List<Formato> findAll() {
        List<Formato> list = new ArrayList<Formato>(formatoDao.findAll());
        return list;
    }

}
