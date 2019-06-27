package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.gitecsa.siarg.model.Demanda;

public class RelatorioDemandasPorSituacaoDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String caixaPostalEnvolvida;

  private Long qtdAbertas;

  private Long qtdAbertasDentroPrazo;
  
  private Long qtdAbertasPrazoVencido;

  private Long qtdFechadas;
  
  private Long qtdFechadasForaDoPrazo;
  
  private Long qtdReabertas;
  
  private Long qtdCanceladas;

  private Long qtdTotal;
  
  private List<Demanda> demandasList;
  
  private Integer situacaoVencimento;
  
  public RelatorioDemandasPorSituacaoDTO() {
    demandasList = new ArrayList<Demanda>();
    qtdAbertas = 0L;
    qtdAbertasDentroPrazo = 0L;
    qtdAbertasPrazoVencido = 0L;
    qtdFechadas = 0L;
    qtdFechadasForaDoPrazo = 0L;
    qtdReabertas = 0L;
    qtdCanceladas = 0L;
    qtdTotal = 0L;
  }

  /**
   * @return the caixaPostalEnvolvida
   */
  public String getCaixaPostalEnvolvida() {
    return caixaPostalEnvolvida;
  }

  /**
   * @param caixaPostalEnvolvida the caixaPostalEnvolvida to set
   */
  public void setCaixaPostalEnvolvida(String caixaPostalEnvolvida) {
    this.caixaPostalEnvolvida = caixaPostalEnvolvida;
  }

  /**
   * @return the qtdAbertas
   */
  public Long getQtdAbertas() {
    return qtdAbertas;
  }

  /**
   * @param qtdAbertas the qtdAbertas to set
   */
  public void setQtdAbertas(Long qtdAbertas) {
    this.qtdAbertas = qtdAbertas;
  }

  /**
   * @return the qtdFechadas
   */
  public Long getQtdFechadas() {
    return qtdFechadas;
  }

  /**
   * @param qtdFechadas the qtdFechadas to set
   */
  public void setQtdFechadas(Long qtdFechadas) {
    this.qtdFechadas = qtdFechadas;
  }

  /**
   * @return the qtdFechadasForaDoPrazo
   */
  public Long getQtdFechadasForaDoPrazo() {
    return qtdFechadasForaDoPrazo;
  }

  /**
   * @param qtdFechadasForaDoPrazo the qtdFechadasForaDoPrazo to set
   */
  public void setQtdFechadasForaDoPrazo(Long qtdFechadasForaDoPrazo) {
    this.qtdFechadasForaDoPrazo = qtdFechadasForaDoPrazo;
  }

  /**
   * @return the qtdReabertas
   */
  public Long getQtdReabertas() {
    return qtdReabertas;
  }

  /**
   * @param qtdReabertas the qtdReabertas to set
   */
  public void setQtdReabertas(Long qtdReabertas) {
    this.qtdReabertas = qtdReabertas;
  }

  /**
   * @return the qtdCanceladas
   */
  public Long getQtdCanceladas() {
    return qtdCanceladas;
  }

  /**
   * @param qtdCanceladas the qtdCanceladas to set
   */
  public void setQtdCanceladas(Long qtdCanceladas) {
    this.qtdCanceladas = qtdCanceladas;
  }

  /**
   * @return the qtdTotal
   */
  public Long getQtdTotal() { // Alm 101785
    qtdTotal = qtdAbertas + qtdFechadas + qtdCanceladas;
    return qtdTotal;
  }

  /**
   * @param qtdTotal the qtdTotal to set
   */
  public void setQtdTotal(Long qtdTotal) {
    this.qtdTotal = qtdTotal;
  }

  /**
   * @return the demandasList
   */
  public List<Demanda> getDemandasList() {
    return demandasList;
  }

  /**
   * @param demandasList the demandasList to set
   */
  public void setDemandasList(List<Demanda> demandasList) {
    this.demandasList = demandasList;
  }

  public Long getQtdAbertasDentroPrazo() {
    return qtdAbertasDentroPrazo;
  }

  public void setQtdAbertasDentroPrazo(Long qtdAbertasDentroPrazo) {
    this.qtdAbertasDentroPrazo = qtdAbertasDentroPrazo;
  }

  public Long getQtdAbertasPrazoVencido() {
    return qtdAbertasPrazoVencido;
  }

  public void setQtdAbertasPrazoVencido(Long qtdAbertasPrazoVencido) {
    this.qtdAbertasPrazoVencido = qtdAbertasPrazoVencido;
  }

  public Integer getSituacaoVencimento() {
    return situacaoVencimento;
  }

  public void setSituacaoVencimento(Integer situacaoVencimento) {
    this.situacaoVencimento = situacaoVencimento;
  }
  
}
