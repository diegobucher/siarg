package br.gov.caixa.gitecsa.siarg.dto;

import java.util.List;

import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class RelatorioVisaoGeralPorSUEGConsolidadoXls {
  
  private Unidade unidadeSubordinacao;
  
  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> relatorios;

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
   * @return the relatorios
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getRelatorios() {
    return relatorios;
  }

  /**
   * @param relatorios the relatorios to set
   */
  public void setRelatorios(List<RelatorioGeralVisaoSuegPorUnidadesDTO> relatorios) {
    this.relatorios = relatorios;
  }

}
