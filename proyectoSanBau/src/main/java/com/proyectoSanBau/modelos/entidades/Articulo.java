package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "articulos")
public class Articulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "no puede estar vacío")
    private String titulo;
    @NotEmpty(message = "no puede estar vacío")
    @Size(min=10, max=200, message = "el tamaño tiene que estar entre 10 y 200 caracteres")
    private String descripción;
    @NotEmpty(message = "no puede estar vacío")
    private int stock;
    @NotEmpty(message = "no puede estar vacío")
    private float precio;
    @NotEmpty(message = "no puede estar vacío")
    private String imagen;

    private Categoria categoria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "articulo_categoria",
            joinColumns = @JoinColumn(name = "articulo_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonIgnoreProperties("articulos")
    private List<Categoria> categorias;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "articulo_formato",
            joinColumns = @JoinColumn(name = "articulo_id"),
            inverseJoinColumns = @JoinColumn(name = "formato_id")
    )
    @JsonIgnoreProperties("articulos")
    private List<Formato> formatos;



}
