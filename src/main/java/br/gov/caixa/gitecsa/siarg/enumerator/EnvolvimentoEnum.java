/**
 * EnvolvimentoEnum.java
 * Versão: 1.0.0.00
 * 14/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Classe: .
 * @author f520296
 */
public enum EnvolvimentoEnum implements EnumInterface<Integer> {

  /**
   * 1 - Demandante.
   */
  DEMANDANTE(1, "Demandante"),

  /**
   * 2 - Responsavel.
   */
  RESPONSAVEL(2, "Responsável"),

  /**
   * 3 - Envolvido.
   */
  ENVOLVIDO(3, "Envolvido"),

  /**
   * 4 - Com Cópia.
   */
  COM_COPIA(4, "Com Cópia");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private EnvolvimentoEnum(final Integer valor, final String descricao) {
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
  public static EnvolvimentoEnum valueOf(Integer valor) {
    for (EnvolvimentoEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static EnvolvimentoEnum valueOfDescricao(final String descricao) {
    for (EnvolvimentoEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }

}
