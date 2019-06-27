package br.gov.caixa.gitecsa.siarg.enumerator;

public enum TipoImportacaoEnum {

  ASSUNTOS("Assuntos"),
  DEMANDAS("Demandas");

  private String descricao;

  private TipoImportacaoEnum(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }
}
