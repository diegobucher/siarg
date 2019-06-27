package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.OcorrenciaLidaDAO;
import br.gov.caixa.gitecsa.siarg.model.OcorrenciaLida;

@Stateless
public class OcorrenciaLidaService extends AbstractService<OcorrenciaLida> {

  private static final long serialVersionUID = 1L;

  @Inject
  private OcorrenciaLidaDAO ocorrenciaLidaDAO;

  @Override
  public List<OcorrenciaLida> consultar(OcorrenciaLida entity) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(OcorrenciaLida entity) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void validaRegras(OcorrenciaLida entity) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void validaRegrasExcluir(OcorrenciaLida entity) {
    // TODO Auto-generated method stub

  }

  @Override
  protected BaseDAO<OcorrenciaLida> getDAO() {
    return ocorrenciaLidaDAO;
  }

  public OcorrenciaLida obterOcorrenciaLida(String matricula, Long idAbrangencia) {
    return ocorrenciaLidaDAO.obterOcorrenciaLida(matricula, idAbrangencia);
  }

}