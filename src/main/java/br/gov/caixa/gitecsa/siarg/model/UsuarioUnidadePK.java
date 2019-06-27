/**
 * UsuarioUnidadePK.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Classe UsuarioUnidadePK - Embebed.
 * @author f520296
 */
@Embeddable
public class UsuarioUnidadePK implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1L;

  /** Chave primária. */
  private Long nuUnidade;

  /** Chave primária. */
  private Long nuUsuario;

  /** Chave primária. */
  private Date dhInicio;

  /** Construtor. */
  public UsuarioUnidadePK() {
    super();
  }

  /**
   * Obter a Chave primária.
   * @return nuUnidade
   */
  @Column(name = "nu_unidade")
  public Long getNuUnidade() {
    return this.nuUnidade;
  }

  /**
   * Gravar a chave.
   * @param nuUnidade ** Unidade **
   */
  public void setNuUnidade(final Long nuUnidade) {
    this.nuUnidade = nuUnidade;
  }

  /**
   * Obter a Chave primária.
   * @return nuUsuario
   */
  @Column(name = "nu_usuario")
  public Long getNuUsuario() {
    return this.nuUsuario;
  }

  /**
   * Gravar a chave.
   * @param nuUsuario ** Usuario **
   */
  public void setNuUsuario(final Long nuUsuario) {
    this.nuUsuario = nuUsuario;
  }

  /**
   * @return the dhInicio
   */
  @Column(name = "dh_inicio", columnDefinition = "timestamp")
  public Date getDhInicio() {
    return this.dhInicio;
  }

  /**
   * @param dhInicio the dhInicio to set
   */
  public void setDhInicio(final Date dhInicio) {
    this.dhInicio = dhInicio;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.dhInicio == null) ? 0 : this.dhInicio.hashCode());
    result = (prime * result) + ((this.nuUnidade == null) ? 0 : this.nuUnidade.hashCode());
    result = (prime * result) + ((this.nuUsuario == null) ? 0 : this.nuUsuario.hashCode());
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
    if (!(obj instanceof UsuarioUnidadePK)) {
      return false;
    }
    UsuarioUnidadePK other = (UsuarioUnidadePK) obj;
    if (this.dhInicio == null) {
      if (other.dhInicio != null) {
        return false;
      }
    } else if (!this.dhInicio.equals(other.dhInicio)) {
      return false;
    }
    if (this.nuUnidade == null) {
      if (other.nuUnidade != null) {
        return false;
      }
    } else if (!this.nuUnidade.equals(other.nuUnidade)) {
      return false;
    }
    if (this.nuUsuario == null) {
      if (other.nuUsuario != null) {
        return false;
      }
    } else if (!this.nuUsuario.equals(other.nuUsuario)) {
      return false;
    }
    return true;
  }

}
