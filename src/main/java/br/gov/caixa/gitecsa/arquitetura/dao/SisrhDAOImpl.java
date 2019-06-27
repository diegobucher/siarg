package br.gov.caixa.gitecsa.arquitetura.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SisrhDAOImpl<T> extends BaseDAOImpl<T> {

  @PersistenceContext(unitName = "sisrhPU")
  private EntityManager em;

  @Override
  public EntityManager getEntityManager() {
    return this.em;
  }
}
