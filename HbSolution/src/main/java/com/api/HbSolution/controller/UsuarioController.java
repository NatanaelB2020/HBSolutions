package com.api.HbSolution.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController extends BaseController<UsuarioEntity, UsuarioService> {

}
