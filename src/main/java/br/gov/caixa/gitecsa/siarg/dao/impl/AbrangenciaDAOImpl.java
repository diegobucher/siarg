/**
 * AbrangenciaDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 28-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.AbrangenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;

public class AbrangenciaDAOImpl extends BaseDAOImpl<Abrangencia> implements AbrangenciaDAO {
  
  @Inject
  protected Logger logger;

  @Override
  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(Long idUsuario) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT abrangencia ");
    hql.append(" FROM Abrangencia abrangencia ");
    hql.append(" INNER JOIN abrangencia.unidadesList unidadesList  ");
    hql.append(" INNER JOIN unidadesList.usuarioUnidadeList usuarioUnidadeList ");
    hql.append(" WHERE usuarioUnidadeList.usuario.id = :idUsuario ");
    hql.append(" AND unidadesList.ativo = :ativo ");
    hql.append(" ORDER BY abrangencia.nome ");

    TypedQuery<Abrangencia> query = this.getEntityManager().createQuery(hql.toString(), Abrangencia.class);

    query.setParameter("idUsuario", idUsuario);
    query.setParameter("ativo", Boolean.TRUE);

    return query.getResultList();
  }
  
  @Override
  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioExcessao(String matricula) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT DISTINCT abrangencia ");
    hql.append(" FROM Abrangencia abrangencia ");
    hql.append(" INNER JOIN abrangencia.unidadesList unidadesList  ");
    hql.append(" INNER JOIN unidadesList.usuarioUnidadeList usuarioUnidadeList ");
    hql.append(" WHERE UPPER(usuarioUnidadeList.usuario.matricula) = UPPER(:matricula) ");
    hql.append(" AND unidadesList.ativo = :ativo ");
    hql.append(" AND ( usuarioUnidadeList.dataFim is null or usuarioUnidadeList.dataFim >= current_timestamp ) ");
    hql.append(" ORDER BY abrangencia.nome ");

    
    TypedQuery<Abrangencia> query = this.getEntityManager().createQuery(hql.toString(), Abrangencia.class);
    
    query.setParameter("matricula", matricula);
    query.setParameter("ativo", Boolean.TRUE);
    
    return query.getResultList();
  }
  
  @Override
  public List<Abrangencia> obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(String matricula) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT DISTINCT abrangencia ");
    hql.append(" FROM Abrangencia abrangencia ");
    hql.append(" INNER JOIN abrangencia.unidadesList unidadesList  ");
    hql.append(" INNER JOIN unidadesList.caixasPostaisList caixasPostaisList  ");
    hql.append(" INNER JOIN unidadesList.usuarioUnidadeList usuarioUnidadeList ");
    hql.append(" WHERE UPPER(usuarioUnidadeList.usuario.matricula) = UPPER(:matricula) ");
    hql.append(" AND unidadesList.ativo = :ativo ");
    hql.append(" AND ( usuarioUnidadeList.dataFim is null or usuarioUnidadeList.dataFim >= current_timestamp ) ");
    hql.append(" ORDER BY abrangencia.nome ");
    
    TypedQuery<Abrangencia> query = this.getEntityManager().createQuery(hql.toString(), Abrangencia.class);
    
    query.setParameter("matricula", matricula);
    query.setParameter("ativo", Boolean.TRUE);
    
    return query.getResultList();
  }
  
  @Override
  public Abrangencia obterAbrangenciaPorDemanda(Long idDemanda) {
    
    StringBuilder hql = new StringBuilder();
    
    hql.append(" SELECT DISTINCT abrangencia ");
    hql.append(" FROM Demanda demanda");
    hql.append(" INNER JOIN demanda.caixaPostalDemandante cxDem ");
    hql.append(" INNER JOIN cxDem.unidade uniDem ");
    hql.append(" INNER JOIN uniDem.abrangencia abrangencia ");
    hql.append(" WHERE demanda.id = :idDemanda");
    
    TypedQuery<Abrangencia> query = this.getEntityManager().createQuery(hql.toString(), Abrangencia.class);
    
    query.setParameter("idDemanda", idDemanda);
    
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      logger.info(e);
      return null;
    }
  }
}
