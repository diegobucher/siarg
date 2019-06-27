/**
 * UnidadeDTO.java
 * Versão: 1.0.0.00
 * Data de Criação : 24-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model.dto;

import java.io.Serializable;

/**
 * Classe de unidade padrão DTO.
 * @author f520296
 */
public class UnidadeDTO implements Serializable {

  /** SERIAL. */
  private static final long serialVersionUID = 5230857028565224390L;

  /** Id da unidade */
  private Long id;

  /** Nome da unidade */
  private String nome;

  /** Sigla da Unidade */
  private String sigla;

  /** Sigla da Unidade */
  private Long abrangencia;

  /** Construtor. */
  public UnidadeDTO() {
    super();
  }

  /**
   * Obter o valor do Atributo.
   * @return id
   */
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
   * @return nome
   */
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
   * @return sigla
   */
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
   * Obter o valor padrão.
   * @return the abrangencia
   */
  public Long getAbrangencia() {
    return this.abrangencia;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param abrangencia the abrangencia to set
   */
  public void setAbrangencia(Long abrangencia) {
    this.abrangencia = abrangencia;
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
    if (!(obj instanceof UnidadeDTO)) {
      return false;
    }
    UnidadeDTO other = (UnidadeDTO) obj;
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
