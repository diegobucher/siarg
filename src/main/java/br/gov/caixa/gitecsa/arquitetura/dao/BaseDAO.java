/**
 * BaseDAO.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Interface para servir de base para os DAOs.
 * @author f520296.
 */
public interface BaseDAO<T> {

  /**
   * Efetua o Persist da entidade no banco.
   * @param entity Entidade a ser persistida
   * @return O Objeto persistido
   */
  public T save(final T entity);

  /**
   * Efetua o Merge da entidade no banco.
   * @param entity Entidade a ser atualizada
   * @return A instancia atualizada
   */
  public T update(final T entity);

  /**
   * Efetua o Remove da entidade no banco.
   * @param entity Entidade a ser removida.
   */
  public void delete(final T entity);

  /**
   * Pesquisa a Entidade pelo id.
   * @param id Id da entidade
   * @return Entidade encontrada
   */
  public T findById(final Serializable id);

  /**
   * Pesquisa todas as Entidades.
   * @return Lista com todas as entidades.
   */
  public List<T> findAll();

  /**
   * Pesquisa todas as Entidades com paginação.
   * @param first Registro inicial
   * @param max Quantidade de registros
   * @return Lista com todas as entidades.
   */
  public List<T> findAll(final int first, final int max);

  /**
   * Pesquisa a Entidade pelo Class e id informados.
   * @param classe Class da entidade
   * @param id Id da entidade
   * @return Entidade encontrada
   */
  public T find(final Class<T> classe, final Serializable id);

  /**
   * Efetua o Persist das entidades da Lista informada.
   * @param entity Lista da entidade
   * @return A propria lista informada.
   */
  public List<T> saveList(final List<T> entity);

  /**
   * Efetua o Merge das entidades da Lista informada.
   * @param entity Lista da entidade
   * @return A propria lista informada.
   */
  public List<T> updateList(final List<T> entity);

}
