/**
 * Feriado.java
 * Versão: 1.0.0.00
 * 11/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Classe: Tabela utilizada para armazenar os feriados nacionais.
 * @author f520296
 */
@Entity
@Table(name = "argtb14_feriado", schema = "argsm001")
public class Feriado implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 2996827321330528539L;

  /** Campo utilizado para armazenar as datas dos feriados. */
  private Date data;

  /** Campo utilizado para armazenar os nomes dos feriados. */
  private String nome;

  /** Construtor Padrão. */
  public Feriado() {
    super();
  }

  /**
   * Obter o valor padrão.
   * @return the data
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Temporal(TemporalType.DATE)
  @Column(name = "dt_feriado", unique = true, nullable = false)
  public Date getData() {
    return this.data;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param data the data to set
   */
  public void setData(final Date data) {
    this.data = data;
  }

  /**
   * Obter o valor padrão.
   * @return the nome
   */
  @Column(name = "no_feriado", nullable = false)
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

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.data == null) ? 0 : this.data.hashCode());
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
    if (!(obj instanceof Feriado)) {
      return false;
    }
    Feriado other = (Feriado) obj;
    if (this.data == null) {
      if (other.data != null) {
        return false;
      }
    } else if (!this.data.equals(other.data)) {
      return false;
    }
    return true;
  }
}
