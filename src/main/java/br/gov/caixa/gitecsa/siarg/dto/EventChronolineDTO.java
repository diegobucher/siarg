package br.gov.caixa.gitecsa.siarg.dto;

public class EventChronolineDTO {

  private String dataInicial;
  private String dataFinal;
  private String descricao;
  private String cor;

  public EventChronolineDTO() {
  }

  public String getDataInicial() {
    return dataInicial;
  }

  public void setDataInicial(String dataInicial) {
    this.dataInicial = dataInicial;
  }

  public String getDataFinal() {
    return dataFinal;
  }

  public void setDataFinal(String dataFinal) {
    this.dataFinal = dataFinal;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getCor() {
    return cor;
  }

  public void setCor(String cor) {
    this.cor = cor;
  }


}
