/**
 * FluxoDemandaDAO.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;

/**
 * Interface DAO para FluxoDemanda.
 * @author f520296
 */
public interface FluxoDemandaDAO extends BaseDAO<FluxoDemanda> {
  
  public List<FluxoDemanda> findByIdDemanda(Long id);

  public FluxoDemanda obterFluxoDemandaComCaixaPostalUnidade(Long idFluxoDemanda);

  public Integer obterPrazoDaCaixaPostalAtualEncaminhadaExterna(Long idDemanda);

  public Integer obterPrazoDaCaixaPostalAtual(Long idDemanda, Long idCaixaPostalResponsavel);

  public CaixaPostal obterCaixaPostalFluxoAnteriorExterna(Demanda demanda);

  List<FluxoDemanda> findAtivosByIdDemanda(Long id);

}
