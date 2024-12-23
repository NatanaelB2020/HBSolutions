package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.repository.BaseRepository;

public abstract class BaseService<T extends BaseEntity> {

    @Autowired
    private BaseRepository<T> repository;

    public T save(T entity) {
        return repository.save(entity);
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}