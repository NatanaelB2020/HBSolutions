package com.api.HbSolution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.api.HbSolution.DTO.EnderecoRequest;
import com.api.HbSolution.DTO.EnderecoResponse;
import com.api.HbSolution.entity.EnderecoEntity;
import com.api.HbSolution.repository.EnderecoRepository;

import jakarta.transaction.Transactional;

@Service
public class EnderecoService extends BaseService<EnderecoEntity> {

    private final RestTemplate restTemplate;
    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, RestTemplate restTemplate) {
        this.enderecoRepository = enderecoRepository;
        this.restTemplate = restTemplate;
        setRepository(enderecoRepository);
    }

    public EnderecoEntity buscarEnderecoPorCep(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar o endereço: " + e.getMessage(), e);
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
        return enderecoRepository.save(endereco);
    }
}
