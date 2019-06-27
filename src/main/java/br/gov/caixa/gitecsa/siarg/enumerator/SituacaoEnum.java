/**
 * SituacaoEnum.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Enum que representa as situações das demandas.
 * @author f520296
 */
public enum SituacaoEnum implements EnumInterface<Integer> {

  /**
   * 0 - Rascunho.
   */
  RASCUNHO(0, "Rascunho"),

  /**
   * 1 - Aberta.
   */
  ABERTA(1, "Aberta"),

  /**
   * 2 - Fechada.
   */
  FECHADA(2, "Fechada"),

  /**
   * 3 - Cancelada.
   */
  CANCELADA(3, "Cancelada"),

  /**
   * 4 - Minuta.
   */
  MINUTA(4, "Minuta");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private SituacaoEnum(final Integer valor, final String descricao) {
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
  public static SituacaoEnum valueOf(Integer valor) {
    for (SituacaoEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static SituacaoEnum valueOfDescricao(final String descricao) {
    for (SituacaoEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
