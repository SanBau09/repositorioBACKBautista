package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="ventas")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventas"})
    private Usuario usuario;

    @Temporal(TemporalType.DATE)
    private Date fechaVenta;

    private float precioTotal;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties("ventas")
    private List<Articulo> articulos;

    //METODOS GET/SET
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public float getPrecioTotal() {
        return precioTotal;
    }
    public void setPrecioTotal(float precioTotal) {
        this.precioTotal = precioTotal;
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }
    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }
}
