package br.gov.caixa.gitecsa.arquitetura.exception;

import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BusinessRollbackException extends AppException {

  /** Serial. */
  private static final long serialVersionUID = -4806939480794045985L;

  /**
   * Lista de erros.
   */
  private List<String> erroList;
  
  private String mensagemFormatada;

  /**
   * Construtor Padrão.
   */
  public BusinessRollbackException() {
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * 
   * @param message
   *          **Mensagem**
   */
  public BusinessRollbackException(final String message) {
    super(message);
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * 
   * @param erroList
   *          **Lista de erros**
   */
  public BusinessRollbackException(final List<String> erroList, String mensagemFormatada) {
    this.erroList = erroList;
    this.mensagemFormatada= mensagemFormatada;
  }

  /**
   * Sobrecarga do construtor. Exception que carrega todas as mensagens de validação da regra de negocio.
   * 
   * @param message
   *          **Mensagem**
   * @param erroList
   *          **Lista de erros**
   */
  public BusinessRollbackException(final String message, final List<String> erroList) {
    super(message);
    this.erroList = erroList;
  }

  /**
   * Obter Lista de erros.
   * 
   * @return List.
   */
  public List<String> getErroList() {
    return erroList;
  }

  /**
   * Gravar Lista de erros.
   * 
   * @param erroList
   */
  public void setErroList(final List<String> erroList) {
    this.erroList = erroList;
  }

  public String getMensagemFormatada() {
    return mensagemFormatada;
  }

  public void setMensagemFormatada(String mensagemFormatada) {
    this.mensagemFormatada = mensagemFormatada;
  }

}
