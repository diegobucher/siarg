package br.gov.caixa.gitecsa.siarg.model.comparator;

import java.util.Comparator;

import br.gov.caixa.gitecsa.siarg.model.Assunto;

public class AssuntoResponsavelArvoreCompletaComparator implements Comparator<Assunto>{

  @Override
  public int compare(Assunto assunto1, Assunto assunto2) {
    
    String a1String = assunto1.getArvoreCompleta() + assunto1.getCaixaPostal().getSigla() ;
    String a2String = assunto2.getArvoreCompleta() + assunto2.getCaixaPostal().getSigla() ;
    
    return a1String.compareTo(a2String);
  }

}
