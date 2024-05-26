package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "formatos")
public class Formato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "no puede estar vacío")
    private String tamanio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "articulo_formato",
            joinColumns = @JoinColumn(name = "formato_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_id")
    )
    @JsonIgnoreProperties("formatos")
    private List<Articulo> articulos ;

    //METODOS GET/SET
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTamanio() {
        return tamanio;
    }
    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }
    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }
}
