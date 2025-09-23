package com.api.HbSolution.service;

import java.util.List;
import java.util.Optional;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.entity.UsuarioAuditable;
import com.api.HbSolution.repository.BaseRepository;
import com.api.HbSolution.security.SecurityUtils;

public abstract class BaseService<T extends BaseEntity> {

    private BaseRepository<T> repository;

    protected void setRepository(BaseRepository<T> repository) {
        this.repository = repository;
    }

    protected BaseRepository<T> getRepository() {
        return repository;
    }

    // ---------------- CRUD ----------------

    public T save(T entity) {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();

        if (usuarioLogado != null) {
            entity.setUsuarioId(usuarioLogado.getId());
            entity.setEmpresaId(usuarioLogado.getEmpresaId());
        }

        // Se a entidade implementar UsuarioAuditable, seta o usuário
        if (entity instanceof UsuarioAuditable ua && usuarioLogado != null) {
            ua.setUsuario(usuarioLogado);
        }

        entity.setAtivo(true); // garante que esteja ativo ao salvar
        return repository.save(entity);
    }

    public Optional<T> findById(Long id) {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) return Optional.empty();
        return repository.findByIdAndEmpresaIdAndAtivoTrue(id, usuarioLogado.getEmpresaId());
    }

    public List<T> findAll() {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) return List.of();
        return repository.findAllByEmpresaIdAndAtivoTrue(usuarioLogado.getEmpresaId());
    }

    // ---------------- Exclusão lógica ----------------
    public void delete(Long id) {
        findById(id).ifPresent(entity -> {
            entity.setAtivo(false);

            // Seta o usuário que realizou a exclusão, se for UsuarioAuditable
            UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
            if (entity instanceof UsuarioAuditable ua && usuarioLogado != null) {
                ua.setUsuario(usuarioLogado);
            }

            repository.save(entity); // salva como inativo
        });
    }

    public void delete(T entity) {
        entity.setAtivo(false);

        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (entity instanceof UsuarioAuditable ua && usuarioLogado != null) {
            ua.setUsuario(usuarioLogado);
        }

        repository.save(entity);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public long count() {
        return findAll().size();
    }
}
