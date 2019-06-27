/**
 * FluxoDemandaService.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.FluxoDemandaDAO;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;

/**
 * Classe de serviços para a entidade FluxoDemanda.
 * @author f520296
 */
@Stateless
public class FluxoDemandaService extends AbstractService<FluxoDemanda> {

  /** Serial. */
  private static final long serialVersionUID = 2196206567494993149L;

  @Inject
  private FluxoDemandaDAO fluxoDemandaDAO;

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.BaseService#consultar(java.lang.Object)
   */
  @Override
  public List<FluxoDemanda> consultar(FluxoDemanda entity) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaCamposObrigatorios(java.lang
   * .Object)
   */
  @Override
  protected void validaCamposObrigatorios(FluxoDemanda entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegras(java.lang.Object)
   */
  @Override
  protected void validaRegras(FluxoDemanda entity) {

  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegrasExcluir(java.lang.Object)
   */
  @Override
  protected void validaRegrasExcluir(FluxoDemanda entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#getDAO()
   */
  @Override
  protected BaseDAO<FluxoDemanda> getDAO() {
    return this.fluxoDemandaDAO;
  }
  
  public List<FluxoDemanda> findByIdDemanda(Long id){
    return fluxoDemandaDAO.findByIdDemanda(id);
  }
  
  public List<FluxoDemanda> findAtivosByIdDemanda(Long id) {
    return fluxoDemandaDAO.findAtivosByIdDemanda(id);
  }

  public FluxoDemanda obterFluxoDemandaComCaixaPostalUnidade(Long idFluxoDemanda) {
    return fluxoDemandaDAO.obterFluxoDemandaComCaixaPostalUnidade(idFluxoDemanda);
  }

  public Integer obterPrazoDaCaixaPostalAtualEncaminhadaExterna(Long idDemanda) {
    return fluxoDemandaDAO.obterPrazoDaCaixaPostalAtualEncaminhadaExterna(idDemanda);
  }

  public Integer obterPrazoDaCaixaPostalAtual(Long idDemanda, Long idCaixaPostalResponsavel) {
    return fluxoDemandaDAO.obterPrazoDaCaixaPostalAtual(idDemanda, idCaixaPostalResponsavel);
  }

  public CaixaPostal obterCaixaPostalFluxoAnteriorExterna(Demanda demanda) {
    return fluxoDemandaDAO.obterCaixaPostalFluxoAnteriorExterna(demanda);
  }


}
