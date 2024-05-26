package com.proyectoSanBau.modelos.servicios;

import com.proyectoSanBau.modelos.dao.IClienteDao;
import com.proyectoSanBau.modelos.entidades.Cliente;
import com.proyectoSanBau.modelos.entidades.Pais;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImp implements IClienteService{
    @Autowired
    private IClienteDao clienteDao;
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return (List<Cliente>) clienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findByIdClient(Long id) {
        return clienteDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Cliente saveClient(Cliente cliente) {
        return clienteDao.save(cliente);
    }
    @Transactional
    @Override
    public void deleteClient(Long id) {
    clienteDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pais> findAllPaises() {
        return clienteDao.findAllPaises();
    }
}
