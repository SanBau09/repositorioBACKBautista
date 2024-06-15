package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Formato;
import com.proyectoSanBau.modelos.entidades.Ilustracion;

import java.util.List;
import java.util.Set;

public interface IFormatoService {

    public Formato saveFormato(Formato formato);

    public List<Formato> findAll();

    public void deleteFormato(Long id);
}
