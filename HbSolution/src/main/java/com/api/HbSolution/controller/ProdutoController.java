package com.api.HbSolution.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.entity.ProdutoEntity;

@RestController
@RequestMapping("/produtos")
public class ProdutoController extends BaseController<ProdutoEntity> {
}
