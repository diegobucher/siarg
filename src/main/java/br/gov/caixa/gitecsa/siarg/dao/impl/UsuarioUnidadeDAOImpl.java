/**
 * UsuarioUnidadeDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 24-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.Calendar;

import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioUnidadeDAO;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * Implementação da interface UsuarioUnidadeDAO.
 * @author f520296
 */
public class UsuarioUnidadeDAOImpl extends BaseDAOImpl<UsuarioUnidade> implements UsuarioUnidadeDAO{
  
  
  @Override
  public UsuarioUnidade findByIdFetch(final Long id) {
    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT usua ");
      hql.append(" FROM UsuarioUnidade usua ");
      hql.append(" INNER JOIN FETCH usua.usuario");
      hql.append(" INNER JOIN FETCH usua.unidade");
      hql.append(" WHERE usua.id = :id ");

      TypedQuery<UsuarioUnidade> query = this.getEntityManager().createQuery(hql.toString(), UsuarioUnidade.class);

      query.setParameter("id", id);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }
  
  @Override
  public boolean existeVinculacaoConflitante(UsuarioUnidade usuarioUnidade) {
    
    try {
      StringBuilder hql = new StringBuilder();
      
      Calendar calDataNoFuturo = Calendar.getInstance();
      calDataNoFuturo.set(Calendar.YEAR, 2199);

      hql.append(" SELECT usua ");
      hql.append(" FROM UsuarioUnidade usua ");
      hql.append(" INNER JOIN FETCH usua.usuario usuario");
      hql.append(" INNER JOIN FETCH usua.unidade unidade");
      hql.append(" WHERE usuario.id = :idUsuario ");
      hql.append(" AND unidade.id = :idUnidade ");
      hql.append(" AND usua.dataInicio <= COALESCE(:dataFim, :dataFuturo)");
      hql.append(" AND COALESCE(usua.dataFim, :dataFuturo) >= :dataInicio ");
      if(usuarioUnidade.getId() != null) {
        hql.append(" AND usua.id != :idUsuarioUnidade ");
      }
      
      TypedQuery<UsuarioUnidade> query = this.getEntityManager().createQuery(hql.toString(), UsuarioUnidade.class);

      query.setParameter("idUsuario", usuarioUnidade.getUsuario().getId());
      query.setParameter("idUnidade", usuarioUnidade.getUnidade().getId());
      query.setParameter("dataInicio", usuarioUnidade.getDataInicio(), TemporalType.DATE);
      query.setParameter("dataFim", usuarioUnidade.getDataFim(), TemporalType.DATE);
      query.setParameter("dataFuturo", calDataNoFuturo.getTime(), TemporalType.DATE);
      if(usuarioUnidade.getId() != null) {
        query.setParameter("idUsuarioUnidade", usuarioUnidade.getId());
      }

      return query.getSingleResult() != null ? true : false;

    } catch (Exception e) {
      return false;
    }
  }
  

}
