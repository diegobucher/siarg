package br.gov.caixa.gitecsa.siarg.dto;

public class ConfiguracaoEmailDTO {
  
  private String host;
  private String porta;
  private String remetente;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPorta() {
    return porta;
  }

  public void setPorta(String porta) {
    this.porta = porta;
  }

  public String getRemetente() {
    return remetente;
  }

  public void setRemetente(String remetente) {
    this.remetente = remetente;
  }

}