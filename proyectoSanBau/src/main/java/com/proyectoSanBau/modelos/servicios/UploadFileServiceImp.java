package com.proyectoSanBau.modelos.servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadFileServiceImp implements IUploadFileService {

    private final Logger log = LoggerFactory.getLogger(UploadFileServiceImp.class);
    private final static String DIRECTORIO_UPLOAD = "src/main/resources/static/img";

    /**
     * Carga un recurso (archivo de imagen) basado en el nombre proporcionado.
     *
     * @param nombreFoto El nombre del archivo de imagen a cargar.
     * @return Un objeto Resource que representa el archivo de imagen cargado.
     * @throws MalformedURLException Si ocurre un error al construir la URL del recurso.
     */
    @Override
    public Resource cargar(String nombreFoto) throws MalformedURLException {
        Path rutaArchivo = getPath(nombreFoto);
        log.info(rutaArchivo.toString());

        Resource recurso = new UrlResource(rutaArchivo.toUri());

        if(!recurso.exists() && !recurso.isReadable()){
            rutaArchivo = Paths.get("src/main/resources/static/img").resolve("notUser.png").toAbsolutePath();

            recurso = new UrlResource(rutaArchivo.toUri());

            log.error("Error no se pudo cargar la imagen: " + nombreFoto);
        }
        return recurso;
    }

    /**
     * Copia un archivo proporcionado como MultipartFile a una ubicación determinada en el sistema de archivos.
     *
     * @param archivo El archivo MultipartFile que se va a copiar.
     * @return El nombre único del archivo copiado, que incluye un UUID y el nombre original del archivo sin espacios.
     * @throws IOException Si ocurre un error durante la operación de copiado del archivo.
     */
    @Override
    public String copiar(MultipartFile archivo) throws IOException {

        //genera un nombre único y reemplaza los espacios en blanco
        String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
        Path rutaArchivo = getPath(nombreArchivo);

        log.info(rutaArchivo.toString());

        Files.copy(archivo.getInputStream(), rutaArchivo);


        return nombreArchivo;
    }

    /**
     * Elimina un archivo de imagen basado en su nombre proporcionado.
     *
     * @param nombreFoto El nombre del archivo de imagen que se desea eliminar.
     * @return true si el archivo se eliminó correctamente, false si no se pudo eliminar o el nombre de archivo es nulo o vacío.
     */
    @Override
    public boolean eliminar(String nombreFoto) {

        if(nombreFoto !=null && nombreFoto.length() > 0){
            Path rutaFotoAnterior = Paths.get("src/main/resources/static/img").resolve(nombreFoto).toAbsolutePath();
            File archivoFotoAnterior = rutaFotoAnterior.toFile();

            if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
                archivoFotoAnterior.delete();

                return true;
            }
        }
        return false;
    }
    /**
     * Retorna la ruta absoluta de un archivo de imagen basado en el nombre proporcionado.
     *
     * @param nombreFoto El nombre del archivo de imagen para el cual se desea obtener la ruta.
     * @return La ruta absoluta del archivo de imagen.
     */
    @Override
    public Path getPath(String nombreFoto) {
        return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
    }
}
