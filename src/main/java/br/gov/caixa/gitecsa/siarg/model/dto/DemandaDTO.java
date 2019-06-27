/**
 * DemandaDTO.java
 * Versão: 1.0.0.00
 * 01/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.DemandaCampo;

/**
 * Classe: DTO para Melhor Performance da tela de carregamento de demanda.
 * @author f520296
 */
public class DemandaDTO implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 2998809254457901671L;

  /** Atributos padrões do DTO. */
  private Long numero;

  /** Atributos padrões do DTO. */
  private Long idDemandaPai;

  /** Atributos padrões do DTO. */
  private Integer prazoDemanda;

  /** Atributos padrões do DTO. */
  private Integer prazoCaixaPostal;

  /** Atributos padrões do DTO. */
  private Boolean flagDemandaPai;

  /** Atributos padrões do DTO. */
  private Boolean flagDemandaFilha;

  /** Atributos padrões do DTO. */
  private Boolean flagRascunho;

  /** Atributos padrões do DTO. */
  private Boolean flagConsulta;

  /** Atributos padrões do DTO. */
  private String demandante;

  /** Atributos padrões do DTO. */
  private String matricula;

  /** Atributos padrões do DTO. */
  private String assunto;

  /** Atributos padrões do DTO. */
  private String titulo;

  /** Atributos padrões do DTO. */
  private String responsavelAtual;

  /** Atributos padrões do DTO. */
  private String colorStatus;

  /** Atributos padrões do DTO. */
  private SituacaoEnum situacao;

  /** Atributos padrões do DTO. */
  private Date dataAberturaDemanda;

  /** Atributos padrões do DTO. */
  private Boolean externa;

  /** Atributos padrões do DTO. */
  private String listaContratosString;
  
  private List<DemandaCampo> camposList;

  /** Construtor Padrão. */
  public DemandaDTO() {
    super();
  }

  public String getStyleColorStatus() {
    return "background-color:" + this.colorStatus;
  }

  /**
   * Obter o valor padrão.
   * @return the numero
   */
  public Long getNumero() {
    return this.numero;
  }
  
  public String getNumeroDemandaFormatado() {
	  
	  StringBuilder sb = new StringBuilder();
	  if(Boolean.TRUE.equals(flagDemandaFilha)){
		  sb.append(this.idDemandaPai);
		  sb.append("/");
	  }
	  sb.append(this.numero);
	  
	  return sb.toString();
  }

  /**
   * Gravar o valor do parâmetro.
   * @param numero the numero to set
   */
  public void setNumero(final Long numero) {
    this.numero = numero;
  }

  /**
   * Obter o valor padrão.
   * @return the situacao
   */
  public SituacaoEnum getSituacao() {
    return this.situacao;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param situacao the situacao to set
   */
  public void setSituacao(final SituacaoEnum situacao) {
    this.situacao = situacao;
  }

  /**
   * Obter o valor padrão.
   * @return the prazoDemanda
   */
  public Integer getPrazoDemanda() {
    return this.prazoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param prazoDemanda the prazoDemanda to set
   */
  public void setPrazoDemanda(final Integer prazoDemanda) {
    this.prazoDemanda = prazoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the dataAberturaDemanda
   */
  public Date getDataAberturaDemanda() {
    return this.dataAberturaDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataAberturaDemanda the dataAberturaDemanda to set
   */
  public void setDataAberturaDemanda(final Date dataAberturaDemanda) {
    this.dataAberturaDemanda = dataAberturaDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the demandante
   */
  public String getDemandante() {
    return this.demandante;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandante the demandante to set
   */
  public void setDemandante(final String demandante) {
    this.demandante = demandante;
  }

  /**
   * Obter o valor padrão.
   * @return the matricula
   */
  public String getMatricula() {
    return this.matricula;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param matricula the matricula to set
   */
  public void setMatricula(final String matricula) {
    this.matricula = matricula;
  }

  /**
   * Obter o valor padrão.
   * @return the assunto
   */
  public String getAssunto() {
    return this.assunto;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assunto the assunto to set
   */
  public void setAssunto(final String assunto) {
    this.assunto = assunto;
  }

  /**
   * Obter o valor padrão.
   * @return the titulo
   */
  public String getTitulo() {
    return this.titulo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param titulo the titulo to set
   */
  public void setTitulo(final String titulo) {
    this.titulo = titulo;
  }

  /**
   * Obter o valor padrão.
   * @return the responsavelAtual
   */
  public String getResponsavelAtual() {
    return this.responsavelAtual;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param responsavelAtual the responsavelAtual to set
   */
  public void setResponsavelAtual(final String responsavelAtual) {
    this.responsavelAtual = responsavelAtual;
  }

  /**
   * Obter o valor padrão.
   * @return the prazoCaixaPostal
   */
  public Integer getPrazoCaixaPostal() {
    return this.prazoCaixaPostal;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param prazoCaixaPostal the prazoCaixaPostal to set
   */
  public void setPrazoCaixaPostal(final Integer prazoCaixaPostal) {
    this.prazoCaixaPostal = prazoCaixaPostal;
  }

  /**
   * Obter o valor padrão.
   * @return the colorStatus
   */
  public String getColorStatus() {
    return this.colorStatus;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param colorStatus the colorStatus to set
   */
  public void setColorStatus(final String colorStatus) {
    this.colorStatus = colorStatus;
  }

  /**
   * Obter o valor padrão.
   * @return the idDemandaPai
   */
  public Long getIdDemandaPai() {
    return this.idDemandaPai;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param idDemandaPai the idDemandaPai to set
   */
  public void setIdDemandaPai(final Long idDemandaPai) {
    this.idDemandaPai = idDemandaPai;
  }

  /**
   * Obter o valor padrão.
   * @return the flagDemandaPai
   */
  public Boolean getFlagDemandaPai() {
    return this.flagDemandaPai;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param flagDemandaPai the flagDemandaPai to set
   */
  public void setFlagDemandaPai(final Boolean flagDemandaPai) {
    this.flagDemandaPai = flagDemandaPai;
  }

  /**
   * Obter o valor padrão.
   * @return the flagRascunho
   */
  public Boolean getFlagRascunho() {
    return this.flagRascunho;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param flagRascunho the flagRascunho to set
   */
  public void setFlagRascunho(final Boolean flagRascunho) {
    this.flagRascunho = flagRascunho;
  }

  /**
   * Obter o valor padrão.
   * @return the flagConsulta
   */
  public Boolean getFlagConsulta() {
    return this.flagConsulta;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param flagConsulta the flagConsulta to set
   */
  public void setFlagConsulta(final Boolean flagConsulta) {
    this.flagConsulta = flagConsulta;
  }

  /**
   * Obter o valor padrão.
   * @return the flagDemandaFilha
   */
  public Boolean getFlagDemandaFilha() {
    return this.flagDemandaFilha;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param flagDemandaFilha the flagDemandaFilha to set
   */
  public void setFlagDemandaFilha(final Boolean flagDemandaFilha) {
    this.flagDemandaFilha = flagDemandaFilha;
  }

  /**
   * Obter o valor padrão.
   * @return the externa
   */
  public Boolean getExterna() {
    return this.externa;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param externa the externa to set
   */
  public void setExterna(final Boolean externa) {
    this.externa = externa;
  }
  
  /**
   * @return the listaContratosString
   */
  public String getListaContratosString() {
    return listaContratosString;
  }

  /**
   * @param listaContratosString the listaContratosString to set
   */
  public void setListaContratosString(String listaContratosString) {
    this.listaContratosString = listaContratosString;
  }
  
  public List<DemandaCampo> getCamposList() {
		return camposList;
  }
  
  public void setCamposList(List<DemandaCampo> camposList) {
		this.camposList = camposList;
   }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.numero == null) ? 0 : this.numero.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DemandaDTO)) {
      return false;
    }
    DemandaDTO other = (DemandaDTO) obj;
    if (this.numero == null) {
      if (other.numero != null) {
        return false;
      }
    } else if (!this.numero.equals(other.numero)) {
      return false;
    }
    return true;
  }

}
