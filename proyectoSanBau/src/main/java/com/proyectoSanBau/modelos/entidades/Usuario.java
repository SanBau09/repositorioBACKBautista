package com.proyectoSanBau.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="usuarios")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "el username no puede estar vacío")
    @Column(unique=true, length = 20)
    private String username;
    @NotNull(message = "el password no puede estar vacío")
    @Column(length = 60)
    private String password;
    private Boolean enabled;

    @NotNull(message = "el nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "apellidos no puede estar vacío")
    private String apellidos;

    @NotNull(message = "el email no puede estar vacío")
    @Column(unique=true)
    private String email;

    @NotNull(message = "el telefono no puede estar vacío")
    @Column(length = 9)
    private String telefono;

    @NotNull(message = "la localidad no puede estar vacía")
    private String localidad;

    @NotNull(message = "la provincia no puede estar vacía")
    private String provincia;

    @NotNull(message = "el país no puede estar vacío")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="paisId")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Pais pais;

    @NotNull(message = "la dirección no puede estar vacía")
    private String direccion;

    @NotNull(message = "el código postal no puede estar vacío")
    private String CP;


    @Temporal(TemporalType.DATE)  //Especifica que un campo de fecha debe ser tratado como una fecha (sin tiempo).
    private Date fechaRegistro;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties("usuario")
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties("usuario")   //para evitar problemas con la serialización/deserialización de propiedades relacionadas.
    private List<Venta> ventas;


    //METODOS GET/SET
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Rol> getRoles() {
        return roles;
    }
    public void setRoles(List<Rol> roles) {
        this.roles = roles;
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

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Pais getPais() {
        return pais;
    }
    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCP() {
        return CP;
    }
    public void setCP(String CP) {
        this.CP = CP;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Venta> getVentas() {
        return ventas;
    }
    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}
