/**
 * RelacaoAssuntosDTO.java
 * Versão: 1.0.0.00
 * 19/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe DTO para confecção do relatório de Demandas Aguardando Atuação da Unidade.
 */
public class DemandasAguardandoUnidadeDTO implements Serializable, Comparable<DemandasAguardandoUnidadeDTO> {

  /** Serial. */
  private static final long serialVersionUID = -302868597993686427L;

  private Long numeroSiarg;

  private Date dataAbertura;

  private String unidadeDemandante;

  private String matriculaDemandante;
  
  private String nomeDemandante;
  
  private String titulo;
  
  private String assunto;
  
  private String responsavelAtual;
  
  private Integer prazoAtual;
  
  private Date ultimoEncaminhamento;

  private Integer diasUltimoEncaminhamento;
  
  private Integer diasComResponsavel;
  
  @Override
  public int compareTo(DemandasAguardandoUnidadeDTO o) {
    return numeroSiarg.compareTo(o.getNumeroSiarg());
  }

	public Long getNumeroSiarg() {
		return numeroSiarg;
	}
	
	public void setNumeroSiarg(Long numeroSiarg) {
		this.numeroSiarg = numeroSiarg;
	}
	
	public Date getDataAbertura() {
		return dataAbertura;
	}
	
	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	
	public String getUnidadeDemandante() {
		return unidadeDemandante;
	}
	
	public void setUnidadeDemandante(String unidadeDemandante) {
		this.unidadeDemandante = unidadeDemandante;
	}
	
	public String getMatriculaDemandante() {
		return matriculaDemandante;
	}
	
	public void setMatriculaDemandante(String matriculaDemandante) {
		this.matriculaDemandante = matriculaDemandante;
	}
	
	public String getNomeDemandante() {
		return nomeDemandante;
	}
	
	public void setNomeDemandante(String nomeDemandante) {
		this.nomeDemandante = nomeDemandante;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getAssunto() {
		return assunto;
	}
	
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	
	public String getResponsavelAtual() {
		return responsavelAtual;
	}
	
	public void setResponsavelAtual(String responsavelAtual) {
		this.responsavelAtual = responsavelAtual;
	}
	
	public Integer getPrazoAtual() {
		return prazoAtual;
	}
	
	public void setPrazoAtual(Integer prazoAtual) {
		this.prazoAtual = prazoAtual;
	}
	
	public Date getUltimoEncaminhamento() {
		return ultimoEncaminhamento;
	}
	
	public void setUltimoEncaminhamento(Date ultimoEncaminhamento) {
		this.ultimoEncaminhamento = ultimoEncaminhamento;
	}
	
	public Integer getDiasUltimoEncaminhamento() {
		return diasUltimoEncaminhamento;
	}
	
	public void setDiasUltimoEncaminhamento(Integer diasUltimoEncaminhamento) {
		this.diasUltimoEncaminhamento = diasUltimoEncaminhamento;
	}
	
	public Integer getDiasComResponsavel() {
		return diasComResponsavel;
	}
	
	public void setDiasComResponsavel(Integer diasComResponsavel) {
		this.diasComResponsavel = diasComResponsavel;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((numeroSiarg == null) ? 0 : numeroSiarg.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DemandasAguardandoUnidadeDTO other = (DemandasAguardandoUnidadeDTO) obj;
    if (numeroSiarg == null) {
      if (other.numeroSiarg != null) {
        return false;
      }
    } else if (!numeroSiarg.equals(other.numeroSiarg)) {
      return false;
    }
    return true;
  }

}
