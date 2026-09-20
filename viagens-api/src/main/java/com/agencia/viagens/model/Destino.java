package com.agencia.viagens.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "destino")
public class Destino {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false, length = 150)
	private String localizacao;

	@Column(length = 1000)
	private String descricao;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "atividade", joinColumns = @JoinColumn(name = "destino_id"))
	@Column(name = "nome")
	private List<String> atividades = new ArrayList<>();

	@Column(name = "media_avaliacoes", nullable = false)
	private double mediaAvaliacoes = 0.0;

	@Column(name = "total_avaliacoes", nullable = false)
	private int totalAvaliacoes = 0;

	public Destino() {
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
		this.atividades = atividades != null ? atividades : new ArrayList<>();
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
