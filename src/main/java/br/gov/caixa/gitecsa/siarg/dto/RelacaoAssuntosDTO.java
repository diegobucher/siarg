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
 * Classe DTO para confecção do relatório de Relação de Assuntos.
 * @author f520296
 */
public class RelacaoAssuntosDTO implements Serializable, Comparable<RelacaoAssuntosDTO> {

  /** Serial. */
  private static final long serialVersionUID = -302868597993686427L;

  private Long numeroAssunto;

  private String nomeAssunto;

  private String responsavel;

  private String responsavelOrdenacao;

  private String atoresFluxo;

  private String observadoresAutorizados;

  private String demandantesPreDefinidos;

  /**
   * Obter o valor padrão.
   * @return the numeroAssunto
   */
  public Long getNumeroAssunto() {
    return this.numeroAssunto;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param numeroAssunto the numeroAssunto to set
   */
  public void setNumeroAssunto(Long numeroAssunto) {
    this.numeroAssunto = numeroAssunto;
  }

  /**
   * Obter o valor padrão.
   * @return the nomeAssunto
   */
  public String getNomeAssunto() {
    return this.nomeAssunto;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param nomeAssunto the nomeAssunto to set
   */
  public void setNomeAssunto(String nomeAssunto) {
    this.nomeAssunto = nomeAssunto;
  }

  /**
   * Obter o valor padrão.
   * @return the responsavel
   */
  public String getResponsavel() {
    return this.responsavel;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param responsavel the responsavel to set
   */
  public void setResponsavel(String responsavel) {
    this.responsavel = responsavel;
  }

  /**
   * Obter o valor padrão.
   * @return the atoresFluxo
   */
  public String getAtoresFluxo() {
    return this.atoresFluxo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param atoresFluxo the atoresFluxo to set
   */
  public void setAtoresFluxo(String atoresFluxo) {
    this.atoresFluxo = atoresFluxo;
  }

  /**
   * Obter o valor padrão.
   * @return the observadoresAutorizados
   */
  public String getObservadoresAutorizados() {
    return this.observadoresAutorizados;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param observadoresAutorizados the observadoresAutorizados to set
   */
  public void setObservadoresAutorizados(String observadoresAutorizados) {
    this.observadoresAutorizados = observadoresAutorizados;
  }

  /**
   * Obter o valor padrão.
   * @return the demandantesPreDefinidos
   */
  public String getDemandantesPreDefinidos() {
    return this.demandantesPreDefinidos;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandantesPreDefinidos the demandantesPreDefinidos to set
   */
  public void setDemandantesPreDefinidos(String demandantesPreDefinidos) {
    this.demandantesPreDefinidos = demandantesPreDefinidos;
  }

  /**
   * @return the responsavelOrdenacao
   */
  public String getResponsavelOrdenacao() {
    return responsavelOrdenacao;
  }
  
  /**
   * @param responsavelOrdenacao the responsavelOrdenacao to set
   */
  public void setResponsavelOrdenacao(String responsavelOrdenacao) {
    this.responsavelOrdenacao = responsavelOrdenacao;
  }

  @Override
  public int compareTo(RelacaoAssuntosDTO dto) {
    return this.responsavelOrdenacao.compareToIgnoreCase(dto.responsavelOrdenacao);
  }


}
