package com.api.HbSolution.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.repository.EmpresaRepository;

@Service
public class EmpresaService extends BaseService<EmpresaEntity> {
    
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
        setRepository(empresaRepository);
    }


    public List<EmpresaEntity> buscarPorNome(String nome) {
        return empresaRepository.findAllByNomeContainingAndAtivo(nome, StatusAtivo.ATIVO);
    }

    public boolean existsByCnpj(String cnpj) {
        return empresaRepository.findByCnpjAndAtivo(cnpj, StatusAtivo.ATIVO).isPresent();
    }

}
