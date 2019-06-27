/**
 * AbrangenciaService.java
 * Versão: 1.0.0.00
 * 23/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.AbrangenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;

/**
 * Classe de serviço da entidade Abrangencia.
 * @author f520296
 */
@Stateless
public class AbrangenciaService extends AbstractService<Abrangencia> {

  /** Serial. */
  private static final long serialVersionUID = -1731428498623406020L;

  @Inject
  private AbrangenciaDAO abrangenciaDAO;

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.BaseService#consultar(java.lang.Object)
   */
  @Override
  public List<Abrangencia> consultar(Abrangencia entity) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaCamposObrigatorios(java.lang
   * .Object)
   */
  @Override
  protected void validaCamposObrigatorios(Abrangencia entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegras(java.lang.Object)
   */
  @Override
  protected void validaRegras(Abrangencia entity) {

  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegrasExcluir(java.lang.Object)
   */
  @Override
  protected void validaRegrasExcluir(Abrangencia entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#getDAO()
   */
  @Override
  protected BaseDAO<Abrangencia> getDAO() {
    return this.abrangenciaDAO;
  }
  
  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(String matricula) {
    return abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(matricula);
  }

  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(Long id) {
    return abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(id);
  }
  
  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(String matricula) {
    return abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(matricula);
  }
  
  public Abrangencia obterAbrangenciaPorDemanda(Long idDemanda) {
    return abrangenciaDAO.obterAbrangenciaPorDemanda(idDemanda);
  }



}
