package com.api.HbSolution.repository;

import com.api.HbSolution.entity.UsuarioEntity;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<UsuarioEntity> {
    Optional<UsuarioEntity> findByEmail(String email);
}
