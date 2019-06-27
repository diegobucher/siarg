/**
 * DataBaseException.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.exception;

import javax.inject.Inject;

import org.apache.log4j.Logger;

/**
 * Erros de Banco de dados.
 * @author f520296
 */
public class DataBaseException extends AppException {

  /** Serial. */
  private static final long serialVersionUID = 6498157868306431575L;

  /** Injeção de Dependência. */
  @Inject
  private Logger logger;

  /**
   * Construtor Padrão sem argumentos.
   */
  public DataBaseException() {
    super();
  }

  /**
   * Exception que carrega todas as mensagens de validação da base de dados.
   * @param e Mensagem
   */
  public DataBaseException(final String e) {
    super(e);
    this.logger.error(e);
  }

  /**
   * Exception que carrega todas as mensagens de validação da base de dados.
   * @param e Throwable
   */
  public DataBaseException(final Throwable e) {
    super(e);
    this.logger.error("Message: " + e.getMessage() + " Cause: " + getCause(), e);
  }

  /**
   * Exception que carrega todas as mensagens de validação da base de dados.
   * @param mensagem Mensagem
   * @param e Exception
   */
  public DataBaseException(final String mensagem, final Exception e) {
    super(mensagem);
    this.logger.error(mensagem + " - " + e.getMessage(), e);
  }

}
