package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.ICategoriaDao;

import com.proyectoSanBau.modelos.entidades.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaServiceImp implements ICategoriaService{

    @Autowired
    private ICategoriaDao categoriaDao;

    @Override
    @Transactional
    public Categoria saveCategoria(Categoria categoria) {return categoriaDao.save(categoria);}

    @Override
    @Transactional
    public void deleteCategoria(Long id) {
        categoriaDao.deleteById(id);
    }
}
