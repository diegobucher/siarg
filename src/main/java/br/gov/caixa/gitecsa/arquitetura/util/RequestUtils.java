package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;

public class RequestUtils {

  public static StreamedContent download(File file, String filename) throws BusinessException {
    try {
      return new DefaultStreamedContent(new FileInputStream(file), FileUtils.getMimeType(file), filename);
    } catch (FileNotFoundException e) {
      throw new BusinessException(MensagemUtil.obterMensagem("MI028"));
    } catch (IOException e) {
      throw new BusinessException(MensagemUtil.obterMensagem("MI028"));
    }
  }

  /**
   * Criar uma resposta para chamadas vai remote command.
   * 
   * @param name
   *          Nome da váriavel de resposta
   * @param response
   *          Conteúdo da resposta
   */
  public static void addRemoteCommandResponse(final String name, final Object response) {
    RequestContext context = RequestContext.getCurrentInstance();
    context.addCallbackParam(name, response);
  }
  
  /**
   * Recupera um valor da sessão a partir da chave dada.
   * 
   * @param key
   *          Chave
   * @return Valor correspondente ou null
   */
  public static Object getSessionValue(String key) {
    if (ObjectUtils.isNullOrEmpty(FacesContext.getCurrentInstance())) {
      return null;
    }
    Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    return (!ObjectUtils.isNullOrEmpty(sessionMap)) ? sessionMap.get(key) : null;
  }
  
  /**
   * Recupera um valor da sessão a partir da chave dada. O valor é removido da sessão após sua chamada.
   * 
   * @param key
   *          Chave
   * @return Valor correspondente ou null
   */
  public static Object getFlashData(String key) {
    Object value = RequestUtils.getSessionValue(key);
    RequestUtils.unsetSessionValue(key);
    return value;
  }
  
  /**
   * Insere um valor na sessão.
   * 
   * @param key
   *          Chave
   * @param value
   *          Valor
   */
  public static void setSessionValue(String key, Object value) {
    RequestUtils.unsetSessionValue(key);
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
  }
  
  /**
   * Remove um valor da sessão a partir da chave dada.
   * 
   * @param key
   *          Chave
   */
  public static void unsetSessionValue(String key) {
    if (!ObjectUtils.isNullOrEmpty(FacesContext.getCurrentInstance())) {
      Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
      if (!ObjectUtils.isNullOrEmpty(sessionMap)) {
        sessionMap.remove(key);
      }
    }
  }
  
  /**
   * Redireciona a requisição para a URL especificada.
   * 
   * @param url
   *          URL de destino.
   * @throws IOException
   */
  public static void redirect(final String url) throws IOException {
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      context.redirect(context.getRequestContextPath() + url);
  }
  
  /**
   * Recupera a view ID (URL) da página atual.
   * 
   * @return URL da página.
   */
  public static String getViewId() {
    return FacesContext.getCurrentInstance().getViewRoot().getViewId();
  }
}
