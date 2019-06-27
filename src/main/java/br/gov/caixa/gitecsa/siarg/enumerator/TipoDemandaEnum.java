/**
 * TipoDemandaEnum.java
 * Versão: 1.0.0.00
 * 12/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Enum que representa os Tipos das demandas.
 * @author f520296
 */
public enum TipoDemandaEnum implements EnumInterface<Integer> {

  /**
   * 1 - Normal.
   */
  NORMAL(1, "NORMAL"),

  /**
   * 2 - Centralizadora.
   */
  CENTRALIZADORA(2, "CENTRALIZADORA"),

  /**
   * 3 - Consulta.
   */
  CONSULTA(3, "CONSULTA");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private TipoDemandaEnum(final Integer valor, final String descricao) {
    this.descricao = descricao;
    this.valor = valor;
  }

  /**
   * Obter Valor.
   * @return valor
   */
  @Override
  public Integer getValor() {
    return this.valor;
  }

  /**
   * Obter Descrição.
   * @return descricao
   */
  @Override
  public String getDescricao() {
    return this.descricao;
  }

  @Override
  public String toString() {
    return this.getDescricao();
  }

  /**
   * Método necessário para funcionar o mapeamento do valor do tipo T.
   */
  public static TipoDemandaEnum valueOf(Integer valor) {
    for (TipoDemandaEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static TipoDemandaEnum valueOfDescricao(final String descricao) {
    for (TipoDemandaEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
