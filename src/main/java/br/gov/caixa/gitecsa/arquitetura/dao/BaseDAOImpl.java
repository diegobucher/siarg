/**
 * BaseDAOImpl.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Implementação da interface BaseDAO.
 * @author f520296.
 */
public abstract class BaseDAOImpl<T> extends AbstractBaseDAOImpl implements BaseDAO<T> {

  /** Classe persistente. */
  private Class<T> persistentClass;

  /**
   * Construtor.
   */
  @SuppressWarnings("unchecked")
  public BaseDAOImpl() {
    this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * Persistencia de DAOs.
   * @param entity **entity**
   * @return entity
   */
  @Override
  public T save(final T entity) {
    getEntityManager().persist(entity);

    return entity;
  }

  /**
   * Alteração de DAOs.
   * @param entity **entity**
   * @return entity
   */
  @Override
  public T update(final T entity) {
    return getEntityManager().merge(entity);
  }

  /**
   * Exclusão de DAOs.
   * @param entity **entity**
   */
  @Override
  public void delete(final T entity) {
    final T attachedEntity = getEntityManager().merge(entity);

    getEntityManager().remove(attachedEntity);
  }

  /**
   * Obter por chave primária.
   * @param id **id**
   * @return Entity
   */
  @SuppressWarnings("unchecked")
  @Override
  public T findById(final Serializable id) {
    return (T) getSession().get(persistentClass, id);
  }

  /**
   * Persistencia de lista de DAOs.
   * @param entitys **entitys**
   * @return entitys
   */
  @Override
  public List<T> saveList(final List<T> entitys) {

    final int batchSize = 100;

    for (int i = 0; i < entitys.size(); i++) {

      T entity = entitys.get(i);
      getEntityManager().persist(entity);

      if (i % batchSize == 0) {
        getEntityManager().flush();
        getEntityManager().clear();
      }
    }

    getEntityManager().flush();
    getEntityManager().clear();

    return entitys;
  }

  /**
   * Alteração de Lista de DAOs.
   * @param entitys **entitys**
   * @return entitys
   */
  @Override
  public List<T> updateList(final List<T> entitys) {

    final int batchSize = 100;

    for (int i = 0; i < entitys.size(); i++) {

      T entity = entitys.get(i);
      getEntityManager().merge(entity);

      if (i % batchSize == 0) {
        getEntityManager().flush();
        getEntityManager().clear();
      }
    }

    getEntityManager().flush();
    getEntityManager().clear();

    return entitys;
  }

  /**
   * Obter Lista de DAOs.
   * @return entityList
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<T> findAll() {
    return getEntityManager().createQuery("select a from " + persistentClass.getName() + " a").getResultList();
  }

  /**
   * Obter Lista de DAOs com limitador de quantidade.
   * @param first **primeiro**
   * @param max **Quantidade**
   * @return entityList
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<T> findAll(final int first, final int max) {
    final org.hibernate.Query query = getSession().createQuery("select a from " + persistentClass.getName() + " a");

    query.setFirstResult(first);

    query.setFetchSize(max);

    query.setMaxResults(max);

    return query.list();
  }

  /**
   * Obter Objeto.
   * @param classe **classe**
   * @param id **id**
   * @return Entity
   */
  @Override
  public T find(final Class<T> classe, final Serializable id) {
    return getEntityManager().find(classe, id);
  }
}

