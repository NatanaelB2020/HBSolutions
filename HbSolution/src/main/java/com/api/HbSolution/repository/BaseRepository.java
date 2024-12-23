package com.api.HbSolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.HbSolution.entity.BaseEntity;

public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
}
