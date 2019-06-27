/**
 * FluxoAssuntoService.java
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
import br.gov.caixa.gitecsa.siarg.dao.FluxoAssuntoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;

/**
 * Classe: .
 * @author f520296
 */
@Stateless
public class FluxoAssuntoService extends AbstractService<FluxoAssunto> {

  /** Serial. */
  private static final long serialVersionUID = -8445514185516292110L;

  @Inject
  private FluxoAssuntoDAO fluxoAssuntoDAO;

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.BaseService#consultar(java.lang.Object)
   */
  @Override
  public List<FluxoAssunto> consultar(FluxoAssunto entity) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaCamposObrigatorios(java.lang
   * .Object)
   */
  @Override
  protected void validaCamposObrigatorios(FluxoAssunto entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegras(java.lang.Object)
   */
  @Override
  protected void validaRegras(FluxoAssunto entity) {

  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegrasExcluir(java.lang.Object)
   */
  @Override
  protected void validaRegrasExcluir(FluxoAssunto entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#getDAO()
   */
  @Override
  protected BaseDAO<FluxoAssunto> getDAO() {
    return this.fluxoAssuntoDAO;
  }

  /**
   * Método para obter todos os assuntos fetch.
   * @return list
   */
  public List<FluxoAssunto> obterTodosAssuntosFetch(TipoFluxoEnum tipoFluxo) {
    return this.fluxoAssuntoDAO.obterTodosAssuntosFetch(tipoFluxo);
  }
  
  public List<FluxoAssunto> obterFluxoAssuntoByAssuntoETipo(Assunto assunto, TipoFluxoEnum tipoFluxo){
    return this.fluxoAssuntoDAO.obterFluxoAssuntoByAssuntoETipo(assunto, tipoFluxo);
  }


}
