package com.agencia.viagens.service;

import com.agencia.viagens.exception.DestinoNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.DestinoRequest;
import com.agencia.viagens.repository.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DestinoService {

	private final DestinoRepository destinoRepository;

	public DestinoService(DestinoRepository destinoRepository) {
		this.destinoRepository = destinoRepository;
	}

	public List<Destino> listarTodos() {
		return destinoRepository.findAll();
	}

	public Destino buscarPorId(Long id) {
		return destinoRepository.findById(id)
				.orElseThrow(() -> new DestinoNotFoundException("Destino com ID " + id + " não encontrado."));
	}

	public List<Destino> pesquisar(String termo) {
		return destinoRepository.pesquisar(termo);
	}

	@Transactional
	public Destino cadastrar(DestinoRequest req) {
		Destino destino = new Destino();
		destino.setNome(req.getNome());
		destino.setLocalizacao(req.getLocalizacao());
		destino.setDescricao(req.getDescricao());
		destino.setAtividades(req.getAtividades());
		return destinoRepository.save(destino);
	}

	@Transactional
	public Destino atualizar(Long id, DestinoRequest req) {
		Destino existente = buscarPorId(id);
		existente.setNome(req.getNome());
		existente.setLocalizacao(req.getLocalizacao());
		existente.setDescricao(req.getDescricao());
		existente.setAtividades(req.getAtividades());
		return destinoRepository.save(existente);
	}

	@Transactional
	public Destino avaliar(Long id, double nota) {
		Destino destino = buscarPorId(id);
		destino.registrarAvaliacao(nota);
		return destinoRepository.save(destino);
	}

	@Transactional
	public void excluir(Long id) {
		if (!destinoRepository.existsById(id)) {
			throw new DestinoNotFoundException("Destino com ID " + id + " não encontrado.");
		}
		destinoRepository.deleteById(id);
	}
}
