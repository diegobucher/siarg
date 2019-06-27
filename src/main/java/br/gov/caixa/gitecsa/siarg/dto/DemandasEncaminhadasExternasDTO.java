package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

public class DemandasEncaminhadasExternasDTO implements Serializable, Comparable<DemandasEncaminhadasExternasDTO> {

  /** Serial. */
  private static final long serialVersionUID = 6621366006274415067L;
  
  private String unidadeExterna;
  
  private String assunto;
  
  private String responsavel;
  
  private Integer qtdEcaminhadas; 
  
  private Integer somatorioQtdEcaminhadas;
  
  private Double tmDemandasEncaminhadas;  
  
  private Integer qtAguardaAcao;
  
  private Integer somatorioQtAguardaAcao;
  
  private Double tmAguardandoAcao; 
  
  /**
   * @return the unidadeExterna
   */
  public String getUnidadeExterna() {
    return unidadeExterna;
  }

  /**
   * @param unidadeExterna the unidadeExterna to set
   */
  public void setUnidadeExterna(String unidadeExterna) {
    this.unidadeExterna = unidadeExterna;
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
   * @return the responsavel
   */
  public String getResponsavel() {
    return responsavel;
  }

  /**
   * @param responsavel the responsavel to set
   */
  public void setResponsavel(String responsavel) {
    this.responsavel = responsavel;
  }

  /**
   * @return the qtdEcaminhadas
   */
  public Integer getQtdEcaminhadas() {
    return qtdEcaminhadas;
  }

  /**
   * @param qtdEcaminhadas the qtdEcaminhadas to set
   */
  public void setQtdEcaminhadas(Integer qtdEcaminhadas) {
    this.qtdEcaminhadas = qtdEcaminhadas;
  }

  /**
   * @return the tmDemandasEncaminhadas
   */
  public Double getTmDemandasEncaminhadas() {
    return tmDemandasEncaminhadas;
  }

  /**
   * @param tmDemandasEncaminhadas the tmDemandasEncaminhadas to set
   */
  public void setTmDemandasEncaminhadas(Double tmDemandasEncaminhadas) {
    this.tmDemandasEncaminhadas = tmDemandasEncaminhadas;
  }

  /**
   * @return the aqtAguardaAcao
   */
  public Integer getQtAguardaAcao() {
    return qtAguardaAcao;
  }

  /**
   * @param aqtAguardaAcao the aqtAguardaAcao to set
   */
  public void setQtAguardaAcao(Integer qtAguardaAcao) {
    this.qtAguardaAcao = qtAguardaAcao;
  }

  /**
   * @return the tmAguardandoAcao
   */
  public Double getTmAguardandoAcao() {
    return tmAguardandoAcao;
  }

  /**
   * @param tmAguardandoAcao the tmAguardandoAcao to set
   */
  public void setTmAguardandoAcao(Double tmAguardandoAcao) {
    this.tmAguardandoAcao = tmAguardandoAcao;
  }

  @Override
  public int compareTo(DemandasEncaminhadasExternasDTO dee) {
    return this.unidadeExterna.compareTo(dee.getUnidadeExterna());
  }

  /**
   * @return the somatorioQtdEcaminhadas
   */
  public Integer getSomatorioQtdEcaminhadas() {
    return somatorioQtdEcaminhadas;
  }

  /**
   * @param somatorioQtdEcaminhadas the somatorioQtdEcaminhadas to set
   */
  public void setSomatorioQtdEcaminhadas(Integer somatorioQtdEcaminhadas) {
    this.somatorioQtdEcaminhadas = somatorioQtdEcaminhadas;
  }

  /**
   * @return the somatorioQtAguardaAcao
   */
  public Integer getSomatorioQtAguardaAcao() {
    return somatorioQtAguardaAcao;
  }

  /**
   * @param somatorioQtAguardaAcao the somatorioQtAguardaAcao to set
   */
  public void setSomatorioQtAguardaAcao(Integer somatorioQtAguardaAcao) {
    this.somatorioQtAguardaAcao = somatorioQtAguardaAcao;
  }

}
