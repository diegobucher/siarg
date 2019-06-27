/**
 * Assunto.java
 * Versão: 1.0.0.00
 * Data de Criação : 28-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;

/**
 * Entidade da Tabela: argsm001.argtb09_assunto.
 * @author f520296
 */
@Entity
@Table(name = "argtb09_assunto", schema = "argsm001")
public class Assunto implements Serializable, Comparable<Assunto> {

  /** Serial. */
  private static final long serialVersionUID = -9063602573317421413L;

  /** Campo utilizado para armazenar o id da tabela de assunto. */
  private Long id;

  /** Campo utilizado para armazenar o id da tabela de caixa-postal. */
  private CaixaPostal caixaPostal;

  /** Campo utilizado para armazenar o id do assunto pai onde o assunto filho está vinculado. */
  private Assunto assuntoPai;

  /** Campo utilizado para armazenar o nome do assunto. */
  private String nome;

  /** Campo utilizado para armazenar o prazo total do assunto que não tiver fluxo definido. */
  private Integer prazo;

  /**
   * Campo utilizado para armazenar o nível do usuário para inclusão de demanda com a situação igual
   * a aberta ou minuta, podendo ser:..
   */
  private Boolean permissaoAbertura;

  /** Campo utilizado para armazenar um texto prédefinido para o assunto. */
  private String textoPreDefinido;

  /** Campo utilizado para armazenar o status do assunto. */
  private Boolean ativo;

  /** Campo com a lista de assuntos filhos. */
  private List<Assunto> assuntosList;

  /** Campo com a lista de assuntos filhos. */
  private List<FluxoAssunto> fluxosAssuntosList;

  /** Lista de Caixas Postais e Assuntos. */
  /** Tabela utilizada para armazenar as Caixas-postais que irão visualizar o assunto com cópia. */
  private List<CaixaPostal> caixasPostaisObservadorList;

  /** Many to Many Demenadante: Assunto x Unidade. */
  private List<Unidade> assuntoUnidadeDemandanteList;
  
  /** Campo que armazena a abrangência da unidade. */
  private Abrangencia abrangencia;
  
  private Boolean flagAssunto;
  
  private Boolean excluido;

  private Boolean contrato;
  
  private String arvoreCompleta;

