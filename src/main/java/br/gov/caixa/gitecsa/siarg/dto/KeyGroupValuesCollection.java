/**
 * KeyValuePairCollection.java
 * Versão: 1.0.0.00
 * Data de Criação : 07-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe genérica para representar um conjunto de agrupamentos.
 * 
 * @author f757783
 */
public class KeyGroupValuesCollection<T> {

  /** Cache de chaves e posições dos elementos */
  private Map<String, Integer> cache = new HashMap<String, Integer>();

  /** Valores */
  private List<KeyGroupValues<T>> values = new ArrayList<KeyGroupValues<T>>();

  /**
   * Insere uma nova entrada na lista de valores ou atualiza-a caso a chave já exista.
   * 
   * @param key
   *          Chave
   * @param members
   *          Valores
   */
  public void put(final String key, final List<T> members) {
    if (this.cache.containsKey(key)) {
      this.values.get(this.cache.get(key)).getValues().addAll(members);
    } else {
      KeyGroupValues<T> entry = new KeyGroupValues<T>(key, members);
      this.values.add(entry);
      this.cache.put(key, this.values.indexOf(entry));
    }
  }

  /**
   * Retorna todos os valores agrupados pela chave informada.
   * 
   * @return Valores
   */
  public List<KeyGroupValues<T>> getValues() {
    return values;
  }
  
  /**
   * Retorna todos os valores agrupados pela chave informada.
   * 
   * @return Valores
   */
  public List<KeyGroupValues<T>> getOrderedValues() {
    Collections.sort(values);
    return values;
  }

}
