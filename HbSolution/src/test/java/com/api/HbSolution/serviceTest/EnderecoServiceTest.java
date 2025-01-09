package com.api.HbSolution.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.api.HbSolution.DTO.EnderecoRequest;
import com.api.HbSolution.DTO.EnderecoResponse;
import com.api.HbSolution.entity.EnderecoEntity;
import com.api.HbSolution.repository.EnderecoRepository;
import com.api.HbSolution.service.EnderecoService;

public class EnderecoServiceTest {

    @Mock
    private EnderecoRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnderecoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuscarEnderecoPorCep() {
        String cep = "12345678";
        EnderecoResponse response = new EnderecoResponse();
        response.setCep(cep);
        response.setLogradouro("Rua Teste");
        response.setBairro("Bairro Teste");
        response.setLocalidade("Cidade Teste");
        response.setUf("UF");
        when(restTemplate.getForObject(anyString(), eq(EnderecoResponse.class))).thenReturn(response);

        EnderecoEntity result = service.buscarEnderecoPorCep(cep);

        assertEquals(cep, result.getCep());
        assertEquals("Rua Teste", result.getLogradouro());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(EnderecoResponse.class));
    }

    @Test
    void testSalvarEndereco() {
        EnderecoRequest request = new EnderecoRequest();
        request.setCep("12345678");
        request.setLogradouro("Rua Teste");
        request.setBairro("Bairro Teste");

        EnderecoEntity entity = new EnderecoEntity();
        entity.setCep(request.getCep());
        entity.setLogradouro(request.getLogradouro());
        entity.setBairro(request.getBairro());
        when(repository.save(any(EnderecoEntity.class))).thenReturn(entity);

        EnderecoEntity result = service.salvarEndereco(request);

        assertEquals("12345678", result.getCep());
        assertEquals("Rua Teste", result.getLogradouro());
        verify(repository, times(1)).save(any(EnderecoEntity.class));
    }
}
