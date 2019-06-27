/**
 * CaixaPostalException.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.arquitetura.exception;

/**
 * Classe que trata todas as exceptions de caixa postal inativa o que não existe.
 * @author f520296
 */
public class CaixaPostalException extends Exception {

  /** Serial. */
  private static final long serialVersionUID = 6859417884478540677L;

  /**
   * Construtor Padrão.
   */
  public CaixaPostalException() {
    super();
  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de validação e a causa de erro da
   * aplicação.
   * @param message **mensagem**
   * @param cause **causa**
   */
  public CaixaPostalException(final String message, final Throwable cause) {
    super(message, cause);

  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de validação da aplicação.
   * @param message **mensagem**
   */
  public CaixaPostalException(final String message) {
    super(message);
  }

  /**
   * Sobrecarga do construtor. Exception que carrega a mensagen de causa de erro da aplicação.
   * @param cause **causa**
   */
  public CaixaPostalException(final Throwable cause) {
    super(cause);
  }

}
