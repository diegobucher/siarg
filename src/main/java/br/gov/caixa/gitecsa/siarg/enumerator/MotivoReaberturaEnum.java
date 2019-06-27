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
 * Enum de Motivo de reabertura.
 * 
 */
public enum MotivoReaberturaEnum implements EnumInterface<Integer> {

  NAO_FOI_CLARA(1, "A resposta à demanda não foi clara"),
  PRECISA_COMPLEMENTACAO(2, "A resposta precisa de complementação"),;

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.MotivoReaberturaEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;

  /**
   * Construtor padrão.
   * 
   * @param valor
   *          **Valor**
   * @param descricao
   *          **Descrição**
   */
  private MotivoReaberturaEnum(final Integer valor, final String descricao) {
    this.descricao = descricao;
    this.valor = valor;
  }

  /**
   * Obter Valor.
   * 
   * @return valor
   */
  @Override
  public Integer getValor() {
    return this.valor;
  }

  /**
   * Obter Descrição.
   * 
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
  public static MotivoReaberturaEnum valueOf(Integer valor) {
    for (MotivoReaberturaEnum tipo : values()) {
      if (tipo.getValor() == valor) {
        return tipo;
      }
    }
    return null;
  }

  public static MotivoReaberturaEnum valueOfDescricao(final String descricao) {
    for (MotivoReaberturaEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }
}
