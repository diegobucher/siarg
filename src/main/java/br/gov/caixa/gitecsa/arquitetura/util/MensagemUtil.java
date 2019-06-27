package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

public final class MensagemUtil {

  private static Properties mensagens;

  public static final Logger logger = Logger.getLogger(MensagemUtil.class);

  private MensagemUtil() {
  }

  private static Properties getMensagens() {
    inicializaProperties();
    return mensagens;
  }

  private static synchronized void inicializaProperties() {
    if (mensagens == null || mensagens.isEmpty()) {
      mensagens = new Properties();
      try {
        InputStream arquivoProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream("bundle.properties");

        mensagens.load(arquivoProperties);
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  /**
   * Obtem a mensagem do bundle e substitui os paramentros.
   */
  public static String obterMensagem(String codigoMensagem, Object... parametros) {
    String mensagem = getMensagens().getProperty(codigoMensagem);
    if (mensagem != null) {
      return MessageFormat.format(mensagem, transformaListaMensagem(parametros));
    }

    return codigoMensagem;
  }

  /**
   * Retorna a mensagem do bundle com a key informada.
   */
  public static String obterMensagem(String codigoMensagem) {
    return obterMensagem(codigoMensagem, new Object[] {});
  }

  /**
   * Retorna uma lista de mensagens dos parametros. Caso exista retorna o valor, caso não exista retorna o proprio
   * parametro.
   */
  public static Object[] transformaListaMensagem(Object[] parametros) {
    if (!Util.isNullOuVazio(parametros)) {
      Object[] novoArray = new Object[parametros.length];
      for (int i = 0; i < parametros.length; i++) {
        Object codigo = parametros[i];
        String mensagem = getMensagens().getProperty((String) codigo);
        if (mensagem != null) {
          novoArray[i] = mensagem;
        } else {
          novoArray[i] = (String) codigo;
        }
      }
      return novoArray;
    }
    return parametros;
  }

  /**
   * Wrapper para o setter do keepMessages.
   * 
   * @param value
   */
  public static void setKeepMessages(Boolean value) {
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(value);
  }

}
