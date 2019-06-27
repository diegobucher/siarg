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
@Table(name = "argtb22_demanda_contrato", schema = "argsm001")
public class DemandaContrato implements Serializable, Comparable<DemandaContrato> {

  /** Serial. */
  private static final long serialVersionUID = -6518648681912492454L;

  private Long id;
  
  private Demanda demanda;
  
  private String numeroContrato;
  
  private Date dataInclusao;

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_demanda_contrato", unique = true, nullable = false)
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the demanda
   */
  @ManyToOne(targetEntity = Demanda.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_demanda")
  public Demanda getDemanda() {
    return demanda;
  }

  /**
   * @param demanda the demanda to set
   */
  public void setDemanda(Demanda demanda) {
    this.demanda = demanda;
  }

  /**
   * @return the numeroContrato
   */
  @Column(name = "nu_contrato", length = 10, columnDefinition = "bpchar")
  public String getNumeroContrato() {
    return numeroContrato;
  }

  /**
   * @param numeroContrato the numeroContrato to set
   */
  public void setNumeroContrato(String numeroContrato) {
    this.numeroContrato = numeroContrato;
  }

  /**
   * @return the dataInclusao
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dt_inclusao")
  public Date getDataInclusao() {
    return dataInclusao;
  }

  /**
   * @param dataInclusao the dataInclusao to set
   */
  public void setDataInclusao(Date dataInclusao) {
    this.dataInclusao = dataInclusao;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DemandaContrato)) {
      return false;
    }
    DemandaContrato other = (DemandaContrato) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(DemandaContrato dc) {
    return this.numeroContrato.compareToIgnoreCase(dc.numeroContrato);
  }

}
