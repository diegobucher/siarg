/**
 * Unidade.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;

/**
 * Entity implementation class for Entity: argsm001.argtb01_unidade.
 * @author f520296
 */
@Entity
@Table(name = "argtb02_unidade", schema = "argsm001")
public class Unidade implements Serializable, Comparable<Unidade> {

  /** Serial. */
  private static final long serialVersionUID = -996277596358105284L;

  /** Identificador da unidade. Chave primária. */
  private Long id;

  /** Campo que armazena o CGC da unidade. */
  private Integer cgcUnidade;

  /** Campo que armazena o nome da unidade. */
  private String nome;

  /** Campo que armazena a sigla da unidade. */
  private String sigla;

  /** Campo que armazena o tipo da unidade. */
  private TipoUnidadeEnum tipoUnidade;

  /** Campo que armazena o status da unidade. Ativa - 1. Inativa - 0. */
  private Boolean ativo;

  /** Lista de Caixas postais. */
  private List<CaixaPostal> caixasPostaisList;

  /** Lista de Caixas postais. */
  private List<UsuarioUnidade> usuarioUnidadeList;

  /** Campo que armazena a abrangência da unidade. */
  private Abrangencia abrangencia;

  /** Many to Many Demenadante: Assunto x Unidade. */
  private List<Assunto> assuntoUnidadeDemandanteList;

  /** Many to Many Demenadante: Assunto x Unidade. */
  private List<Unidade> unidadesSubordinadasList;
  
  /** Many to one Unidade. */
  private Unidade unidadeSubordinacao;
  
  private Boolean isRelConsolidadoAssunto;

