package br.gov.caixa.gitecsa.arquitetura.service;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Interface contendo operações de consulta e manutenção ao banco de dados.
 *
 * @param <T>
 *          Entidade sobre a qual serão realizadas as operações de consulta e manutenção.
 * 
 */
public interface BaseService<T> extends Serializable {

  public T findById(Long id) throws Exception;

  public List<T> consultar(T entity) throws Exception;

  public List<T> findAll() throws Exception;

  public T save(T entity) throws RequiredException, BusinessException, Exception;

  public T update(T entity) throws RequiredException, BusinessException, Exception;

  public List<T> update(Collection<T> listEntity) throws RequiredException, BusinessException, Exception;

  public void remove(T entity) throws BusinessException, Exception;

}
