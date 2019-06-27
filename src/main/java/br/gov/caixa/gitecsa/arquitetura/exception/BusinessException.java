/**
 * BusinessException.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.exception;

import java.util.List;

/**
 * Classe que trata todas as exceptions do negocio.
 * @author jsouzaa
 */
public class BusinessException extends AppException {

  /** Serial. */
  private static final long serialVersionUID = -4806939480794045985L;

  /**
   * Lista de erros.
   */
  private List<String> erroList;

  /**
   * Construtor Padrão.
   */
  public BusinessException() {
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * @param message **Mensagem**
   */
  public BusinessException(final String message) {
    super(message);
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * @param erroList **Lista de erros**
   */
  public BusinessException(final List<String> erroList) {
    this.erroList = erroList;
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * @param message **Mensagem**
   * @param erroList **Lista de erros**
   */
  public BusinessException(final String message, final List<String> erroList) {
    super(message);
    this.erroList = erroList;
  }

  /**
   * Obter Lista de erros.
   * @return List.
   */
  public List<String> getErroList() {
    return erroList;
  }

  /**
   * Gravar Lista de erros.
   * @param erroList
   */
  public void setErroList(final List<String> erroList) {
    this.erroList = erroList;
  }

}
