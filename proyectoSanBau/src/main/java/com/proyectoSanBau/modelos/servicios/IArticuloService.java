package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Articulo;
import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;

import java.util.List;
import java.util.Set;

public interface IArticuloService {

    public Articulo saveArticulo(Articulo articulo);

    public List<Articulo> findAll();

    Set<Categoria> findAllCategorias();

    public Articulo findByIdArticulo(Long id);

    public void deleteArt(Long id);
}