  /**
   * Obter o valor do Atributo.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_unidade", unique = true, nullable = false)
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
   * @return the cgcUnidade
   */
  @Column(name = "nu_cgc_unidade", nullable = false)
  public Integer getCgcUnidade() {
    return this.cgcUnidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param cgcUnidade the cgcUnidade to set
   */
  public void setCgcUnidade(final Integer cgcUnidade) {
    this.cgcUnidade = cgcUnidade;
  }

  /**
   * Obter o valor do Atributo.
   * @return the nome
   */
  @Column(name = "no_unidade", nullable = false, length = 70)
  public String getNome() {
    return this.nome;
  }

  /**
   * Gravar o valor do Atributo.
   * @param nome the nome to set
   */
  public void setNome(final String nome) {
    this.nome = nome;
  }

  /**
   * Obter o valor do Atributo.
   * @return the sigla
   */
  @Column(name = "sg_unidade", nullable = false, length = 50)
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
   * @return the tipoUnidade
   */
  @Column(name = "ic_tipo_unidade", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = TipoUnidadeEnum.NOME_ENUM) })
  public TipoUnidadeEnum getTipoUnidade() {
    return this.tipoUnidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param tipoUnidade the tipoUnidade to set
   */
  public void setTipoUnidade(TipoUnidadeEnum tipoUnidade) {
    this.tipoUnidade = tipoUnidade;
  }

  /**
   * Obter o valor padrão.
   * @return the ativo
   */
  @Column(name = "ic_ativo")
  public Boolean getAtivo() {
    return this.ativo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param ativo the ativo to set
   */
  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }

  /**
   * @Column(name = "ic_ativo", nullable = false) Obter o valor do Atributo.
   * @return caixasPostais
   */
  @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY, cascade= {CascadeType.PERSIST, CascadeType.MERGE})
  public List<CaixaPostal> getCaixasPostaisList() {
    return this.caixasPostaisList;
  }

  @Transient
  public List<CaixaPostal> getCaixasPostaisAtivasList() {
    
    List<CaixaPostal> caixasAtivas = new ArrayList<CaixaPostal>();
    if(this.caixasPostaisList != null) {
      for (CaixaPostal caixa : this.caixasPostaisList) {
        if(Boolean.TRUE.equals(caixa.isAtivo())) {
          caixasAtivas.add(caixa);
        }
      }
    }
    return caixasAtivas;
  }

  /**
   * Gravar o valor do Atributo.
   * @param caixasPostais the caixasPostais to set
   */
  public void setCaixasPostaisList(final List<CaixaPostal> caixasPostaisList) {
    this.caixasPostaisList = caixasPostaisList;
  }

  /**
   * Adicionar Uma Caixa Postal para essa unidade.
   * @param caixaPostal caixa postal
   * @return caixasPostais
   */
  public List<CaixaPostal> addCaixaPostal(final CaixaPostal caixaPostal) {
    this.caixasPostaisList.add(caixaPostal);
    caixaPostal.setUnidade(this);
    return this.caixasPostaisList;
  }

  /**
   * Remover uma caixa postal dessa unidade.
   * @param caixaPostal caixa postal
   * @return caixasPostais
   */
  public List<CaixaPostal> removeCaixaPostal(final CaixaPostal caixaPostal) {
    this.caixasPostaisList.remove(caixaPostal);
    caixaPostal.setUnidade(null);
    return this.caixasPostaisList;
  }

  /**
   * Obter o valor do Atributo - auto relacionamento.
   * @return usuarioUnidadeList
   */
  @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
  public List<UsuarioUnidade> getUsuarioUnidadeList() {
    return this.usuarioUnidadeList;
  }

  /**
   * Gravar o valor do Atributo.
   * @param usuarioUnidadeList the usuarioUnidadeList to set
   */
  public void setUsuarioUnidadeList(final List<UsuarioUnidade> usuarioUnidadeList) {
    this.usuarioUnidadeList = usuarioUnidadeList;
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

  /**
   * Obter o valor padrão.
   * @return the assuntoUnidadeDemandanteList
   */
  @ManyToMany(targetEntity = Assunto.class, mappedBy = "assuntoUnidadeDemandanteList")
  public List<Assunto> getAssuntoUnidadeDemandanteList() {
    return this.assuntoUnidadeDemandanteList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assuntoUnidadeDemandanteList the assuntoUnidadeDemandanteList to set
   */
  public void setAssuntoUnidadeDemandanteList(List<Assunto> assuntoUnidadeDemandanteList) {
    this.assuntoUnidadeDemandanteList = assuntoUnidadeDemandanteList;
  }

  /**
   * @return the unidadeSubordinacao
   */
  @ManyToOne
  @JoinColumn(name = "nu_unidade_subordinacao")
  public Unidade getUnidadeSubordinacao() {
    return unidadeSubordinacao;
  }

  /**
   * @param unidadeSubordinacao the unidadeSubordinacao to set
   */
  public void setUnidadeSubordinacao(Unidade unidadeSubordinacao) {
    this.unidadeSubordinacao = unidadeSubordinacao;
  }

  /**
   * @return the unidadesSubordinadasList
   */
  @OneToMany(mappedBy = "unidadeSubordinacao", fetch = FetchType.LAZY)
  public List<Unidade> getUnidadesSubordinadasList() {
    return unidadesSubordinadasList;
  }

  /**
   * @param unidadesSubordinadasList the unidadesSubordinadasList to set
   */
  public void setUnidadesSubordinadasList(List<Unidade> unidadesSubordinadasList) {
    this.unidadesSubordinadasList = unidadesSubordinadasList;
  }
  
  /**
   * @return the isRelConsolidadoAssunto
   */
  @Column(name = "ic_rel_consolidado_assunto")
  public Boolean getIsRelConsolidadoAssunto() {
    return isRelConsolidadoAssunto;
  }

  /**
   * @param isRelConsolidadoAssunto the isRelConsolidadoAssunto to set
   */
  public void setIsRelConsolidadoAssunto(Boolean isRelConsolidadoAssunto) {
    this.isRelConsolidadoAssunto = isRelConsolidadoAssunto;
  }
  
  @Transient
  public String getNomeSUEGParaCombo() {
    String retorno = "";
    retorno += StringUtils.substring(sigla, 0,4);
    retorno += " - ";
    retorno += StringUtils.substring(sigla, 4,5);
    return retorno;
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
    if (!(obj instanceof Unidade)) {
      return false;
    }
    Unidade other = (Unidade) obj;
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
  public int compareTo(Unidade unidade) {
    return this.sigla.compareToIgnoreCase(unidade.sigla);
  }

}
