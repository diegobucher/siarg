package br.gov.caixa.gitecsa.siarg.dto;

import java.util.Date;

public class RelatorioConsolidadoDTO {
  
  private String respostaDe;
  
  private Date data;
  
  private String demanda;
  
  private String detalhe;

  public String getRespostaDe() {
    return respostaDe;
  }

  public void setRespostaDe(String respostaDe) {
    this.respostaDe = respostaDe;
  }

  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }

  public String getDemanda() {
    return demanda;
  }

  public void setDemanda(String demanda) {
    this.demanda = demanda;
  }

  public String getDetalhe() {
    return detalhe;
  }

  public void setDetalhe(String detalhe) {
    this.detalhe = detalhe;
  }

}
