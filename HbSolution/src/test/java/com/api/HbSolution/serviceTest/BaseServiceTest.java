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
import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.entity.UsuarioAuditable;
import com.api.HbSolution.repository.BaseRepository;
import com.api.HbSolution.service.BaseService;

public class BaseServiceTest {

    // Entidade concreta que implementa UsuarioAuditable
    private static class TestEntity extends BaseEntity implements UsuarioAuditable {
        private UsuarioEntity usuario;

        @Override
        public void setUsuario(UsuarioEntity usuario) {
            this.usuario = usuario;
        }

        @Override
        public UsuarioEntity getUsuario() {
            return usuario;
        }
    }

    @Mock
    private BaseRepository<TestEntity> repository;

    @InjectMocks
    private BaseService<TestEntity> service = new BaseService<>() {};

    private TestEntity entity;
    private EmpresaEntity empresa;
    private UsuarioEntity usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        empresa = new EmpresaEntity();
        empresa.setId(1L);

        usuario = new UsuarioEntity();
        usuario.setId(99L);

        entity = new TestEntity();
        entity.setId(1L);
        entity.setEmpresa(empresa);

        service.setEmpresaAtual(empresa);
        service.setUsuarioAtual(usuario);

        when(repository.findByIdAndEmpresa(1L, empresa)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);
        when(repository.save(entity)).thenReturn(entity);
    }

    @Test
    void testSave() {
        TestEntity savedEntity = service.save(entity);

        assertEquals(entity, savedEntity);
        assertEquals(usuario, entity.getUsuario()); // verifica se o usuário foi setado
        verify(repository, times(1)).save(entity);
    }

    @Test
    void testFindById() {
        Optional<TestEntity> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository, times(1)).findByIdAndEmpresa(1L, empresa);
    }

    @Test
    void testDelete() {
        service.delete(1L);

        verify(repository, times(1)).findByIdAndEmpresa(1L, empresa);
        verify(repository, times(1)).delete(entity);
        assertEquals(usuario, entity.getUsuario()); // verifica se o usuário foi setado na exclusão
    }
}
