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

@Entity
@Table(name = "argtb19_ocorrencias", schema = "argsm001")
public class Ocorrencia implements Serializable{

  /** Serial. */
  private static final long serialVersionUID = -4562320875968918154L;
  
  /** Campo utilizado para armazenar o id da tabela Ocorrência. */
  private Long id;
  
  /** Campo utilizado para armazenar o titulo da tabela Ocorrência. */
  private String titulo;
  
  /** Campo utilizado para armazenar o conteudo da tabela Ocorrência. */
  private String conteudo;
  
  /** Campo utilizado para armazenar a unidade da tabela Ocorrência. */
  private Unidade unidade;
  
  /** Campo utilizado para armazenar a matricula do usuário que cadastrou a ocorrência. */
  private String matricula;

  /** Campo utilizado para armazenar o nome do usuário que cadastrou a ocorrência. */
  private String nomeUsuario;
  
  /** Campo utilizado para armazenar Data de cadastro da ocorrência. */
  private Date dhPublicacao;
  
  /** Campo utilizado para armazenar Data de expiração da ocorrência. */
  private Date dhExpiracao;
  
  /** Campo utilizado para exclusão lógica da ocorrência. */
  private boolean ativo;

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_ocorrencia", unique = true, nullable = false)
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return the titulo
   */
  @Column(name = "no_titulo", length = 200)
  public String getTitulo() {
    return titulo;
  }

  /**
   * @param titulo the titulo to set
   */
  public void setTitulo(final String titulo) {
    this.titulo = titulo;
  }
  
  /**
   * @return the conteudo
   */
  @Column(name = "no_conteudo")
  public String getConteudo() {
    return conteudo;
  }

  /**
   * @param conteudo the conteudo to set
   */
  public void setConteudo(final String conteudo) {
    this.conteudo = conteudo;
  }

  /**
   * @return the unidade
   */
  @ManyToOne(targetEntity = Unidade.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_unidade", nullable = false)
  public Unidade getUnidade() {
    return unidade;
  }

  /**
   * @param unidade the unidade to set
   */
  public void setUnidade(final Unidade unidade) {
    this.unidade = unidade;
  }

  /**
   * @return the matricula
   */
  @Column(name = "co_matricula", columnDefinition = "bpchar", length = 7)
  public String getMatricula() {
    return matricula;
  }

  /**
   * @param matricula the matricula to set
   */
  public void setMatricula(final String matricula) {
    this.matricula = matricula;
  }
  
  /**
   * @return the dhPublicacao
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_publicacao")
  public Date getDhPublicacao() {
    return dhPublicacao;
  }

  /**
   * @param dhPublicacao the dhPublicacao to set
   */
  public void setDhPublicacao(final Date dhPublicacao) {
    this.dhPublicacao = dhPublicacao;
  }

  /**
   * @return the dhExpiracao
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_expiracao")
  public Date getDhExpiracao() {
    return dhExpiracao;
  }

  /**
   * @param dhExpiracao the dhExpiracao to set
   */
  public void setDhExpiracao(final Date dhExpiracao) {
    this.dhExpiracao = dhExpiracao;
  }
  

  /**
   * @return the nomeUsuario
   */
  @Column(name = "no_usuario", length = 100)
  public String getNomeUsuario() {
    return nomeUsuario;
  }

  /**
   * @param nomeUsuario the nomeUsuario to set
   */
  public void setNomeUsuario(String nomeUsuario) {
    this.nomeUsuario = nomeUsuario;
  }

  /**
   * @return the ativo
   */
  @Column(name = "ic_ativo")
  public boolean isAtivo() {
    return ativo;
  }

  /**
   * @param ativo the ativo to set
   */
  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /* (non-Javadoc)
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
    if (!(obj instanceof Ocorrencia)) {
      return false;
    }
    Ocorrencia other = (Ocorrencia) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
