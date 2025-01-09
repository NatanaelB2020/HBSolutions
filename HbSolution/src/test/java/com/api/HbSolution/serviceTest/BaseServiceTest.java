package com.api.HbSolution.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.repository.BaseRepository;
import com.api.HbSolution.service.BaseService;

public class BaseServiceTest {

    @Mock
    private BaseRepository<BaseEntity> repository;

    @InjectMocks
    private BaseService<BaseEntity> service = new BaseService<>() {
    };

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        BaseEntity entity = mock(BaseEntity.class); // Criando o mock de BaseEntity
        when(repository.save(entity)).thenReturn(entity);

        BaseEntity savedEntity = service.save(entity);

        assertEquals(entity, savedEntity);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        BaseEntity entity = mock(BaseEntity.class); // Criando o mock de BaseEntity
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<BaseEntity> result = service.findById(id);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }
}
