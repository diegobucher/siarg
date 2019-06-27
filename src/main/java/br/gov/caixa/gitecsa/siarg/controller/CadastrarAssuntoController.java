/**
 * InclusaoDemandaController.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.AuditoriaService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.PerfilService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.service.UsuarioService;

/**
 * Classe Controller para tela de cadastro de usuario
 */
@Named
@ViewScoped
public class CadastrarAssuntoController extends BaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Injections necessários
   */
  @Inject
  @ConfigurationRepository("configuracoes.properties")
  private Properties configuracoes;

  @EJB
  private UsuarioService usuarioService;

  @EJB
  private AbrangenciaService abrangenciaService;

  @EJB
  private UnidadeService unidadeService;

  @EJB
  private PerfilService perfilService;

  @EJB
  private AuditoriaService auditoria;

  @EJB
  private DemandaService demandaService;

  @EJB
  private AssuntoService assuntoService;

  /*
   * Campos da tela
   */

  /*
   * Campos do Modal
   */

  /** Abrangência selecionada */
  private Abrangencia abrangencia;

  private Unidade unidadeLogada;

  private String matriculaLogado;

  private List<Assunto> assuntosList;

  private Assunto assuntoEmDetalhe;

  private String arvoreAssuntoStr;

  private String nomeCategoria;

  private String mensagemConfirmacaoExclusao;

  private Assunto assuntoAExcluir;

  @PostConstruct
  public void init() {
    limpar();

    UnidadeDTO unidadeDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.unidadeLogada = this.unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());

    UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
    matriculaLogado = usuario.getNuMatricula();
    abrangencia = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    
    carregarCategorias();
  }

  private void limpar() {
    nomeCategoria = null;
    assuntoEmDetalhe = null;
    arvoreAssuntoStr = null;
    assuntosList = new ArrayList<Assunto>();
  }

  public void carregarCategorias() {
    limpar();
    if (abrangencia != null) {
      assuntosList = assuntoService.findCategoriasNaoExcluidos(abrangencia);
    } else {
      assuntosList = new ArrayList<Assunto>();
    }
  }

  public void detalharAssunto(Assunto assunto) throws DataBaseException {
    this.assuntoEmDetalhe = assuntoService.findByIdEager(assunto.getId());
    this.arvoreAssuntoStr = assuntoService.obterArvoreAssuntos(assuntoEmDetalhe);
    this.assuntosList = assuntoService.findAssuntosByPaiNaoExcluidos(assunto);

  }

  public String abrirConfigurarAssunto() {

    JSFUtil.setSessionMapValue("categoriaSelecionada", this.assuntoEmDetalhe);
    JSFUtil.setSessionMapValue("abrangenciaSelecionada", this.abrangencia);
    JSFUtil.setSessionMapValue(CadastrarAssuntoFluxoController.ASSUNTO_SELECIONADO, null);

    String viewId = "/paginas/cadastro/assunto/fluxo.xhtml?faces-redirect=true";
    return viewId;
  }

  public String editarConfigurarAssunto(Assunto assunto) {

    JSFUtil.setSessionMapValue(CadastrarAssuntoFluxoController.CATEGORIA_SELECIONADA, this.assuntoEmDetalhe);
    JSFUtil.setSessionMapValue(CadastrarAssuntoFluxoController.ABRANGENCIA_SELECIONADA, this.abrangencia);
    JSFUtil.setSessionMapValue(CadastrarAssuntoFluxoController.ASSUNTO_SELECIONADO, assunto);

    String viewId = "/paginas/cadastro/assunto/fluxo.xhtml?faces-redirect=true";
    return viewId;
  }

  public void handlerAbrirModalCancelarCategoria(Assunto categoria) {
    mensagemConfirmacaoExclusao = MensagemUtil.obterMensagem("assunto.mensagem.confirmacao.exclusao.categoria.simples");
    this.assuntoAExcluir = categoria;
  }

  public void handlerAbrirModalExcluirAssunto(Assunto assunto) {
    mensagemConfirmacaoExclusao = MensagemUtil.obterMensagem("assunto.mensagem.confirmacao.exclusao.assunto");
    this.assuntoAExcluir = assunto;
  }

  public boolean flagExibirBtnIncluirCategoria() {

    if (assuntoEmDetalhe != null) {
      Integer qtdNivelAtual = 1;
      Assunto assuntoLoop = assuntoEmDetalhe;
      while (assuntoLoop.getAssuntoPai() != null) {
        try {
          assuntoLoop = assuntoService.findByIdEager(assuntoLoop.getAssuntoPai().getId());
          qtdNivelAtual++;
        } catch (DataBaseException e) {
        }
      }

      if (qtdNivelAtual >= 3) {
        return false;
      }

    }

    return true;
  }

  public void incluirCategoria() throws RequiredException, BusinessException, DataBaseException {

    if (validarIncluirCategoria()) {
      Assunto categoria = new Assunto();
      categoria.setAbrangencia(abrangencia);
      categoria.setNome(nomeCategoria);
      categoria.setAssuntoPai(assuntoEmDetalhe);
      categoria.setAtivo(false);
      categoria.setFlagAssunto(false);
      categoria.setExcluido(false);
      try {
        this.assuntoService.salvarCategoria(categoria, matriculaLogado, unidadeLogada);
        this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.itemIncluido", "Categoria"));
        recarregarTela();
      } catch (Exception e) {
        getLogger().error(e);
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
        RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
      }
    }
  }

  public void excluirCategoria() throws RequiredException, BusinessException, DataBaseException {

    try {
      this.assuntoService.excluirCategoria(assuntoAExcluir, matriculaLogado, unidadeLogada);
      this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", "Categoria"));
      assuntoAExcluir = null;
      recarregarTela();
    } catch (Exception e) {
      getLogger().error(e);
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
    }
  }

  public void excluirAssunto() throws RequiredException, BusinessException, DataBaseException {

    try {
      this.assuntoService.excluirAssunto(assuntoAExcluir, matriculaLogado, unidadeLogada);
      this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", "Assunto"));
      assuntoAExcluir = null;
      recarregarTela();
    } catch (Exception e) {
      getLogger().error(e);
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
    }
  }

  public void handlerAbrirModalNovaCategoria() {
    this.nomeCategoria = null;
  }

  private boolean validarIncluirCategoria() {

    if (StringUtils.isBlank(nomeCategoria)) {

      FacesMessage facesMessage =
          new FacesMessage(FacesMessage.SEVERITY_ERROR, MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"),
              null);
      FacesContext.getCurrentInstance().addMessage("messageModalIncluir", facesMessage);

      // this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"));
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
      return false;
    }

    return true;
  }

  private void recarregarTela() throws DataBaseException {
    if (assuntoEmDetalhe == null) {
      voltarInicio();
    } else {
      detalharAssunto(assuntoEmDetalhe);
    }
  }

  public void voltar() throws DataBaseException {
    if (assuntoEmDetalhe.getAssuntoPai() == null) {
      voltarInicio();
    } else {
      detalharAssunto(assuntoEmDetalhe.getAssuntoPai());
    }
  }

  public void voltarInicio() throws DataBaseException {
    carregarCategorias();
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  public Assunto getAssuntoEmDetalhe() {
    return assuntoEmDetalhe;
  }

  public void setAssuntoEmDetalhe(Assunto assuntoEmDetalhe) {
    this.assuntoEmDetalhe = assuntoEmDetalhe;
  }

  public String getArvoreAssuntoStr() {
    return arvoreAssuntoStr;
  }

  public void setArvoreAssuntoStr(String arvoreAssuntoStr) {
    this.arvoreAssuntoStr = arvoreAssuntoStr;
  }

  public List<Assunto> getAssuntosList() {
    return assuntosList;
  }

  public void setAssuntosList(List<Assunto> assuntosList) {
    this.assuntosList = assuntosList;
  }

  public String getNomeCategoria() {
    return nomeCategoria;
  }

  public void setNomeCategoria(String nomeCategoria) {
    this.nomeCategoria = nomeCategoria;
  }

  public String getMensagemConfirmacaoExclusao() {
    return mensagemConfirmacaoExclusao;
  }

  public void setMensagemConfirmacaoExclusao(String mensagemConfirmacaoExclusao) {
    this.mensagemConfirmacaoExclusao = mensagemConfirmacaoExclusao;
  }

}
