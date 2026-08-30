package com.agencia.viagens.service;

import com.agencia.viagens.exception.DestinoNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.DestinoRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class DestinoService {

	private final Map<Long, Destino> repositorio = new ConcurrentHashMap<>();
	private final AtomicLong contadorId = new AtomicLong(1);

	public DestinoService() {
		Destino paris = new Destino(contadorId.getAndIncrement(), "Paris", "Paris, França",
				"A Cidade Luz encanta com sua arquitetura, gastronomia e arte.",
				Arrays.asList("Torre Eiffel", "Museu do Louvre", "Champs-Élysées"));
		paris.registrarAvaliacao(5.0);
		paris.registrarAvaliacao(4.5);
		repositorio.put(paris.getId(), paris);

		Destino rio = new Destino(contadorId.getAndIncrement(), "Rio de Janeiro", "Rio de Janeiro, Brasil",
				"Praias deslumbrantes, samba e o famoso Cristo Redentor.",
				Arrays.asList("Cristo Redentor", "Pão de Açúcar", "Praia de Copacabana"));
		rio.registrarAvaliacao(4.8);
		repositorio.put(rio.getId(), rio);

		Destino kyoto = new Destino(contadorId.getAndIncrement(), "Kyoto", "Kyoto, Japão",
				"A cidade dos templos, geishas e jardins zen.",
				Arrays.asList("Templo Kinkaku-ji", "Arashiyama", "Fushimi Inari"));
		kyoto.registrarAvaliacao(4.9);
		repositorio.put(kyoto.getId(), kyoto);
	}

	public List<Destino> listarTodos() {
		return new ArrayList<>(repositorio.values());
	}

	public Destino buscarPorId(Long id) {
		Destino destino = repositorio.get(id);
		if (destino == null) {
			throw new DestinoNotFoundException("Destino com ID " + id + " não encontrado.");
		}
		return destino;
	}

	public List<Destino> pesquisar(String termo) {
		String termoBusca = termo.toLowerCase();
		return repositorio.values().stream().filter(d -> d.getNome().toLowerCase().contains(termoBusca)
				|| d.getLocalizacao().toLowerCase().contains(termoBusca)).collect(Collectors.toList());
	}

	public Destino cadastrar(DestinoRequest req) {
		Long novoId = contadorId.getAndIncrement();
		Destino destino = new Destino(novoId, req.getNome(), req.getLocalizacao(), req.getDescricao(),
				req.getAtividades());
		repositorio.put(novoId, destino);
		return destino;
	}

	public Destino atualizar(Long id, DestinoRequest req) {
		Destino existente = buscarPorId(id);
		existente.setNome(req.getNome());
		existente.setLocalizacao(req.getLocalizacao());
		existente.setDescricao(req.getDescricao());
		existente.setAtividades(req.getAtividades());
		return existente;
	}

	public Destino avaliar(Long id, double nota) {
		Destino destino = buscarPorId(id);
		destino.registrarAvaliacao(nota);
		return destino;
	}

	public void excluir(Long id) {
		if (!repositorio.containsKey(id)) {
			throw new DestinoNotFoundException("Destino com ID " + id + " não encontrado.");
		}
		repositorio.remove(id);
	}
}
