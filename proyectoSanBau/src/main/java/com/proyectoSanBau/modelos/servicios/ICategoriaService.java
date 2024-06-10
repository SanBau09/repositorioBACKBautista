package com.proyectoSanBau.modelos.servicios;


import com.proyectoSanBau.modelos.entidades.Categoria;

public interface ICategoriaService {

    public Categoria saveCategoria(Categoria categoria);
    public void deleteCategoria(Long id);
}
