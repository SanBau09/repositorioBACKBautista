package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.IArticuloDao;
import com.proyectoSanBau.modelos.entidades.Articulo;
import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
public class ArticuloServiceImp implements IArticuloService{

    @Autowired
    private IArticuloDao articuloDao;

    @Override
    @Transactional
    public Articulo saveArticulo(Articulo articulo) {
        return articuloDao.save(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findAll() {
        return (List<Articulo>) articuloDao.findAll().stream().filter(x -> x.isEsActivo())
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Categoria> findAllCategorias() {return articuloDao.findAllCategorias();
    }

    @Override
    @Transactional(readOnly = true)
    public Articulo findByIdArticulo(Long id) {
        return articuloDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void deleteArt(Long id) {
        Articulo articulo = this.findByIdArticulo(id);
        articulo.setEsActivo(false);
        this.saveArticulo(articulo);
    }
}
