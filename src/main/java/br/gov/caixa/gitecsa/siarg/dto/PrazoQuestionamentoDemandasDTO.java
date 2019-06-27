package br.gov.caixa.gitecsa.siarg.dto;

import java.util.Date;

import br.gov.caixa.gitecsa.siarg.model.Demanda;

public class PrazoQuestionamentoDemandasDTO {
  
  private Long idDemanda;
  
  private Demanda demanda;

  private String assuntoCompleto;
  
  private Date dataDemanda;
  
  private Date dataUltimaAlteracao;
  
  private Integer diasSemInteracao;

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
   * @return the dataDemanda
   */
  public Date getDataDemanda() {
    return dataDemanda;
  }

  /**
   * @param dataDemanda the dataDemanda to set
   */
  public void setDataDemanda(Date dataDemanda) {
    this.dataDemanda = dataDemanda;
  }

  /**
   * @return the dataUltimaAlteracao
   */
  public Date getDataUltimaAlteracao() {
    return dataUltimaAlteracao;
  }

  /**
   * @param dataUltimaAlteracao the dataUltimaAlteracao to set
   */
  public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
    this.dataUltimaAlteracao = dataUltimaAlteracao;
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
    if (!(obj instanceof PrazoQuestionamentoDemandasDTO)) {
      return false;
    }
    PrazoQuestionamentoDemandasDTO other = (PrazoQuestionamentoDemandasDTO) obj;
    if (idDemanda == null) {
      if (other.idDemanda != null) {
        return false;
      }
    } else if (!idDemanda.equals(other.idDemanda)) {
      return false;
    }
    return true;
  }
  
}
