package br.gov.caixa.gitecsa.siarg.dto;

public class EmailAcaoUsuarioDTO {

  private String descricaoAcao;
  private String emailDestino;

  private String caixaOrigem;
  private String caixaDestino;
  
  private String numeroDemanda;
  
  private String macriculaUsuarioDemandante;
  private String usuarioDemandante;

  private String arvoreAssunto;
  private String tituloDemanda;

  private String dataLimite;
  private String prazo;
  private String resumo;

  private String urlSistema;
  
  public String getDescricaoAcao() {
    return descricaoAcao;
  }

  public void setDescricaoAcao(String descricaoAcao) {
    this.descricaoAcao = descricaoAcao;
  }

  public String getEmailDestino() {
    return emailDestino;
  }

  public void setEmailDestino(String emailDestino) {
    this.emailDestino = emailDestino;
  }

  public String getCaixaDestino() {
    return caixaDestino;
  }

  public void setCaixaDestino(String caixaDestino) {
    this.caixaDestino = caixaDestino;
  }

  public String getUsuarioDemandante() {
    return usuarioDemandante;
  }

  public void setUsuarioDemandante(String usuarioDemandante) {
    this.usuarioDemandante = usuarioDemandante;
  }

  public String getArvoreAssunto() {
    return arvoreAssunto;
  }

  public void setArvoreAssunto(String arvoreAssunto) {
    this.arvoreAssunto = arvoreAssunto;
  }

  public String getTituloDemanda() {
    return tituloDemanda;
  }

  public void setTituloDemanda(String tituloDemanda) {
    this.tituloDemanda = tituloDemanda;
  }

  public String getDataLimite() {
    return dataLimite;
  }

  public void setDataLimite(String dataLimite) {
    this.dataLimite = dataLimite;
  }

  public String getPrazo() {
    return prazo;
  }

  public void setPrazo(String prazo) {
    this.prazo = prazo;
  }

  public String getResumo() {
    return resumo;
  }

  public void setResumo(String resumo) {
    this.resumo = resumo;
  }

  public String getUrlSistema() {
    return urlSistema;
  }

  public void setUrlSistema(String urlSistema) {
    this.urlSistema = urlSistema;
  }

  public String getNumeroDemanda() {
    return numeroDemanda;
  }

  public void setNumeroDemanda(String numeroDemanda) {
    this.numeroDemanda = numeroDemanda;
  }

  public String getMacriculaUsuarioDemandante() {
    return macriculaUsuarioDemandante;
  }

  public void setMacriculaUsuarioDemandante(String macriculaUsuarioDemandante) {
    this.macriculaUsuarioDemandante = macriculaUsuarioDemandante;
  }

  public String getCaixaOrigem() {
    return caixaOrigem;
  }

  public void setCaixaOrigem(String caixaOrigem) {
    this.caixaOrigem = caixaOrigem;
  }

}
