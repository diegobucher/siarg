/**
 * BaseController.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.arquitetura.exception;

import java.util.List;

/**
 * Classe que trata todas as exceptions de campos requeridos.
 * @author f520296
 */
public class RequiredException extends AppException {

  /** Serial. */
  private static final long serialVersionUID = -4806939480794045985L;

  /**
   * Lista de erros.
   */
  private List<String> erroList;

  /**
   * Construtor Padrão.
   */
  public RequiredException() {
  }

  /**
   * Exception que carrega todas as mensagens de validação dos campos requeridos.
   * @param message Mensagem
   */
  public RequiredException(final String message) {
    super(message);
  }

  /**
   * Exception que carrega todas as mensagens de validação dos campos requeridos.
   * @param erroList Lista de erros
   */
  public RequiredException(final List<String> erroList) {
    this.erroList = erroList;
  }

  /**
   * Exception que carrega todas as mensagens de validação dos campos requeridos.
   * @param message Mensagem
   * @param erroList Lista de erros
   */
  public RequiredException(final String message, final List<String> erroList) {
    super(message);
    this.erroList = erroList;
  }

  /**
   * Obter a Lista de erros.
   * @return erroList
   */
  public List<String> getErroList() {
    return erroList;
  }

  /**
   * Gravar a lista de erros.
   * @param erroList **lista de erros**
   */
  public void setErroList(final List<String> erroList) {
    this.erroList = erroList;
  }
}
