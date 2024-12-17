package com.api.HbSolution.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.EnderecoEntity;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController extends BaseController<EnderecoEntity> {
}
