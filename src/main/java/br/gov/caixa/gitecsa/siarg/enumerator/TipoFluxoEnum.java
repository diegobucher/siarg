/**
 * TipoFluxoEnum.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Enum de tipo de fluxo.
 * @author f520296
 */
public enum TipoFluxoEnum implements EnumInterface<Integer> {

  /**
   * 1 - Demandante definido.
   */
  DEMANDANTE_DEFINIDO(1, "DEMANDANTE_DEFINIDO"),

  /**
   * 2 - Demandante responsável.
   */
  DEMANDANTE_RESPONSAVEL(2, "DEMANDANTE_RESPONSAVEL"),

  /**
   * 3 - Outros demandantes.
   */
  OUTROS_DEMANDANTES(3, "OUTROS_DEMANDANTES");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private TipoFluxoEnum(final Integer valor, final String descricao) {
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
  public static TipoFluxoEnum valueOf(Integer valor) {
    for (TipoFluxoEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static TipoFluxoEnum valueOfDescricao(final String descricao) {
    for (TipoFluxoEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
