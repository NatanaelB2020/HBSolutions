package com.api.HbSolution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.EnderecoRequest;
import com.api.HbSolution.entity.EnderecoEntity;
import com.api.HbSolution.service.EnderecoService;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController extends BaseController<EnderecoEntity, EnderecoService> {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/buscar/{cep}")
    public EnderecoEntity buscarPorCep(@PathVariable String cep) {
        return enderecoService.buscarEnderecoPorCep(cep);
    }

    // Método para salvar o endereço (complemento não obrigatório)
    @PostMapping("/salvar")
    public EnderecoEntity salvarEndereco(@RequestBody EnderecoRequest enderecoRequest) {
        return enderecoService.salvarEndereco(enderecoRequest);
    }

}
