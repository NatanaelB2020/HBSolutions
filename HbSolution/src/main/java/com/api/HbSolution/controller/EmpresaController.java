package com.api.HbSolution.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController extends BaseController<EmpresaEntity, EmpresaService> {
}