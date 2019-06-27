/**
 * RemoteCommandMessage.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.arquitetura.dto;

import br.gov.caixa.gitecsa.arquitetura.enumerator.RemoteCommandMessageTypeEnum;

/**
 * Objeto de transferência de dados que representa as mensagens retornadas por um remote command.
 * 
 * @author f757783
 */
public class RemoteCommandMessage {

  /** Conteúdo da mensagem */
  private String message;

  /** Nível de criticidade da mensagem */
  private RemoteCommandMessageTypeEnum type;
  
  /** Obtém a mensagem */
  public String getMessage() {
    return message;
  }
  
  /** Define a mensagem */
  public void setMessage(final String message) {
    this.message = message;
  }

  /** Obtém o nível de criticidade da mensagem */
  public RemoteCommandMessageTypeEnum getType() {
    return type;
  }

  /** Define o nível de criticidade da mensagem */
  public void setType(final RemoteCommandMessageTypeEnum type) {
    this.type = type;
  }
}