  /** Construtor Padrão. */
  public Assunto() {
    super();
  }

  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_assunto", unique = true, nullable = false)
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
   * @return the assuntoPai
   */
  @ManyToOne
  @JoinColumn(name = "nu_assunto_pai")
  public Assunto getAssuntoPai() {
    return this.assuntoPai;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assuntoPai the assuntoPai to set
   */
  public void setAssuntoPai(Assunto assuntoPai) {
    this.assuntoPai = assuntoPai;
  }

  /**
   * Obter o valor padrão.
   * @return the nome
   */
  @Column(name = "no_assunto", length = 200)
  public String getNome() {
    return this.nome;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param nome the nome to set
   */
  public void setNome(final String nome) {
    this.nome = nome;
  }

  /**
   * Obter o valor padrão.
   * @return the prazo
   */
  @Column(name = "pz_dias_assunto")
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
   * @return the permissaoAbertura
   */
  @Column(name = "ic_permissao_abertura")
  public Boolean getPermissaoAbertura() {
    return this.permissaoAbertura;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param permissaoAbertura the permissaoAbertura to set
   */
  public void setPermissaoAbertura(Boolean permissaoAbertura) {
    this.permissaoAbertura = permissaoAbertura;
  }

  /**
   * Obter o valor padrão.
   * @return the textoPreDefinido
   */
  @Column(name = "de_texto_predefinido")
  public String getTextoPreDefinido() {
    return this.textoPreDefinido;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param textoPreDefinido the textoPreDefinido to set
   */
  public void setTextoPreDefinido(final String textoPreDefinido) {
    this.textoPreDefinido = textoPreDefinido;
  }

  /**
   * Obter o valor padrão.
   * @return the ativo
   */
  @Column(name = "ic_ativo", nullable = false)
  public Boolean getAtivo() {
    return this.ativo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param ativo the ativo to set
   */
  public void setAtivo(final Boolean ativo) {
    this.ativo = ativo;
  }

  /**
   * Obter o valor padrão.
   * @return the assuntosList
   */
  @OneToMany(mappedBy = "assuntoPai")
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
  @OneToMany(mappedBy = "assunto", fetch = FetchType.LAZY, cascade= {CascadeType.ALL}, orphanRemoval=true)
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
  @ManyToMany(fetch = FetchType.LAZY, cascade= {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
  @JoinTable(schema = "argsm001", name = "argtb11_observador_assunto", joinColumns = @JoinColumn(name = "nu_assunto",
      referencedColumnName = "nu_assunto"), inverseJoinColumns = { @JoinColumn(name = "nu_caixa_postal",
      referencedColumnName = "nu_caixa_postal") })
  public List<CaixaPostal> getCaixasPostaisObservadorList() {
    return this.caixasPostaisObservadorList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixasPostaisList the caixasPostaisList to set
   */
  public void setCaixasPostaisObservadorList(List<CaixaPostal> caixasPostaisObservadorList) {
    this.caixasPostaisObservadorList = caixasPostaisObservadorList;
  }

  /**
   * Retorna o nome do assunto com o responsável caso seja o último nó.
   * @return Nome e caixa-postal responsável
   */
  @Transient
  public String getNomeEResponsavel() {
    if (Hibernate.isInitialized(this.assuntosList) && ObjectUtils.isNullOrEmpty(this.assuntosList)
        && Hibernate.isInitialized(this.assuntoPai) && !ObjectUtils.isNullOrEmpty(this.assuntoPai)
        && Hibernate.isInitialized(this.caixaPostal) && !ObjectUtils.isNullOrEmpty(this.caixaPostal)) {
      return String.format("%s (Responsável: %s)", this.nome, this.caixaPostal.getSigla());
    }

    return this.nome;
  }

  /**
   * Obter o valor padrão.
   * @return the caixasPostaisDemandanteList
   */
  @ManyToMany(fetch = FetchType.LAZY, cascade= {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH})
//  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(schema = "argsm001", name = "argtb12_demandante", inverseJoinColumns = @JoinColumn(name = "nu_unidade",
      referencedColumnName = "nu_unidade"), joinColumns = { @JoinColumn(name = "nu_assunto",
      referencedColumnName = "nu_assunto") })
  public List<Unidade> getAssuntoUnidadeDemandanteList() {
    return this.assuntoUnidadeDemandanteList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixasPostaisDemandanteList the caixasPostaisDemandanteList to set
   */
  public void setAssuntoUnidadeDemandanteList(List<Unidade> assuntoUnidadeDemandanteList) {
    this.assuntoUnidadeDemandanteList = assuntoUnidadeDemandanteList;
  }

  /**
   * Obter o valor do Atributo.
   * @return abrangencia
   */
  @ManyToOne(targetEntity = Abrangencia.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_abrangencia", nullable = false)
  public Abrangencia getAbrangencia() {
    return this.abrangencia;
  }

  /**
   * Gravar o valor do Atributo.
   * @param abrangencia the abrangencia to set
   */
  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  @Column(name = "ic_assunto")
  public Boolean getFlagAssunto() {
    return flagAssunto;
  }

  public void setFlagAssunto(Boolean flagAssunto) {
    this.flagAssunto = flagAssunto;
  }

  @Column(name="ic_exclusao")
  public Boolean getExcluido() {
    return excluido;
  }

  public void setExcluido(Boolean excluido) {
    this.excluido = excluido;
  }
  
  /**
   * @return the contrato
   */
  @Column(name="ic_numero_contrato")
  public Boolean getContrato() {
    return contrato;
  }

  /**
   * @param contrato the contrato to set
   */
  public void setContrato(Boolean contrato) {
    this.contrato = contrato;
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
    if (!(obj instanceof Assunto)) {
      return false;
    }
    Assunto other = (Assunto) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Assunto assunto) {
    return this.id.compareTo(assunto.getId());
  }
  
  @Transient
  public String getArvoreCompleta() {
    return arvoreCompleta;
  }

  public void setArvoreCompleta(String arvoreCompleta) {
    this.arvoreCompleta = arvoreCompleta;
  }

}
