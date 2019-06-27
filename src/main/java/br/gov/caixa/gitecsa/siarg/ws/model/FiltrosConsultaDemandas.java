package br.gov.caixa.gitecsa.siarg.ws.model;

import java.util.Date;

public class FiltrosConsultaDemandas {

  //Campos Obrigatórios
  private Long idAbrangencia;
  private Date dataInicial;
  private Date dataFinal;
  private Integer aberta;
  private Integer reaberta; 
  private Integer externa;
  private Integer tipoConsulta;
  private Integer conteudo;
  
  //Campos Opcionais
  private Long coAssunto;
  private String cpDemandante;
  private String cpDemandada;
  
  private String cpResponsavelAssunto;
  private String cpResponsavelAtual;
  private Integer coUnidadeDemandante;
  private Integer coUnidadeDemandada;
  private Integer coUnidadeRespAssunto;
  private Integer coUnidadeRespAtual;
  private String nuContrato;
  private Integer prazoDemanda;
  private Integer prazoResponsavel;
  private Integer tipoIteracao;
  
  
  public Integer getAberta() {
    return aberta;
  }

  public void setAberta(Integer aberta) {
    this.aberta = aberta;
  }

  public Integer getReaberta() {
    return reaberta;
  }

  public void setReaberta(Integer reaberta) {
    this.reaberta = reaberta;
  }

  public Integer getExterna() {
    return externa;
  }

  public void setExterna(Integer externa) {
    this.externa = externa;
  }


  public Date getDataInicial() {
    return dataInicial;
  }

  public void setDataInicial(Date dataInicial) {
    this.dataInicial = dataInicial;
  }

  public Date getDataFinal() {
    return dataFinal;
  }

  public void setDataFinal(Date dataFinal) {
    this.dataFinal = dataFinal;
  }

  public Long getIdAbrangencia() {
    return idAbrangencia;
  }

  public void setIdAbrangencia(Long idAbrangencia) {
    this.idAbrangencia = idAbrangencia;
  }

  public Integer getTipoConsulta() {
    return tipoConsulta;
  }

  public void setTipoConsulta(Integer tipoConsulta) {
    this.tipoConsulta = tipoConsulta;
  }

  public String getCpResponsavelAssunto() {
    return cpResponsavelAssunto;
  }

  public void setCpResponsavelAssunto(String cpResponsavelAssunto) {
    this.cpResponsavelAssunto = cpResponsavelAssunto;
  }

  public String getCpResponsavelAtual() {
    return cpResponsavelAtual;
  }

  public void setCpResponsavelAtual(String cpResponsavelAtual) {
    this.cpResponsavelAtual = cpResponsavelAtual;
  }

  public Integer getCoUnidadeDemandante() {
    return coUnidadeDemandante;
  }

  public void setCoUnidadeDemandante(Integer coUnidadeDemandante) {
    this.coUnidadeDemandante = coUnidadeDemandante;
  }

  public Integer getCoUnidadeDemandada() {
    return coUnidadeDemandada;
  }

  public void setCoUnidadeDemandada(Integer coUnidadeDemandada) {
    this.coUnidadeDemandada = coUnidadeDemandada;
  }

  public Integer getCoUnidadeRespAssunto() {
    return coUnidadeRespAssunto;
  }

  public void setCoUnidadeRespAssunto(Integer coUnidadeRespAssunto) {
    this.coUnidadeRespAssunto = coUnidadeRespAssunto;
  }

  public Integer getCoUnidadeRespAtual() {
    return coUnidadeRespAtual;
  }

  public void setCoUnidadeRespAtual(Integer coUnidadeRespAtual) {
    this.coUnidadeRespAtual = coUnidadeRespAtual;
  }

  public Integer getPrazoDemanda() {
    return prazoDemanda;
  }

  public void setPrazoDemanda(Integer prazoDemanda) {
    this.prazoDemanda = prazoDemanda;
  }

  public Integer getPrazoResponsavel() {
    return prazoResponsavel;
  }

  public void setPrazoResponsavel(Integer prazoResponsavel) {
    this.prazoResponsavel = prazoResponsavel;
  }

  public Integer getTipoIteracao() {
    return tipoIteracao;
  }

  public void setTipoIteracao(Integer tipoIteracao) {
    this.tipoIteracao = tipoIteracao;
  }

  public Long getCoAssunto() {
    return coAssunto;
  }

  public void setCoAssunto(Long coAssunto) {
    this.coAssunto = coAssunto;
  }

  public String getCpDemandante() {
    return cpDemandante;
  }

  public void setCpDemandante(String cpDemandante) {
    this.cpDemandante = cpDemandante;
  }

  public String getCpDemandada() {
    return cpDemandada;
  }

  public void setCpDemandada(String cpDemandada) {
    this.cpDemandada = cpDemandada;
  }

  public String getNuContrato() {
    return nuContrato;
  }

  public void setNuContrato(String nuContrato) {
    this.nuContrato = nuContrato;
  }

  public Integer getConteudo() {
    return conteudo;
  }

  public void setConteudo(Integer conteudo) {
    this.conteudo = conteudo;
  }

}
