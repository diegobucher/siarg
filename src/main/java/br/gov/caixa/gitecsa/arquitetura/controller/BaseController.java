/**
 * BaseController.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.web.FacesMessager;

/**
 * Classe Base para Controller do JSF.
 * @author f520296
 */
public abstract class BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = -3630069325535795544L;

  /** Injeção de Dependência. */
  @Inject
  protected FacesMessager facesMessager;

  /** Constante. */
  public static final Logger LOGGER = Logger.getLogger(BaseController.class);

  /**
   * Construtor default.
   */
  public BaseController() {
    super();
  }

  /**
   * AjaxHandler que não faz nada para ser utilizado em necessidades de executar Ajax.
   * @return null
   */
  public String ajaxHandler() {
    return null;
  }

  /**
   * Esconde o componente do id especificado.
   * @param idComponente **ID do Componente**
   */
  protected void hideDialog(final String idComponente) {
    RequestContext.getCurrentInstance().execute(idComponente + ".hide()");
  }

  /**
   * Mostra o componente do id especificado.
   * @param idComponente **ID do Componente**
   */
  protected void showDialog(final String idComponente) {
    RequestContext.getCurrentInstance().execute(idComponente + ".show()");
  }

  /**
   * Faz update nos componentes dos respectivos ids passados como parâmetro.
   * @param idComponente **ID do Componente**
   */
  protected void updateComponentes(final String... idComponente) {
    for (String id : idComponente) {
      FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
  }

  /**
   * Obter mensagem de erro.
   * @param e **Exception**
   * @return String
   */
  protected String getRootErrorMessage(final Exception e) {
    String errorMessage = MensagemUtil.obterMensagem("general.crud.rootErrorMessage");
    if (e == null) {
      // This shouldn't happen, but return the default messages
      return errorMessage;
    }

    JSFUtil.addErrorMessage(errorMessage);

    // Start with the exception and recurse to find the root cause
    Throwable t = e;
    while (t != null) {
      // Get the message from the Throwable class instance
      errorMessage = t.getLocalizedMessage();
      t = t.getCause();
    }
    // This is the root cause message
    return errorMessage;
  }

  /**
   * Obter Faces Messager.
   * @return facesMessager
   */
  public FacesMessager getFacesMessager() {
    return facesMessager;
  }

  /**
   * Gravar Faces Messager.
   * @param facesMessager **facesMessager**
   */
  public void setFacesMessager(final FacesMessager facesMessager) {
    this.facesMessager = facesMessager;
  }

  /**
   * Obter Log.
   * @return logger
   */
  public abstract Logger getLogger();

}
