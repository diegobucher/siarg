/**
 * KeyGroupValues.java
 * Versão: 1.0.0.00
 * Data de Criação : 07-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dto;

import java.util.List;

/**
 * Classe genérica para representar o par: chave e lista de valores.
 * 
 * @author f757783
 */
public class KeyGroupValues<T> implements Comparable<KeyGroupValues<T>>{

  /** Chave */
  private String key;

  /** Lista de valores */
  private List<T> values;

  /**
   * Construtor
   */
  public KeyGroupValues() {
    super();
  }

  /**
   * Construtor
   * 
   * @param key
   *          Chave
   * @param values
   *          Valor
   */
  public KeyGroupValues(final String key, final List<T> values) {
    this.key = key;
    this.values = values;
  }

  /**
   * Obtém a chave.
   * 
   * @return A chave
   */
  public String getKey() {
    return key;
  }

  /**
   * Seta a chave.
   * 
   * @param key
   *          Chave
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Obém a lista de valores do agrupamento.
   * 
   * @return Valores
   */
  public List<T> getValues() {
    return values;
  }

  /**
   * Seta os valores od agrupamento.
   * 
   * @param value
   *          Valores
   */
  public void setValues(List<T> value) {
    this.values = value;
  }

  @Override
  public int compareTo(KeyGroupValues<T> o) {
    return this.key.compareTo(o.getKey());
  }

}
