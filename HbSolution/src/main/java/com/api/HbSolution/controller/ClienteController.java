package com.api.HbSolution.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.ClienteEntity;

@RestController
@RequestMapping("/clientes")
public class ClienteController extends BaseController<ClienteEntity> {
}
