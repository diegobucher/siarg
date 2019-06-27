/**
 * Perfil.java
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: argsm001.argtb03_perfil.
 * @author f520296
 */
@Entity
@Table(name = "argtb03_perfil", schema = "argsm001")
public class Perfil implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 3021543895159640769L;

  /** Identificador do Perfil. Chave primária. */
  private Long id;

  /** Campo que armazena o nome do perfil. */
  private String nome;

  /** Lista de Usuários por perfil. */
  private List<Usuario> listaUsuarios;

  /** Lista de Usuarios e Perfis. */
  private List<Perfil> listaPerfis;

  /**
   * Construtor padrão.
   */
  public Perfil() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_perfil", unique = true, nullable = false)
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
   * @return the nome
   */
  @Column(name = "no_perfil", nullable = false, length = 70)
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
   * @return listaUsuarios
   */
  @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
  public List<Usuario> getListaUsuarios() {
    return this.listaUsuarios;
  }

  /**
   * Gravar o valor do Atributo.
   * @param listaUsuarios the listaUsuarios to set
   */
  public void setListaUsuarios(List<Usuario> listaUsuarios) {
    this.listaUsuarios = listaUsuarios;
  }

  /**
   * Adicionar Uma Caixa Postal para essa unidade.
   * @param caixaPostal caixa postal
   * @return caixasPostais
   */
  public List<Usuario> addCaixaPostal(final Usuario usuario) {
    this.listaUsuarios.add(usuario);
    usuario.setPerfil(this);
    return this.listaUsuarios;
  }

  /**
   * Remover uma caixa postal dessa unidade.
   * @param caixaPostal caixa postal
   * @return caixasPostais
   */
  public List<Usuario> removeCaixaPostal(final Usuario usuario) {
    this.listaUsuarios.remove(usuario);
    usuario.setPerfil(null);
    return this.listaUsuarios;
  }

  /**
   * Obter o valor do Atributo.
   * @return listaPerfis
   */
  @ManyToMany(targetEntity = Menu.class, mappedBy = "listaPerfis")
  public List<Perfil> getListaPerfis() {
    return this.listaPerfis;
  }

  /**
   * Gravar o valor do Atributo.
   * @param listaPerfis the listaPerfis to set
   */
  public void setListaPerfis(List<Perfil> listaPerfis) {
    this.listaPerfis = listaPerfis;
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
    if (!(obj instanceof Perfil)) {
      return false;
    }
    Perfil other = (Perfil) obj;
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
