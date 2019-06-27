package br.gov.caixa.gitecsa.siarg.model.comparator;

import java.util.Comparator;

import br.gov.caixa.gitecsa.siarg.dto.RelatorioDemandasPorSituacaoDTO;

public class RelatorioDemandasPorSituacaoDTOComparator implements Comparator<RelatorioDemandasPorSituacaoDTO> {

  @Override
  public int compare(RelatorioDemandasPorSituacaoDTO rel1, RelatorioDemandasPorSituacaoDTO rel2) {
    return rel1.getCaixaPostalEnvolvida().toUpperCase().compareTo(rel2.getCaixaPostalEnvolvida().toUpperCase());
  }

}
