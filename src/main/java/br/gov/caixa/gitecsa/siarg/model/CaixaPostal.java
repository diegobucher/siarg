/**
 * CaixaPostal.java
 * Versão: 1.0.0.00
 * Data de Criação : 22-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: argsm001.argtb06_caixa_postal.
 * @author f520296
 */
@Entity
@Table(name = "argtb08_caixa_postal", schema = "argsm001")
public class CaixaPostal implements Serializable, Comparable<CaixaPostal> {

  /** Serial. */
  private static final long serialVersionUID = -449979042956568506L;

  /** Identificador da Caixa Postal. Chave primária. */
  private Long id;

  /** Campo que armazena a unidade da Caixa postal. */
  private Unidade unidade;

  /** Campo que armazena a sigla da Caixa postal. */
  private String sigla;

  /** Campo que armazena o email da Caixa postal. */
  private String email;

  /** Campo que armazena o status de recebimento de email da Caixa postal. */
  private Boolean recebeEmail;

  /** Campo que armazena o status da Caixa postal. */
  private Boolean ativo;

  /** Campo que armazena a lista de assuntos para essa caixa postal. */
  private List<Assunto> assuntosList;

  /** Campo que armazena a lista de assuntos para essa caixa postal. */
  private List<FluxoAssunto> fluxosAssuntosList;

  /** Lista de Caixas Postais e Assuntos. */
  /** Tabela utilizada para armazenar as Caixas-postais que irão visualizar o assunto com cópia. */
  private List<Assunto> assuntosObservadosList;

  /** Lista de Caixas Postais e Demandas. */
  /** Tabela utilizada para armazenar as Caixas-postais que irão visualizar a demanda. */
  private List<Demanda> demandasObservadasList;

  /** Campo que armazena a lista de assuntos para essa caixa postal. */
  private List<FluxoDemanda> fluxosDemandasList;

  /** Transiente */
  private Long totalDemandasDaCaixaPostal;
  
  private CaixaPostal referenciaTransiente;

  /**
   * Construtor Padrão sem argumentos.
   */
  public CaixaPostal() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_caixa_postal", unique = true, nullable = false)
  public Long getId() {
    return this.id;
  }

  /**
   * Gravar o valor do Atributo.
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * Obter o valor do Atributo.
   * @return unidade
   */

  @ManyToOne(targetEntity = Unidade.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_unidade", nullable = false)
  public Unidade getUnidade() {
    return this.unidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidade the unidade to set
   */
  public void setUnidade(final Unidade unidade) {
    this.unidade = unidade;
  }

  /**
   * Obter o valor do Atributo.
   * @return sigla
   */
  @Column(name = "sg_caixa_postal", nullable = false, length = 20)
  public String getSigla() {
    return this.sigla;
  }

  /**
   * Gravar o valor do Atributo.
   * @param sigla the sigla to set
   */
  public void setSigla(final String sigla) {
    this.sigla = sigla;
  }

  /**
   * Obter o valor do Atributo.
   * @return email
   */
  @Column(name = "no_email", nullable = false, length = 200)
  public String getEmail() {
    return this.email;
  }

  /**
   * Gravar o valor do Atributo.
   * @param email the email to set
   */
  public void setEmail(final String email) {
    this.email = email;
  }

  /**
   * Obter o valor do Atributo.
   * @return recebeEmail
   */
  @Column(name = "ic_recebe_email", nullable = false)
  public Boolean getRecebeEmail() {
    return this.recebeEmail;
  }

  /**
   * Gravar o valor do Atributo.
   * @param recebeEmail the recebeEmail to set
   */
  public void setRecebeEmail(final Boolean recebeEmail) {
    this.recebeEmail = recebeEmail;
  }

  /**
   * Obter o valor do Atributo.
   * @return ativo
   */
  @Column(name = "ic_ativo", nullable = false)
  public Boolean isAtivo() {
    return this.ativo;
  }

  /**
   * Gravar o valor do Atributo.
   * @param ativo the ativo to set
   */
  public void setAtivo(final Boolean ativo) {
    this.ativo = ativo;
  }

  /**
   * Obter o valor padrão.
   * @return the assuntosList
   */
  @OneToMany(mappedBy = "caixaPostal", fetch = FetchType.LAZY)
  public List<Assunto> getAssuntosList() {
    return this.assuntosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assuntosList the assuntosList to set
   */
  public void setAssuntosList(final List<Assunto> assuntosList) {
    this.assuntosList = assuntosList;
  }

  /**
   * Obter o valor padrão.
   * @return the fluxosList
   */
  @OneToMany(mappedBy = "caixaPostal", fetch = FetchType.LAZY)
  public List<FluxoAssunto> getFluxosAssuntosList() {
    return this.fluxosAssuntosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param fluxosList the fluxosList to set
   */
  public void setFluxosAssuntosList(final List<FluxoAssunto> fluxosAssuntosList) {
    this.fluxosAssuntosList = fluxosAssuntosList;
  }

  /**
   * Obter o valor padrão.
   * @return the caixasPostaisList
   */
  @ManyToMany(targetEntity = Assunto.class, mappedBy = "caixasPostaisObservadorList")
  public List<Assunto> getAssuntosObservadosList() {
    return this.assuntosObservadosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixasPostaisList the caixasPostaisList to set
   */
  public void setAssuntosObservadosList(List<Assunto> assuntosObservadosList) {
    this.assuntosObservadosList = assuntosObservadosList;
  }

  /**
   * Obter o valor padrão.
   * @return the fluxosDemandasList
   */
  @OneToMany(mappedBy = "caixaPostal", fetch = FetchType.LAZY)
  public List<FluxoDemanda> getFluxosDemandasList() {
    return this.fluxosDemandasList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param fluxosDemandasList the fluxosDemandasList to set
   */
  public void setFluxosDemandasList(List<FluxoDemanda> fluxosDemandasList) {
    this.fluxosDemandasList = fluxosDemandasList;
  }

  /**
   * Obter o valor padrão.
   * @return the totalDemandasDaCaixaPostal
   */
  @Transient
  public Long getTotalDemandasDaCaixaPostal() {
    return this.totalDemandasDaCaixaPostal;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param totalDemandasDaCaixaPostal the totalDemandasDaCaixaPostal to set
   */
  public void setTotalDemandasDaCaixaPostal(Long totalDemandasDaCaixaPostal) {
    this.totalDemandasDaCaixaPostal = totalDemandasDaCaixaPostal;
  }

  /**
   * Obter o valor padrão.
   * @return the demandasObservadasList
   */
  @ManyToMany(targetEntity = Demanda.class, mappedBy = "caixasPostaisObservadorList")
  public List<Demanda> getDemandasObservadasList() {
    return this.demandasObservadasList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandasObservadasList the demandasObservadasList to set
   */
  public void setDemandasObservadasList(List<Demanda> demandasObservadasList) {
    this.demandasObservadasList = demandasObservadasList;
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
    if (!(obj instanceof CaixaPostal)) {
      return false;
    }
    CaixaPostal other = (CaixaPostal) obj;
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
  public int compareTo(final CaixaPostal caixaPostal) {
    return this.sigla.compareToIgnoreCase(caixaPostal.getSigla());
  }

  @Transient
  public CaixaPostal getReferenciaTransiente() {
    return referenciaTransiente;
  }

  public void setReferenciaTransiente(CaixaPostal referenciaTransiente) {
    this.referenciaTransiente = referenciaTransiente;
  }

}
