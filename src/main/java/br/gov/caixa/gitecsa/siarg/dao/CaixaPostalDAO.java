/**
 * CaixaPostalDAO.java
 * Versão: 1.0.0.00
 * 11/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

/**
 * Interface DAO de Caixa Postal.
 * @author f520296
 */
public interface CaixaPostalDAO extends BaseDAO<CaixaPostal> {

  /**
   * Obtém as caixas postais da unidade.
   * @param unidade Unidade
   * @return Lista de caixas-postais da unidade
   */
  List<CaixaPostal> findByUnidade(Unidade unidade);

  /**
   * Obtém as caixas postais cuja a unidade é de um dos tipos especificados
   * @param tiposUnidade Tipo de unidade
   * @return Lista de caixas postais
   */
  List<CaixaPostal> findByTipoUnidade(final TipoUnidadeEnum... tiposUnidade);

  /**
   * Obtém as caixas postais a partir de um range de ids.
   * @param ids Lista de ids
   * @return Lista de caixas postais
   */
  List<CaixaPostal> findByRangeId(final List<Long> ids);

  List<CaixaPostal> findObservadoresByDemanda(Long idDemanda);

  List<CaixaPostal> findObservadoresByAssunto(Long idAssunto);

  List<CaixaPostal> findByAbrangenciaTipoUnidade(Abrangencia abrangencia, TipoUnidadeEnum... tiposUnidade);

  CaixaPostal findByIdFetch(Long idCaixaPostal);

}
