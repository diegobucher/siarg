package br.gov.caixa.gitecsa.siico.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.siico.dao.UnidadeSiicoDAO;
import br.gov.caixa.gitecsa.siico.vo.UnidadeSiicoVO;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class UnidadeSiicoService implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1l;

  /** Injeção de Dependência. */
  @Inject
  private UnidadeSiicoDAO unidadeSiicoDAO;

  public UnidadeSiicoVO obterUnidadePorCGC(Integer numeroCGC) {
    return unidadeSiicoDAO.obterUnidadePorCGC(numeroCGC);
  }

}
