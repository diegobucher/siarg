package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

public class QuantidadeIteracoesDTO implements Serializable, Comparable<QuantidadeIteracoesDTO> {

  private static final long serialVersionUID = 1L;

  private String caixaPostal;

  private long qtdeIteracoesQuestionar;
  
  private Double tempoMedioQuestionar = 0d;
  
  private Double tempoMedioParcialQuest = 0d;
  
  private long qtdeIteracoesConsultar;
  
  private Double tempoMedioConsultar = 0d;
  
  private Double tempoMedioParcialConsu = 0d;
  
  private long qtdeIteracoesEncaminhar;
  
  private Double tempoMedioEncaminhar = 0d;
  
  private Double tempoMedioParcialEnc = 0d;
  
  private long qtdeIteracoesResponder;
  
  private Double tempoMedioResponder = 0d;
  
  private Double tempoMedioParcialResp = 0d;
  
  private long totalIteracoesQuestionar;
  
  private long totalIteracoesConsultar;
  
  private long totalIteracoesEncaminhar;
  
  private long totalIteracoesResponder;
  
  private Double totaltempoMedioQuestionar = 0d;
  
  private Double totaltempoMedioConsultar = 0d;
  
  private Double totaltempoMedioEncaminhar = 0d;
  
  private Double totaltempoMedioResponder = 0d;
  
  public QuantidadeIteracoesDTO() {
  }

  public String getCaixaPostal() {
	return caixaPostal;
}

public void setCaixaPostal(String caixaPostal) {
	this.caixaPostal = caixaPostal;
}

public long getQtdeIteracoesQuestionar() {
	return qtdeIteracoesQuestionar;
}

public void setQtdeIteracoesQuestionar(long qtdeIteracoesQuestionar) {
	this.qtdeIteracoesQuestionar = qtdeIteracoesQuestionar;
}

public Double getTempoMedioQuestionar() {
	return tempoMedioQuestionar;
}

public void setTempoMedioQuestionar(Double tempoMedioQuestionar) {
	this.tempoMedioQuestionar = tempoMedioQuestionar;
}

public long getQtdeIteracoesConsultar() {
	return qtdeIteracoesConsultar;
}

public void setQtdeIteracoesConsultar(long qtdeIteracoesConsultar) {
	this.qtdeIteracoesConsultar = qtdeIteracoesConsultar;
}

public Double getTempoMedioConsultar() {
	return tempoMedioConsultar;
}

public void setTempoMedioConsultar(Double tempoMedioConsultar) {
	this.tempoMedioConsultar = tempoMedioConsultar;
}

public long getQtdeIteracoesEncaminhar() {
	return qtdeIteracoesEncaminhar;
}

public void setQtdeIteracoesEncaminhar(long qtdeIteracoesEncaminhar) {
	this.qtdeIteracoesEncaminhar = qtdeIteracoesEncaminhar;
}

public Double getTempoMedioEncaminhar() {
	return tempoMedioEncaminhar;
}

public void setTempoMedioEncaminhar(Double tempoMedioEncaminhar) {
	this.tempoMedioEncaminhar = tempoMedioEncaminhar;
}

public long getQtdeIteracoesResponder() {
	return qtdeIteracoesResponder;
}

public void setQtdeIteracoesResponder(long qtdeIteracoesResponder) {
	this.qtdeIteracoesResponder = qtdeIteracoesResponder;
}

public Double getTempoMedioResponder() {
	return tempoMedioResponder;
}

public void setTempoMedioResponder(Double tempoMedioResponder) {
	this.tempoMedioResponder = tempoMedioResponder;
}

public long getTotalIteracoesQuestionar() {
	return totalIteracoesQuestionar;
}

public void setTotalIteracoesQuestionar(long totalIteracoesQuestionar) {
	this.totalIteracoesQuestionar = totalIteracoesQuestionar;
}

public long getTotalIteracoesConsultar() {
	return totalIteracoesConsultar;
}

public void setTotalIteracoesConsultar(long totalIteracoesConsultar) {
	this.totalIteracoesConsultar = totalIteracoesConsultar;
}

public long getTotalIteracoesEncaminhar() {
	return totalIteracoesEncaminhar;
}

public void setTotalIteracoesEncaminhar(long totalIteracoesEncaminhar) {
	this.totalIteracoesEncaminhar = totalIteracoesEncaminhar;
}

public long getTotalIteracoesResponder() {
	return totalIteracoesResponder;
}

public void setTotalIteracoesResponder(long totalIteracoesResponder) {
	this.totalIteracoesResponder = totalIteracoesResponder;
}

public Double getTotaltempoMedioQuestionar() {
	return totaltempoMedioQuestionar;
}

public void setTotaltempoMedioQuestionar(Double totaltempoMedioQuestionar) {
	this.totaltempoMedioQuestionar = totaltempoMedioQuestionar;
}

public Double getTotaltempoMedioConsultar() {
	return totaltempoMedioConsultar;
}

public void setTotaltempoMedioConsultar(Double totaltempoMedioConsultar) {
	this.totaltempoMedioConsultar = totaltempoMedioConsultar;
}

public Double getTotaltempoMedioEncaminhar() {
	return totaltempoMedioEncaminhar;
}

public void setTotaltempoMedioEncaminhar(Double totaltempoMedioEncaminhar) {
	this.totaltempoMedioEncaminhar = totaltempoMedioEncaminhar;
}

public Double getTotaltempoMedioResponder() {
	return totaltempoMedioResponder;
}

public void setTotaltempoMedioResponder(Double totaltempoMedioResponder) {
	this.totaltempoMedioResponder = totaltempoMedioResponder;
}

public Double getTempoMedioParcialQuest() {
	return tempoMedioParcialQuest;
}

public void setTempoMedioParcialQuest(Double tempoMedioParcialQuest) {
	this.tempoMedioParcialQuest = tempoMedioParcialQuest;
}

public Double getTempoMedioParcialConsu() {
	return tempoMedioParcialConsu;
}

public void setTempoMedioParcialConsu(Double tempoMedioParcialConsu) {
	this.tempoMedioParcialConsu = tempoMedioParcialConsu;
}

public Double getTempoMedioParcialEnc() {
	return tempoMedioParcialEnc;
}

public void setTempoMedioParcialEnc(Double tempoMedioParcialEnc) {
	this.tempoMedioParcialEnc = tempoMedioParcialEnc;
}

public Double getTempoMedioParcialResp() {
	return tempoMedioParcialResp;
}

public void setTempoMedioParcialResp(Double tempoMedioParcialResp) {
	this.tempoMedioParcialResp = tempoMedioParcialResp;
}

public static long getSerialversionuid() {
	return serialVersionUID;
  }

  @Override
  public int compareTo(QuantidadeIteracoesDTO dto) {
	  return this.caixaPostal.compareTo(dto.getCaixaPostal());
  }
}
