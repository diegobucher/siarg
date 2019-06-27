/**
 * MenuDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 23-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.MenuDAO;
import br.gov.caixa.gitecsa.siarg.model.Menu;

/**
 * Implementação da interface MenuDAO.
 * @author f520296
 */
public class MenuDAOImpl extends BaseDAOImpl<Menu> implements MenuDAO {
  
  @Inject
  protected Logger logger;

  /**
   * Obter a lista de menus pais.
   * @return list
   */
  @Override
  public List<Menu> obterListaMenusPaisPorPerfil(Long idPerfil, Long idAbrangencia) {

    StringBuilder hql = new StringBuilder();

    hql.append(" SELECT DISTINCT menu ");
    hql.append(" FROM MenuPerfil menuPerfil ");
    hql.append(" INNER JOIN menuperfil.menu menu ");
    hql.append(" INNER JOIN menuperfil.abrangencia abran ");
    hql.append(" INNER JOIN menu.listaPerfis listaPerfis ");

    hql.append(" WHERE menu.menuPai is null ");
    hql.append(" AND listaPerfis.id = :idPerfil ");
    hql.append(" AND abran.id = :idAbrangencia ");
    hql.append(" ORDER BY menu.ordem ");

    TypedQuery<Menu> query = getEntityManager().createQuery(hql.toString(), Menu.class);

    query.setParameter("idPerfil", idPerfil);
    query.setParameter("idAbrangencia", idAbrangencia);

    return query.getResultList();

  }

  @Override
  public Menu findByIdEager(Long idMenu) {
    
    try {
      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT DISTINCT menu ");
      hql.append(" FROM Menu menu ");
      hql.append(" LEFT JOIN FETCH menu.listaPerfis listaPerfis ");
      hql.append(" WHERE menu.id = :idMenu ");
      
      TypedQuery<Menu> query = getEntityManager().createQuery(hql.toString(), Menu.class);
      
      query.setParameter("idMenu", idMenu);
      
      return query.getSingleResult();      
    } catch (Exception e) {
      return null;
    }
    
  }
  @Override
  public Menu findByIdEager(Long idMenu, Long idAbrangencia) {
    
    try {
      StringBuilder hql = new StringBuilder();
      
      hql.append(" SELECT DISTINCT menu ");
      hql.append(" FROM MenuPerfil menuPerfil ");
      hql.append(" INNER JOIN menuPerfil.menu menu ");
      hql.append(" LEFT JOIN FETCH menu.listaPerfis listaPerfis ");
      hql.append(" WHERE menu.id = :idMenu ");
      hql.append(" AND menuPerfil.abrangencia.id = :idAbrangencia ");
      
      TypedQuery<Menu> query = getEntityManager().createQuery(hql.toString(), Menu.class);
      
      query.setParameter("idMenu", idMenu);
      query.setParameter("idAbrangencia", idAbrangencia);
      
      return query.getSingleResult();      
    } catch (Exception e) {
      logger.info(e);
      return null;
    }
    
  }

}
