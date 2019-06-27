/**
 * RelacaoAssuntosDTO.java
 * Versão: 1.0.0.00
 * 19/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

/**
 * Classe DTO para confecção do relatório de Demandas em Aberto.
 * @author f520296
 */
public class DemandasEmAbertoDTO implements Serializable, Comparable<DemandasEmAbertoDTO> {

  /** Serial. */
  private static final long serialVersionUID = -302868597993686427L;

  private String areaDemandada;

  private String assunto;

  private Long numeroDemanda;

  private String dtPrazoVencimento;

  private String dtPrazoVencimentoOrdenacao;
  
  private Integer prazo;
  
  private Integer situacaoVencimento;
  
  /** Atributos padrões do DTO. */
  private Long idDemandaPai;
  
  /** Atributos padrões do DTO. */
  private Boolean flagDemandaFilha;
  
  /**Sera utilizado para preenchimento no caso de a demandada for @EXTERNO*/
  private String areaDemandateAnterior;

  /**
   * @return the areaDemandada
   */
  public String getAreaDemandada() {
    return areaDemandada;
  }

  /**
   * @param areaDemandada the areaDemandada to set
   */
  public void setAreaDemandada(String areaDemandada) {
    this.areaDemandada = areaDemandada;
  }

  /**
   * @return the assunto
   */
  public String getAssunto() {
    return assunto;
  }

  /**
   * @param assunto the assunto to set
   */
  public void setAssunto(String assunto) {
    this.assunto = assunto;
  }

  /**
   * @return the numeroDemanda
   */
  public Long getNumeroDemanda() {
    return numeroDemanda;
  }
  
  public String getNumeroDemandaFormatado() {
	  
	  StringBuilder sb = new StringBuilder();
	  if(Boolean.TRUE.equals(flagDemandaFilha)){
		  sb.append(this.idDemandaPai);
		  sb.append("/");
	  }
	  sb.append(this.numeroDemanda);
	  
	  return sb.toString();
  }

  /**
   * @param numeroDemanda the numeroDemanda to set
   */
  public void setNumeroDemanda(Long numeroDemanda) {
    this.numeroDemanda = numeroDemanda;
  }

  /**
   * @return the dtPrazoVencimento
   */
  public String getDtPrazoVencimento() {
    return dtPrazoVencimento;
  }

  /**
   * @param dtPrazoVencimento the dtPrazoVencimento to set
   */
  public void setDtPrazoVencimento(String dtPrazoVencimento) {
    this.dtPrazoVencimento = dtPrazoVencimento;
  }

  @Override
  public int compareTo(DemandasEmAbertoDTO arg0) {
    return this.dtPrazoVencimentoOrdenacao.compareTo(arg0.getDtPrazoVencimentoOrdenacao());
  }

  /**
   * @return the prazo
   */
  public Integer getPrazo() {
    return prazo;
  }

  /**
   * @param prazo the prazo to set
   */
  public void setPrazo(Integer prazo) {
    this.prazo = prazo;
  }

  public Integer getSituacaoVencimento() {
    return situacaoVencimento;
  }

  public void setSituacaoVencimento(Integer situacaoVencimento) {
    this.situacaoVencimento = situacaoVencimento;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((areaDemandada == null) ? 0 : areaDemandada.hashCode());
    result = prime * result + ((assunto == null) ? 0 : assunto.hashCode());
    result = prime * result + ((dtPrazoVencimento == null) ? 0 : dtPrazoVencimento.hashCode());
    result = prime * result + ((numeroDemanda == null) ? 0 : numeroDemanda.hashCode());
    result = prime * result + ((prazo == null) ? 0 : prazo.hashCode());
    result = prime * result + ((situacaoVencimento == null) ? 0 : situacaoVencimento.hashCode());
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
    DemandasEmAbertoDTO other = (DemandasEmAbertoDTO) obj;
    if (areaDemandada == null) {
      if (other.areaDemandada != null) {
        return false;
      }
    } else if (!areaDemandada.equals(other.areaDemandada)) {
      return false;
    }
    if (assunto == null) {
      if (other.assunto != null) {
        return false;
      }
    } else if (!assunto.equals(other.assunto)) {
      return false;
    }
    if (dtPrazoVencimento == null) {
      if (other.dtPrazoVencimento != null) {
        return false;
      }
    } else if (!dtPrazoVencimento.equals(other.dtPrazoVencimento)) {
      return false;
    }
    if (numeroDemanda == null) {
      if (other.numeroDemanda != null) {
        return false;
      }
    } else if (!numeroDemanda.equals(other.numeroDemanda)) {
      return false;
    }
    if (prazo == null) {
      if (other.prazo != null) {
        return false;
      }
    } else if (!prazo.equals(other.prazo)) {
      return false;
    }
    if (situacaoVencimento == null) {
      if (other.situacaoVencimento != null) {
        return false;
      }
    } else if (!situacaoVencimento.equals(other.situacaoVencimento)) {
      return false;
    }
    return true;
  }

  public String getDtPrazoVencimentoOrdenacao() {
    return dtPrazoVencimentoOrdenacao;
  }

  public void setDtPrazoVencimentoOrdenacao(String dtPrazoVencimentoOrdenacao) {
    this.dtPrazoVencimentoOrdenacao = dtPrazoVencimentoOrdenacao;
  }

public Long getIdDemandaPai() {
	return idDemandaPai;
}

public void setIdDemandaPai(Long idDemandaPai) {
	this.idDemandaPai = idDemandaPai;
}

public Boolean getFlagDemandaFilha() {
	return flagDemandaFilha;
}

public void setFlagDemandaFilha(Boolean flagDemandaFilha) {
	this.flagDemandaFilha = flagDemandaFilha;
}

public String getAreaDemandateAnterior() {
	return areaDemandateAnterior;
}

public void setAreaDemandateAnterior(String areaDemandateAnterior) {
	this.areaDemandateAnterior = areaDemandateAnterior;
}




}
