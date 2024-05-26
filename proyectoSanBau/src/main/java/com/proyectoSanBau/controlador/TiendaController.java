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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                    response.put("error",e.getMessage());
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        }catch(DataAccessException e){
            response.put("mensaje", "Error al insertar nuevo articulo en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El articulo ha sido añadido con éxito");
        response.put("articulo", articuloNuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
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
