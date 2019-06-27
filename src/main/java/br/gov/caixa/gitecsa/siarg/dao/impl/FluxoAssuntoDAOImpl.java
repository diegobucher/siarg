/**
 * FluxoAssuntoDAOImpl.java
 * Versão: 1.0.0.00
 * 23/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.FluxoAssuntoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;

/**
 * Classe: .
 * 
 * @author f520296
 */
public class FluxoAssuntoDAOImpl extends BaseDAOImpl<FluxoAssunto> implements
		FluxoAssuntoDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.caixa.gitecsa.siarg.dao.FluxoAssuntoDAO#obterTodosAssuntosFetch()
	 */
	@Override
	public List<FluxoAssunto> obterTodosAssuntosFetch(TipoFluxoEnum tipoFluxo) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT fluxo ");
		hql.append(" FROM FluxoAssunto fluxo ");
		hql.append(" LEFT JOIN FETCH fluxo.caixaPostal caixaPostal ");
		hql.append(" LEFT JOIN FETCH fluxo.assunto assunto ");
		hql.append(" WHERE 1 = 1 ");
		if (TipoFluxoEnum.DEMANDANTE_DEFINIDO.equals(tipoFluxo)) {
			hql.append(" AND fluxo.tipoFluxo = :tipoFluxo ");
		}
		hql.append(" ORDER BY fluxo.ordem ");

		TypedQuery<FluxoAssunto> query = this.getEntityManager().createQuery(
				hql.toString(), FluxoAssunto.class);

		if (TipoFluxoEnum.DEMANDANTE_DEFINIDO.equals(tipoFluxo)) {
			query.setParameter("tipoFluxo", tipoFluxo);
		}

		return query.getResultList();
	}
	
  @Override
  public List<FluxoAssunto> obterFluxoAssuntoByAssuntoETipo(Assunto assunto, TipoFluxoEnum tipoFluxo) {
    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT fluxo ");
    hql.append(" FROM FluxoAssunto fluxo ");
    hql.append(" LEFT JOIN FETCH fluxo.caixaPostal caixaPostal ");
    hql.append(" LEFT JOIN FETCH fluxo.assunto assunto ");
    hql.append(" WHERE assunto.id = :idAssunto");
    if (tipoFluxo != null) {
      hql.append(" AND fluxo.tipoFluxo = :tipoFluxo ");
    }
    hql.append(" ORDER BY fluxo.ordem ");

    TypedQuery<FluxoAssunto> query = this.getEntityManager().createQuery(
        hql.toString(), FluxoAssunto.class);

    query.setParameter("idAssunto", assunto.getId());
    if (tipoFluxo != null) {
      query.setParameter("tipoFluxo", tipoFluxo);
    }

    return query.getResultList();
  }

}
