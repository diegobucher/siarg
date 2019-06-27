package br.gov.caixa.gitecsa.arquitetura.util;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;

public class JSFUtil {

  public static String getRequestParameter(String name) {
    return (String) getContext().getExternalContext().getRequestParameterMap().get(name);
  }

  /**
   * Retorna um objeto setado na sessão.
   */
  public static Object getSessionMapValue(String key) {
    if (getContext() != null) {
      return getContext().getExternalContext().getSessionMap().get(key);
    } else {
      return null;
    }
  }

  public static void setSessionMapValue(String key, Object value) {
    getContext().getExternalContext().getSessionMap().put(key, value);
  }

  public static Object getApplicationMapValue(String key) {
    return getContext().getExternalContext().getApplicationMap().get(key);
  }

  public static void setApplicationMapValue(String key, Object value) {
    getContext().getExternalContext().getApplicationMap().put(key, value);
  }

  /**
   * Retorna o ServletResponse.
   */
  public static HttpServletResponse getServletResponse() {
    FacesContext context = getContext();

    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

    return response;
  }

  public static FacesContext getContext() {
    return FacesContext.getCurrentInstance();
  }

  /**
   * Adiciona uma mensagem de erro.
   */
  public static void addErrorMessage(Exception ex, String defaultMsg) {
    String msg = ex.getLocalizedMessage();
    if (msg != null && msg.length() > 0) {
      addErrorMessage(msg);
    } else {
      addErrorMessage(defaultMsg);
    }
  }

  public static void addErrorMessage(String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
    getContext().addMessage(null, facesMsg);
  }

  /**
   * Retorna o UsuarioLdap da sessão.
   */
  public static UsuarioLdap getUsuario() {
    if (getSessionMapValue("usuario") != null) {
      return (UsuarioLdap) getSessionMapValue("usuario");
    }
    return null;
  }

  /**
   * Verifica se o perfil passado está na sessão.
   */
  @SuppressWarnings("unchecked")
  public static boolean isPerfil(String vlr) {

    List<String> perfilUsuario = (List<String>) getSessionMapValue("perfisUsuario");

    for (String perfil : perfilUsuario) {
      if (perfil.trim().toUpperCase().equalsIgnoreCase(vlr.trim().toLowerCase())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Cria um array de selectItem.
   * 
   * @param entities
   *          List
   * @param selectOne
   *          boolean
   * @return SelectItem[]
   */
  public static SelectItem[] getSelectItems(final List<?> entities, final boolean selectOne) {
    int size = selectOne ? entities.size() + 1 : entities.size();

    SelectItem[] items = new SelectItem[size];

    int i = 0;

    if (selectOne) {
      items[0] = new SelectItem("", "---");
      i++;
    }

    for (Object x : entities) {
      items[i++] = new SelectItem(x, x.toString());
    }

    return items;
  }

  /**
   * Adiciona mensagens de erro.
   * 
   * @param messages
   *          List de mensagens
   */
  public static void addErrorMessages(final List<String> messages) {
    for (String message : messages) {
      addErrorMessage(message);
    }
  }

  /**
   * Adiciona um Message do tipo Warn.
   * 
   * @param msg
   *          message
   */
  public static void addWarnMessage(final String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
    FacesContext.getCurrentInstance().addMessage("Mensagem", facesMsg);
  }

  /**
   * Adiciona um Message do tipo Success.
   * 
   * @param msg
   *          message
   */
  public static void addSuccessMessage(final String msg) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
    FacesContext.getCurrentInstance().addMessage("Sucesso", facesMsg);
  }

  /**
   * Adiciona um Message do tipo Success.
   * 
   * @param msg
   *          message
   * @param fiedlId
   *          id do campo
   */
  public static void addSuccessMessage(final String msg, final String fiedlId) {
    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
    FacesContext.getCurrentInstance().addMessage(fiedlId, facesMsg);
  }

  /**
   * Retorna o objeto pelo request parameter informado.
   * 
   * @param requestParameterName
   *          String
   * @param converter
   *          Converter
   * @param component
   *          UIComponent
   * @return Object
   */
  public static Object getObjectFromRequestParameter(final String requestParameterName, final Converter converter,
      final UIComponent component) {
    String theId = JSFUtil.getRequestParameter(requestParameterName);
    return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
  }

  /**
   * Adiciona uma mensagem de validação.
   * 
   * @param validacao
   *          Boolean
   * @param message
   *          String
   */
  public static void setListenerValidadao(final Boolean validacao, final String message) {
    RequestContext context = RequestContext.getCurrentInstance();

    if (!validacao) {
      JSFUtil.addErrorMessage(message);
    }

    context.addCallbackParam("validacao", validacao);
  }

  /**
   * Retorna o IP.
   * 
   * @return String
   */
  public static String getIP() {
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

    String ip = request.getLocalAddr();

    return ip;
  }

  /**
   * Retorna o FlashScope.
   * 
   * @return Flash
   */
  public static Flash flashScope() {
    return (FacesContext.getCurrentInstance().getExternalContext().getFlash());
  }

}
