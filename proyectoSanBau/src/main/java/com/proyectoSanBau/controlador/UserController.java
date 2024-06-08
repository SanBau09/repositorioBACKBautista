package com.proyectoSanBau.controlador;


import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import com.proyectoSanBau.modelos.entidades.Rol;
import com.proyectoSanBau.modelos.entidades.Usuario;
import com.proyectoSanBau.modelos.servicios.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin(origins = {"*"})  //permite que cualquier dominio acceda a los recursos del backend sin restricciones de CORS.
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("registro")
    public ResponseEntity<?> create(@Valid @RequestBody Usuario usuario, BindingResult result){
        Usuario usuarioNuevo = null;
        Map<String, Object> response = new HashMap<>();

        //manejo de errores para la validación
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        // Verificar si el email ya existe
        if(usuarioService.existsByEmail(usuario.getEmail())){
            response.put("mensaje", "el email ya está en uso");
            response.put("error","el email ya está en uso");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el username ya existe
        if(usuarioService.existsByUsername(usuario.getUsername())){
            response.put("mensaje", "el nombre de usuario ya está en uso");
            response.put("error","el nombre de usuario ya está en uso");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }


        try{
            usuario.setFechaRegistro(new Date());
            ArrayList<Rol> listaRoles = new ArrayList<Rol>();
            Rol rolUser = new Rol();
            rolUser.setId(2L); //asignarle el id 2 (ROLE_USER) a los usuarios que se registren
            rolUser.setNombre("ROLE_USER"); //asignarle el nombre (ROLE_USER) a los usuarios que se registren
            listaRoles.add(rolUser);
            usuario.setRoles(listaRoles);

            usuario.setEnabled(true);

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            usuarioNuevo = usuarioService.saveUsuario(usuario);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al insertar nuevo usuario en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado con éxito");
        response.put("usuario", usuarioNuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("paises")
    public List<Pais> listarPaises(){
        return usuarioService.findAllPaises();
    }

    @Secured({"ROLE_USER"})
    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable Long id){

        Usuario usuario = null;
        Map<String, Object> response = new HashMap<>();

        try{
            usuario = usuarioService.findByIdUsuario(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al lanzar la consulta en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(usuario == null){
            response.put("mensaje", "El usuario ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }

    @Secured({"ROLE_USER"})
    @GetMapping("user/{username}")
    public ResponseEntity<?> show(@PathVariable String username){

        Usuario usuario = null;
        Map<String, Object> response = new HashMap<>();

        try{
            usuario = usuarioService.findByUsername(username);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al lanzar la consulta en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(usuario == null){
            response.put("mensaje", "El usuario USERNAME: ".concat(username.concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @PutMapping("{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Usuario usuario,BindingResult result, @PathVariable Long id){

        Usuario usuarioActual = usuarioService.findByIdUsuario(id);
        Usuario usuarioUpdate = null;

        Map<String, Object> response = new HashMap<>();

        //manejo de errores para la validación
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(usuarioActual == null){
            response.put("mensaje", "Error: no se puedo editar, el usuario ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            usuarioActual.setNombre(usuario.getNombre());
            usuarioActual.setApellidos(usuario.getApellidos());
            usuarioActual.setTelefono(usuario.getTelefono());
            usuarioActual.setCP(usuario.getCP());
            usuarioActual.setDireccion(usuario.getDireccion());
            usuarioActual.setProvincia(usuario.getProvincia());
            usuarioActual.setLocalidad(usuario.getLocalidad());
            usuarioActual.setPais(usuario.getPais());

            usuarioUpdate = usuarioService.saveUsuario(usuarioActual);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al actualizar usuario en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido actualizado con éxito");
        response.put("usuario", usuarioUpdate);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

}
