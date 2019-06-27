package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

public class ExportacaoAssuntoDTO implements Serializable{
  
  private Long numeroAssunto;
  
  private String arvoreAssuntoAtual;
  
  private String categoria1;

  private String categoria2;
  
  private String categoria3;
  
  private String nomeAssunto;

  private String responsavel;
  
  private Integer prazoResponsavel;
  
  private String fluxoAssunto;
  
  private String prazoFluxoAssunto;
  
  private String demandantesPreDefinidos;
  
  private String observadoresAssunto;
  
  private String ativo;
  
//  private String refleteDemandas;

  public Long getNumeroAssunto() {
    return numeroAssunto;
  }

  public void setNumeroAssunto(Long numeroAssunto) {
    this.numeroAssunto = numeroAssunto;
  }

  public String getArvoreAssuntoAtual() {
    return arvoreAssuntoAtual;
  }

  public void setArvoreAssuntoAtual(String arvoreAssuntoAtual) {
    this.arvoreAssuntoAtual = arvoreAssuntoAtual;
  }

  public String getCategoria1() {
    return categoria1;
  }

  public void setCategoria1(String categoria1) {
    this.categoria1 = categoria1;
  }

  public String getCategoria2() {
    return categoria2;
  }

  public void setCategoria2(String categoria2) {
    this.categoria2 = categoria2;
  }

  public String getCategoria3() {
    return categoria3;
  }

  public void setCategoria3(String categoria3) {
    this.categoria3 = categoria3;
  }

  public String getNomeAssunto() {
    return nomeAssunto;
  }

  public void setNomeAssunto(String nomeAssunto) {
    this.nomeAssunto = nomeAssunto;
  }

  public String getResponsavel() {
    return responsavel;
  }

  public void setResponsavel(String responsavel) {
    this.responsavel = responsavel;
  }

  public Integer getPrazoResponsavel() {
    return prazoResponsavel;
  }

  public void setPrazoResponsavel(Integer prazoResponsavel) {
    this.prazoResponsavel = prazoResponsavel;
  }

  public String getFluxoAssunto() {
    return fluxoAssunto;
  }

  public void setFluxoAssunto(String fluxoAssunto) {
    this.fluxoAssunto = fluxoAssunto;
  }

  public String getPrazoFluxoAssunto() {
    return prazoFluxoAssunto;
  }

  public void setPrazoFluxoAssunto(String prazoFluxoAssunto) {
    this.prazoFluxoAssunto = prazoFluxoAssunto;
  }

  public String getDemandantesPreDefinidos() {
    return demandantesPreDefinidos;
  }

  public void setDemandantesPreDefinidos(String demandantesPreDefinidos) {
    this.demandantesPreDefinidos = demandantesPreDefinidos;
  }

  public String getObservadoresAssunto() {
    return observadoresAssunto;
  }

  public void setObservadoresAssunto(String observadoresAssunto) {
    this.observadoresAssunto = observadoresAssunto;
  }

  public String getAtivo() {
    return ativo;
  }

  public void setAtivo(String ativo) {
    this.ativo = ativo;
  }

//  public String getRefleteDemandas() {
//    return refleteDemandas;
//  }
//
//  public void setRefleteDemandas(String refleteDemandas) {
//    this.refleteDemandas = refleteDemandas;
//  }


}