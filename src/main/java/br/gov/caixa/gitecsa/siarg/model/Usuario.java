/**
 * Usuario.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: argsm001.argtb02_usuario.
 * @author f520296
 */
@Entity
@Table(name = "argtb04_usuario", schema = "argsm001")
public class Usuario implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 4699471616780223075L;

  /** Identificador do Perfil. Chave primária. */
  private Long id;

  /** Campo que armazena a matricula do usuário. */
  private String matricula;

  /** Campo que armazena o nome do usuário. */
  private String nome;

  /** Campo que armazena o perfil do usuário. */
  private Perfil perfil;

  /** Lista de Unidades do usuário. */
  private List<UsuarioUnidade> usuarioUnidadeList;

  /**
   * Construtor padrão.
   */
  public Usuario() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_usuario", unique = true, nullable = false)
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
   * @return the matricula
   */
  @Column(name = "co_matricula", nullable = false, length = 7, columnDefinition = "bpchar")
  public String getMatricula() {
    return this.matricula;
  }

  /**
   * Gravar o valor do Atributo.
   * @param matricula the matricula to set
   */
  public void setMatricula(final String matricula) {
    this.matricula = matricula;
  }

  /**
   * Obter o valor do Atributo.
   * @return the nome
   */
  @Column(name = "no_usuario", nullable = false, length = 70)
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
   * @return the perfil
   */
  @ManyToOne(targetEntity = Perfil.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_perfil", nullable = false)
  public Perfil getPerfil() {
    return this.perfil;
  }

  /**
   * Gravar o valor do Atributo.
   * @param perfil the perfil to set
   */
  public void setPerfil(final Perfil perfil) {
    this.perfil = perfil;
  }

  /**
   * Obtem o valor da Lista.
   * @return list
   */
  @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
  public List<UsuarioUnidade> getUsuarioUnidadeList() {
    return this.usuarioUnidadeList;
  }

  /**
   * Gravar o valor da Lista.
   * @param usuarioUnidadeList **Lista**
   */
  public void setUsuarioUnidadeList(final List<UsuarioUnidade> usuarioUnidadeList) {
    this.usuarioUnidadeList = usuarioUnidadeList;
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
    if (!(obj instanceof Usuario)) {
      return false;
    }
    Usuario other = (Usuario) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
