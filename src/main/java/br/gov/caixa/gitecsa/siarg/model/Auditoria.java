package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAuditoriaEnum;

/**
 * The persistent class for the argtb18_auditoria database table.
 * 
 */
@Entity
@Table(name = "argtb18_auditoria", schema = "argsm001")
public class Auditoria implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_auditoria")
  private Long id;

  @Column(name = "co_matricula", columnDefinition = "bpchar(7)")
  private String matricula;

  @Column(name = "nu_cgc_unidade")
  private Integer cgcUnidade;

  @Column(name = "de_auditoria")
  private String descricao;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_auditoria")
  private Date dataAuditoria;

  @Column(name = "no_tabela_referenciada")
  private String nomeTabelaReferenciada;

  @Column(name = "nu_chave_tabela")
  private Long numeroChaveTabela;

  @Enumerated(EnumType.STRING)
  @Column(name = "ic_operacao", columnDefinition = "bpchar(1)")
  private AcaoAuditoriaEnum acaoAuditoriaEnum;
  
  public static final String SEPARADOR = "#&";

  public Auditoria() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMatricula() {
    return this.matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public Integer getCgcUnidade() {
    return this.cgcUnidade;
  }

  public void setCgcUnidade(Integer cgcUnidade) {
    this.cgcUnidade = cgcUnidade;
  }

  public String getDescricao() {
    return this.descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Date getDataAuditoria() {
    return this.dataAuditoria;
  }

  public void setDataAuditoria(Date dataAuditoria) {
    this.dataAuditoria = dataAuditoria;
  }

  public String getNomeTabelaReferenciada() {
    return this.nomeTabelaReferenciada;
  }

  public void setNomeTabelaReferenciada(String nomeTabelaReferenciada) {
    this.nomeTabelaReferenciada = nomeTabelaReferenciada;
  }

  public Long getNumeroChaveTabela() {
    return this.numeroChaveTabela;
  }

  public void setNumeroChaveTabela(Long numeroChaveTabela) {
    this.numeroChaveTabela = numeroChaveTabela;
  }

  public AcaoAuditoriaEnum getAcaoAuditoriaEnum() {
    return acaoAuditoriaEnum;
  }

  public void setAcaoAuditoriaEnum(AcaoAuditoriaEnum acaoAuditoriaEnum) {
    this.acaoAuditoriaEnum = acaoAuditoriaEnum;
  }

}