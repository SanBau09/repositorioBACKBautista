package com.proyectoSanBau.controlador;

import com.proyectoSanBau.modelos.entidades.Pais;
import com.proyectoSanBau.modelos.servicios.IClienteService;
import com.proyectoSanBau.modelos.servicios.IUploadFileService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.proyectoSanBau.modelos.entidades.Cliente;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IUploadFileService uploadService;

    private final Logger log = LoggerFactory.getLogger(ClienteRestController.class);
    /**
     * Método para obtener una lista de todos los clientes.
     *
     * @return List<Cliente> Una lista que contiene todos los clientes disponibles en la base de datos.
     */
    @GetMapping("/clientes")
    public List<Cliente> index(){
    return clienteService.findAll();
    }

    /**
     * Método para obtener una página de clientes, donde cada página contiene un número específico de clientes.
     *
     * @param numPag El número de página que se desea recuperar.
     * @return Page<Cliente> Una página de clientes, donde cada página contiene un número específico de clientes.
     */
    @GetMapping("/clientes/page/{numPag}")
    public Page<Cliente> index(@PathVariable Integer numPag){
        Pageable pageable = PageRequest.of(numPag, 4); //mostrará 4 clientes por página

        return clienteService.findAll(pageable);
    }

    /**
     * Método para obtener los detalles de un cliente por su ID, accesible tanto para ROLE_ADMIN como para ROLE_USER.
     *
     * @param id El ID del cliente del que se desean obtener los detalles.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el objeto Cliente si se encuentra,
     *         o un mensaje de error si el cliente no existe en la base de datos.
     */
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){

        Cliente cliente = null;
        Map<String, Object> response = new HashMap<>();

        try{
            cliente = clienteService.findByIdClient(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al lanzar la consulta en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(cliente == null){
            response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }

    /**
     * Método para crear un nuevo cliente, con permisos requeridos de ROLE_ADMIN.
     *
     * @param cliente El objeto Cliente con los datos del nuevo cliente a crear.
     * @param result El objeto BindingResult que contiene los resultados de la validación.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el cliente creado si la creación fue exitosa,
     *         o una lista de errores si la validación falla, o un mensaje de error en caso contrario.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/clientes")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){
        Cliente clienteNuevo = null;
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

        try{
            cliente.setFecha(new Date());
            clienteNuevo = clienteService.saveClient(cliente);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al insertar nuevo cliente en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido creado con éxito");
        response.put("cliente", clienteNuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Método para actualizar un cliente por su ID, con permisos requeridos de ROLE_ADMIN.
     *
     * @param cliente El objeto Cliente con los datos actualizados del cliente.
     * @param result El objeto BindingResult que contiene los resultados de la validación.
     * @param id El ID del cliente a actualizar.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el cliente actualizado si la actualización fue exitosa,
     *         o una lista de errores si la validación falla, o un mensaje de error en caso contrario.
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente,BindingResult result, @PathVariable Long id){

        Cliente clienteActual = clienteService.findByIdClient(id);
        Cliente clienteUpdate = null;

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

        if(clienteActual == null){
            response.put("mensaje", "Error: no se puedo editar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            clienteActual.setNombre(cliente.getNombre());
            clienteActual.setApellidos(cliente.getApellidos());
            clienteActual.setEmail(cliente.getEmail());
            clienteActual.setPais(cliente.getPais());

            clienteUpdate = clienteService.saveClient(clienteActual);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al actualizar cliente en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido actualizado con éxito");
        response.put("cliente", clienteUpdate);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Método para eliminar un cliente por su ID, con permisos requeridos de ROLE_ADMIN.
     *
     * @param id El ID del cliente a eliminar.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación.
     *         Devuelve un mensaje de éxito si el cliente se elimina correctamente, o un mensaje de error si ocurre un problema.
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{//si el cliente tiene una foto la elimina tb
            Cliente cliente = clienteService.findByIdClient(id);
            String nombreFotoAnterior = cliente.getFoto();

            uploadService.eliminar(nombreFotoAnterior);

            clienteService.deleteClient(id);
        }catch(DataAccessException e){
            response.put("mensaje", "Error al eliminar cliente en la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El cliente ha sido eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * Método para subir un archivo (imagen) asociado a un cliente.
     *
     * @param archivo El archivo (imagen) a subir.
     * @param id El ID del cliente al que se asociará la imagen.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación y el cliente actualizado si la subida fue exitosa,
     *         o un mensaje de error en caso contrario.
     */
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping("clientes/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo")MultipartFile archivo, @RequestParam("id") Long id){
        Map<String, Object> response = new HashMap<>();

        Cliente cliente = clienteService.findByIdClient(id);

        if(!archivo.isEmpty()){
            String nombreArchivo = null;

            try{
                nombreArchivo = uploadService.copiar(archivo);
            }catch (IOException e){
                response.put("mensaje", "Error al subir la imagen " + nombreArchivo);
                response.put("error",e.getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            //para reemlazar la foto anterior por la nueva
            String nombreFotoAnterior = cliente.getFoto();

            uploadService.eliminar(nombreFotoAnterior);

            cliente.setFoto(nombreArchivo);

            clienteService.saveClient(cliente);

            response.put("cliente", cliente);
            response.put("mensaje", "Has subido correctamente la imagen: " + nombreArchivo);

        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /**
     * Recupera una foto del directorio de subidas y la devuelve como recurso.
     *
     * @param nombreFoto El nombre de la foto a recuperar.
     * @return ResponseEntity con el recurso de la foto y las cabeceras HTTP necesarias para la descarga.
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

    @Secured("ROLE_ADMIN")
    @GetMapping("/clientes/paises")
    public List<Pais> listarPaises(){
        return clienteService.findAllPaises();
    }
}
