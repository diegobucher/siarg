/**
 * Atendimento.java
 * Versão: 1.0.0.00
 * 07/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.MotivoReaberturaEnum;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * Entidade da Tabela: argsm001.argtb15_atendimento. Tabela utilizada para armazenar os atendimento
 * da demanda.
 * @author f520296
 */

@Entity
@Table(name = "argtb15_atendimento", schema = "argsm001")
public class Atendimento implements Serializable, Comparable<Atendimento> {

  /** Serial. */
  private static final long serialVersionUID = 6670243077046284996L;

  /** Campo utilizado para armazenar o Id da tabela atendimento. */
  private Long id;

  /** Campo utilizado para armazenar o id da tabela demanda. */
  private Demanda demanda;

  /** Campo utilizado para armazenar o id da tabela de fluxo_demanda. */
  private FluxoDemanda fluxoDemanda;

  /** campo utilizado para armazenar a matrícula do usuário. */
  private String matricula;

  /** Campo utilizado para armazenar a descrição do atendimento. */
  private String descricao;

  /** Campo utilizado para armazenar o nome do arquivo que foi anexado na demanda. */
  private String anexoAtendimento;

  /** Campo utilizado para armazenar a data em que o atendimento chegou na referida caixa-postal. */
  private Date dataHoraRecebimento;

  /** Campo utilizado para armazenar a Data em que o atendimento foi realizado. */
  private Date dataHoraAtendimento;

  /** Campo utilizado para armazenar o id da tabela unidade. */
  private Unidade unidadeExterna;

  /**
   * Campo utilizado para armazenar a acao do atendimento.
   */
  private AcaoAtendimentoEnum acaoEnum;
  
  /**
   * Campo utilizado para armazenar o motivo de reabertura.
   */
  private MotivoReaberturaEnum motivoReabertura;
  
  private String nomeUsuarioAtendimento;


  /**
   * Construtor Padrão.
   */
  public Atendimento() {
    super();
  }

  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_atendimento", unique = true, nullable = false)
  public Long getId() {
    return this.id;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * Obter o valor padrão.
   * @return the demanda
   */
  @ManyToOne
  @JoinColumn(name = "nu_demanda")
  public Demanda getDemanda() {
    return this.demanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demanda the demanda to set
   */
  public void setDemanda(final Demanda demanda) {
    this.demanda = demanda;
  }

  /**
   * Obter o valor padrão.
   * @return the fluxoDemanda
   */
  @ManyToOne
  @JoinColumn(name = "nu_fluxo_demanda")
  public FluxoDemanda getFluxoDemanda() {
    return this.fluxoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param fluxoDemanda the fluxoDemanda to set
   */
  public void setFluxoDemanda(final FluxoDemanda fluxoDemanda) {
    this.fluxoDemanda = fluxoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the matricula
   */
  @Column(name = "co_matricula", nullable = false, length = 7, columnDefinition = "bpchar")
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
   * @return the descricao
   */
  @Column(name = "de_atendimento")
  public String getDescricao() {
    return this.descricao;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param descricao the descricao to set
   */
  public void setDescricao(final String descricao) {
    this.descricao = descricao;
  }

  /**
   * Obter o valor padrão.
   * @return the anexoAtendimento
   */
  @Column(name = "no_anexo_atendimento")
  public String getAnexoAtendimento() {
    return this.anexoAtendimento;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param anexoAtendimento the anexoAtendimento to set
   */
  public void setAnexoAtendimento(final String anexoAtendimento) {
    this.anexoAtendimento = anexoAtendimento;
  }

  /**
   * Obter o valor padrão.
   * @return the dataHoraRecebimento
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_recebimento")
  public Date getDataHoraRecebimento() {
    return this.dataHoraRecebimento;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataHoraRecebimento the dataHoraRecebimento to set
   */
  public void setDataHoraRecebimento(final Date dataHoraRecebimento) {
    this.dataHoraRecebimento = dataHoraRecebimento;
  }

  /**
   * Obter o valor padrão.
   * @return the dataHoraAtendimento
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_atendimento")
  public Date getDataHoraAtendimento() {
    return this.dataHoraAtendimento;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataHoraAtendimento the dataHoraAtendimento to set
   */
  public void setDataHoraAtendimento(final Date dataHoraAtendimento) {
    this.dataHoraAtendimento = dataHoraAtendimento;
  }

  /**
   * Obtém a unidade externa.
   * @return Unidade
   */
  @ManyToOne
  @JoinColumn(name = "nu_unidade_externa")
  public Unidade getUnidadeExterna() {
    return this.unidadeExterna;
  }

  /**
   * Seta a unidade externa.
   * @param unidadeExterna
   */
  public void setUnidadeExterna(Unidade unidadeExterna) {
    this.unidadeExterna = unidadeExterna;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
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
    if (!(obj instanceof Atendimento)) {
      return false;
    }
    Atendimento other = (Atendimento) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Atendimento atendimento) {
    return this.id.compareTo(atendimento.id);
  }

  @Column(name = "nu_acao", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = AcaoAtendimentoEnum.NOME_ENUM) })
  public AcaoAtendimentoEnum getAcaoEnum() {
    return this.acaoEnum;
  }

  public void setAcaoEnum(AcaoAtendimentoEnum acaoEnum) {
    this.acaoEnum = acaoEnum;
  }

  @Column(name = "nu_motivo", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = MotivoReaberturaEnum.NOME_ENUM) })
  public MotivoReaberturaEnum getMotivoReabertura() {
    return motivoReabertura;
  }

  public void setMotivoReabertura(MotivoReaberturaEnum motivoReabertura) {
    this.motivoReabertura = motivoReabertura;
  }
  
  @Column(name="no_usuario_atendimento")
  public String getNomeUsuarioAtendimento() {
    return nomeUsuarioAtendimento;
  }

  public void setNomeUsuarioAtendimento(String nomeUsuarioAtendimento) {
    this.nomeUsuarioAtendimento = nomeUsuarioAtendimento;
  }

}
