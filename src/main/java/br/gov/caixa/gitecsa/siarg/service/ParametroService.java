package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.model.Parametro;

@Stateless
public class ParametroService extends AbstractService<Parametro>{

  /** Serial. */
  private static final long serialVersionUID = 8135391370812345624L;

  @Inject
  private ParametroDAO parametroDAO;
  
  @Override
  public List<Parametro> consultar(Parametro entity) throws Exception {
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(Parametro entity) {
    
  }

  @Override
  protected void validaRegras(Parametro entity) {
    
  }

  @Override
  protected void validaRegrasExcluir(Parametro entity) {
    
  }

  @Override
  protected BaseDAO<Parametro> getDAO() {
    return this.parametroDAO;
  }

  public Parametro obterParametroByNome(String param) {
    return parametroDAO.obterParametroByNome(param);
  }

}
