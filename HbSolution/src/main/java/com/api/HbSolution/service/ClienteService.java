package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.repository.ClienteRepository;

@Service
public class ClienteService extends BaseService<ClienteEntity> {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        super.setRepository(clienteRepository); // esse setter deve existir na BaseService
    }

    public Optional<ClienteEntity> findById(Long id) {
        return clienteRepository.findByIdAndEmpresa(id, getEmpresaAtual());
    }

    public List<ClienteEntity> findAll() {
        return clienteRepository.findAllByEmpresa(getEmpresaAtual());
    }

    public void delete(Long id) {
        clienteRepository.findByIdAndEmpresa(id, getEmpresaAtual())
                         .ifPresent(clienteRepository::delete);
    }
}

