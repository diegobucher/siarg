package br.gov.caixa.gitecsa.siarg.model.comparator;

import java.util.Comparator;

import br.gov.caixa.gitecsa.siarg.model.Assunto;

public class AssuntoNomeComparator implements Comparator<Assunto> {

  @Override
  public int compare(Assunto ass1, Assunto ass2) {
    return ass1.getNome().toUpperCase().compareTo(ass2.getNome().toUpperCase());
  }

}
