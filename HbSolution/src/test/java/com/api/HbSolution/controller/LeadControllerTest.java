package com.api.HbSolution.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusLead;
import com.api.HbSolution.exception.GlobalExceptionHandler;
import com.api.HbSolution.exception.ResourceNotFoundException;
import com.api.HbSolution.security.JwtAuthenticationFilter;
import com.api.HbSolution.security.JwtUtil;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.LeadService;
import com.api.HbSolution.service.UsuarioDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = LeadController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeadService leadService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void buscarLeads_retornaPaginaComLeads() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmpresaId(10L);
        usuario.setRole("ADMIN");

        LeadEntity lead = new LeadEntity();
        lead.setId(7L);
        lead.setNome("Maria Silva");
        lead.setEmail("maria@teste.com");
        lead.setTelefone("11999999999");
        lead.setOrigem(OrigemLead.INDICACAO);
        lead.setStatus(StatusLead.QUALIFICADO);
        lead.setScore(90);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);
            when(leadService.buscarLeadsFiltrados(eq(10L), eq("Maria"), eq(null), eq(null), eq(null), eq(null), any()))
                    .thenReturn(new PageImpl<>(List.of(lead), PageRequest.of(0, 20), 1));

            mockMvc.perform(get("/api/leads/busca")
                    .param("nome", "Maria")
                    .param("page", "0")
                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("Maria Silva"))
                    .andExpect(jsonPath("$.content[0].status").value("QUALIFICADO"));
        }
    }

    @Test
    void atualizarScore_quandoLeadExiste_retornaLeadAtualizado() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmpresaId(10L);
        usuario.setRole("ADMIN");

        LeadEntity lead = new LeadEntity();
        lead.setId(5L);
        lead.setNome("Pedro");
        lead.setEmail("pedro@teste.com");
        lead.setTelefone("11999999998");
        lead.setOrigem(OrigemLead.SITE);
        lead.setStatus(StatusLead.NOVO);
        lead.setScore(30);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);
            when(leadService.buscarPorIdValidandoEmpresa(5L)).thenReturn(lead);
            when(leadService.calcularScoreAutomatico(lead)).thenReturn(91);
            when(leadService.save(lead)).thenReturn(lead);

            mockMvc.perform(post("/api/leads/5/atualizar-score")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.score").value(91));
        }
    }

    @Test
    void atualizarScore_quandoLeadNaoExiste_retornaApiError404() throws Exception {
        when(leadService.buscarPorIdValidandoEmpresa(99L))
                .thenThrow(new ResourceNotFoundException("Lead não encontrado"));

        mockMvc.perform(post("/api/leads/99/atualizar-score")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Lead não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/leads/99/atualizar-score"));
    }
}
