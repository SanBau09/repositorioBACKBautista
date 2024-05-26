package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name= "categorias")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private boolean esGaleria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ilustracion_categoria",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "ilustracion_id")
    )
    @JsonIgnoreProperties("categorias")
    private Set<Ilustracion> ilustraciones = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "articulo_categoria",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_id")
    )
    @JsonIgnoreProperties("categorias")
    private List<Articulo> articulos ;


    //METODOS GET/SET
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsGaleria() {
        return esGaleria;
    }
    public void setEsGaleria(boolean esGaleria) {
        this.esGaleria = esGaleria;
    }

    public Set<Ilustracion> getIlustraciones() {
        return ilustraciones;
    }
    public void setIlustraciones(Set<Ilustracion> ilustraciones) {
        this.ilustraciones = ilustraciones;
    }


    public List<Articulo> getArticulos() {
        return articulos;
    }
    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }
}
