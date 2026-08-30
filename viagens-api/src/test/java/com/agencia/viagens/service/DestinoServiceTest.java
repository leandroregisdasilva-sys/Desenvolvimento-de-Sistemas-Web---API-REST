package com.agencia.viagens.service;

import com.agencia.viagens.exception.DestinoNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.DestinoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DestinoServiceTest {

    private DestinoService service;

    @BeforeEach
    void setUp() {
        service = new DestinoService();
    }

    @Test
    void deveListarTodosOsDestinos() {
        List<Destino> destinos = service.listarTodos();
        assertFalse(destinos.isEmpty());
    }

    @Test
    void deveCadastrarNovoDestino() {
        DestinoRequest req = new DestinoRequest();
        req.setNome("Lisboa");
        req.setLocalizacao("Lisboa, Portugal");
        req.setDescricao("Capital histórica de Portugal.");
        req.setAtividades(Arrays.asList("Torre de Belém", "Alfama"));

        Destino criado = service.cadastrar(req);

        assertNotNull(criado.getId());
        assertEquals("Lisboa", criado.getNome());
    }

    @Test
    void deveBuscarDestinoPorId() {
        assertNotNull(service.buscarPorId(1L));
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExiste() {
        assertThrows(DestinoNotFoundException.class, () -> service.buscarPorId(999L));
    }

    @Test
    void devePesquisarPorNome() {
        List<Destino> resultado = service.pesquisar("Paris");
        assertFalse(resultado.isEmpty());
    }

    @Test
    void devePesquisarPorLocalizacao() {
        List<Destino> resultado = service.pesquisar("brasil");
        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveRecalcularMediaCorretamente() {
        Destino antes = service.buscarPorId(1L);
        int totalAnterior = antes.getTotalAvaliacoes();

        service.avaliar(1L, 3.0);

        Destino depois = service.buscarPorId(1L);
        assertEquals(totalAnterior + 1, depois.getTotalAvaliacoes());
    }

    @Test
    void deveExcluirDestino() {
        DestinoRequest req = new DestinoRequest();
        req.setNome("Teste");
        req.setLocalizacao("Teste, País");
        Destino criado = service.cadastrar(req);

        service.excluir(criado.getId());

        assertThrows(DestinoNotFoundException.class, () -> service.buscarPorId(criado.getId()));
    }
}
