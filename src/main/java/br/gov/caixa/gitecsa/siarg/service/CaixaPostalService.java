/**
 * CaixaPostalService.java
 * Versão: 1.0.0.00
 * 11/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

/**
 * Classe de serviços da caixa postal.
 * @author f520296
 */
@Stateless
public class CaixaPostalService extends AbstractService<CaixaPostal> {

  /** Serial. */
  private static final long serialVersionUID = 8210723343424626457L;

  @Inject
  private CaixaPostalDAO caixaPostalDAO;

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.BaseService#consultar(java.lang.Object)
   */
  @Override
  public List<CaixaPostal> consultar(CaixaPostal entity) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaCamposObrigatorios(java.lang
   * .Object)
   */
  @Override
  protected void validaCamposObrigatorios(CaixaPostal entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegras(java.lang.Object)
   */
  @Override
  protected void validaRegras(CaixaPostal entity) {

  }

  /*
   * (non-Javadoc)
   * @see
   * br.gov.caixa.gitecsa.arquitetura.service.AbstractService#validaRegrasExcluir(java.lang.Object)
   */
  @Override
  protected void validaRegrasExcluir(CaixaPostal entity) {

  }

  /*
   * (non-Javadoc)
   * @see br.gov.caixa.gitecsa.arquitetura.service.AbstractService#getDAO()
   */
  @Override
  protected BaseDAO<CaixaPostal> getDAO() {
    return this.caixaPostalDAO;
  }

  /**
   * Obtém as caixas-postais da unidade.
   * @param unidade Unidade
   * @return Lista de caixas-postais da unidade
   */
  public List<CaixaPostal> findByUnidade(final Unidade unidade) {
    return this.caixaPostalDAO.findByUnidade(unidade);
  }

  /**
   * Obtém as caixas-postais da unidade.
   * @param unidade Unidade
   * @return Lista de caixas-postais da unidade
   */
  public List<CaixaPostal> findByUnidade(final UnidadeDTO unidade) {
    Unidade entity = new Unidade();
    entity.setId(unidade.getId());
    return this.caixaPostalDAO.findByUnidade(entity);
  }

  /**
   * Obtém as caixas-postais cuja a unidade é de um dos tipos especificados
   * @param tiposUnidade Tipo de unidade
   * @return Lista de caixas-postais
   */
  public List<CaixaPostal> findByTipoUnidade(final TipoUnidadeEnum... tiposUnidade) {
    return this.caixaPostalDAO.findByTipoUnidade(tiposUnidade);
  }
  
  public List<CaixaPostal> findByAbrangenciaTipoUnidade(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade){
    return this.caixaPostalDAO.findByAbrangenciaTipoUnidade(abrangencia, tiposUnidade);
  }


  /**
   * Obtém as caixas postais a partir de um range de ids.
   * @param ids Lista de ids
   * @return Lista de caixas postais
   */
  public List<CaixaPostal> findByRangeId(final List<Long> ids) {
    return this.caixaPostalDAO.findByRangeId(ids);
  }

  public List<CaixaPostal> findObservadoresByDemanda(Long idDemanda) {
    return this.caixaPostalDAO.findObservadoresByDemanda(idDemanda);
  }

  public List<CaixaPostal> findObservadoresByAssunto(Long idAssunto) {
    return this.caixaPostalDAO.findObservadoresByAssunto(idAssunto);
  }
  
  public CaixaPostal findByIdFetch(Long idCaixaPostal) {
    return this.caixaPostalDAO.findByIdFetch(idCaixaPostal);
  }


}
