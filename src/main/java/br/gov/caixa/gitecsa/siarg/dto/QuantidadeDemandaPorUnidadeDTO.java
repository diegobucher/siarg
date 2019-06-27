package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

public class QuantidadeDemandaPorUnidadeDTO implements Serializable{

	public String getSituacao() {
		return situacao;
	}

	private static final long serialVersionUID = -8670911649971440320L;
	
	private String siglaCaixaPostal;
	
	private Long quantidadeDemandas;
	
	private String situacao;
	
	private Long numeroDemanda;
	
	

	public QuantidadeDemandaPorUnidadeDTO() {
		super();
		// TODO Auto-generated constructor stub
	}






	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public QuantidadeDemandaPorUnidadeDTO(String siglaCaixaPostal, Long quantidadeDemandas, String situacao) {
		super();
		this.siglaCaixaPostal = siglaCaixaPostal;
		this.quantidadeDemandas = quantidadeDemandas;
		this.situacao = situacao;
	}

	public String getSiglaCaixaPostal() {
		return siglaCaixaPostal;
	}

	public void setSiglaCaixaPostal(String siglaCaixaPostal) {
		this.siglaCaixaPostal = siglaCaixaPostal;
	}

	public Long getQuantidadeDemandas() {
		return quantidadeDemandas;
	}

	public void setQuantidadeDemandas(Long quantidadeDemandas) {
		this.quantidadeDemandas = quantidadeDemandas;
	}






	public Long getNumeroDemanda() {
		return numeroDemanda;
	}






	public void setNumeroDemanda(Long numeroDemanda) {
		this.numeroDemanda = numeroDemanda;
	}

	

}
