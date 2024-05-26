package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {
    public List<Cliente> findAll();
    public Page<Cliente> findAll(Pageable pageable);

    public Cliente findByIdClient(Long id);
    public Cliente saveClient(Cliente cliente);

    public void deleteClient(Long id);

    public List<Pais> findAllPaises();
}
