/**
 * Demanda.java
 * Versão: 1.0.0.00
 * 04/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.caixa.gitecsa.arquitetura.hibernate.EnumUserType;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;

/**
 * Entidade da Tabela: argsm001.argtb13_demanda.
 * @author f520296
 */
@Entity
@Table(name = "argtb13_demanda", schema = "argsm001")
public class Demanda implements Serializable, Comparable<Demanda> {

  /** Serial. */
  private static final long serialVersionUID = 6466765214890032997L;

  /** Campo utilizado para armazenar o id da tabela demanda. */
  private Long id;

  /** Campo utilizado para armazenar o id da demanda pai. */
  private Demanda demandaPai;

  /** Campo utilizado para armazenar o id da tabela situação. */
  private SituacaoEnum situacao;

  /** Campo utilizado para armazenar o id da tabela de assunto. */
  private Assunto assunto;

  /** Campo utilizado para armazenar o id da tabela de caixa-postal Demandante. */
  private CaixaPostal caixaPostalDemandante;

  /** Campo utilizado para armazenar o id da tabela de caixa-postal Responsável. */
  private CaixaPostal caixaPostalResponsavel;

  /** Armazenar a data e hora em que a demanda foi disponibilizada com a situação de ABERTA. */
  private Date dataHoraAbertura;

  /** Armazenar a data e hora em que a demanda foi disponibilizada com a situação de ABERTA. */
  private Date dataHoraEncerramento;

  /** Armazenar a data e hora em que a demanda foi disponibilizada com a situação de ABERTA. */
  private String titulo;

  /** Campo utilizado para armazenar a descrição da demanda. */
  private String textoDemanda;

  /** Campo para armazenar a matrícula do usuário que incluiu a demanda com a situação = MINUTA. */
  private String matriculaMinuta;

  /** Armazenar a matrícula do usuário que incluiu ou atualizou a demanda com a situação = ABERTA. */
  private String matriculaDemandante;

  /** Armazenar a matrícula do usuário que incluiu ou atualizou a demanda com a situação = ABERTA. */
  private String matriculaRascunho;

  /** Campo utilizado para informar se a demanda armazenada é uma demanda pai. */
  private Boolean flagDemandaPai;

  /** Campo utilizado para armazenar cor que foi atribuída para demanda. */
  private String cor;

  /** Campo utilizado para armazenar cor que foi atribuída para demanda. */
  private String anexoDemanda;

  /** Campo utilizado para auto relacionamento Pai e Filho. */
  private List<Demanda> demandaFilhosList;

  /** Campo utilizado para armazenar relacionamento com FluxoDemanda. */
  private List<FluxoDemanda> fluxosDemandasList;

  /** Campo utilizado para armazenar o tipo da Demanda. */
  private TipoDemandaEnum tipoDemanda;

  /** Campo utilizado para armazenar os observadores das demandas. */
  private List<CaixaPostal> caixasPostaisObservadorList;

  /** Campo utilizado para armazenar os observadores das demandas. */
  private List<Atendimento> atendimentosList;
  
  /** Campo utilizado para armazenar a lista de contratos para esta demanda. */
  private List<DemandaContrato> contratosList;
  
  /** Campo utilizado para armazenar a lista de campos obrigatorios para esta demanda. */
  private List<DemandaCampo> camposList;
  
  private String nomeUsuarioDemandante;
  
  private String nomeUsuarioMinuta;
  
  private String nomeUsuarioRascunho;
  
  @Transient
  public String situacaoDemandaRelatorio;

  /** Construtor Padrão. */
  public Demanda() {
    super();
  }
  

