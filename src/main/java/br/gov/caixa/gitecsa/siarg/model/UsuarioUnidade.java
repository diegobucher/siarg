/**
 * UsuarioUnidade.java
 * Versão: 1.0.0.00
 * Data de Criação : 22-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: argsm001.argtb07_usuario_unidade.
 * @author f520296
 */
@Entity
@Table(name = "argtb07_usuario_unidade", schema = "argsm001")
public class UsuarioUnidade implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1L;

//  /** Embebed ID. */
//  private UsuarioUnidadePK id;
  
  private Long id;

  /** Identificador da unidade. Chave primária. */
  private Unidade unidade;

  /** Identificador do usuário. Chave primária. */
  private Usuario usuario;

  /** Campo utilizado para informar a Data em que o usuário foi cadastrado na unidade. */
  private Date dataInicio;

  /** Campo utilizado para informar a Data em que o usuário foi removido da unidade. */
  private Date dataFim;

  /** Campo utilizado para informar observações pertinentesda unidade. */
  private String observacao;
  
  private UsuarioUnidade usuarioUnidadeTransient;

  /**
   * Construtor Padrão.
   */
  public UsuarioUnidade() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return unidade
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "nu_unidade", nullable = false)
  public Unidade getUnidade() {
    return this.unidade;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidade the unidade to set
   */
  public void setUnidade(Unidade unidade) {
    this.unidade = unidade;
  }

  /**
   * Obter o valor do Atributo.
   * @return usuario
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "nu_usuario", nullable = false)
  public Usuario getUsuario() {
    return this.usuario;
  }

  /**
   * Gravar o valor do Atributo.
   * @param usuario the usuario to set
   */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  /**
   * Obter o valor do Atributo.
   * @return dataInicio
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_inicio")
  public Date getDataInicio() {
    return this.dataInicio;
  }

  /**
   * Gravar o valor do Atributo.
   * @param dataInicio the dataInicio to set
   */
  public void setDataInicio(Date dataInicio) {
    this.dataInicio = dataInicio;
  }

  /**
   * Obter o valor do Atributo.
   * @return dataFim
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_fim")
  public Date getDataFim() {
    return this.dataFim;
  }

  /**
   * Gravar o valor do Atributo.
   * @param dataFim the dataFim to set
   */
  public void setDataFim(Date dataFim) {
    this.dataFim = dataFim;
  }

  /**
   * Obter o valor do Atributo.
   * @return observacao
   */
  @Column(name = "de_observacao")
  public String getObservacao() {
    return this.observacao;
  }

  /**
   * Gravar o valor do Atributo.
   * @param observacao **observação**
   */
  public void setObservacao(String observacao) {
    this.observacao = observacao;
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
    if (!(obj instanceof UsuarioUnidade)) {
      return false;
    }
    UsuarioUnidade other = (UsuarioUnidade) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_usuario_unidade", unique = true, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Transient
  public UsuarioUnidade getUsuarioUnidadeTransient() {
    return usuarioUnidadeTransient;
  }

  public void setUsuarioUnidadeTransient(UsuarioUnidade usuarioUnidadeTransient) {
    this.usuarioUnidadeTransient = usuarioUnidadeTransient;
  }

}
