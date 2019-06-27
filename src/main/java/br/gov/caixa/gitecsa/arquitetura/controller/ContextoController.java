/**
 * ContextoController.java
 * Versão: 1.0.0.00
 * Data de Criação : 22/11/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */

package br.gov.caixa.gitecsa.arquitetura.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

/**
 * Classe de contexto para os conttrollers da aplicação.
 * @author f520296
 */
@SessionScoped
@Named
public class ContextoController implements Serializable {

  /** Constante */
  private static final int TEMPO_ESPERA = 250;

  /** Constante */
  private static final long serialVersionUID = -36222423182436073L;

  /** Variáveis Globais */
  private String crudMessage;

  /** Variáveis Globais */
  private Object object;

  /** Variáveis Globais */
  private Object objectFilter;

  /** Variáveis Globais */
  private String idComponente;

  /** Variáveis Globais */
  private String telaOrigem;

  /** Variáveis Globais */
  private String acao;

  /**
   * Após a Construção - Inicia após carga.
   */
  @PostConstruct
  public void init() {
    System.out.println("Sessão iniciada!");
  }

  /**
   * Executa antes de destruir o contexto.
   */
  @PreDestroy
  public void detroy() {
    System.out.println("Sessão encerrada!");
  }

  /**
   * Obter mensagem de crud.
   * @return string.
   */
  public String getCrudMessage() {
    return crudMessage;
  }

  /**
   * Atribuir valor a variável.
   * @param crudMessage **String crud**
   */
  public void setCrudMessage(final String crudMessage) {
    this.crudMessage = crudMessage;
  }

  /**
   * Obter o Object.
   * @return Object
   */
  public Object getObject() {
    return object;
  }

  /**
   * Atribuir valor a variável.
   * @param object **Objeto**
   */
  public void setObject(final Object object) {
    this.object = object;
  }

  /**
   * Restringe o tamanho de um atributo.
   * @param value **valor**
   * @param maximo **tamanho máximo**
   * @return valor truncado
   */
  public String truncaValor(final String value, final int maximo) {
    if (value.trim().length() > maximo) {
      return value.substring(0, maximo);
    }

    return value;
  }

  /**
   * Método usado para guardar o id do componente que chamou o evento. Esse método será usado normalmente em botões que
   * chamam dialogs.
   */
  public void guardarIdComponente() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> map = context.getExternalContext().getRequestParameterMap();
    idComponente = map.get("idComponente");
  }

  /**
   * Este método trabalha em conjunto com o método guardarIdComponente. Também será usado normalmente junto com os
   * dialogs. Quando o dialog for fechado este método dará foco no botão que chamou o dialog.
   * @throws InterruptedException Exception gerada
   */
  public void giveFocus() throws InterruptedException {
    Thread.sleep(TEMPO_ESPERA);
    RequestContext.getCurrentInstance().execute("giveFocus('" + idComponente + "')");
  }

  /**
   * Obter o ID do componente.
   * @return string
   */
  public String getIdComponente() {
    return idComponente;
  }

  /**
   * Gravar o ID do componente.
   * @param idComponente **Id do Componente**
   */
  public void setIdComponente(final String idComponente) {
    this.idComponente = idComponente;
  }

  /**
   * Método responsável por obter o nome da tela, que será utilizada para voltar a tela chamadadora ao clicar em
   * voltar.
   * @return telaOrigem
   */
  public String getTelaOrigem() {
    return telaOrigem;
  }

  /**
   * Método responsável por obter o nome da tela, que será utilizada para voltar a tela chamadadora ao clicar em
   * voltar.
   * @param telaOrigem **Tela Anterior**
   */
  public void setTelaOrigem(final String telaOrigem) {
    this.telaOrigem = telaOrigem;
  }

  /**
   * Propriedade utilizada para manter o filtro utilizada em uma tela de consulta.
   * @return objectFilter
   */
  public Object getObjectFilter() {
    return objectFilter;
  }

  /**
   * Propriedade utilizada para guardar o filtro utilizada em uma tela de consulta.
   * @param objectFilter **Filtro**
   */
  public void setObjectFilter(final Object objectFilter) {
    this.objectFilter = objectFilter;
  }

  /**
   * Limpa os campos.
   */
  public void limpar() {
    this.crudMessage = null;
    this.idComponente = null;
    this.object = null;
    this.objectFilter = null;
    this.telaOrigem = null;
  }

  /**
   * Obter a ação do componente.
   * @return string
   */
  public String getAcao() {
    return acao;
  }

  /**
   * Gravar a ação do componente.
   * @param acao **Ação**
   */
  public void setAcao(final String acao) {
    this.acao = acao;
  }
}

