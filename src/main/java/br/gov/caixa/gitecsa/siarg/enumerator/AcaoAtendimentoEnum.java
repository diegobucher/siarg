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
 * Enum de Acao do atedimento.
 * 
 * @author f520296
 */
public enum AcaoAtendimentoEnum implements EnumInterface<Integer> {

  INCLUIR(1, "Incluir", "foi incluida com sucesso em nossa base"),
  QUESTIONAR(2, "Questionar", "está aguardando nova interação dessa unidade"),
  CONSULTAR(3, "Consultar", "foi consultada com sucesso em nossa base"),
  ENCAMINHAR(4, "Encaminhar", "está aguardando nova interação dessa unidade"),
  RESPONDER(5, "Responder", "foi respondida"),
  REABRIR(6, "Reabrir", "está aguardando nova interação dessa unidade"),
  CANCELAR(7, "Cancelar", "foi cancelada"),
  RASCUNHO(8, "Rascunho", ""),
  INFORMAR(9, "Informar", ""),
  FECHAR(10, "Fechar", "foi fechada"),
  CONSOLIDAR(11, "Consolidar", ""),
  IMPRIMIR(12, "Imprimir", ""),
  EMAIL_EXTERNO(13, "Email Externo", ""),
  MIGRAR(14, "Migrar", ""),
  ATUALIZAR(15, "Atualizar", "foi atualizada");

  /** Constante. */
  public static final String NOME_ENUM = "br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum";

  /** Variáveis de classe. */
  private Integer valor;

  /** Variáveis de classe. */
  private String descricao;
  
  private String descricaoEmail;

  /**
   * Construtor padrão.
   * 
   * @param valor
   *          **Valor**
   * @param descricao
   *          **Descrição**
   */
  private AcaoAtendimentoEnum(final Integer valor, final String descricao, final String descricaoEmail) {
    this.descricao = descricao;
    this.descricaoEmail = descricaoEmail;
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
  public static AcaoAtendimentoEnum valueOf(Integer valor) {
    for (AcaoAtendimentoEnum tipo : values()) {
      if (tipo.getValor().equals(valor)) {
        return tipo;
      }
    }
    return null;
  }

  public static AcaoAtendimentoEnum valueOfDescricao(final String descricao) {
    for (AcaoAtendimentoEnum tipo : values()) {
      if (tipo.getDescricao().equals(descricao)) {
        return tipo;
      }
    }
    return null;
  }

  public String getDescricaoEmail() {
    return descricaoEmail;
  }
}
