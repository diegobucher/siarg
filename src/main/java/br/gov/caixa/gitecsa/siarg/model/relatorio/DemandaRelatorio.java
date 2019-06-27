package br.gov.caixa.gitecsa.siarg.model.relatorio;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;

/**
 * Entidade da Tabela: argsm001.argtb13_demanda.
 * @author f520296
 */
@Entity
@Table(name = "argtb13_demanda", schema = "argsm001")
public class DemandaRelatorio implements Serializable, Comparable<DemandaRelatorio> {
	
	private static final long serialVersionUID = 535785538733319329L;



  /** Campo utilizado para armazenar o id da tabela demanda. */
  private Long id;


  /** Campo utilizado para armazenar o id da tabela situação. */
  private SituacaoEnum situacao;


  /** Armazenar a data e hora em que a demanda foi disponibilizada com a situação de ABERTA. */
  private Date dataHoraAbertura;

  /** Armazenar a data e hora em que a demanda foi disponibilizada com a situação de ABERTA. */
  private Date dataHoraEncerramento;

  /** Campo utilizado para armazenar relacionamento com FluxoDemanda. */
  private List<FluxoDemanda> fluxosDemandasList;

  /** Campo utilizado para armazenar os observadores das demandas. */
  private List<Atendimento> atendimentosList;


  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @Column(name = "nu_demanda", unique = true, nullable = false, insertable=false, updatable=false)
  public Long getId() {
    return this.id;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * Obter o valor padrão.
   * @return the situacao
   */
  @Column(name = "ic_situacao", columnDefinition = "int2" , insertable=false, updatable=false)
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = SituacaoEnum.NOME_ENUM) })
  public SituacaoEnum getSituacao() {
    return this.situacao;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param situacao the situacao to set
   */
  public void setSituacao(SituacaoEnum situacao) {
    this.situacao = situacao;
  }





  /**
   * Obter o valor padrão.
   * @return the dataHoraAbertura
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_abertura", insertable=false, updatable=false)
  public Date getDataHoraAbertura() {
    return this.dataHoraAbertura;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataHoraAbertura the dataHoraAbertura to set
   */
  public void setDataHoraAbertura(final Date dataHoraAbertura) {
    this.dataHoraAbertura = dataHoraAbertura;
  }

  /**
   * Obter o valor padrão.
   * @return the dataHoraEncerramento
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_fechamento", insertable=false, updatable=false)
  public Date getDataHoraEncerramento() {
    return this.dataHoraEncerramento;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataHoraEncerramento the dataHoraEncerramento to set
   */
  public void setDataHoraEncerramento(Date dataHoraEncerramento) {
    this.dataHoraEncerramento = dataHoraEncerramento;
  }



  /**
   * Obter o valor padrão.
   * @return the fluxosDemandasList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.LAZY)
  public List<FluxoDemanda> getFluxosDemandasList() {
    return this.fluxosDemandasList;
  }
  
  

  /**
   * Gravar o valor do parâmetro.
   * @param fluxosDemandasList the fluxosDemandasList to set
   */
  public void setFluxosDemandasList(List<FluxoDemanda> fluxosDemandasList) {
    this.fluxosDemandasList = fluxosDemandasList;
  }




  /**
   * Obter o valor padrão.
   * @return the atendimentosList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.LAZY)
  public List<Atendimento> getAtendimentosList() {
    return this.atendimentosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param atendimentosList the atendimentosList to set
   */
  public void setAtendimentosList(List<Atendimento> atendimentosList) {
    this.atendimentosList = atendimentosList;
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
    if (!(obj instanceof DemandaRelatorio)) {
      return false;
    }
    DemandaRelatorio other = (DemandaRelatorio) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(DemandaRelatorio demanda) {
    if (this.id < demanda.id) {
      return -1;
    }
    if (this.id > demanda.id) {
      return 1;
    }
    return 0;
  }


}