package br.gov.caixa.gitecsa.siarg.dto;

import java.util.Date;

import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;

public class ExtratoDemandasDTO {
  
  private Long idDemanda;
  
  private Demanda demanda;

  private String assuntoCompleto;
  
  private Integer prazo;
  
  private Date ultimaMovimentacao;
  
  private Integer diasSemInteracao;
  
  private String cor;
  
  private CaixaPostal caixaPostalResponsavel;
  
  private String nomeCaixaPostalResponsavel;
  
  /**
   * @return the idDemanda
   */
  public Long getIdDemanda() {
    return idDemanda;
  }

  /**
   * @param idDemanda the idDemanda to set
   */
  public void setIdDemanda(Long idDemanda) {
    this.idDemanda = idDemanda;
  }

  /**
   * @return the demanda
   */
  public Demanda getDemanda() {
    return demanda;
  }

  /**
   * @param demanda the demanda to set
   */
  public void setDemanda(Demanda demanda) {
    this.demanda = demanda;
  }

  /**
   * @return the assuntoCompleto
   */
  public String getAssuntoCompleto() {
    return assuntoCompleto;
  }

  /**
   * @param assuntoCompleto the assuntoCompleto to set
   */
  public void setAssuntoCompleto(String assuntoCompleto) {
    this.assuntoCompleto = assuntoCompleto;
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

  /**
   * @return the ultimaMovimentacao
   */
  public Date getUltimaMovimentacao() {
    return ultimaMovimentacao;
  }

  /**
   * @param ultimaMovimentacao the ultimaMovimentacao to set
   */
  public void setUltimaMovimentacao(Date ultimaMovimentacao) {
    this.ultimaMovimentacao = ultimaMovimentacao;
  }

  /**
   * @return the diasSemInteracao
   */
  public Integer getDiasSemInteracao() {
    return diasSemInteracao;
  }

  /**
   * @param diasSemInteracao the diasSemInteracao to set
   */
  public void setDiasSemInteracao(Integer diasSemInteracao) {
    this.diasSemInteracao = diasSemInteracao;
  }
  
  /**
   * @return the cor
   */
  public String getCor() {
    return cor;
  }

  /**
   * @param cor the cor to set
   */
  public void setCor(String cor) {
    this.cor = cor;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((idDemanda == null) ? 0 : idDemanda.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ExtratoDemandasDTO)) {
      return false;
    }
    ExtratoDemandasDTO other = (ExtratoDemandasDTO) obj;
    if (idDemanda == null) {
      if (other.idDemanda != null) {
        return false;
      }
    } else if (!idDemanda.equals(other.idDemanda)) {
      return false;
    }
    return true;
  }

  /**
   * @return the caixaPostalResponsavel
   */
  public CaixaPostal getCaixaPostalResponsavel() {
    return caixaPostalResponsavel;
  }

  /**
   * @param caixaPostalResponsavel the caixaPostalResponsavel to set
   */
  public void setCaixaPostalResponsavel(CaixaPostal caixaPostalResponsavel) {
    this.caixaPostalResponsavel = caixaPostalResponsavel;
  }

  /**
   * @return the nomeCaixaPostalResponsavel
   */
  public String getNomeCaixaPostalResponsavel() {
    return nomeCaixaPostalResponsavel;
  }

  /**
   * @param nomeCaixaPostalResponsavel the nomeCaixaPostalResponsavel to set
   */
  public void setNomeCaixaPostalResponsavel(String nomeCaixaPostalResponsavel) {
    this.nomeCaixaPostalResponsavel = nomeCaixaPostalResponsavel;
  }

}
