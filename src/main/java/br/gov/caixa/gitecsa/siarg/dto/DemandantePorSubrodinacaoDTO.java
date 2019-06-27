package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class DemandantePorSubrodinacaoDTO implements Serializable, Comparable<DemandantePorSubrodinacaoDTO>{

  /** Serial. */
  private static final long serialVersionUID = -2383268627702844096L;
  
  private Unidade subordinacao;

  private Long idUnidadeDemandante;
  
  private Unidade unidadeDemandante;
  
  private Integer qtdDemandas;

  private Map<String, Integer> listaDemandadas;
  
  private List<String> nomeUnidadesList;

  private List<String> siglaUnidadesDemandasList;
  
  private List<Integer> valorUnidadesList;
  
  public String letraSueg;

  /**
   * @return the unidadeDemandante
   */
  public Unidade getUnidadeDemandante() {
    return unidadeDemandante;
  }

  /**
   * @param unidadeDemandante the unidadeDemandante to set
   */
  public void setUnidadeDemandante(Unidade unidadeDemandante) {
    this.unidadeDemandante = unidadeDemandante;
  }

  /**
   * @return the subordinacao
   */
  public Unidade getSubordinacao() {
    return subordinacao;
  }

  /**
   * @param subordinacao the subordinacao to set
   */
  public void setSubordinacao(Unidade subordinacao) {
    this.subordinacao = subordinacao;
  }

  /**
   * @return the qtdDemandas
   */
  public Integer getQtdDemandas() {
    return qtdDemandas;
  }

  /**
   * @param qtdDemandas the qtdDemandas to set
   */
  public void setQtdDemandas(Integer qtdDemandas) {
    this.qtdDemandas = qtdDemandas;
  }

  /**
   * @return the nomeUnidadesList
   */
  public List<String> getNomeUnidadesList() {
    return nomeUnidadesList;
  }

  /**
   * @param nomeUnidadesList the nomeUnidadesList to set
   */
  public void setNomeUnidadesList(List<String> nomeUnidadesList) {
    this.nomeUnidadesList = nomeUnidadesList;
  }

  /**
   * @return the valorUnidadesList
   */
  public List<Integer> getValorUnidadesList() {
    return valorUnidadesList;
  }

  /**
   * @param valorUnidadesList the valorUnidadesList to set
   */
  public void setValorUnidadesList(List<Integer> valorUnidadesList) {
    this.valorUnidadesList = valorUnidadesList;
  }

  /**
   * @return the listaDemandadas
   */
  public Map<String, Integer> getListaDemandadas() {
    return listaDemandadas;
  }

  /**
   * @param listaDemandadas the listaDemandadas to set
   */
  public void setListaDemandadas(Map<String, Integer> listaDemandadas) {
    this.listaDemandadas = listaDemandadas;
  }
  
  /**
   * @return the letraSueg
   */
  public String getLetraSueg() {
    return letraSueg;
  }

  /**
   * @param letraSueg the letraSueg to set
   */
  public void setLetraSueg(String letraSueg) {
    this.letraSueg = letraSueg;
  }
  
  /**
   * @return the idUnidadeDemandante
   */
  public Long getIdUnidadeDemandante() {
    return idUnidadeDemandante;
  }
  
  /**
   * @param idUnidadeDemandante the idUnidadeDemandante to set
   */
  public void setIdUnidadeDemandante(Long idUnidadeDemandante) {
    this.idUnidadeDemandante = idUnidadeDemandante;
  }
  
  @Override
  public int compareTo(DemandantePorSubrodinacaoDTO dto) {
    return this.letraSueg.compareTo(dto.getLetraSueg());
  }

  /**
   * @return the siglaUnidadesDemandasList
   */
  public List<String> getSiglaUnidadesDemandasList() {
    return siglaUnidadesDemandasList;
  }

  /**
   * @param siglaUnidadesDemandasList the siglaUnidadesDemandasList to set
   */
  public void setSiglaUnidadesDemandasList(List<String> siglaUnidadesDemandasList) {
    this.siglaUnidadesDemandasList = siglaUnidadesDemandasList;
  }

}