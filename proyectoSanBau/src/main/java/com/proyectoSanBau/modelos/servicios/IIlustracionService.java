package com.proyectoSanBau.modelos.servicios;


import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;


import java.util.List;
import java.util.Set;

public interface IIlustracionService {

    public Ilustracion saveIlustracion(Ilustracion ilustracion);

    public void deleteIlu(Long id);

    public Set<Ilustracion> findAll();

    public Ilustracion findByIdIlustracion(Long id);

    Set<Categoria> findAllCategorias();

    List<String> validarIlustracion(Ilustracion ilustracion);
}
