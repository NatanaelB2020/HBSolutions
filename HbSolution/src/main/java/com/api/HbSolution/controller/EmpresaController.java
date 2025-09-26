package com.api.HbSolution.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController extends BaseController<EmpresaEntity, EmpresaService> {

    private EmpresaService service;

    @GetMapping("/buscar")
    public List<EmpresaEntity> buscarPorNome(@RequestParam String nomeFantasia) {
        return service.buscarPorNome(nomeFantasia);
    }

}