/**
 * Abrangencia.java
 * Versão: 1.0.0.00
 * Data de Criação : 28-11-2017
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entidade da Tabela: argsm001.argtb08_abrangencia.
 * @author f520296 Allysson Aderne Improta 
 */
@Entity
@Table(name = "argtb01_abrangencia", schema = "argsm001")
public class Abrangencia implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = -7999347718574956892L;

  /** Identificador da Abrangencia. Chave primária. */
  private Long id;

  /** Campo que armazena o nome da Abrangencia. */
  private String nome;

  /** Lista de unidades da Abrangencia. */
  private List<Unidade> unidadesList;

  /**
   * Construtor.
   */
  public Abrangencia() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_abrangencia", unique = true, nullable = false)
  public Long getId() {
    return this.id;
  }

  /**
   * Gravar o valor do Atributo.
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Obter o valor do Atributo.
   * @return nome
   */
  @Column(name = "no_abrangencia", nullable = false, length = 70)
  public String getNome() {
    return this.nome;
  }

  /**
   * Gravar o valor do Atributo.
   * @param nome the nome to set
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * Obter o valor do Atributo.
   * @return unidadesList
   */
  @OneToMany(mappedBy = "abrangencia", fetch = FetchType.LAZY)
  public List<Unidade> getUnidadesList() {
    return this.unidadesList;
  }

  /**
   * Gravar o valor do Atributo.
   * @param unidadesList the unidadesList to set
   */
  public void setUnidadesList(List<Unidade> unidadesList) {
    this.unidadesList = unidadesList;
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
    if (!(obj instanceof Abrangencia)) {
      return false;
    }
    Abrangencia other = (Abrangencia) obj;
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
