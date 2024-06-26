package com.proyectoSanBau.controlador;

import com.proyectoSanBau.modelos.entidades.*;
import com.proyectoSanBau.modelos.servicios.*;
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
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    private IArticuloService articuloService;
    @Autowired
    private IUploadFileService uploadService;

    @Autowired
    private IFormatoService formatoService;

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IUsuarioService usuarioService;


    /**
     * Obtiene una lista de todos los artículos.
     *
     * @return lista de artículos
     */
    @GetMapping("/articulos")
    public List<Articulo> index(){
        return articuloService.findAll();
    }

    /**
     * Crea un nuevo artículo con una imagen.
     *
     * @param archivo archivo de la imagen del artículo
     * @param articulo JSON del artículo
     * @return respuesta de la operación
     */
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
                    art.setEsActivo(true);
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

    /**
     * Actualiza un artículo existente.
     *
     * @param articulo artículo con los nuevos datos
     * @param result resultado de la validación
     * @param id identificador del artículo a actualizar
     * @return respuesta de la operación
     */
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

    /**
     * Elimina un artículo.
     *
     * @param id identificador del artículo a eliminar
     * @return respuesta de la operación
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/articulos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{//si el articulo tiene una foto la elimina tb
            Articulo articulo = articuloService.findByIdArticulo(id);

            articuloService.deleteArt(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al eliminar articulo en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El articulo se ha eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * Obtiene una lista de todas las categorías.
     *
     * @return lista de categorías
     */
    @GetMapping("/categorias")
    public Set<Categoria> listarCategorias(){
        Set<Categoria> categorias = articuloService.findAllCategorias();

        return categorias;
    }

    /**
     * Sube una imagen para un artículo.
     *
     * @param archivo archivo de la imagen
     * @param id identificador del artículo
     * @return respuesta de la operación
     */
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping("articulos/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo")MultipartFile archivo, @RequestParam("id") Long id){
        Map<String, Object> response = new HashMap<>();

        Articulo articulo = articuloService.findByIdArticulo(id);

        if(!archivo.isEmpty()){
            String nombreArchivo = null;

            try{
                nombreArchivo = uploadService.copiar(archivo);
            }catch (IOException e){
                response.put("mensaje", "Error al subir la imagen " + nombreArchivo);
                response.put("errors",e.getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            //para reemlazar la foto anterior por la nueva
            String nombreFotoAnterior = articulo.getImagen();

            uploadService.eliminar(nombreFotoAnterior);

            articulo.setImagen(nombreArchivo);

            articuloService.saveArticulo(articulo);

            response.put("articulo", articulo);
            response.put("mensaje", "Has subido correctamente la imagen: " + nombreArchivo);

        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene una imagen subida.
     *
     * @param nombreFoto nombre del archivo de la imagen
     * @return imagen como recurso
     */
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

    /**
     * Crea un nuevo formato.
     *
     * @param formato formato a crear
     * @return respuesta de la operación
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/formatos")
    public ResponseEntity<?> createFormato(@RequestBody Formato formato){
        Map<String, Object> response = new HashMap<>();

        try {
            Formato nuevoFormato = formatoService.saveFormato(formato);
            response.put("mensaje", "El formato ha sido creado con éxito");
            response.put("formato", nuevoFormato);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar el formato en la base de datos");
            response.put("errors", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una lista de todos los formatos.
     *
     * @return lista de formatos
     */
    @GetMapping("/formatos")
    public List<Formato> listarFormatos(){
        List<Formato> formatos = formatoService.findAll();

        return formatos;
    }

    /**
     * Elimina un formato.
     *
     * @param id identificador del formato a eliminar
     * @return respuesta de la operación
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/formatos/{id}")
    public ResponseEntity<?> deleteFormato(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{
            formatoService.deleteFormato(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al eliminar formato en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El formato se ha eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * Realiza una nueva venta.
     *
     * @param venta venta a realizar
     * @return respuesta de la operación
     */
    @Secured("ROLE_USER")
    @PostMapping("/venta")
    public ResponseEntity<?> createVenta(@RequestBody Venta venta){
        Map<String, Object> response = new HashMap<>();

        try {
            venta.setFechaVenta(new Date());

            Venta nuevaVenta = ventaService.saveVenta(venta);
            response.put("mensaje", "Venta ha sido realizada con éxito");
            response.put("venta", nuevaVenta);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar la venta en la base de datos");
            response.put("errors", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una lista de todas las ventas.
     *
     * @return lista de ventas
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/ventas")
    public List<Venta> listarVentas(){
        List<Venta> ventas = ventaService.findAll();

        return ventas;
    }

}
