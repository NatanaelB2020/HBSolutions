package com.api.HbSolution.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.service.BaseService;

public abstract class BaseController<T extends BaseEntity> {

    @Autowired
    private BaseService<T> service;

    @PostMapping
    public T create(@RequestBody T entity) {
        return service.save(entity);
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<T> getAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}