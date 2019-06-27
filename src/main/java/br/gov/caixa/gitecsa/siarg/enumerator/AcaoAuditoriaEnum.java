package br.gov.caixa.gitecsa.siarg.enumerator;

public enum AcaoAuditoriaEnum {

  I("Inclusão"),
  A("Alteração"),
  E("Exclusão");

  private String descricao;

  private AcaoAuditoriaEnum(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }
}
