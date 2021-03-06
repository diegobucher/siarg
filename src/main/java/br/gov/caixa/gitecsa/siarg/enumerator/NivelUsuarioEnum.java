/**
 * NivelUsuarioEnum.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.enumerator;

import br.gov.caixa.gitecsa.arquitetura.enumerator.EnumInterface;

/**
 * Enum de nível de usuário.
 * @author f520296
 */
public enum NivelUsuarioEnum implements EnumInterface<Integer> {

  /**
   * 1 - TODOS.
   */
  TODOS(1, "TODOS"),

  /**
   * 2 - GERENCIAL.
   */
  GERENCIAL(2, "GERENCIAL");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.NivelUsuarioEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * @param valor **Valor**
   * @param descricao **Descrição**
   */
  private NivelUsuarioEnum(final Integer valor, final String descricao) {
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
  public static NivelUsuarioEnum valueOf(Integer valor) {
    for (NivelUsuarioEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static NivelUsuarioEnum valueOfDescricao(final String descricao) {
    for (NivelUsuarioEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
