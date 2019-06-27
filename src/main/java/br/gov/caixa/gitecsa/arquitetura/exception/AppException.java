/**
 * AppException.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.exception;

/**
 * Classe que trata todas as exceptions de aplicação.
 * @author f520296
 */
public class AppException extends Exception {

  /** Serial. */
  private static final long serialVersionUID = -5177640136821474101L;

  /**
   * Construtor Padrão.
   */
  public AppException() {
    super();
  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de validação e a cauxa de erro da aplicação.
   * @param message **mensagem**
   * @param cause **causa**
   */
  public AppException(final String message, final Throwable cause) {
    super(message, cause);

  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de validação da aplicação.
   * @param message **mensagem**
   */
  public AppException(final String message) {
    super(message);
  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de causa de erro da aplicação.
   * @param cause **causa**
   */
  public AppException(final Throwable cause) {
    super(cause);
  }

}