  /**
   * Constrói um clone da demanda passada como parâmetro. Esse contrutor tem como objetivo
   * principal, criar demandas filhas que espelham os dados da demanda pai.
   * @param entity Demanda pai
   */
  public Demanda(final Demanda entity) {
    this.id = null;
    this.matriculaMinuta = entity.getMatriculaMinuta();
    this.matriculaRascunho = entity.getMatriculaRascunho();
    this.matriculaDemandante = entity.getMatriculaDemandante();
    this.nomeUsuarioDemandante = entity.getNomeUsuarioDemandante();

    this.cor = entity.getCor();
    this.dataHoraAbertura = entity.getDataHoraAbertura();

    this.assunto = entity.getAssunto();
    this.titulo = entity.getTitulo();
    this.textoDemanda = entity.getTextoDemanda();
    this.situacao = entity.getSituacao();

    this.caixaPostalDemandante = entity.getCaixaPostalDemandante();
    this.caixasPostaisObservadorList = entity.getCaixasPostaisObservadorList();
  }

  /**
   * Obter o valor padrão.
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_demanda", unique = true, nullable = false)
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
   * @return the demandaPai
   */
  @ManyToOne
  @JoinColumn(name = "nu_demanda_pai")
  public Demanda getDemandaPai() {
    return this.demandaPai;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandaPai the demandaPai to set
   */
  public void setDemandaPai(final Demanda demandaPai) {
    this.demandaPai = demandaPai;
  }

  /**
   * Obter o valor padrão.
   * @return the situacao
   */
  @Column(name = "ic_situacao", columnDefinition = "int2")
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
   * @return the assunto
   */
  @ManyToOne(targetEntity = Assunto.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_assunto")
  public Assunto getAssunto() {
    return this.assunto;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param assunto the assunto to set
   */
  public void setAssunto(final Assunto assunto) {
    this.assunto = assunto;
  }

  /**
   * Obter o valor padrão.
   * @return the caixaPostalDemandante
   */
  @ManyToOne(targetEntity = CaixaPostal.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_caixa_postal_demandante")
  public CaixaPostal getCaixaPostalDemandante() {
    return this.caixaPostalDemandante;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixaPostalDemandante the caixaPostalDemandante to set
   */
  public void setCaixaPostalDemandante(final CaixaPostal caixaPostalDemandante) {
    this.caixaPostalDemandante = caixaPostalDemandante;
  }

  /**
   * Obter o valor padrão.
   * @return the caixaPostalResponsavel
   */
  @ManyToOne(targetEntity = CaixaPostal.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_caixa_postal_responsavel")
  public CaixaPostal getCaixaPostalResponsavel() {
    return this.caixaPostalResponsavel;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixaPostalResponsavel the caixaPostalResponsavel to set
   */
  public void setCaixaPostalResponsavel(final CaixaPostal caixaPostalResponsavel) {
    this.caixaPostalResponsavel = caixaPostalResponsavel;
  }

  /**
   * Obter o valor padrão.
   * @return the dataHoraAbertura
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_abertura")
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
  @Column(name = "dh_fechamento")
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
   * @return the titulo
   */
  @Column(name = "no_titulo")
  public String getTitulo() {
    return this.titulo;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param titulo the titulo to set
   */
  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  /**
   * Obter o valor padrão.
   * @return the textoDemanda
   */
  @Column(name = "de_texto_demanda", columnDefinition = "text")
  public String getTextoDemanda() {
    return this.textoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param textoDemanda the textoDemanda to set
   */
  public void setTextoDemanda(final String textoDemanda) {
    this.textoDemanda = textoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the matriculaMinuta
   */
  @Column(name = "co_matricula_minuta", nullable = false, length = 7, columnDefinition = "bpchar")
  public String getMatriculaMinuta() {
    return this.matriculaMinuta;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param matriculaMinuta the matriculaMinuta to set
   */
  public void setMatriculaMinuta(final String matriculaMinuta) {
    this.matriculaMinuta = matriculaMinuta;
  }

  /**
   * Obter o valor padrão.
   * @return the matriculaDemandante
   */
  @Column(name = "co_matricula_demandante", nullable = false, length = 7, columnDefinition = "bpchar")
  public String getMatriculaDemandante() {
    return this.matriculaDemandante;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param matriculaDemandante the matriculaDemandante to set
   */
  public void setMatriculaDemandante(final String matriculaDemandante) {
    this.matriculaDemandante = matriculaDemandante;
  }

  /**
   * Obter o valor padrão.
   * @return the matriculaRascunho
   */
  @Column(name = "co_matricula_rascunho", nullable = false, length = 7, columnDefinition = "bpchar")
  public String getMatriculaRascunho() {
    return this.matriculaRascunho;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param matriculaRascunho the matriculaRascunho to set
   */
  public void setMatriculaRascunho(String matriculaRascunho) {
    this.matriculaRascunho = matriculaRascunho;
  }

  /**
   * Obter o valor padrão.
   * @return the flagDemandaPai
   */
  @Column(name = "ic_demanda_pai", nullable = false)
  public Boolean getFlagDemandaPai() {
    return this.flagDemandaPai;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param flagDemandaPai the flagDemandaPai to set
   */
  public void setFlagDemandaPai(Boolean flagDemandaPai) {
    this.flagDemandaPai = flagDemandaPai;
  }

  /**
   * Obter o valor padrão.
   * @return the cor
   */
  @Column(name = "no_cor", length = 50)
  public String getCor() {
    return this.cor;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param cor the cor to set
   */
  public void setCor(final String cor) {
    this.cor = cor;
  }

  /**
   * Obter o valor padrão.
   * @return the anexoDemanda
   */
  @Column(name = "no_anexo_demanda", length = 100)
  public String getAnexoDemanda() {
    return this.anexoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param anexoDemanda the anexoDemanda to set
   */
  public void setAnexoDemanda(String anexoDemanda) {
    this.anexoDemanda = anexoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the demandaFilhosList
   */
  @OneToMany(mappedBy = "demandaPai", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
  public List<Demanda> getDemandaFilhosList() {
    return this.demandaFilhosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandaFilhosList the demandaFilhosList to set
   */
  public void setDemandaFilhosList(List<Demanda> demandaFilhosList) {
    this.demandaFilhosList = demandaFilhosList;
  }

  /**
   * Obter o valor padrão.
   * @return the fluxosDemandasList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
  public List<FluxoDemanda> getFluxosDemandasList() {
    return this.fluxosDemandasList;
  }
  
  @Transient
  public List<FluxoDemanda> getFluxosDemandasAtivoList() {
    List<FluxoDemanda> fluxoList = new ArrayList<FluxoDemanda>();
    
    if(getFluxosDemandasList() != null) {
      for (FluxoDemanda fluxoDemanda : getFluxosDemandasList()) {
        if(fluxoDemanda.isAtivo()) {
          fluxoList.add(fluxoDemanda) ;
        }
      }
      
    }
    
    return fluxoList;
  }
  
  @Transient
  public List<FluxoDemanda> getFluxosDemandasAtivoSemFluxoDemandanteList() {
    List<FluxoDemanda> fluxoList = new ArrayList<FluxoDemanda>();
    
    if(getFluxosDemandasList() != null) {
      for (FluxoDemanda fluxoDemanda : getFluxosDemandasList()) {
        if(fluxoDemanda.isAtivo() && !fluxoDemanda.getOrdem().equals(0)) {
          fluxoList.add(fluxoDemanda) ;
        }
      }
      
    }
    
    return fluxoList;
  }
  
  
  @Transient
  public FluxoDemanda getPenultimoFluxoDemanda() {
    List<FluxoDemanda> fluxoList = getFluxosDemandasAtivoList();
    Collections.sort(fluxoList);
    
    Integer ultimoFluxo = 0;
    for (FluxoDemanda fluxoDemanda : fluxoList) {
      ultimoFluxo = fluxoDemanda.getOrdem();
    }
    
    for (FluxoDemanda fluxoDemanda : fluxoList) {
      if(fluxoDemanda.getOrdem().equals(ultimoFluxo-1)) {
        return fluxoDemanda;
      }
    }
    
    return null;
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
   * @return the tipoDemanda
   */
  @Column(name = "ic_tipo_demanda", columnDefinition = "int2")
  @Type(type = EnumUserType.CLASS_NAME, parameters = { @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
      value = TipoDemandaEnum.NOME_ENUM) })
  public TipoDemandaEnum getTipoDemanda() {
    return this.tipoDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param tipoDemanda the tipoDemanda to set
   */
  public void setTipoDemanda(final TipoDemandaEnum tipoDemanda) {
    this.tipoDemanda = tipoDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the caixasPostaisObservadorList
   */
  @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.MERGE)
  @JoinTable(schema = "argsm001", name = "argtb17_observador_demanda", joinColumns = @JoinColumn(name = "nu_demanda",
      referencedColumnName = "nu_demanda"), inverseJoinColumns = { @JoinColumn(name = "nu_caixa_postal",
      referencedColumnName = "nu_caixa_postal") })
  public List<CaixaPostal> getCaixasPostaisObservadorList() {
    return this.caixasPostaisObservadorList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixasPostaisObservadorList the caixasPostaisObservadorList to set
   */
  public void setCaixasPostaisObservadorList(List<CaixaPostal> caixasPostaisObservadorList) {
    this.caixasPostaisObservadorList = caixasPostaisObservadorList;
  }

  /**
   * Obter o valor padrão.
   * @return the atendimentosList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
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
  
  @Transient
  public FluxoDemanda getPrimeiroFluxo() {
    if(fluxosDemandasList != null ) {
      Collections.sort(fluxosDemandasList);
      return fluxosDemandasList.iterator().next();
    }
    
    return null;
  }
  
  @Transient
  public String getListaContratosString() {
    String retorno = "";
    if(contratosList == null || contratosList.isEmpty() ) {
      return "-";
    }
    Collections.sort(contratosList);
    for (DemandaContrato contrato : contratosList) {
      retorno += contrato.getNumeroContrato();
      retorno += " | ";
    }
    retorno = StringUtils.substring(retorno, 0, retorno.length() - 2);
    
    return retorno;
  }

  @Column(name="no_usuario_demandante")
  public String getNomeUsuarioDemandante() {
    return nomeUsuarioDemandante;
  }
  
  public void setNomeUsuarioDemandante(String nomeUsuarioDemandante) {
    this.nomeUsuarioDemandante = nomeUsuarioDemandante;
  }

  @Column(name="no_usuario_minuta")
  public String getNomeUsuarioMinuta() {
    return nomeUsuarioMinuta;
  }

  public void setNomeUsuarioMinuta(String nomeUsuarioMinuta) {
    this.nomeUsuarioMinuta = nomeUsuarioMinuta;
  }

  @Column(name="no_usuario_rascunho")
  public String getNomeUsuarioRascunho() {
    return nomeUsuarioRascunho;
  }

  public void setNomeUsuarioRascunho(String nomeUsuarioRascunho) {
    this.nomeUsuarioRascunho = nomeUsuarioRascunho;
  }
  
  /**
   * @return the contratosList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.LAZY, cascade = { CascadeType.ALL})
  public List<DemandaContrato> getContratosList() {
    return contratosList;
  }

  /**
   * @param contratosList the contratosList to set
   */
  public void setContratosList(List<DemandaContrato> contratosList) {
    this.contratosList = contratosList;
  }
  
  /**
   * @return the camposList
   */
  @OneToMany(mappedBy = "demanda", fetch = FetchType.EAGER, cascade = { CascadeType.ALL})
  public List<DemandaCampo> getCamposList() {
	return camposList;
  }

  /**
   * @param camposList the camposList to set
   */
  public void setCamposList(List<DemandaCampo> camposList) {
	this.camposList = camposList;
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
    if (!(obj instanceof Demanda)) {
      return false;
    }
    Demanda other = (Demanda) obj;
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
  public int compareTo(Demanda demanda) {
    if (this.id < demanda.id) {
      return -1;
    }
    if (this.id > demanda.id) {
      return 1;
    }
    return 0;
  }

  /**
   * @return the situacaoDemandaRelatorio
   */
  @Transient
  public String getSituacaoDemandaRelatorio() {
    return situacaoDemandaRelatorio;
  }

  /**
   * @param situacaoDemandaRelatorio the situacaoDemandaRelatorio to set
   */
  @Transient
  public void setSituacaoDemandaRelatorio(String situacaoDemandaRelatorio) {
    this.situacaoDemandaRelatorio = situacaoDemandaRelatorio;
  }
  
}