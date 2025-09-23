/*package com.api.HbSolution.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.repository.UsuarioRepository;
import com.api.HbSolution.service.UsuarioService;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveWithPasswordEncoding() {
        UsuarioEntity user = new UsuarioEntity();
        user.setSenha("12345");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(user)).thenReturn(user);

        UsuarioEntity result = service.save(user);

        assertEquals("encodedPassword", result.getSenha());
        verify(passwordEncoder, times(1)).encode("12345");
        verify(repository, times(1)).save(user);
    }

    @Test
    void testAuthenticate() {
        String email = "test@example.com";
        String senha = "12345";
        UsuarioEntity user = new UsuarioEntity();
        user.setSenha("encodedPassword");
        when(repository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(senha, "encodedPassword")).thenReturn(true);

        UsuarioEntity result = service.authenticate(email, senha);

        assertEquals(user, result);
        verify(repository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(senha, "encodedPassword");
    }
}*/
