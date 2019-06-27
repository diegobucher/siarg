package br.gov.caixa.gitecsa.siarg.ws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.gov.caixa.gitecsa.siarg.ws.serializer.CustomDateTimeSerializer;

public class DemandaAbertaDTO {
  
  @JsonProperty("co_demanda")
  private Long numeroDemanda;
  
  @JsonProperty("co_assunto")
  private Long codigoAssunto;

  @JsonProperty("assunto")
  private String arvoreAssunto;

  @JsonProperty("nome_assunto_principal")
  private String nomeAssunto;
  
  @JsonProperty("codigo_unidade")
  private Integer codigoUnidadePrimeiroFluxo;
  
  @JsonProperty("codigo_unidade_demandante")
  private Integer cgcUnidadeDemandante;
  
  @JsonProperty("cp_unidade_demandante")
  private String siglaCaixaDemandante;
  
  @JsonProperty("cp_unidade_responsavel")
  private String siglaCaixaResponsavel;

  @JsonProperty("codigo_unidade_responsavel_assunto")
  private Integer cgcUnidadeResponsavel;
  
  @JsonProperty("titulo")
  private String titulo;
  
  @JsonProperty("conteudo")
  private String conteudo;

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonProperty("dt_demanda")
  private Date dataAbertura;
  
  @JsonProperty("co_situacao")
  private Integer coSituacao;
  
  @JsonProperty("no_situacao")
  private String nomeSituacao;
  
  @JsonProperty("prazo_demanda")
  private Integer prazoDemanda;

  @JsonProperty("prazo_unid_destino")
  private Integer prazoUnidadeAtual;
  
  @JsonProperty("prazo_restante")
  private Integer prazoRestante;
  
  @JsonProperty("demanda_externa")
  private Integer demandaNaExterna;
  
  @JsonProperty("demanda_tipo_consulta")
  private Integer demandaTipoConsulta;
  
  @JsonProperty("demanda_reaberta")
  private Integer demandaReaberta;

  @JsonProperty("numero_contratos")
  private List<String> numeroContratoList;

  @JsonProperty("atendimentos")
  private List<AtendimentoDTO> atendimentoList;
  
  public DemandaAbertaDTO() {
    atendimentoList = new ArrayList<>();
    numeroContratoList = new ArrayList<>();
  }
  
  public Long getNumeroDemanda() {
    return numeroDemanda;
  }

  public void setNumeroDemanda(Long numeroDemanda) {
    this.numeroDemanda = numeroDemanda;
  }

  public String getNomeAssunto() {
    return nomeAssunto;
  }

  public void setNomeAssunto(String nomeAssunto) {
    this.nomeAssunto = nomeAssunto;
  }

  public String getSiglaCaixaDemandante() {
    return siglaCaixaDemandante;
  }

  public void setSiglaCaixaDemandante(String siglaCaixaDemandante) {
    this.siglaCaixaDemandante = siglaCaixaDemandante;
  }

  public String getSiglaCaixaResponsavel() {
    return siglaCaixaResponsavel;
  }

  public void setSiglaCaixaResponsavel(String siglaCaixaResponsavel) {
    this.siglaCaixaResponsavel = siglaCaixaResponsavel;
  }

  public Date getDataAbertura() {
    return dataAbertura;
  }

  public void setDataAbertura(Date dataAbertura) {
    this.dataAbertura = dataAbertura;
  }

  public String getNomeSituacao() {
    return nomeSituacao;
  }

  public void setNomeSituacao(String nomeSituacao) {
    this.nomeSituacao = nomeSituacao;
  }

  public Integer getPrazoDemanda() {
    return prazoDemanda;
  }

  public void setPrazoDemanda(Integer prazoDemanda) {
    this.prazoDemanda = prazoDemanda;
  }

  public Integer getPrazoUnidadeAtual() {
    return prazoUnidadeAtual;
  }

  public void setPrazoUnidadeAtual(Integer prazoUnidadeAtual) {
    this.prazoUnidadeAtual = prazoUnidadeAtual;
  }

  public Integer getPrazoRestante() {
    return prazoRestante;
  }

  public void setPrazoRestante(Integer prazoRestante) {
    this.prazoRestante = prazoRestante;
  }

  public List<AtendimentoDTO> getAtendimentoList() {
    return atendimentoList;
  }

  public void setAtendimentoList(List<AtendimentoDTO> atendimentoList) {
    this.atendimentoList = atendimentoList;
  }

  public Integer getCoSituacao() {
    return coSituacao;
  }

  public void setCoSituacao(Integer coSituacao) {
    this.coSituacao = coSituacao;
  }

  public List<String> getNumeroContratoList() {
    return numeroContratoList;
  }

  public void setNumeroContratoList(List<String> numeroContratoList) {
    this.numeroContratoList = numeroContratoList;
  }

  public Long getCodigoAssunto() {
    return codigoAssunto;
  }

  public void setCodigoAssunto(Long codigoAssunto) {
    this.codigoAssunto = codigoAssunto;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getConteudo() {
    return conteudo;
  }

  public void setConteudo(String conteudo) {
    this.conteudo = conteudo;
  }

  public String getArvoreAssunto() {
    return arvoreAssunto;
  }

  public void setArvoreAssunto(String arvoreAssunto) {
    this.arvoreAssunto = arvoreAssunto;
  }

  public Integer getCgcUnidadeDemandante() {
    return cgcUnidadeDemandante;
  }

  public void setCgcUnidadeDemandante(Integer cgcUnidadeDemandante) {
    this.cgcUnidadeDemandante = cgcUnidadeDemandante;
  }

  public Integer getCgcUnidadeResponsavel() {
    return cgcUnidadeResponsavel;
  }

  public void setCgcUnidadeResponsavel(Integer cgcUnidadeResponsavel) {
    this.cgcUnidadeResponsavel = cgcUnidadeResponsavel;
  }

  public Integer getCodigoUnidadePrimeiroFluxo() {
    return codigoUnidadePrimeiroFluxo;
  }

  public void setCodigoUnidadePrimeiroFluxo(Integer codigoUnidadePrimeiroFluxo) {
    this.codigoUnidadePrimeiroFluxo = codigoUnidadePrimeiroFluxo;
  }

  public Integer getDemandaNaExterna() {
    return demandaNaExterna;
  }

  public void setDemandaNaExterna(Integer demandaNaExterna) {
    this.demandaNaExterna = demandaNaExterna;
  }

  public Integer getDemandaTipoConsulta() {
    return demandaTipoConsulta;
  }

  public void setDemandaTipoConsulta(Integer demandaTipoConsulta) {
    this.demandaTipoConsulta = demandaTipoConsulta;
  }

  public Integer getDemandaReaberta() {
    return demandaReaberta;
  }

  public void setDemandaReaberta(Integer demandaReaberta) {
    this.demandaReaberta = demandaReaberta;
  }

}
