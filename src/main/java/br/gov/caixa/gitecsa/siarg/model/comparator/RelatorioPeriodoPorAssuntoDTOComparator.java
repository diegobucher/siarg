package br.gov.caixa.gitecsa.siarg.model.comparator;

import java.util.Comparator;

import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;

public class RelatorioPeriodoPorAssuntoDTOComparator implements Comparator<RelatorioPeriodoPorAssuntoDTO> {

  @Override
  public int compare(RelatorioPeriodoPorAssuntoDTO rel1, RelatorioPeriodoPorAssuntoDTO rel2) {
    return rel1.getAreaDemandada().toUpperCase().compareTo(rel2.getAreaDemandada().toUpperCase());
  }

}
