/**
 * FluxoDemandaDAOImpl.java
 * Versão: 1.0.0.00
 * 15/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.FluxoDemandaDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;

/**
 * Classe de implementação da interface FluxoDemandaDAO.
 * 
 * @author f520296
 */
public class FluxoDemandaDAOImpl extends BaseDAOImpl<FluxoDemanda> implements FluxoDemandaDAO {

  @Override
  public List<FluxoDemanda> findByIdDemanda(Long id) {

    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT fluDem ");
    hql.append(" FROM FluxoDemanda fluDem ");
    hql.append(" INNER JOIN fluDem.demanda dem ");
    hql.append(" INNER JOIN FETCH fluDem.caixaPostal caixaPostal ");
    hql.append(" INNER JOIN FETCH caixaPostal.unidade unidade ");
    hql.append(" WHERE dem.id = :idDemanda ");
    hql.append(" ORDER BY fluDem.ordem ");

    TypedQuery<FluxoDemanda> query = this.getEntityManager().createQuery(hql.toString(), FluxoDemanda.class);
    query.setParameter("idDemanda", id);

    return query.getResultList();
  }
  
  @Override
  public List<FluxoDemanda> findAtivosByIdDemanda(Long id) {
    
    StringBuilder hql = new StringBuilder();
    hql.append(" SELECT fluDem ");
    hql.append(" FROM FluxoDemanda fluDem ");
    hql.append(" INNER JOIN fluDem.demanda dem ");
    hql.append(" WHERE dem.id = :idDemanda ");
    hql.append(" AND fluDem.ativo = TRUE");
    hql.append(" ORDER BY fluDem.ordem ");
    
    TypedQuery<FluxoDemanda> query = this.getEntityManager().createQuery(hql.toString(), FluxoDemanda.class);
    query.setParameter("idDemanda", id);
    
    return query.getResultList();
  }

  @Override
  public FluxoDemanda obterFluxoDemandaComCaixaPostalUnidade(Long idFluxoDemanda) {
    try {

      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT DISTINCT fluxoDemanda ");
      hql.append(" FROM FluxoDemanda fluxoDemanda ");
      hql.append(" LEFT JOIN FETCH fluxoDemanda.caixaPostal caixaPostal ");
      hql.append(" LEFT JOIN FETCH caixaPostal.unidade unidade ");
      hql.append(" WHERE fluxoDemanda.id = :idFluxoDemanda ");
      
      TypedQuery<FluxoDemanda> query = this.getEntityManager().createQuery(hql.toString(), FluxoDemanda.class);
      
      query.setParameter("idFluxoDemanda", idFluxoDemanda);
      
      return query.getSingleResult();
      
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
    
  }

  @Override
  public Integer obterPrazoDaCaixaPostalAtualEncaminhadaExterna(Long idDemanda) {
    try {

      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT fluxoDemanda.prazo ");
      hql.append(" FROM FluxoDemanda fluxoDemanda ");
      hql.append(" INNER JOIN fluxoDemanda.demanda demanda ");
      hql.append(" INNER JOIN fluxoDemanda.caixaPostal caixaPostal ");
      hql.append(" INNER JOIN caixaPostal.unidade unidade ");
      hql.append(" WHERE 1 = 1 ");
      hql.append(" AND (unidade.tipoUnidade = :externa OR unidade.tipoUnidade = :arrobaExterna) ");
      hql.append(" AND demanda.id = :idDemanda ");
      
      TypedQuery<Integer> query = this.getEntityManager().createQuery(hql.toString(), Integer.class);
      
      query.setParameter("externa", TipoUnidadeEnum.EXTERNA);
      query.setParameter("arrobaExterna", TipoUnidadeEnum.ARROBA_EXTERNA);
      query.setParameter("idDemanda", idDemanda);
      
      return query.getSingleResult();
      
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Integer obterPrazoDaCaixaPostalAtual(Long idDemanda, Long idCaixaPostalResponsavel) {
    try {
      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT fluxoDemanda.prazo ");
      hql.append(" FROM FluxoDemanda fluxoDemanda ");
      hql.append(" INNER JOIN fluxoDemanda.demanda demanda ");
      hql.append(" INNER JOIN fluxoDemanda.caixaPostal caixaPostal ");
      hql.append(" INNER JOIN caixaPostal.unidade unidade ");
      hql.append(" WHERE 1 = 1 ");
      hql.append(" AND (unidade.tipoUnidade = :matriz OR unidade.tipoUnidade = :filial) ");
      hql.append(" AND caixaPostal.id = :idCaixaPostalResponsavel ");
      hql.append(" AND demanda.id = :idDemanda ");
      
      TypedQuery<Integer> query = this.getEntityManager().createQuery(hql.toString(), Integer.class);
      
      query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);
      query.setParameter("filial", TipoUnidadeEnum.FILIAL);
      query.setParameter("idCaixaPostalResponsavel", idCaixaPostalResponsavel);
      query.setParameter("idDemanda", idDemanda);
      
      return query.getSingleResult();
      
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public CaixaPostal obterCaixaPostalFluxoAnteriorExterna(Demanda demanda) {
    try {
      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT caixaPostal ");
      hql.append(" FROM FluxoDemanda fluxoDemanda ");
      hql.append(" INNER JOIN fluxoDemanda.demanda demanda ");
      hql.append(" INNER JOIN fluxoDemanda.caixaPostal caixaPostal ");
      hql.append(" INNER JOIN caixaPostal.unidade unidade ");
      hql.append(" WHERE 1 = 1 ");
      hql.append(" AND demanda.id = :idDemanda ");
      hql.append(" AND (unidade.tipoUnidade = :matriz OR unidade.tipoUnidade = :filial) ");
      hql.append(" AND fluxoDemanda.ativo = true ");
      hql.append(" ORDER BY fluxoDemanda.ordem DESC ");
      
      TypedQuery<CaixaPostal> query = this.getEntityManager().createQuery(hql.toString(), CaixaPostal.class).setMaxResults(1);
      
      query.setParameter("matriz", TipoUnidadeEnum.MATRIZ);
      query.setParameter("filial", TipoUnidadeEnum.FILIAL);
      query.setParameter("idDemanda", demanda.getId());
      
      return query.getSingleResult();
      
    } catch (NoResultException e) {
      return null;
    }
  }

}
