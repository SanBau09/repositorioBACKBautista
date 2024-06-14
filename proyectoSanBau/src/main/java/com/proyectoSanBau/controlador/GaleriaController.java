package com.proyectoSanBau.controlador;

import com.proyectoSanBau.modelos.entidades.Categoria;
import com.proyectoSanBau.modelos.entidades.Ilustracion;
import com.proyectoSanBau.modelos.servicios.ICategoriaService;
import com.proyectoSanBau.modelos.servicios.IIlustracionService;
import com.proyectoSanBau.modelos.servicios.IUploadFileService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
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
@RequestMapping("/galeria")
public class GaleriaController {

    @Autowired
    private IIlustracionService ilustracionService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IUploadFileService uploadService;

    /**
     * Obtiene todas las ilustraciones disponibles.
     *
     * @return Conjunto de ilustraciones encontradas.
     */
    @GetMapping("/ilustraciones")
    public Set<Ilustracion> index(){
        return ilustracionService.findAll();
    }

    /**
     * Crea una nueva ilustración a partir de un archivo y datos de ilustración recibidos.
     * El método está asegurado con el rol ROLE_ADMIN.
     *
     * @param archivo     Archivo de imagen de la ilustración a subir.
     * @param ilustracion Datos de la ilustración en formato JSON.
     * @return ResponseEntity con un mensaje de éxito y la ilustración creada, o errores en caso de fallo.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/ilustraciones", consumes = {"multipart/form-data"})
    public ResponseEntity<?> create(@RequestParam("archivo") MultipartFile archivo, @RequestParam("ilustracion") String ilustracion){
        Ilustracion ilustracionNueva = null;
        Map<String, Object> response = new HashMap<>();

        try{
            if(!archivo.isEmpty()){
                String nombreArchivo = null;
                ObjectMapper mapper = new ObjectMapper();
                try{
                    Ilustracion ilu = mapper.readValue(ilustracion, Ilustracion.class);
                    List<String> errors = ilustracionService.validarIlustracion(ilu);
                    if (errors.stream().count() > 0)
                    {
                        response.put("errors", errors);
                        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                    }
                    else {
                        nombreArchivo = uploadService.copiar(archivo);
                        ilu.setImagen(nombreArchivo);
                        ilustracionNueva = ilustracionService.saveIlustracion(ilu);
                    }
                }catch (IOException e){
                    response.put("mensaje", "Error al subir la imagen " + nombreArchivo);
                    response.put("errors",e.getMessage());
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        }catch(DataAccessException e){
            response.put("mensaje", "Error al insertar nueva ilustración en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La ilustración ha sido añadida con éxito");
        response.put("ilustracion", ilustracionNueva);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Actualiza una ilustración existente identificada por su ID.
     *
     * @param ilustracion La ilustración con los nuevos datos a actualizar.
     * @param result      Resultado de la validación de la ilustración.
     * @param id          ID de la ilustración a actualizar.
     * @return ResponseEntity con un mensaje de éxito y la ilustración actualizada, o errores en caso de falla.
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/ilustraciones/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Ilustracion ilustracion, BindingResult result, @PathVariable Long id){

        Ilustracion ilustracionActual = ilustracionService.findByIdIlustracion(id);
        Ilustracion ilustracionUpdate = null;

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

        if(ilustracionActual == null){
            response.put("mensaje", "Error: no se puedo editar, la ilustración ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            ilustracionActual.setTitulo(ilustracion.getTitulo());
            ilustracionActual.setCategorias(ilustracion.getCategorias());

            ilustracionUpdate = ilustracionService.saveIlustracion(ilustracionActual);

        }catch(DataAccessException e){
            response.put("mensaje", "Error al actualizar ilustración en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La ilustración ha sido actualizada con éxito");
        response.put("ilustracion", ilustracionUpdate);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Elimina una ilustración existente identificada por su ID, incluyendo su foto asociada si existe.
     *
     * @param id ID de la ilustración a eliminar.
     * @return ResponseEntity con un mensaje de éxito tras la eliminación, o errores en caso de fallo.
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/ilustraciones/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{//si la ilustracion tiene una foto la elimina tb
            Ilustracion ilustracion = ilustracionService.findByIdIlustracion(id);
            String nombreFotoAnterior = ilustracion.getImagen();

            uploadService.eliminar(nombreFotoAnterior);

            ilustracionService.deleteIlu(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al eliminar ilustración en la base de datos");
            response.put("errors",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "La ilustración se ha eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * Sube y actualiza la imagen de una ilustración existente identificada por su ID.
     *
     * @param archivo Archivo de imagen a subir.
     * @param id      ID de la ilustración a la cual se le actualizará la imagen.
     * @return ResponseEntity con la ilustración actualizada y un mensaje de éxito, o errores en caso de fallo.
     */
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping("ilustraciones/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo")MultipartFile archivo, @RequestParam("id") Long id){
        Map<String, Object> response = new HashMap<>();

        Ilustracion ilustracion = ilustracionService.findByIdIlustracion(id);

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
            String nombreFotoAnterior = ilustracion.getImagen();

            uploadService.eliminar(nombreFotoAnterior);

            ilustracion.setImagen(nombreArchivo);

            ilustracionService.saveIlustracion(ilustracion);

            response.put("ilustracion", ilustracion);
            response.put("mensaje", "Has subido correctamente la imagen: " + nombreArchivo);

        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Retorna la foto con el nombre especificado desde el servicio de carga.
     *
     * @param nombreFoto Nombre de la foto a recuperar.
     * @return ResponseEntity con el recurso de la foto y cabeceras adecuadas, o error si no se puede cargar la foto.
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
     * Retorna un conjunto de todas las categorías disponibles.
     *
     * @return Set de objetos Categoria que representan todas las categorías disponibles.
     */
    @GetMapping("/categorias")
    public Set<Categoria> listarCategorias(){
        Set<Categoria> categorias = ilustracionService.findAllCategorias();

        return categorias;
    }

    /**
     * Crea una nueva categoría en el sistema.
     *
     * @param categoria Objeto Categoria que representa la nueva categoría a ser creada.
     * @return ResponseEntity<Map<String, Object>> ResponseEntity que contiene un mapa con el mensaje de éxito
     *         y la categoría creada en caso de éxito, o un mensaje de error en caso de fallo.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/categorias")
    public ResponseEntity<?> createCat(@RequestBody Categoria categoria){
        Map<String, Object> response = new HashMap<>();

        try {
            Categoria nuevaCategoria = categoriaService.saveCategoria(categoria);
            response.put("mensaje", "La categoría ha sido creada con éxito");
            response.put("categoria", nuevaCategoria);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar la categoría en la base de datos");
            response.put("errors", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina una categoría existente del sistema.
     *
     * @param id Identificador único de la categoría a ser eliminada.
     * @return ResponseEntity<Map<String, Object>> ResponseEntity que contiene un mapa con el mensaje de éxito
     *         en caso de éxito, o un mensaje de error en caso de fallo.
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<?> deleteCat(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            categoriaService.deleteCategoria(id);
            response.put("mensaje", "La categoría ha sido eliminada con éxito");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la categoría en la base de datos");
            response.put("errors", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
