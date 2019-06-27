package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class RegistrosIndividuaisVisaoSuegPorUnidadesDTO implements Serializable, Comparable<RegistrosIndividuaisVisaoSuegPorUnidadesDTO> {
  
  /** Serial. */
  private static final long serialVersionUID = 5885191528133804913L;
  
  private Unidade unidadeSubordinacao;
  
  private Unidade unidadeDemandante;
  
  private Long demandasAbertaParaTratar;
  
  private Long demandasFechadaParaTratar;
  
  private Long totalDemandasParaTratar;

  private Long demandasAbertasRealizadas;
  
  private Long demandasFechadaRealizadas;
  
  private Long totalDemandasRealizadas;

  /**
   * @return the unidadeSubordinacao
   */
  public Unidade getUnidadeSubordinacao() {
    return unidadeSubordinacao;
  }

  /**
   * @param unidadeSubordinacao the unidadeSubordinacao to set
   */
  public void setUnidadeSubordinacao(Unidade unidadeSubordinacao) {
    this.unidadeSubordinacao = unidadeSubordinacao;
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
   * @return the demandasAbertaParaTratar
   */
  public Long getDemandasAbertaParaTratar() {
    return demandasAbertaParaTratar;
  }

  /**
   * @param demandasAbertaParaTratar the demandasAbertaParaTratar to set
   */
  public void setDemandasAbertaParaTratar(Long demandasAbertaParaTratar) {
    this.demandasAbertaParaTratar = demandasAbertaParaTratar;
  }

  /**
   * @return the demandasFechadaParaTratar
   */
  public Long getDemandasFechadaParaTratar() {
    return demandasFechadaParaTratar;
  }

  /**
   * @param demandasFechadaParaTratar the demandasFechadaParaTratar to set
   */
  public void setDemandasFechadaParaTratar(Long demandasFechadaParaTratar) {
    this.demandasFechadaParaTratar = demandasFechadaParaTratar;
  }

  /**
   * @return the totalDemandasParaTratar
   */
  public Long getTotalDemandasParaTratar() {
    return totalDemandasParaTratar;
  }

  /**
   * @param totalDemandasParaTratar the totalDemandasParaTratar to set
   */
  public void setTotalDemandasParaTratar(Long totalDemandasParaTratar) {
    this.totalDemandasParaTratar = totalDemandasParaTratar;
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
   * @return the demandasFechadaRealizadas
   */
  public Long getDemandasFechadaRealizadas() {
    return demandasFechadaRealizadas;
  }

  /**
   * @param demandasFechadaRealizadas the demandasFechadaRealizadas to set
   */
  public void setDemandasFechadaRealizadas(Long demandasFechadaRealizadas) {
    this.demandasFechadaRealizadas = demandasFechadaRealizadas;
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

  @Override
  public int compareTo(RegistrosIndividuaisVisaoSuegPorUnidadesDTO dto) {
    return this.unidadeDemandante.getSigla().compareToIgnoreCase(dto.getUnidadeDemandante().getSigla());
  }
}
