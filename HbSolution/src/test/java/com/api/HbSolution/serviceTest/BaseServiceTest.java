package com.api.HbSolution.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.entity.EmpresaEntity;

import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.entity.UsuarioAuditable;
import com.api.HbSolution.repository.BaseRepository;
import com.api.HbSolution.service.BaseService;
import com.api.HbSolution.security.SecurityUtils;

public class BaseServiceTest {

    // Entidade concreta para testes
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
        usuario.setEmpresaId(empresa.getId());

        entity = new TestEntity();
        entity.setId(1L);
        entity.setEmpresaId(empresa.getId());
        entity.setAtivo(StatusAtivo.ATIVO); // ✅ inicial ativo

        // Ajuste: usa métodos com enum StatusAtivo
        when(repository.findByIdAndEmpresaIdAndAtivo(1L, empresa.getId(), StatusAtivo.ATIVO))
                .thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
    }

    @Test
    void testSave() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            TestEntity savedEntity = service.save(entity);

            assertEquals(entity, savedEntity);
            assertEquals(usuario, entity.getUsuario());
            assertEquals(usuario.getEmpresaId(), entity.getEmpresaId());
            assertEquals(StatusAtivo.ATIVO, entity.getAtivo()); // ✅ checagem com enum
            verify(repository, times(1)).save(entity);
        }
    }

    @Test
    void testFindById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            Optional<TestEntity> result = service.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(entity, result.get());
            verify(repository, times(1))
                    .findByIdAndEmpresaIdAndAtivo(1L, usuario.getEmpresaId(), StatusAtivo.ATIVO);
        }
    }

    @Test
    void testDelete() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            service.delete(1L);

            // Verifica que a busca ocorreu
            verify(repository, times(1))
                    .findByIdAndEmpresaIdAndAtivo(1L, usuario.getEmpresaId(), StatusAtivo.ATIVO);

            // Verifica que o save foi chamado (exclusão lógica)
            verify(repository, times(1)).save(entity);

            // A entidade deve ter o usuário setado
            assertEquals(usuario, entity.getUsuario());

            // A entidade deve estar inativa (ativo = INATIVO)
            assertEquals(StatusAtivo.INATIVO, entity.getAtivo());
        }
    }
}
