package br.gov.caixa.gitecsa.siarg.dto;

import java.util.List;

public class EmailDTO {
  
  private String destinatario;
  private String assunto;
  private String conteudo;
  private List<String> comCopiaList;

  public String getAssunto() {
    return assunto;
  }

  public void setAssunto(String assunto) {
    this.assunto = assunto;
  }

  public String getConteudo() {
    return conteudo;
  }

  public void setConteudo(String conteudo) {
    this.conteudo = conteudo;
  }

  public String getDestinatario() {
    return destinatario;
  }

  public void setDestinatario(String destinatario) {
    this.destinatario = destinatario;
  }

  public List<String> getComCopiaList() {
    return comCopiaList;
  }

  public void setComCopiaList(List<String> comCopiaList) {
    this.comCopiaList = comCopiaList;
  }

}