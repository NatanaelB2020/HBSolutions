package com.api.HbSolution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.HbSolution.DTO.EnderecoRequest;
import com.api.HbSolution.DTO.EnderecoResponse;
import com.api.HbSolution.entity.EnderecoEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.repository.EnderecoRepository;
import com.api.HbSolution.security.SecurityUtils;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService extends BaseService<EnderecoEntity> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public EnderecoEntity buscarEnderecoPorCep(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        EnderecoResponse enderecoResponse = restTemplate.getForObject(url, EnderecoResponse.class);

        if (enderecoResponse != null && !enderecoResponse.isErro()) {
            EnderecoEntity enderecoEntity = new EnderecoEntity();
            enderecoEntity.setLogradouro(enderecoResponse.getLogradouro());
            enderecoEntity.setBairro(enderecoResponse.getBairro());
            enderecoEntity.setCidade(enderecoResponse.getLocalidade());
            enderecoEntity.setEstado(enderecoResponse.getUf());
            enderecoEntity.setCep(enderecoResponse.getCep());
            return enderecoEntity;
        } else {
            throw new RuntimeException("CEP inválido ou não encontrado");
        }
    }

    @Transactional
    public EnderecoEntity salvarEndereco(EnderecoRequest enderecoRequest) {
        EnderecoEntity endereco = new EnderecoEntity();
        endereco.setLogradouro(enderecoRequest.getLogradouro());
        endereco.setBairro(enderecoRequest.getBairro());
        endereco.setCidade(enderecoRequest.getCidade());
        endereco.setEstado(enderecoRequest.getEstado());
        endereco.setCep(enderecoRequest.getCep());
        endereco.setNumero(enderecoRequest.getNumero());
        endereco.setComplemento(enderecoRequest.getComplemento());

        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado != null) {
            endereco.setUsuarioId(usuarioLogado.getId());
            endereco.setEmpresaId(usuarioLogado.getEmpresaId());
        }

        endereco.setAtivo(true); // sempre ativo ao criar

        return enderecoRepository.save(endereco);
    }

    @Override
    public List<EnderecoEntity> findAll() {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) return List.of();
        return enderecoRepository.findAllByEmpresaIdAndAtivoTrue(usuarioLogado.getEmpresaId());
    }

    @Override
    public Optional<EnderecoEntity> findById(Long id) {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) return Optional.empty();
        return enderecoRepository.findByIdAndEmpresaIdAndAtivoTrue(id, usuarioLogado.getEmpresaId());
    }
}
