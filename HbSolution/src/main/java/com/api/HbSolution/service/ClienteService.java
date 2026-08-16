package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.repository.ClienteRepository;
import com.api.HbSolution.security.SecurityUtils;

@Service
public class ClienteService extends BaseService<ClienteEntity> {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        super.setRepository(clienteRepository);
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<ClienteEntity> findAll() {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null)
            return List.of();
        return clienteRepository.findAllByEmpresaIdAndAtivo(usuarioLogado.getEmpresaId(), StatusAtivo.ATIVO);
    }

    @Override
    public Optional<ClienteEntity> findById(Long id) {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null)
            return Optional.empty();
        return clienteRepository.findByIdAndEmpresaIdAndAtivo(id, usuarioLogado.getEmpresaId(), StatusAtivo.ATIVO);
    }

    // Método extra para buscar clientes de uma empresa específica (opcional)
    public List<ClienteEntity> findAllByEmpresaId(Long empresaId) {
        return clienteRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }
}
