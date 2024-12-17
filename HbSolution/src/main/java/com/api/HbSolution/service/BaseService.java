package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.repository.BaseRepository;

@Service
public class BaseService<T extends BaseEntity> {

    @Autowired
    @Qualifier("baseRepository")
    private BaseRepository<T> repository;

    public T save(T entity) {
        return repository.save(entity);
    }

    public T findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        T entity = findById(id);
        repository.delete(entity);
    }
}
