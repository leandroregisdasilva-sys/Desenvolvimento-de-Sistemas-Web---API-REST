package com.agencia.viagens.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class DestinoRequest {

	@NotBlank(message = "O nome do destino é obrigatório.")
	@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
	private String nome;

	@NotBlank(message = "A localização é obrigatória.")
	@Size(max = 150, message = "A localização deve ter no máximo 150 caracteres.")
	private String localizacao;

	@Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
	private String descricao;

	private List<String> atividades;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<String> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<String> atividades) {
		this.atividades = atividades;
	}
}
