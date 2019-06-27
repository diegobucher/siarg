package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

import br.gov.caixa.gitecsa.siarg.model.Assunto;

public class RelatorioPeriodoPorAssuntoDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String areaDemandada;

  private Long numeroAssunto;

  private String nomeAssunto;

  private Long qtdAbertaPeriodoInicial;
  private Long qtdFechadaPeriodoInicial;

  private Long qtdAbertaPeriodoFinal;
  private Long qtdFechadaPeriodoFinal;

  private Assunto assunto;
  
  public RelatorioPeriodoPorAssuntoDTO() {
  }
  

  public Long getNumeroAssunto() {
    return numeroAssunto;
  }

  public void setNumeroAssunto(Long numeroAssunto) {
    this.numeroAssunto = numeroAssunto;
  }

  public String getNomeAssunto() {
    return nomeAssunto;
  }

  public void setNomeAssunto(String nomeAssunto) {
    this.nomeAssunto = nomeAssunto;
  }

  public String getAreaDemandada() {
    return areaDemandada;
  }

  public void setAreaDemandada(String areaDemandada) {
    this.areaDemandada = areaDemandada;
  }

  public Long getQtdAbertaPeriodoInicial() {
    return qtdAbertaPeriodoInicial;
  }

  public void setQtdAbertaPeriodoInicial(Long qtdAbertaPeriodoInicial) {
    this.qtdAbertaPeriodoInicial = qtdAbertaPeriodoInicial;
  }

  public Long getQtdFechadaPeriodoInicial() {
    return qtdFechadaPeriodoInicial;
  }

  public void setQtdFechadaPeriodoInicial(Long qtdFechadaPeriodoInicial) {
    this.qtdFechadaPeriodoInicial = qtdFechadaPeriodoInicial;
  }

  public Long getQtdAbertaPeriodoFinal() {
    return qtdAbertaPeriodoFinal;
  }

  public void setQtdAbertaPeriodoFinal(Long qtdAbertaPeriodoFinal) {
    this.qtdAbertaPeriodoFinal = qtdAbertaPeriodoFinal;
  }

  public Long getQtdFechadaPeriodoFinal() {
    return qtdFechadaPeriodoFinal;
  }

  public void setQtdFechadaPeriodoFinal(Long qtdFechadaPeriodoFinal) {
    this.qtdFechadaPeriodoFinal = qtdFechadaPeriodoFinal;
  }

  public Long getQtdComparativo() {
    return getQtdTotalPeriodoFinal()-getQtdTotalPeriodoInicial();
  }

  public Long getQtdTotalPeriodoInicial() {
    return getQtdAbertaPeriodoInicial() + getQtdFechadaPeriodoInicial();
  }

  public Long getQtdTotalPeriodoFinal() {
    return getQtdAbertaPeriodoFinal() + getQtdFechadaPeriodoFinal();
  }


  public Assunto getAssunto() {
    return assunto;
  }


  public void setAssunto(Assunto assunto) {
    this.assunto = assunto;
  }

}
