/**
 * UsuarioDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioDAO;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * Implementação da interface UsuarioDAO.
 * @author f520296.
 */
public class UsuarioDAOImpl extends BaseDAOImpl<Usuario> implements UsuarioDAO {

  /**
   * Obter o Usuário por Matricula.
   */
  @Override
  public Usuario obterUsuarioExcessaoPorMatricula(final String matricula) {
    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT DISTINCT usuario ");
      hql.append(" FROM Usuario usuario ");
      hql.append(" INNER JOIN usuario.usuarioUnidadeList usuarioUnidadeList ");
      hql.append(" WHERE upper(usuario.matricula) = upper(:matricula) ");
      hql.append(" and ( usuarioUnidadeList.dataFim is null or usuarioUnidadeList.dataFim > current_timestamp ) ");

      TypedQuery<Usuario> query = this.getEntityManager().createQuery(hql.toString(), Usuario.class);

      query.setParameter("matricula", matricula);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }
  
  @Override
  public Usuario buscarUsuarioPorMatriculaFetch(final String matricula) {
    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT usuario ");
      hql.append(" FROM Usuario usuario ");
      hql.append(" INNER JOIN FETCH usuario.perfil");
      hql.append(" WHERE upper(usuario.matricula) = upper(:matricula) ");

      TypedQuery<Usuario> query = this.getEntityManager().createQuery(hql.toString(), Usuario.class);

      query.setParameter("matricula", matricula);

      return query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }
  
  @Override
  public List<UsuarioUnidade> buscarUsuarioUnidadePorMatricula(final String matricula, final Long idAbrangencia) {
    try {
      StringBuilder hql = new StringBuilder();

      hql.append(" SELECT usuarioUnidade ");
      hql.append(" FROM UsuarioUnidade usuarioUnidade ");
      hql.append("  INNER JOIN FETCH usuarioUnidade.unidade unidade ");
      hql.append("  INNER JOIN FETCH usuarioUnidade.usuario usuario ");
      hql.append("  INNER JOIN FETCH unidade.abrangencia abrangencia ");
      hql.append(" WHERE upper(usuario.matricula) = upper(:matricula) ");
      hql.append(" AND abrangencia.id = :idAbrangencia  ");
      hql.append(" ORDER BY usuarioUnidade.dataInicio DESC");

      TypedQuery<UsuarioUnidade> query = this.getEntityManager().createQuery(hql.toString(), UsuarioUnidade.class);

      query.setParameter("matricula", matricula);
      query.setParameter("idAbrangencia", idAbrangencia);

      return query.getResultList();

    } catch (NoResultException e) {
      return null;
    }
  }
}
