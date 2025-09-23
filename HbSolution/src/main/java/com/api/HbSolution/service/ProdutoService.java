package com.api.HbSolution.service;

import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.ProdutoEntity;

@Service
public class ProdutoService extends BaseService<ProdutoEntity> {

    @Override
    public ProdutoEntity save(ProdutoEntity entity) {
    
        System.out.println("Salvando produto: " + entity);
        return super.save(entity);
    }
}
