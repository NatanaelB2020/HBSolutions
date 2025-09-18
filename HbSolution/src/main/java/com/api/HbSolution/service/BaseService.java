package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.repository.BaseRepository;

public abstract class BaseService<T extends BaseEntity> {

    private BaseRepository<T> repository;
    private EmpresaEntity empresaAtual;
    private UsuarioEntity usuarioAtual; // usuário logado que fez a alteração

    // Setter para injetar o repository específico no service concreto
    protected void setRepository(BaseRepository<T> repository) {
        this.repository = repository;
    }

    protected BaseRepository<T> getRepository() {
        return repository;
    }

    // Getter e setter da empresa atual
    public void setEmpresaAtual(EmpresaEntity empresa) {
        this.empresaAtual = empresa;
    }

    protected EmpresaEntity getEmpresaAtual() {
        return empresaAtual;
    }

    // Setter do usuário atual
    public void setUsuarioAtual(UsuarioEntity usuario) {
        this.usuarioAtual = usuario;
    }

    protected UsuarioEntity getUsuarioAtual() {
        return usuarioAtual;
    }

    // ---------------- CRUD Genérico ----------------

    public T save(T entity) {
        entity.setEmpresa(empresaAtual);
        // Se a entidade implementar UsuarioAuditable, seta o usuário
        if (entity instanceof com.api.HbSolution.entity.UsuarioAuditable ua && usuarioAtual != null) {
            ua.setUsuario(usuarioAtual);
        }
        return repository.save(entity);
    }

    public Optional<T> findById(Long id) {
        return repository.findByIdAndEmpresa(id, empresaAtual);
    }

    public List<T> findAll() {
        return repository.findAllByEmpresa(empresaAtual);
    }

    public void delete(Long id) {
        Optional<T> entity = repository.findByIdAndEmpresa(id, empresaAtual);
        entity.ifPresent(e -> {
            // Se a entidade implementar UsuarioAuditable, seta o usuário
            if (e instanceof com.api.HbSolution.entity.UsuarioAuditable ua && usuarioAtual != null) {
                ua.setUsuario(usuarioAtual);
            }
            repository.delete(e);
        });
    }

    public void delete(T entity) {
        entity.setEmpresa(empresaAtual);
        if (entity instanceof com.api.HbSolution.entity.UsuarioAuditable ua && usuarioAtual != null) {
            ua.setUsuario(usuarioAtual);
        }
        repository.delete(entity);
    }

    public boolean existsById(Long id) {
        return repository.findByIdAndEmpresa(id, empresaAtual).isPresent();
    }

    public long count() {
        return findAll().size();
    }
}
