/**
 * RemoteCommandMessageTypeEnum.java
 * Versão: 1.0.0.00
 * Data de Criação : 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.arquitetura.enumerator;

import com.google.gson.annotations.SerializedName;

/**
 * Enumera os níveis de criticidade de mensagens.
 * 
 * @author f520296
 */
public enum RemoteCommandMessageTypeEnum {

  /**
   * Erro
   */
  @SerializedName("error")
  ERROR,
  
  /**
   * Informação
   */
  @SerializedName("info")
  INFO,
  
  /**
   * Aviso
   */
  @SerializedName("warning")
  WARNING,
  
  /**
   * Sucesso
   */
  @SerializedName("success")
  SUCCESS;
}
