package com.proyectoSanBau.controlador;

import com.proyectoSanBau.modelos.entidades.Articulo;
import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import com.proyectoSanBau.modelos.servicios.IArticuloService;
import com.proyectoSanBau.modelos.servicios.IUploadFileService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    private IArticuloService articuloService;
    @Autowired
    private IUploadFileService uploadService;


    @GetMapping("/articulos")
    public List<Articulo> index(){
        return articuloService.findAll();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/articulos", consumes = {"multipart/form-data"})
    public ResponseEntity<?> create(@RequestParam("archivo") MultipartFile archivo, @RequestParam("articulo") String articulo){
        Articulo articuloNuevo = null;
        Map<String, Object> response = new HashMap<>();

        try{
            if(!archivo.isEmpty()){
                String nombreArchivo = null;
                ObjectMapper mapper = new ObjectMapper();
                try{
                    Articulo art = mapper.readValue(articulo, Articulo.class);
                    nombreArchivo = uploadService.copiar(archivo);

                    art.setImagen(nombreArchivo);
                    articuloNuevo = articuloService.saveArticulo(art);
                }catch (IOException e){
                    response.put("mensaje", "Error al subir la imagen " + nombreArchivo);
                    response.put("errors",e.getMessage());
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        }catch(DataAccessException e){
            response.put("mensaje", "Error al insertar nuevo articulo en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El articulo ha sido añadido con éxito");
        response.put("articulo", articuloNuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/articulos/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Articulo articulo, BindingResult result, @PathVariable Long id){

        Articulo articuloActual = articuloService.findByIdArticulo(id);
        Articulo articuloUpdate = null;

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

        if(articuloActual == null){
            response.put("mensaje", "Error: no se puedo editar, el artículo ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            articuloActual.setTitulo(articulo.getTitulo());
            articuloActual.setDescripcion(articulo.getDescripcion());
            articuloActual.setFormatos(articulo.getFormatos());
            articuloActual.setPrecio(articulo.getPrecio());
            articuloActual.setCategorias(articulo.getCategorias());

            articuloUpdate = articuloService.saveArticulo(articuloActual);

        }catch(DataAccessException e){
            response.put("mensaje", "Error al actualizar articulo en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El artículo ha sido actualizado con éxito");
        response.put("articulo", articuloUpdate);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/articulos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{//si el articulo tiene una foto la elimina tb
            Articulo articulo = articuloService.findByIdArticulo(id);
            String nombreFotoAnterior = articulo.getImagen();

            uploadService.eliminar(nombreFotoAnterior);

            articuloService.deleteArt(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al eliminar articulo en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El articulo se ha eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/categorias")
    public Set<Categoria> listarCategorias(){
        Set<Categoria> categorias = articuloService.findAllCategorias();

        return categorias;
    }

    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){

        Resource recurso = null;

        try{
            recurso = uploadService.cargar(nombreFoto);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
    }

}
