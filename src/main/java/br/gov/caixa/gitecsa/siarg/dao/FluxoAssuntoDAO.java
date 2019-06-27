/**
 * FluxoAssuntoDAO.java
 * Versão: 1.0.0.00
 * 23/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;

/**
 * Classe: .
 * @author f520296
 */
public interface FluxoAssuntoDAO extends BaseDAO<FluxoAssunto> {

  /**
   * Método: .
   * @return
   */
  List<FluxoAssunto> obterTodosAssuntosFetch(TipoFluxoEnum tipoFluxo);

  List<FluxoAssunto> obterFluxoAssuntoByAssuntoETipo(Assunto assunto, TipoFluxoEnum tipoFluxo);

}
