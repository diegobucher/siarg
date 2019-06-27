package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class RelatorioGeralVisaoSuegPorUnidadesDTO implements Serializable, Comparable<RelatorioGeralVisaoSuegPorUnidadesDTO>{

  /** Serial. */
  private static final long serialVersionUID = -4057633098922649274L;
  
  private Long idUnidadeSubordinacao;

  private Long idUnidadeDemandante;

  private Unidade unidadeDemandante;

  private Long demandasAbertasTratar;
  
  private Long demandasFechadasTratar;
  
  private Long totalDemandasTratar;

  private Long demandasAbertasRealizadas;
  
  private Long demandasFechadasRealizadas;
  
  private Long totalDemandasRealizadas;
  
  private String subordinacao;
  
  private String cgcUnidade;
  
  private String nomeUnidade;
  
  private Long totalDemandasRealizadasSUEG;
  
  private Long totalDemandasTratarSUEG;
  
  private List<RegistrosIndividuaisVisaoSuegPorUnidadesDTO> registrosIndivioduaisList;
  
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
   * @return the totalDemandasTratar
   */
  public Long getTotalDemandasTratar() {
    return totalDemandasTratar;
  }

  /**
   * @param totalDemandasTratar the totalDemandasTratar to set
   */
  public void setTotalDemandasTratar(Long totalDemandasTratar) {
    this.totalDemandasTratar = totalDemandasTratar;
  }

  /**
   * @return the totalDemandasRealizadas
   */
  public Long getTotalDemandasRealizadas() {
    return totalDemandasRealizadas;
  }

  /**
   * @param totalDemandasRealizadas the totalDemandasRealizadas to set
   */
  public void setTotalDemandasRealizadas(Long totalDemandasRealizadas) {
    this.totalDemandasRealizadas = totalDemandasRealizadas;
  }

  /**
   * @return the registrosIndivioduaisList
   */
  public List<RegistrosIndividuaisVisaoSuegPorUnidadesDTO> getRegistrosIndivioduaisList() {
    return registrosIndivioduaisList;
  }

  /**
   * @param registrosIndivioduaisList the registrosIndivioduaisList to set
   */
  public void setRegistrosIndivioduaisList(List<RegistrosIndividuaisVisaoSuegPorUnidadesDTO> registrosIndivioduaisList) {
    this.registrosIndivioduaisList = registrosIndivioduaisList;
  }

  @Override
  public int compareTo(RelatorioGeralVisaoSuegPorUnidadesDTO dto) {
    return this.unidadeDemandante.getSigla().compareToIgnoreCase(dto.getUnidadeDemandante().getSigla());
  }

  /**
   * @return the idUnidadeSubordinacao
   */
  public Long getIdUnidadeSubordinacao() {
    return idUnidadeSubordinacao;
  }

  /**
   * @param idUnidadeSubordinacao the idUnidadeSubordinacao to set
   */
  public void setIdUnidadeSubordinacao(Long idUnidadeSubordinacao) {
    this.idUnidadeSubordinacao = idUnidadeSubordinacao;
  }

  /**
   * @return the demandasAbertasTratar
   */
  public Long getDemandasAbertasTratar() {
    return demandasAbertasTratar;
  }

  /**
   * @param demandasAbertasTratar the demandasAbertasTratar to set
   */
  public void setDemandasAbertasTratar(Long demandasAbertasTratar) {
    this.demandasAbertasTratar = demandasAbertasTratar;
  }

  /**
   * @return the demandasFechadasTratar
   */
  public Long getDemandasFechadasTratar() {
    return demandasFechadasTratar;
  }

  /**
   * @param demandasFechadasTratar the demandasFechadasTratar to set
   */
  public void setDemandasFechadasTratar(Long demandasFechadasTratar) {
    this.demandasFechadasTratar = demandasFechadasTratar;
  }

  /**
   * @return the demandasAbertasRealizadas
   */
  public Long getDemandasAbertasRealizadas() {
    return demandasAbertasRealizadas;
  }

  /**
   * @param demandasAbertasRealizadas the demandasAbertasRealizadas to set
   */
  public void setDemandasAbertasRealizadas(Long demandasAbertasRealizadas) {
    this.demandasAbertasRealizadas = demandasAbertasRealizadas;
  }

  /**
   * @return the demandasFechadasRealizadas
   */
  public Long getDemandasFechadasRealizadas() {
    return demandasFechadasRealizadas;
  }

  /**
   * @param demandasFechadasRealizadas the demandasFechadasRealizadas to set
   */
  public void setDemandasFechadasRealizadas(Long demandasFechadasRealizadas) {
    this.demandasFechadasRealizadas = demandasFechadasRealizadas;
  }

  /**
   * @return the subordinacao
   */
  public String getSubordinacao() {
    this.subordinacao = StringUtils.substring(this.unidadeDemandante.getUnidadeSubordinacao().getSigla(), 
                          this.unidadeDemandante.getUnidadeSubordinacao().getSigla().length() -1);
    return subordinacao;
  }

  /**
   * @param subordinacao the subordinacao to set
   */
  public void setSubordinacao(String subordinacao) {
    this.subordinacao = subordinacao;
  }

  /**
   * @return the cgcUnidade
   */
  public String getCgcUnidade() {
    this.cgcUnidade = String.format("%04d", this.unidadeDemandante.getCgcUnidade());
    return cgcUnidade;
  }

  /**
   * @param cgcUnidade the cgcUnidade to set
   */
  public void setCgcUnidade(String cgcUnidade) {
    this.cgcUnidade = cgcUnidade;
  }

  /**
   * @return the nomeUnidade
   */
  public String getNomeUnidade() {
    this.nomeUnidade = this.getUnidadeDemandante().getSigla();
    return nomeUnidade;
  }

  /**
   * @param nomeUnidade the nomeUnidade to set
   */
  public void setNomeUnidade(String nomeUnidade) {
    this.nomeUnidade = nomeUnidade;
  }

  /**
   * @return the totalDemandasRealizadasSUEG
   */
  public Long getTotalDemandasRealizadasSUEG() {
    return totalDemandasRealizadasSUEG;
  }

  /**
   * @param totalDemandasRealizadasSUEG the totalDemandasRealizadasSUEG to set
   */
  public void setTotalDemandasRealizadasSUEG(Long totalDemandasRealizadasSUEG) {
    this.totalDemandasRealizadasSUEG = totalDemandasRealizadasSUEG;
  }

  /**
   * @return the totalDemandasTratarSUEG
   */
  public Long getTotalDemandasTratarSUEG() {
    return totalDemandasTratarSUEG;
  }

  /**
   * @param totalDemandasTratarSUEG the totalDemandasTratarSUEG to set
   */
  public void setTotalDemandasTratarSUEG(Long totalDemandasTratarSUEG) {
    this.totalDemandasTratarSUEG = totalDemandasTratarSUEG;
  }

 
}
