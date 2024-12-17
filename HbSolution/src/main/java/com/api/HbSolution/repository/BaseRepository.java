package com.api.HbSolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepository<T extends com.api.HbSolution.entity.BaseEntity> extends JpaRepository<T, Long> {

}
