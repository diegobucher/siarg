/**
 * FluxoAssunto.java
 * Versão: 1.0.0.00
 * 01/12/2017
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
import javax.persistence.Transient;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * Classe: Tabela utilizada para informar o fluxo da demanda.
 * @author f520296
 */
@Entity
@Table(name = "argtb10_fluxo_assunto", schema = "argsm001")
public class FluxoAssunto implements Serializable, Comparable<FluxoAssunto> {

  /** Serial. */
  private static final long serialVersionUID = -3425792430078931935L;

  /**
   * Campo utilizado para armazenar o id da tabela de fluxo.
   */
  private Long id;

  /**
   * Campo utilizado para armazenar o id da tabela de caixa-postal.
   */
  private CaixaPostal caixaPostal;

  /**
   * Campo utilizado para armazenar o id da tabela de assunto.
   */
  private Assunto assunto;

  /**
   * Campo utilizado para armazenar o prazo total do assunto que não tiver fluxo definido.
   */
  private Integer prazo;

  /**
   * Campo utilizado para armazenar o tipo do fluxo do assunto selecionado.
   */
  private TipoFluxoEnum tipoFluxo;

  /**
   * Campo utilizado para armazenar a ordem do fluxo do assunto selecionado.
   */
  private Integer ordem;
  
  private FluxoAssunto referenciaTransiente;

  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_fluxo_assunto", unique = true, nullable = false)
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
   * @return the assunto
   */
  @ManyToOne(targetEntity = Assunto.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_assunto", nullable = false)
  public Assunto getAssunto() {
    return this.assunto;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assunto the assunto to set
   */
  public void setAssunto(final Assunto assunto) {
    this.assunto = assunto;
  }

  /**
   * Obter o valor padrão.
   * @return the prazo
   */
  @Column(name = "pz_dias_fluxo_assunto")
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
   * @return the tipoFluxo
   */
  @Column(name = "ic_tipo_fluxo", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = TipoFluxoEnum.NOME_ENUM) })
  public TipoFluxoEnum getTipoFluxo() {
    return this.tipoFluxo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param tipoFluxo the tipoFluxo to set
   */
  public void setTipoFluxo(final TipoFluxoEnum tipoFluxo) {
    this.tipoFluxo = tipoFluxo;
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
    if (!(obj instanceof FluxoAssunto)) {
      return false;
    }
    FluxoAssunto other = (FluxoAssunto) obj;
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
  public int compareTo(FluxoAssunto fa) {
    return this.ordem.compareTo(fa.ordem);
  }

  @Transient
  public FluxoAssunto getReferenciaTransiente() {
    return referenciaTransiente;
  }

  public void setReferenciaTransiente(FluxoAssunto referenciaTransiente) {
    this.referenciaTransiente = referenciaTransiente;
  }
}
