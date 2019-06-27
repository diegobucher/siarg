/**
 * AbstractBaseDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

/**
 * Classe abstrata de base para os DAOs herdarem.
 * @author f520296.
 */
public abstract class AbstractBaseDAOImpl {

  /**
   * Entity Manager com Persistence Unit.
   */
  @PersistenceContext(unitName = "siargPU")
  private EntityManager em;

  /**
   * Obter a sessão.
   * @return sessão
   */
  protected Session getSession() {
    return em.unwrap(Session.class);
  }

  /**
   * Settando valor no entity manager.
   * @param em **Entity Manager em**
   */
  public void setEntityManager(final EntityManager em) {
    this.em = em;
  }

  /**
   * Obtendo o entity manager.
   * @return em.
   */
  public EntityManager getEntityManager() {
    return this.em;
  }

}
