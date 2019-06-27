package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "argtb21_parametro", schema = "argsm001")
public class Parametro implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_parametro")
  private Long id;

  @Column(name = "de_parametro")
  private String valor;

  @Column(name = "no_parametro")
  private String nome;

  public Parametro() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getValor() {
    return this.valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

}