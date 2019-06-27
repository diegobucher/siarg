/**
 * BaseViewCrud.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.service.BaseService;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;

/**
 * Classe base para CRUD de classes que utilizam o ViewScoped com apenas uma tela.
 * @author cagalvaom
 * @param <T> Parametro tipado
 */
public abstract class BaseViewCrud<T> extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1L;

  /** Variáveis de classe. */
  private T instance;

  /** Variáveis de classe. */
  private List<T> lista;

  /** Variáveis de classe. */
  private Long id;

  /**
   * Construtor Padrão sem argumentos.
   */
  public BaseViewCrud() {

  }

  /**
   * Retorna a service responsável pelo controle da página.
   * @return BaseService
   */
  protected abstract BaseService<T> getService();

  /**
   * Retorna o id da entity.
   * @return Long
   */
  protected abstract Long getEntityId(final T referenceValue);

  /**
   * Criacao da nova instancia.
   * @return T
   */
  protected abstract T newInstance();

  /**
   * Retorna a entity pelo id.
   * @return T
   */
  protected T load(final Long id) throws Exception {
    return getService().findById(id);
  }

  /**
   * Lista todas os objetos da classe.
   * @return - List com todos os objetos
   * @throws Exception - Exception gerada
   */
  protected List<T> getAll() throws Exception {
    return getService().findAll();
  }

  /**
   * Finaliza a edicao de um registro Normalmente esse metodo deve ser invocado chamando um metodo de atualizacao na
   * entidade(update).
   * @param referenceValue **Referência**
   * @throws RequiredException **Excessão**
   * @throws BusinessException **Excessão**
   * @throws Exception **Excessão**
   */
  protected void updateImpl(final T referenceValue) throws RequiredException, BusinessException, Exception {
    getService().update(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.atualizado"));
    limparForm();
  }

  /**
   * Finaliza a criacao de um registro Normalmente esse metodo deve ser invocado chamando um metodo de criacao na
   * entidade(insert).
   * @param referenceValue **Referência**
   * @throws RequiredException **Excessão**
   * @throws BusinessException **Excessão**
   * @throws Exception **Excessão**
   */
  protected void saveImpl(final T referenceValue) throws RequiredException, BusinessException, Exception {
    getService().save(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.salvo"));
    limparForm();
  }

  /**
   * Finaliza a remocao de um registro Normalmente esse metodo deve ser invocado chamando um metodo de remocao na
   * entidade(delete).
   * @param referenceValue **Referência**
   * @throws BusinessException **Excessão**
   * @throws Exception **Excessão**
   */
  protected void deleteImpl(final T referenceValue) throws BusinessException, Exception {
    getService().remove(referenceValue);
    getFacesMessager().info(MensagemUtil.obterMensagem("general.crud.excluido"));
  }

  /**
   * Indica se a instancia e nova, ou uma ja existente.
   * @return boolean
   */
  public boolean isManaged() {
    return getEntityId(instance) != null && getEntityId(instance) != 0;
  }

  /**
   * Carrega o instance com base no id.
   * @return T
   */
  public T loadInstance() {
    try {
      return load(getId());
    } catch (Exception e) {
      getRootErrorMessage(e);
    }

    return null;
  }

  /**
   * Lista todos os objetos da classe.
   * @return List
   */
  public List<T> allInstance() {
    try {
      return getAll();
    } catch (Exception e) {
      getRootErrorMessage(e);
    }

    return null;
  }

  /**
   * Método utilizado para limpar o formulario na tela de consulta.
   */
  public void limparForm() {
    id = null;
    instance = newInstance();
  }

  /**
   * Persiste ou atualiza uma instancia na base de dados.
   */
  public void save() {
    try {
      if (isManaged()) {
        updateImpl(getInstance());
      } else {
        saveImpl(getInstance());
      }
    } catch (RequiredException re) {
      getFacesMessager().addMessageError(re);
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(be);
    } catch (Exception e) {
      getFacesMessager().addMessageError(getRootErrorMessage(e));
    }
  }

  /**
   * Remove uma entidade.
   * @param event **Evento**
   */
  public void delete(final ActionEvent event) {
    try {
      deleteImpl(instance);
    } catch (BusinessException be) {
      getFacesMessager().addMessageError(be);
    } catch (Exception e) {
      getRootErrorMessage(e);
    }
  }

  /**
   * Retorna o instance existente ou cria um novo.
   * @return T Instance
   */
  public T getInstance() {
    if (instance == null) {
      if (getId() != null) {
        instance = loadInstance();
      } else {
        instance = newInstance();
      }
    }
    return instance;
  }

  /**
   * Gravar a Instância.
   * @param instance **Instância**
   */
  public void setInstance(final T instance) {
    this.instance = instance;
  }

  /**
   * Obter o ID.
   * @return id
   */
  public Long getId() {
    return id;
  }

  /**
   * Gravar o ID.
   * @param id **Id do componente**
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * Método responsável por informar se a instance é nula ou não.
   * @return boolean se instance é nulo
   */
  public boolean instanceIsNull() {
    return this.instance == null;
  }

  /**
   * Obter Lista.
   * @return list
   */
  public List<T> getLista() {
    return lista;
  }

  /**
   * Gravar Lista.
   * @param lista **List**
   */
  public void setLista(final List<T> lista) {
    this.lista = lista;
  }
}

