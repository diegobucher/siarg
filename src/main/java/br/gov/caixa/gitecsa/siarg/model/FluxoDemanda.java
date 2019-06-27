/**
 * FluxoDemanda.java
 * Versão: 1.0.0.00
 * 04/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;

/**
 * Entidade da Tabela: argsm001.argtb16_fluxo_demanda.
 * @author f520296
 */
@Entity
@Table(name = "argtb16_fluxo_demanda", schema = "argsm001")
public class FluxoDemanda implements Serializable, Comparable<FluxoDemanda> {

  /** Serial. */
  private static final long serialVersionUID = -998343780754250068L;

  /** Campo utilizado para armazenar o id da tabela de fluxo_demanda. */
  private Long id;

  /** Campo utilizado para armazenar o id da tabela de caixa-postal. */
  private CaixaPostal caixaPostal;

  /** Campo utilizado para armazenar o id da tabela demanda. */
  private Demanda demanda;

  /** Campo utilizado para armazenar o prazo (em dias) da demanda. */
  private Integer prazo;

  /** Campo utilizado para armazenar o tipo do fluxo do assunto selecionado. */
  private TipoFluxoEnum tipoFluxoDemanda;

  /** Campo utilizado para armazenar a ordem do fluxo do assunto selecionado. */
  private Integer ordem;
  
  /** Campo utilizado para exclusão lógica da caixa do fluxo da demanda. */
  private boolean ativo;

  /** Construtor. */
  public FluxoDemanda() {
    super();
    this.ativo = true;
  }
  
  /**
   * Constrói uma instância de FluxoDemanda com base no FluxoAssunto dado.
   * 
   * @param entity
   *          Fluxo assunto
   */
  public FluxoDemanda(final FluxoAssunto entity) {
    this();
    this.id = null;
    this.ordem = entity.getOrdem();
    this.prazo = entity.getPrazo();
    this.caixaPostal = entity.getCaixaPostal();
    this.tipoFluxoDemanda = entity.getTipoFluxo();
  }
  
  /**
   * Constrói uma instância de FluxoDemanda com base na demanda e prazo dados. Esse construtor deve ser usado
   * exclusivamente para demandas com tipo de fluxo igual a 2 (demandante responsável).
   * 
   * @param entity
   *          Demanda
   * @param prazo
   *          Prazo de atendimento
   */
  public FluxoDemanda(final Demanda entity, final Integer prazo) {
    this();
    this.id = null;
    this.ordem = 1;
    this.prazo = prazo;
    this.demanda = entity;
    this.caixaPostal = entity.getCaixaPostalResponsavel();
    this.tipoFluxoDemanda = TipoFluxoEnum.DEMANDANTE_RESPONSAVEL;
  }

  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_fluxo_demanda", unique = true, nullable = false)
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
   * @return the caixaPostal
   */
  @ManyToOne(targetEntity = CaixaPostal.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_caixa_postal", nullable = false)
  public CaixaPostal getCaixaPostal() {
    return this.caixaPostal;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixaPostal the caixaPostal to set
   */
  public void setCaixaPostal(final CaixaPostal caixaPostal) {
    this.caixaPostal = caixaPostal;
  }

  /**
   * Obter o valor padrão.
   * @return the demanda
   */
  @ManyToOne(targetEntity = Demanda.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_demanda", nullable = false)
  public Demanda getDemanda() {
    return this.demanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demanda the demanda to set
   */
  public void setDemanda(Demanda demanda) {
    this.demanda = demanda;
  }

  /**
   * Obter o valor padrão.
   * @return the prazo
   */
  @Column(name = "pz_fluxo_demanda")
  public Integer getPrazo() {
    return this.prazo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param prazo the prazo to set
   */
  public void setPrazo(final Integer prazo) {
    this.prazo = prazo;
  }

  /**
   * Obter o valor padrão.
   * @return the tipoFluxoDemanda
   */
  @Column(name = "ic_tipo_fluxo_demanda", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = TipoFluxoEnum.NOME_ENUM) })
  public TipoFluxoEnum getTipoFluxoDemanda() {
    return this.tipoFluxoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param tipoFluxoDemanda the tipoFluxoDemanda to set
   */
  public void setTipoFluxoDemanda(final TipoFluxoEnum tipoFluxoDemanda) {
    this.tipoFluxoDemanda = tipoFluxoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the ordem
   */
  @Column(name = "nu_ordem")
  public Integer getOrdem() {
    return this.ordem;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param ordem the ordem to set
   */
  public void setOrdem(final Integer ordem) {
    this.ordem = ordem;
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
    if (!(obj instanceof FluxoDemanda)) {
      return false;
    }
    FluxoDemanda other = (FluxoDemanda) obj;
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
  public int compareTo(FluxoDemanda fluxoDemanda) {
    return this.ordem.compareTo(fluxoDemanda.ordem);
  }

  @Column(name = "ic_ativo", insertable=false)
  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

}
