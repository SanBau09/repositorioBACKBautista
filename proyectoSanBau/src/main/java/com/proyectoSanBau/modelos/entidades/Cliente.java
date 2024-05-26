package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotEmpty(message = "no puede estar vacío")
    @Size(min=4, max=12, message = "el tamaño tiene que estar entre 4 y 12 caracteres")
    private String nombre;
    @Column(nullable = false)
    @NotEmpty(message = "no puede estar vacío")
    private String apellidos;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "no puede estar vacío")
    @Email(message = "no cumple con la estructura que debe tener un email")
    private String email;
    @Column
    @NotNull(message = "no puede estar vacío")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    private String foto;

    @NotNull(message = "el país no puede estar vacío")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="paisId")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Pais pais;


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

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Pais getPais() {
        return pais;
    }
    public void setPais(Pais pais) {
        this.pais = pais;
    }

    private static final long serialVersionUID = 1L;
}
