package com.agencia.viagens.model;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class AvaliacaoRequest {

	@NotNull(message = "A nota é obrigatória.")
	@DecimalMin(value = "1.0", message = "A nota mínima é 1.0.")
	@DecimalMax(value = "5.0", message = "A nota máxima é 5.0.")
	private Double nota;

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}
}
