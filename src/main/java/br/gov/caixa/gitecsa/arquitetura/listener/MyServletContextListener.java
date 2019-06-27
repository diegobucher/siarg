package br.gov.caixa.gitecsa.arquitetura.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Classe que inicializa o contexto.
 */
public class MyServletContextListener implements ServletContextListener {

  /**
   * Implementação vazia.
   */
  public void contextDestroyed(ServletContextEvent sce) {

  }

  /**
   * Execução ao inicializar o contexto.
   */
  public void contextInitialized(ServletContextEvent sce) {
    System.getProperties().put("org.apache.el.parser.COERCE_TO_ZERO", "false");
  }
}
