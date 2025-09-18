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

    private EnderecoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Injetando os mocks no serviço
        service = new EnderecoService(repository, restTemplate);
    }

    @Test
    void testBuscarEnderecoPorCep() {
        String cep = "12345678";

        // Dados mockados da API
        EnderecoResponse response = new EnderecoResponse();
        response.setCep(cep);
        response.setLogradouro("Rua Teste");
        response.setBairro("Bairro Teste");
        response.setLocalidade("Cidade Teste");
        response.setUf("UF");

        // Configura mock do RestTemplate
        when(restTemplate.getForObject(anyString(), eq(EnderecoResponse.class)))
                .thenReturn(response);

        // Chama o serviço
        EnderecoEntity entity = service.buscarEnderecoPorCep(cep);

        // Verifica se os dados foram preenchidos corretamente
        assertEquals("12345678", entity.getCep());
        assertEquals("Rua Teste", entity.getLogradouro());
        assertEquals("Bairro Teste", entity.getBairro());
        assertEquals("Cidade Teste", entity.getCidade());
        assertEquals("UF", entity.getEstado());
    }

    @Test
    void testSalvarEndereco() {
        // Dados de entrada
        EnderecoRequest request = new EnderecoRequest();
        request.setCep("12345678");
        request.setLogradouro("Rua Teste");
        request.setBairro("Bairro Teste");
        request.setCidade("Cidade Teste");
        request.setEstado("UF");
        request.setNumero("100");
        request.setComplemento("Apto 101");

        // Entidade que o repository deve retornar
        EnderecoEntity entity = new EnderecoEntity();
        entity.setCep(request.getCep());
        entity.setLogradouro(request.getLogradouro());
        entity.setBairro(request.getBairro());
        entity.setCidade(request.getCidade());
        entity.setEstado(request.getEstado());
        entity.setNumero(request.getNumero());
        entity.setComplemento(request.getComplemento());

        when(repository.save(any(EnderecoEntity.class))).thenReturn(entity);

        // Chama o serviço
        EnderecoEntity result = service.salvarEndereco(request);

        // Verifica os valores retornados
        assertEquals("12345678", result.getCep());
        assertEquals("Rua Teste", result.getLogradouro());
        assertEquals("Bairro Teste", result.getBairro());
        assertEquals("Cidade Teste", result.getCidade());
        assertEquals("UF", result.getEstado());
        assertEquals("100", result.getNumero());
        assertEquals("Apto 101", result.getComplemento());

        verify(repository, times(1)).save(any(EnderecoEntity.class));
    }
}
