package br.gov.caixa.gitecsa.siarg.ws.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.gov.caixa.gitecsa.siarg.ws.serializer.CustomDateTimeSerializer;

public class AtendimentoDTO {

  @JsonProperty("co_historico")
  private Long numeroAtendimento;

  @JsonProperty("co_acao")
  private Integer numeroAcao;
  
  @JsonProperty("descricao_acao")
  private String descricaoAcao;
  
  @JsonProperty("co_usuario")
  private String matriculaUsuario;
  
  @JsonProperty("nome_usuario")
  private String nomeUsuario;

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonProperty("dt_historico")
  private Date dataHistorico;
  
  @JsonProperty("cp_unidade_origem")
  private String siglaCaixaOrigem;

  @JsonProperty("cp_unidade_destino")
  private String siglaCaixaDestino;
  
//  @JsonIgnore
//  private Date dataHoraAtendimento;
  
  public Long getNumeroAtendimento() {
    return numeroAtendimento;
  }

  public void setNumeroAtendimento(Long numeroAtendimento) {
    this.numeroAtendimento = numeroAtendimento;
  }


  public String getDescricaoAcao() {
    return descricaoAcao;
  }

  public void setDescricaoAcao(String descricaoAcao) {
    this.descricaoAcao = descricaoAcao;
  }

  public String getMatriculaUsuario() {
    return matriculaUsuario;
  }

  public void setMatriculaUsuario(String matriculaUsuario) {
    this.matriculaUsuario = matriculaUsuario;
  }

  public Date getDataHistorico() {
    return dataHistorico;
  }

  public void setDataHistorico(Date dataHistorico) {
    this.dataHistorico = dataHistorico;
  }

  public String getSiglaCaixaOrigem() {
    return siglaCaixaOrigem;
  }

  public void setSiglaCaixaOrigem(String siglaCaixaOrigem) {
    this.siglaCaixaOrigem = siglaCaixaOrigem;
  }

  public String getSiglaCaixaDestino() {
    return siglaCaixaDestino;
  }

  public void setSiglaCaixaDestino(String siglaCaixaDestino) {
    this.siglaCaixaDestino = siglaCaixaDestino;
  }

//  public Date getDataHoraAtendimento() {
//    return dataHoraAtendimento;
//  }
//
//  public void setDataHoraAtendimento(Date dataHoraAtendimento) {
//    this.dataHoraAtendimento = dataHoraAtendimento;
//  }

  public Integer getNumeroAcao() {
    return numeroAcao;
  }

  public void setNumeroAcao(Integer numeroAcao) {
    this.numeroAcao = numeroAcao;
  }

  public String getNomeUsuario() {
    return nomeUsuario;
  }

  public void setNomeUsuario(String nomeUsuario) {
    this.nomeUsuario = nomeUsuario;
  }

}