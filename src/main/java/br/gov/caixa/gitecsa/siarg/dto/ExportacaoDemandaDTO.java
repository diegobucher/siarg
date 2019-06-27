package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

public class ExportacaoDemandaDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String arvoreAssuntoAtual;

  private Long numeroDemanda;

  private String fluxoDemanda;

  private String prazoFluxoDemanda;

  private String responsavelAtual;

  private String caixaPostalDemandante;

  private String observadoresDemanda;

  public Long getNumeroDemanda() {
    return numeroDemanda;
  }

  public void setNumeroDemanda(Long numeroDemanda) {
    this.numeroDemanda = numeroDemanda;
  }

  public String getFluxoDemanda() {
    return fluxoDemanda;
  }

  public void setFluxoDemanda(String fluxoDemanda) {
    this.fluxoDemanda = fluxoDemanda;
  }

  public String getPrazoFluxoDemanda() {
    return prazoFluxoDemanda;
  }

  public void setPrazoFluxoDemanda(String prazoFluxoDemanda) {
    this.prazoFluxoDemanda = prazoFluxoDemanda;
  }

  public String getResponsavelAtual() {
    return responsavelAtual;
  }

  public void setResponsavelAtual(String responsavelAtual) {
    this.responsavelAtual = responsavelAtual;
  }

  public String getCaixaPostalDemandante() {
    return caixaPostalDemandante;
  }

  public void setCaixaPostalDemandante(String caixaPostalDemandante) {
    this.caixaPostalDemandante = caixaPostalDemandante;
  }

  public String getObservadoresDemanda() {
    return observadoresDemanda;
  }

  public void setObservadoresDemanda(String observadoresDemanda) {
    this.observadoresDemanda = observadoresDemanda;
  }

  public String getArvoreAssuntoAtual() {
    return arvoreAssuntoAtual;
  }

  public void setArvoreAssuntoAtual(String arvoreAssuntoAtual) {
    this.arvoreAssuntoAtual = arvoreAssuntoAtual;
  }

}