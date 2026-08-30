package com.agencia.viagens.model;

import java.util.ArrayList;
import java.util.List;

public class Destino {

	private Long id;
	private String nome;
	private String localizacao;
	private String descricao;
	private List<String> atividades;
	private double mediaAvaliacoes;
	private int totalAvaliacoes;

	public Destino() {
		this.atividades = new ArrayList<>();
		this.mediaAvaliacoes = 0.0;
		this.totalAvaliacoes = 0;
	}

	public Destino(Long id, String nome, String localizacao, String descricao, List<String> atividades) {
		this.id = id;
		this.nome = nome;
		this.localizacao = localizacao;
		this.descricao = descricao;
		this.atividades = atividades != null ? new ArrayList<>(atividades) : new ArrayList<>();
		this.mediaAvaliacoes = 0.0;
		this.totalAvaliacoes = 0;
	}

	public void registrarAvaliacao(double nota) {
		this.totalAvaliacoes++;

		this.mediaAvaliacoes = this.mediaAvaliacoes + (nota - this.mediaAvaliacoes) / this.totalAvaliacoes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
		this.atividades = atividades != null ? new ArrayList<>(atividades) : new ArrayList<>();
	}

	public double getMediaAvaliacoes() {
		return mediaAvaliacoes;
	}

	public void setMediaAvaliacoes(double mediaAvaliacoes) {
		this.mediaAvaliacoes = mediaAvaliacoes;
	}

	public int getTotalAvaliacoes() {
		return totalAvaliacoes;
	}

	public void setTotalAvaliacoes(int totalAvaliacoes) {
		this.totalAvaliacoes = totalAvaliacoes;
	}
}
