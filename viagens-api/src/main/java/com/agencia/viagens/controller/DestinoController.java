package com.agencia.viagens.controller;

import com.agencia.viagens.model.AvaliacaoRequest;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.DestinoRequest;
import com.agencia.viagens.service.DestinoService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/destinos")
public class DestinoController {

	private final DestinoService destinoService;

	public DestinoController(DestinoService destinoService) {
		this.destinoService = destinoService;
	}

	@GetMapping
	public ResponseEntity<List<Destino>> listar(@RequestParam(required = false) String busca) {

		List<Destino> resultado = (busca != null && !busca.isBlank()) ? destinoService.pesquisar(busca)
				: destinoService.listarTodos();

		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Destino> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(destinoService.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<Destino> cadastrar(@Valid @RequestBody DestinoRequest request) {
		Destino criado = destinoService.cadastrar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Destino> atualizar(@PathVariable Long id, @Valid @RequestBody DestinoRequest request) {

		return ResponseEntity.ok(destinoService.atualizar(id, request));
	}

	@PatchMapping("/{id}/avaliacao")
	public ResponseEntity<Destino> avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest request) {

		return ResponseEntity.ok(destinoService.avaliar(id, request.getNota()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
		destinoService.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
