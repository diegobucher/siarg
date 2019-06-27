/**
 * PerfilDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.siarg.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.model.Perfil;

/**
 * Implementação da interface PerfilDAO.
 * @author f520296
 */
public class PerfilDAOImpl extends BaseDAOImpl<Perfil> implements PerfilDAO {

  /**
   * Obter o perfil do usuário por Id.
   */
  @Override
  public Perfil obterPerfilPorId(Long id) {

    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT perfil ");
      hql.append(" FROM Perfil perfil ");
      hql.append(" WHERE perfil.id = :id ");

      TypedQuery<Perfil> query = getEntityManager().createQuery(hql.toString(), Perfil.class);

      query.setParameter("id", id);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

}
