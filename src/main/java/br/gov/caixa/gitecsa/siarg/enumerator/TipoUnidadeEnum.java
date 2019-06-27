/**
 * TipoUnidadeEnum.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Enum que representa os tipos de Unidades.
 * @author f520296
 */
public enum TipoUnidadeEnum implements EnumInterface<Integer> {

  /**
   * Unidade: Matriz.
   */
  ARROBA_EXTERNA(0, "@EXTERNA"),

  /**
   * Unidade: Matriz.
   */
  MATRIZ(1, "MATRIZ"),

  /**
   * Unidade: Filial.
   */
  FILIAL(2, "FILIAL"),

  /**
   * Unidade: Externa.
   */
  EXTERNA(3, "EXTERNA");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private TipoUnidadeEnum(final Integer valor, final String descricao) {
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
  public static TipoUnidadeEnum valueOf(Integer valor) {
    for (TipoUnidadeEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static TipoUnidadeEnum valueOfDescricao(final String descricao) {
    for (TipoUnidadeEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
