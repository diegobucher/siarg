/**
 * InclusaoDemandaController.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.dto.DemandaHistoricoDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandaVinculadaDTO;
import br.gov.caixa.gitecsa.siarg.dto.EventChronolineDTO;
import br.gov.caixa.gitecsa.siarg.dto.KeyGroupValuesCollection;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioConsolidadoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.MotivoReaberturaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.AtendimentoService;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.DemandaContratoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import br.gov.caixa.gitecsa.siarg.service.FluxoDemandaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

/**
 * Classe Controller para tela de tratamento de demandas.
 */
@Named
@ViewScoped
public class TratarDemandaController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1L;

  /* Inicio dos Services necessários. */
  @Inject
  private DemandaService demandaService;

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private FluxoDemandaService fluxoDemandaService;

  @Inject
  private FeriadoService feriadoService;

  @Inject
  private AtendimentoService atendimentoService;

  @Inject
  private CaixaPostalService caixaPostalService;

  @Inject
  private UnidadeService unidadeService;

  /** Service que contém as regras de negócio da entidade demanda */
  @Inject
  private DemandaContratoService demandaContratoService;
  /* Fim dos Services necessários. */

  /** Demanda em tratamento */
  private Demanda demanda;

  /** Lista dos Fluxos da demanda. */
  private List<FluxoDemanda> fluxoDemandaList;

  /** Lista dos Historicos da Demanda. */
  private List<DemandaHistoricoDTO> historicoList;

  /** Lista dos Observadores do Assunto. */
  private List<CaixaPostal> observadorAssuntoList;

  /** Lista dos Observadores da Demanda. */
  private List<CaixaPostal> observadorDemandaList;

  /** Lista dos Observadores que são exibidos para seleção. */
  private List<CaixaPostal> caixaPostalObservadorDisponivesList;

  /** Lista dos Observadores que foram selecionados na tela. */
  private List<CaixaPostal> caixaPostalObservadorSelecionadosList;

  /** Lista com as demanda Vinculadas/Filhas da demanda em tratamento. */
  private List<DemandaVinculadaDTO> demandasVinculadasList;

  /** Id da demanda a ser tratada */
  private Long idDemanda;

  /** Caixa postal selecionada no sistema */
  private CaixaPostal caixaPostalSelecionada;

  /** Unidade selecionada no sistema */
  private Unidade unidadeSelecionada;

  /** Matricula do usuario logado no sistema */
  private String matriculaUsuarioLogado;

  private String nomeUsuarioLogado;

  /** Constante para pegar na sessão o Id da demanda */
  public static final String PARAM_ID_DEMANDA = "ID_DEMANDA";

  /** Parâmetro Remote Command para o conteúdo do editor */
  private static final String CONTENT = "content";

  /** Conteudo do HTML Editor */
  private String editorContent;

  /** Parâmetro Remote Command para prazo */
  private static final String DIAS_PRAZO = "dias_prazo";

  /** Parâmetro Remote Command que contém a lista de caixas-postais marcadas */
  private static final String IDS_CAIXAS_POSTAIS = "ids_caixas_postais";

  /** Caixas-postais marcadas */
  private List<Long> caixasPostaisMarcadasConsulta;

  /** Anexo */
  private UploadedFile anexo;

  private Atendimento atendimento;

  private Boolean flagExibirAvisoAnexo;

  private List<Atendimento> atendimentoList;

  private MotivoReaberturaEnum motivoReaberturaEnum;

  /** Lista de unidades/caixas-postais */
  private KeyGroupValuesCollection<CaixaPostal> agrupamentoCaixaPostal;

  /** Formatação do nome do agrupamento de rede */
  private static final String PREFIXO_AGRUPAMENTO_REDE = "Rede %s";

  /** Arquivos zip */
  private static final String EXT_ZIP = ".zip";

  /** Número de caracteres para o substring da Rede */
  private static final int NUM_CHARS_SUBSTRING = 5;

  private Integer prazoConsulta;

  private List<Unidade> unidadeExternaList;

  private Unidade unidadeExternaSelecionada;

  private boolean proximaCaixaExterna;

  /** Tamanho máximo do anexo em bytes (5MB) */
  private static final long TAM_MAX_ANEXO = 5242880;

  private List<Demanda> demandasFilhasList;

  private String conteudoJsonGrafico;

  private String contratoTemp;
  
  private CaixaPostal caixaPostalOrdemUm;

  /** Primeira caixa depois da demandante (ordem 1) */
  private static final int CAIXA_ORDEM_UM = 1;

  @PostConstruct
  public void init() {
    this.contratoTemp = "";
    this.caixaPostalSelecionada = (CaixaPostal) JSFUtil.getSessionMapValue("caixaPostal");
    UnidadeDTO unidadeDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.matriculaUsuarioLogado = (String) JSFUtil.getSessionMapValue("loggedUser");
    UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
    this.nomeUsuarioLogado = usuario.getNomeUsuario();
    this.unidadeSelecionada = this.unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());

    carregarDados();
  }

  public String detalharDemandaVinculada(Long idDemandaVinculada) {
    JSFUtil.setSessionMapValue(TratarDemandaController.PARAM_ID_DEMANDA, idDemandaVinculada);
    return redirecionarPaginaAtual();
  }

  public void carregarDados() {

    this.editorContent = null;
    this.atendimento = new Atendimento();

    this.idDemanda = (Long) JSFUtil.getSessionMapValue(TratarDemandaController.PARAM_ID_DEMANDA);

    this.demanda = demandaService.findByIdFetch(idDemanda);
    if (demanda != null) {

      this.fluxoDemandaList = fluxoDemandaService.findByIdDemanda(idDemanda);
      for (Iterator<FluxoDemanda> iterator = fluxoDemandaList.iterator(); iterator.hasNext();) {
        FluxoDemanda fluxo = (FluxoDemanda) iterator.next();
        
        /**Pegar a caixa postal de ordem um*/
        if(fluxo.getOrdem() ==1) {
        	this.caixaPostalOrdemUm = fluxo.getCaixaPostal();
        }
        // Removendo os FluxoDemanda inativos
        if (!fluxo.isAtivo()) {
          iterator.remove();
        }
      }

      carregarHistorico();
      carregarCaixasPostaisEmCopia();
      carregarDadosRascunho();
      carregarDemandasVinculadas();
      carregarDadosGrafico();

      this.prazoConsulta = this.demanda.getAssunto().getPrazo();

      unidadeExternaList =
          unidadeService.obterUnidadesECaixasPostaisPorTipo(unidadeSelecionada.getAbrangencia(), TipoUnidadeEnum.EXTERNA);

      proximaCaixaExterna = isCaixaResponsavelPelaExterna();
    }
  }

  private void carregarDadosGrafico() {

    List<EventChronolineDTO> eventosList = new ArrayList<EventChronolineDTO>();

    Boolean atendimentoQuestionarDemandante = false;

    // Adiciona os historicos apartir do atendimento
    for (int i = 0; i < atendimentoList.size(); i++) {

      Atendimento atendimento = atendimentoList.get(i);
      EventChronolineDTO evento = new EventChronolineDTO();

      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)) {
        continue;
      }

      // Caso o atendimento esteja com a ação igual à “Consultar”, o sistema não deve exibí-lo no grafico.
      // Somente se foi quem gerou
      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)
          && !(atendimento.getFluxoDemanda().getCaixaPostal().equals(caixaPostalSelecionada)
              || ((demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
                  || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA))
                  && isCaixaResponsavelPelaExterna()))) {
        continue;
      }

      boolean respondeuAoDemandante = false;

      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)
      // Se o primeiro fluxo demanda
          && (atendimento.getFluxoDemanda().getOrdem().equals(1) || (
          // Um externa fluxo 2 respondeu
          (atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
              || atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))
              && atendimento.getFluxoDemanda().getOrdem() <= 2))) {
        respondeuAoDemandante = true;
      }

      Date dataHoraAtendimento = atendimento.getDataHoraAtendimento();

      // Caso não tenha sido atendimento então simula a data atendimento igual a hoje
      if (dataHoraAtendimento == null
          || (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO))) {
        dataHoraAtendimento = new Date();
      }

      String caixaResponsavel = null;

      if (atendimento.getFluxoDemanda() != null) {
        caixaResponsavel = atendimento.getFluxoDemanda().getCaixaPostal().getSigla();
      } else {
        caixaResponsavel = demanda.getCaixaPostalDemandante().getSigla();
      }

      String descricao = "Atendimento: " + caixaResponsavel;
      evento.setDescricao(descricao);
      evento.setDataInicial(DateUtil.formatData(atendimento.getDataHoraRecebimento(), DateUtil.FORMATO_DATA_AMERICANO));
      evento.setDataFinal(DateUtil.formatData(dataHoraAtendimento, DateUtil.FORMATO_DATA_AMERICANO));

      // ------------

      if (atendimento.getAcaoEnum() != null) {

        if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CANCELAR)) {
          evento.setCor("#808080");
        } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.FECHAR)) {
          evento.setCor("#337AB7");
        } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.REABRIR)) {
          evento.setCor("#6600cc");
        } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER) && respondeuAoDemandante) {
          evento.setCor("#337AB7");
        } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)) {
          evento.setCor("#ec971f");
          evento.setDataInicial(evento.getDataFinal());
        }
      }

      if (atendimentoQuestionarDemandante.equals(true)) {

        Date dataFim = new Date();

        if (atendimento.getDataHoraAtendimento() != null) {
          dataFim = atendimento.getDataHoraAtendimento();
        }

        Integer prazo = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), dataFim);
        if (prazo.equals(0)) {
          evento.setCor("#fad839");
        } else {
          evento.setCor("#ee4749");
        }
      }

      // Parte que Calcula as cores apartir dos prazos
      if (StringUtils.isBlank(evento.getCor())) {

        Date dataFim = new Date();

        if (atendimento.getDataHoraAtendimento() != null) {
          dataFim = atendimento.getDataHoraAtendimento();
        }

        Integer prazo = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), dataFim);

        Integer prazoFluxo = null;
        if (atendimento.getFluxoDemanda() != null) {
          prazoFluxo = atendimento.getFluxoDemanda().getPrazo();
          // Atendimento na mão do demandante pega o prazo do primeiro fluxo
        } else {
          prazoFluxo = fluxoDemandaList.iterator().next().getPrazo();
        }

        if (prazo < (prazoFluxo - 1)) {
          evento.setCor("#00a651");
        } else if (prazo == prazoFluxo || prazo == (prazoFluxo - 1)) {
          evento.setCor("#fad839");
        } else {
          evento.setCor("#ee4749");
        }
      }

      if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)
      // Se o primeiro fluxo demanda
          && (atendimento.getFluxoDemanda().getOrdem().equals(1) || (
          // Um externa fluxo 2 respondeu
          (atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
              || atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))
              && atendimento.getFluxoDemanda().getOrdem() <= 2))) {
        atendimentoQuestionarDemandante = true;
      } else {
        // IGNORA ATUALIZAR RASCUNHO CONSULTAR
        if (!(atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)
            || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO)
            || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)))) {
          atendimentoQuestionarDemandante = false;
        }
      }

      eventosList.add(evento);
    }

    Gson gson = new Gson();
    this.conteudoJsonGrafico = gson.toJson(eventosList);
  }

  /**
   * Verifica se todos os campos foram preenchidos corretamente.
   */
  private boolean validarCampos() {
    boolean valido = true;

    if (getFlagExibirReabrir() && this.motivoReaberturaEnum == null) {
      valido = false;
      this.facesMessager.addMessageError("Informe os dados obrigatórios.");
    } else if (editorContent == null || ObjectUtils.isNullOrEmpty(Jsoup.parse(this.editorContent).text())) {
      valido = false;
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
    } else if (!this.isAnexoValido()) {
      this.anexo = null;
      valido = false;
    }

    return valido;
  }

  public void validarCamposByRemoteCommand() {

    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    String fileName = params.get("nome");
    Long fileSize = 0l;
    if (params.get("tamanho") != null && !params.get("tamanho").equals("")) {
      fileSize = Long.parseLong(params.get("tamanho"));
    }

    if (editorContent == null || ObjectUtils.isNullOrEmpty(Jsoup.parse(this.editorContent).text())) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
    } else {

      if (ObjectUtils.isNullOrEmpty(fileName)) {
        return;
      }

      if (fileSize > TAM_MAX_ANEXO) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA013"));
        RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
        RequestContext.getCurrentInstance().addCallbackParam("limparAnexo", "true");

        return;
      } else if (!fileName.toLowerCase().endsWith(EXT_ZIP)) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA012"));
        RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
        RequestContext.getCurrentInstance().addCallbackParam("limparAnexo", "true");

        return;
      }

    }

  }

  public List<MotivoReaberturaEnum> getMotivosList() {
    return Arrays.asList(MotivoReaberturaEnum.values());
  }

  private void carregarDadosRascunho() {
    this.flagExibirAvisoAnexo = false;

    if (this.demanda.getSituacao().equals(SituacaoEnum.MINUTA) || this.demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {
      // this.atendimento.setDescricao(this.demanda.getTextoDemanda());
      this.editorContent = demanda.getTextoDemanda();

      if (StringUtils.isNotBlank(demanda.getAnexoDemanda())) {
        this.flagExibirAvisoAnexo = true;
      }

    } else {
      // List<Atendimento> atendimentoList = atendimentoService.obterAtendimentosPorDemanda(idDemanda);
      // Collections.sort(atendimentoList);
      Atendimento rascunho = null;
      for (Atendimento atendimento : atendimentoList) {
        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO)) {
          rascunho = atendimento;
        }
      }
      if (rascunho != null) {
        this.editorContent = rascunho.getDescricao();
        if (StringUtils.isNotBlank(rascunho.getAnexoAtendimento())) {
          this.flagExibirAvisoAnexo = true;
        }
      }

    }
  }

  private FluxoDemanda getPrimeiroDestinoFluxoDemandaAtivo(Demanda demanda) {

    List<FluxoDemanda> fluxoDemandaList = getFluxoDemandaListAtivos(demanda);

    for (FluxoDemanda fluxoDem : fluxoDemandaList) {
      if (fluxoDem.getOrdem() == 1) {
        return fluxoDem;
      }
    }

    return null;
  }

  private void carregarDemandasVinculadas() {

    this.demandasVinculadasList = new ArrayList<DemandaVinculadaDTO>();

    // List<Demanda> demandasVinculadasList;

    // Se for a demanda pai carrega todos os vinculados
    if (this.demanda.getFlagDemandaPai()) {
      demandasFilhasList = demandaService.findFilhosIniciaisByIdPaiPostalFetch(this.idDemanda);
    } else {
      demandasFilhasList = demandaService.findFilhosConsultaByIdPaiECaixaPostalFetch(idDemanda, caixaPostalSelecionada);
    }

    if (demandasFilhasList != null) {

      for (Demanda demandaFilha : demandasFilhasList) {
        DemandaVinculadaDTO demandaVinculadaDTO = new DemandaVinculadaDTO();
        demandaVinculadaDTO.setId(demandaFilha.getId());

        FluxoDemanda fluxo = getPrimeiroDestinoFluxoDemandaAtivo(demandaFilha);
        if(fluxo != null) {
          demandaVinculadaDTO.setUnidadeDemandada(fluxo.getCaixaPostal().getSigla());
        }
        demandaVinculadaDTO.setSituacao(demandaFilha.getSituacao().getDescricao());
        demandaVinculadaDTO.setResponsavelAtual(demandaFilha.getCaixaPostalResponsavel().getSigla());

        if (demandaFilha.getSituacao().equals(SituacaoEnum.FECHADA)) {
          demandaVinculadaDTO.setClassePrazo("prazo-blue");
        } else if (demandaFilha.getSituacao().equals(SituacaoEnum.CANCELADA)) {
          demandaVinculadaDTO.setClassePrazo("prazo-gray");
        } else {
          Integer prazoUteis = 0; 
          if(fluxo != null) {
            prazoUteis = fluxo.getPrazo();
          }
          Integer diasUteisPassados =
              feriadoService.calcularQtdDiasUteisEntreDatas(demandaFilha.getDataHoraAbertura(), new Date());

          if (diasUteisPassados > prazoUteis) {
            demandaVinculadaDTO.setClassePrazo("prazo-red");
          } else if (diasUteisPassados == prazoUteis || diasUteisPassados == (prazoUteis - 1)) {
            demandaVinculadaDTO.setClassePrazo("prazo-yellow");
          } else if (diasUteisPassados < (prazoUteis - 1)) {
            demandaVinculadaDTO.setClassePrazo("prazo-green");
          }
        }

        this.demandasVinculadasList.add(demandaVinculadaDTO);
      }
    }

  }

  public boolean getFlagExibeDemandasVinculadas() {
    if (demandasVinculadasList != null && !demandasVinculadasList.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }

  public String cancelar() {

    MensagemUtil.setKeepMessages(true);

    if (this.validarCampos()) {
      try {
        demandaService.cancelar(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            caixaPostalSelecionada, this.editorContent, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda cancelada com sucesso.");
        return redirecionarPaginaAtual();
      } catch (Exception e) {
        LOGGER.error(e);
      }
    }

    return null;
  }

  public String salvarRascunho() {

    MensagemUtil.setKeepMessages(true);

    if (this.validarCampos()) {

      try {
        demandaService.salvarRascunho(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            caixaPostalSelecionada, this.editorContent, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda rascunhada com sucesso.");
        return redirecionarPaginaAtual();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public String encaminhar() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {

        if (demanda.getSituacao().equals(SituacaoEnum.MINUTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {

          demanda.setTextoDemanda(this.editorContent);

          if (demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
              && !(unidadeSelecionada.getTipoUnidade().equals(TipoUnidadeEnum.MATRIZ)
                  || Boolean.TRUE.equals(JSFUtil.getSessionMapValue("flagGerente"))
                  || demanda.getAssunto().getPermissaoAbertura())) {
            demandaService.atualizarRascunhoParaMinuta(demanda, anexo, caixaPostalObservadorSelecionadosList,
                matriculaUsuarioLogado, nomeUsuarioLogado);

            // Se for Minuta sempre entra no else.
            // Alguns Rascunhos também entram
          } else {
            demandaService.encaminharDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
                caixaPostalSelecionada, this.editorContent, nomeUsuarioLogado);
          }

          // Outras situações da demanda DIFERENTES DE MINUTA/RASCUNHO
        } else {
          demandaService.encaminharDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
              caixaPostalSelecionada, this.editorContent, nomeUsuarioLogado);
        }
        this.facesMessager.addMessageInfo("Demanda encaminhada com sucesso.");
        return redirecionarPaginaAtual();
      }

    } catch (Exception e) {
      LOGGER.error(e);
    }

    return null;
  }

  public String atualizar() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.atualizarDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda atualizada com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }

    return null;
  }

  public String encaminharExterna() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.encaminharDemandaExterna(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.caixaPostalSelecionada, this.editorContent, unidadeExternaSelecionada, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda encaminhada com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  public String reabrir() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.reabrirDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, motivoReaberturaEnum, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda reaberta com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }

    return null;
  }

  public String questionar() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.questionarDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda questionada com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  public String responder() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.responderDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda respondida com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;

  }

  public String fechar() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.fecharDemanda(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda fechada com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  public String consultar() {

    MensagemUtil.setKeepMessages(true);

    try {
      if (this.validarCampos()) {
        demandaService.consultarDemandas(demanda, anexo, caixaPostalObservadorSelecionadosList, matriculaUsuarioLogado,
            this.editorContent, this.caixaPostalSelecionada, this.prazoConsulta, this.caixasPostaisMarcadasConsulta,
            nomeUsuarioLogado);
        this.facesMessager.addMessageInfo("Demanda consultada com sucesso.");
        return redirecionarPaginaAtual();
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }

    return null;

  }

  private void carregarHistorico() {
    atendimentoList = atendimentoService.obterAtendimentosPorDemanda(this.idDemanda);
    Collections.sort(atendimentoList);

    this.historicoList = new ArrayList<DemandaHistoricoDTO>();

    if (atendimentoList != null && !atendimentoList.isEmpty()) {

      // Pegando segundo atendimento, pois o primeiro é a abertura da demandante para a própria demandante
      Atendimento primeiroAtendimento = atendimentoList.iterator().next();
      // Atendimento segundoAtendimento = primeiroAtendimento;
      Atendimento segundoAtendimento = atendimentoList.get(CAIXA_ORDEM_UM);

      final String COM_ENVIO_EMAIL = "Com envio de email";

      // Adiciona o historico apartir do demandante
      DemandaHistoricoDTO historico = new DemandaHistoricoDTO();
      historico.setId(0l);
      historico.setDescricao(demanda.getTextoDemanda());
      historico.setOrigem(primeiroAtendimento.getFluxoDemanda().getCaixaPostal().getSigla());
      // historico.setOrigem(demanda.getCaixaPostalDemandante().getSigla());
      historico.setDataRecebimento(demanda.getDataHoraAbertura());
      historico.setDataAtendimento(demanda.getDataHoraAbertura());

      historico.setDestino(segundoAtendimento.getFluxoDemanda().getCaixaPostal().getSigla());
      if (segundoAtendimento.getFluxoDemanda().getCaixaPostal().getRecebeEmail()) {
        historico.setEmail(COM_ENVIO_EMAIL);
      }

      if (demanda.getMatriculaDemandante() != null) {
        historico.setMatricula(demanda.getMatriculaDemandante().toUpperCase());
      }
      historico.setNomeUsuario(demanda.getNomeUsuarioDemandante());
      historico.setAnexo(demanda.getAnexoDemanda());
      historico.setOrdem(1);
      historico.setAcao("Abrir");
      historico.setClasseCor("bg-white");
      historicoList.add(historico);
      Integer ordem = 2;
      Boolean atendimentoQuestionarDemandante = false;

      // Adiciona os historicos apartir do atendimento ordem 1
      for (int i = 0; i < atendimentoList.size(); i++) {
        Atendimento atendimento = atendimentoList.get(i);

        if (AcaoAtendimentoEnum.INCLUIR.equals(atendimento.getAcaoEnum())) {
          continue;
        }

        // Caso o atendimento esteja com a ação igual à “Rascunho”, o sistema não deve exibí-lo no histórico.
        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO)) {
          continue;
        }

        // Caso o atendimento esteja com a ação igual à “Consultar”, o sistema não deve exibí-lo no histórico.
        // Somente se foi quem gerou
        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)
            && !(atendimento.getFluxoDemanda().getCaixaPostal().equals(caixaPostalSelecionada)
                || ((demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
                    || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA))
                    && isCaixaResponsavelPelaExterna()))) {
          continue;
        }

        // Ignora Antedimentos que não tiveram ação
        if (atendimento.getDataHoraAtendimento() == null) {
          continue;
        }

        historico = new DemandaHistoricoDTO();
        historico.setId(atendimento.getId());
        historico.setDescricao(atendimento.getDescricao());

        CaixaPostal caixaOrigem = null;
        // TODO Essa Situação não é para acontecer
        if (atendimento.getFluxoDemanda() == null) {
          caixaOrigem = demanda.getCaixaPostalDemandante();
          // Caso seja um atendimento de MIGRAR e estava numa caixa externa pega a origem do atendimento anterior que
          // enviou a externa
        } else if (atendimento.getAcaoEnum() != null
            && atendimento.getFluxoDemanda().getCaixaPostal().getSigla().equals(TipoUnidadeEnum.ARROBA_EXTERNA.getDescricao())) {

          // PEGAR O ULTIMO ATENDIMENTO ANTES DESSA MIGRACAO, IGNORANDO ATUALIZACAO

          if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.MIGRAR)
              || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)
              || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)) {
            int j = 1;
            while (i > j) {
              Atendimento atendimentoAnteriorLoop = atendimentoList.get(i - j);
              if (atendimentoAnteriorLoop.getAcaoEnum() != null
                  && !atendimentoAnteriorLoop.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)) {
                caixaOrigem = atendimentoAnteriorLoop.getFluxoDemanda().getCaixaPostal();
                break;
              }
              j++;
            }
          } else {
            // } else if(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)) {
            caixaOrigem = atendimento.getFluxoDemanda().getCaixaPostal();
          }

        } else {
          caixaOrigem = atendimento.getFluxoDemanda().getCaixaPostal();
        }

        historico.setDataRecebimento(atendimento.getDataHoraRecebimento());
        historico.setDataAtendimento(atendimento.getDataHoraAtendimento());

        boolean respondeuAoDemandante = false;

        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER)
        // Se o primeiro fluxo demanda
            && (atendimento.getFluxoDemanda().getOrdem().equals(1) || (
            // Um externa fluxo 2 respondeu
            (atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
                || atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))
                && atendimento.getFluxoDemanda().getOrdem() <= 2))) {
          respondeuAoDemandante = true;
        }

        CaixaPostal caixaDestino = null;

        if (atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)
            || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR))) {
          caixaDestino = atendimento.getFluxoDemanda().getCaixaPostal();

        } else if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.FECHAR)) {
          // Inverte de Demandande para destino
          caixaDestino = caixaOrigem;
          caixaOrigem = demanda.getCaixaPostalDemandante();

        } else if (atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CANCELAR))) {
          caixaDestino = demanda.getCaixaPostalDemandante();
          caixaOrigem = demanda.getCaixaPostalDemandante();
        } else {

          // Se o i atual não é o ultimo atendimento
          if (i < (atendimentoList.size() - 1)) {

            // Pega o destino da caixa postal do proximo registro de atendimento
            if (atendimentoList.get(i + 1).getFluxoDemanda() != null) {
              caixaDestino = atendimentoList.get(i + 1).getFluxoDemanda().getCaixaPostal();
            } else {
              caixaDestino = demanda.getCaixaPostalDemandante();
            }
            // Se for o ultimo registro que respondeu
          } else if (i == (atendimentoList.size() - 1) && respondeuAoDemandante) {
            caixaDestino = demanda.getCaixaPostalDemandante();
            // Se for o Ultimo atendimento, uma migração e o destino Caixa Demandante
          } else if (i == (atendimentoList.size() - 1) && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.MIGRAR)) {

            caixaDestino = atendimento.getFluxoDemanda().getCaixaPostal(); // ADICIONANDO
          }
        }

        historico.setOrigem(caixaOrigem.getSigla());
        historico.setDestino(caixaDestino.getSigla());

        if (Boolean.TRUE.equals(caixaDestino.getRecebeEmail())) {
          historico.setEmail(COM_ENVIO_EMAIL);
        }
        if (atendimento.getMatricula() != null) {
          historico.setMatricula(atendimento.getMatricula().toUpperCase());
        }
        historico.setNomeUsuario(atendimento.getNomeUsuarioAtendimento());

        historico.setAnexo(atendimento.getAnexoAtendimento());
        historico.setOrdem(ordem);
        if (atendimento.getAcaoEnum() != null) {
          historico.setAcao(atendimento.getAcaoEnum().getDescricao());
        }

        if (atendimento.getAcaoEnum() != null) {

          if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CANCELAR)) {
            historico.setClasseCor("bg-grey");
          } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.FECHAR)) {
            historico.setClasseCor("bg-blue");
          } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.REABRIR)) {
            historico.setClasseCor("bg-violet");
          } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RESPONDER) && respondeuAoDemandante) {
            historico.setClasseCor("bg-blue");
          } else if (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)) {
            historico.setClasseCor("bg-orange");
          }
        }

        // SE É PRIMEIRO ATEND DEPOIS DO QUESTIONAR FAZ A REGRA NOVA
        if (atendimentoQuestionarDemandante.equals(true)) {

          Date dataFim = new Date();

          if (atendimento.getDataHoraAtendimento() != null) {
            dataFim = atendimento.getDataHoraAtendimento();
          }

          Integer prazo = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), dataFim);
          if (prazo.equals(0)) {
            historico.setClasseCor("bg-warning");
          } else {
            historico.setClasseCor("bg-secondary");
          }
        }

        // Parte que Calcula as cores apartir dos prazos
        if (StringUtils.isBlank(historico.getClasseCor())) {

          Date dataFim = new Date();

          if (atendimento.getDataHoraAtendimento() != null) {
            dataFim = atendimento.getDataHoraAtendimento();
          }

          Integer prazo = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), dataFim);

          Integer prazoFluxo = null;
          if (atendimento.getFluxoDemanda() != null) {
            prazoFluxo = atendimento.getFluxoDemanda().getPrazo();
            // Atendimento na mão do demandante pega o prazo do primeiro fluxo
          } else {
            prazoFluxo = fluxoDemandaList.iterator().next().getPrazo();
          }

          if (prazo < (prazoFluxo - 1)) {
            historico.setClasseCor("bg-success");
          } else if (prazo == prazoFluxo || prazo == (prazoFluxo - 1)) {
            historico.setClasseCor("bg-warning");
          } else {
            historico.setClasseCor("bg-secondary");
          }
        }

        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.QUESTIONAR)
        // Se o primeiro fluxo demanda
            && (atendimento.getFluxoDemanda().getOrdem().equals(1) || (
            // Um externa fluxo 2 respondeu
            (atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
                || atendimento.getFluxoDemanda().getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))
                && atendimento.getFluxoDemanda().getOrdem() <= 2))) {
          atendimentoQuestionarDemandante = true;
        } else {
          // IGNORA ATUALIZAR RASCUNHO CONSULTAR
          if (!(atendimento.getAcaoEnum() != null && (atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)
              || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO)
              || atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.CONSULTAR)))) {
            atendimentoQuestionarDemandante = false;
          }
        }

        ordem++;
        historicoList.add(historico);
      }
      Collections.reverse(historicoList);
    }
  }

  /**
   * Carrega a lista de caixas-postais observadoras e predefinidas pelo responsável.
   */
  private void carregarCaixasPostaisEmCopia() {

    this.observadorDemandaList = caixaPostalService.findObservadoresByDemanda(this.idDemanda);
    this.observadorAssuntoList = caixaPostalService.findObservadoresByAssunto(this.demanda.getAssunto().getId());

    this.caixaPostalObservadorDisponivesList =
        this.caixaPostalService.findByAbrangenciaTipoUnidade(unidadeSelecionada.getAbrangencia(), TipoUnidadeEnum.MATRIZ,
            TipoUnidadeEnum.FILIAL);

    // Remove os Observadores existentes (Assunto/Demanda) e o Demandante
    this.caixaPostalObservadorDisponivesList.removeAll(this.observadorAssuntoList);
    this.caixaPostalObservadorDisponivesList.removeAll(this.observadorDemandaList);
    this.caixaPostalObservadorDisponivesList.remove(this.demanda.getCaixaPostalDemandante());

    // Adicionando Observadores da demanda para exibir na caixa de já cadastrados
    // Observador Assunto + Observador Demanda
    this.observadorAssuntoList.addAll(observadorDemandaList);
  }

  @SuppressWarnings("unchecked")
  public void alterarObservador(AjaxBehaviorEvent event) {
    List<CaixaPostal> caixaList = (List<CaixaPostal>) ((UIOutput) event.getSource()).getValue();

    for (CaixaPostal caixaPostal : caixaList) {
      // ADICIONA NA BASE O NOVO OBSERVADOR
      demandaService.adicionarObservadorDemanda(this.idDemanda, caixaPostal);
    }

    // RECARREGA AS INFORMACOES DE OBSERVADORES
    carregarCaixasPostaisEmCopia();

    // UPDATE NOS COMPONENTES DA VIEW
  }

  private String redirecionarPaginaAtual() {
    String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
    return viewId + "?faces-redirect=true";
  }

  /**
   * Seta o prazo da demanda em dias.
   */
  public void setDadosModalConsultaFromRemoteCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    this.prazoConsulta = Integer.valueOf(params.get(DIAS_PRAZO));

    Type type = new TypeToken<List<Long>>() {
    }.getType();
    this.caixasPostaisMarcadasConsulta = new Gson().fromJson(params.get(IDS_CAIXAS_POSTAIS), type);
  }

  public void downloadRascunhoAnexoDemanda() throws IOException {
    File file = null;

    String path = null;

    if (this.demanda.getSituacao().equals(SituacaoEnum.MINUTA) || this.demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {
      path = demandaService.obterDiretorioAnexosPorAbrangencia(this.demanda) + demanda.getAnexoDemanda();
    } else {
      Atendimento rascunho = new Atendimento();
      for (Atendimento atendimento : atendimentoList) {
        if (atendimento.getAcaoEnum() != null && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO)) {
          rascunho = atendimento;
        }
      }
      path = demandaService.obterDiretorioAnexosPorAbrangencia(this.demanda) + rascunho.getAnexoAtendimento();
    }

    file = new File(path);

    if (file.exists()) {
      Faces.sendFile(file, true);
    } else {
      this.facesMessager.addMessageError("O arquivo selecionado não encontra-se disponível para download.");
    }
  }

  public void downloadAnexoHistorico(Long idAtendimento) throws IOException {
    File file = null;

    for (DemandaHistoricoDTO hist : historicoList) {
      if (hist.getId().equals(idAtendimento)) {
        String path = demandaService.obterDiretorioAnexosPorAbrangencia(this.demanda) + hist.getAnexo();

        file = new File(path);
      }
    }
    if (file != null && file.exists()) {
      Faces.sendFile(file, true);
    } else {
      this.facesMessager.addMessageError("O arquivo selecionado não encontra-se disponível para download.");
    }
  }

  public boolean getFlagExibirInteracao() {
    if ((caixaPostalSelecionada.equals(demanda.getCaixaPostalResponsavel())
        || caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante()) || isCaixaResponsavelPelaExterna())
        && ((demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
            || demanda.getSituacao().equals(SituacaoEnum.MINUTA)))) {
      return Boolean.TRUE;
    } else if (isPodeReabrirDemanda()) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  private boolean isPodeReabrirDemanda() {
    if (caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante()) && demanda.getSituacao().equals(SituacaoEnum.FECHADA)
        && isDemandaSimples() && (unidadeSelecionada.getTipoUnidade().equals(TipoUnidadeEnum.MATRIZ)
            || Boolean.TRUE.equals(JSFUtil.getSessionMapValue("flagGerente")) || demanda.getAssunto().getPermissaoAbertura())) {

      Date dataMaximaReabrir = feriadoService.adicionarDiasUteis(demanda.getDataHoraEncerramento(), 2);
      Date hojeTruncado = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

      if (hojeTruncado.after(dataMaximaReabrir)) {
        return false;
      } else {
        return true;
      }

    }
    return false;
  }

  /**
   * Quando não tem pai e não é pai.
   */
  private boolean isDemandaSimples() {
    if (this.demanda.getDemandaPai() == null && !this.demanda.getFlagDemandaPai()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getFlagExibirReabrir() {
    return isPodeReabrirDemanda();
  }

  public boolean getFlagExibirBotoes() {
    if ((caixaPostalSelecionada.equals(demanda.getCaixaPostalResponsavel())
        || caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante()) || isCaixaResponsavelPelaExterna())
        && (demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
            || demanda.getSituacao().equals(SituacaoEnum.MINUTA))) {
      return Boolean.TRUE;
    } else if (isPodeReabrirDemanda()) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  public boolean getFlagExibirCancelar() {
    if (caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante())) {

      // Ser uma demanda filha (demanda vinculada à uma demanda pai) e possuir situação igual à “Aberta”
      if (demanda.getDemandaPai() != null && demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
        return Boolean.TRUE;
        // a. Ser uma demanda simples e possuir a situação igual à “Aberta”, “Rascunho” ou “Minuta”;
      } else if ((demanda.getDemandaPai() == null && !demanda.getFlagDemandaPai())
          && (demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
              || (demanda.getSituacao().equals(SituacaoEnum.MINUTA))
                  && Boolean.TRUE.equals(JSFUtil.getSessionMapValue("flagGerente")))) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  public boolean getFlagExibirFechar() {
    if (caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante()) && demanda.getFlagDemandaPai()
        && demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  public boolean getFlagExibirBotoesResponsavel() {
    if (caixaPostalSelecionada.equals(demanda.getCaixaPostalResponsavel()) || isCaixaResponsavelPelaExterna()) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public boolean getFlagExibirBotaoCaixaAnterior() {
    if (demanda.getCaixaPostalResponsavel().equals(demanda.getCaixaPostalDemandante())) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  public boolean getFlagExibirBotoesCaixaAnteriorEProxima() {
    if (demanda.getCaixaPostalResponsavel().equals(caixaPostalSelecionada) || isCaixaResponsavelPelaExterna()) {
      return true;
    }

    return false;
  }

  public boolean getFlagExibirBotaoCaixaProxima() {
    FluxoDemanda ultimoFluxo = null;

    for (FluxoDemanda f : fluxoDemandaList) {
      ultimoFluxo = f;
    }

    // Teste se é o ultimo fluxo
    if (ultimoFluxo != null &&
        (demanda.getCaixaPostalResponsavel().equals(ultimoFluxo.getCaixaPostal())
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;

  }

  public boolean getFlagExibirEncaminhar() {
    FluxoDemanda ultimoFluxo = null;

    for (FluxoDemanda f : fluxoDemandaList) {
      ultimoFluxo = f;
    }

    // Teste se é o ultimo fluxo
    if (ultimoFluxo != null &&
        (demanda.getCaixaPostalResponsavel().equals(ultimoFluxo.getCaixaPostal())
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA))) {
      return Boolean.FALSE;
    }

    if ((caixaPostalSelecionada.equals(demanda.getCaixaPostalResponsavel())
        || caixaPostalSelecionada.equals(demanda.getCaixaPostalDemandante()))

        && (demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
            || (demanda.getSituacao().equals(SituacaoEnum.MINUTA)
                && Boolean.TRUE.equals(JSFUtil.getSessionMapValue("flagGerente"))))) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }

  }

  public boolean getFlagExibirAtualizarConsultar() {
    if (demanda.getSituacao().equals(SituacaoEnum.ABERTA)
        && !demanda.getCaixaPostalDemandante().equals(demanda.getCaixaPostalResponsavel())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public boolean getFlagExibirPesquisarUltimasRespostas() {
    if (demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public boolean getFlagExibirSalvarRascunho() {
    if ((demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.MINUTA)
        || demanda.getSituacao().equals(SituacaoEnum.RASCUNHO))
        && (demanda.getCaixaPostalResponsavel().equals(caixaPostalSelecionada) || isCaixaResponsavelPelaExterna())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public boolean getFlagExibirHistorico() {

    boolean temAtendimentos = false;

    if (atendimentoList != null && !atendimentoList.isEmpty()) {
      temAtendimentos = true;
    }

    if (!demanda.getSituacao().equals(SituacaoEnum.MINUTA) && !demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
        && !(demanda.getSituacao().equals(SituacaoEnum.CANCELADA) && !temAtendimentos)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public boolean getFlagExibirLinhaDoTempo() {

    boolean temAtendimentos = false;

    if (atendimentoList != null && !atendimentoList.isEmpty()) {
      temAtendimentos = true;
    }

    if (!demanda.getSituacao().equals(SituacaoEnum.MINUTA) && !demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)
        && !(demanda.getSituacao().equals(SituacaoEnum.CANCELADA) && !temAtendimentos)) {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }

  public boolean getFlagExibirAvisoAnexo() {
    return this.flagExibirAvisoAnexo;
  }

  /**
   * Obtém o agrupamento das unidades/caixas-postais ativas.
   * 
   * @return Unidades/caixas-postais ativas
   */
  public KeyGroupValuesCollection<CaixaPostal> getAgrupamentoCaixaPostal() {

    if (ObjectUtils.isNullOrEmpty(this.agrupamentoCaixaPostal)) {

      List<CaixaPostal> caixasPostaisEnvolvidas = new ArrayList<CaixaPostal>();
      caixasPostaisEnvolvidas.add(this.demanda.getCaixaPostalDemandante());
      for (FluxoDemanda fluxoDem : fluxoDemandaList) {
        caixasPostaisEnvolvidas.add(fluxoDem.getCaixaPostal());
      }

      this.agrupamentoCaixaPostal =
          demandaService.getAgrupamentoCaixaPostal(unidadeSelecionada.getAbrangencia(), caixasPostaisEnvolvidas);

    }

    return this.agrupamentoCaixaPostal;
  }

  public String getCaixaAnterior() {

    for (int i = 0; i < fluxoDemandaList.size(); i++) {

      FluxoDemanda fluxo = fluxoDemandaList.get(i);

      if (fluxo.getCaixaPostal().equals(demanda.getCaixaPostalResponsavel())) {

        if (fluxo.getOrdem() == 1) {
          return demanda.getCaixaPostalDemandante().getSigla();
        } else {
          return fluxoDemandaList.get(i - 1).getCaixaPostal().getSigla();
        }

        // Se a demanda está na @Externa e o fluxo em loop for @Externa
      } else if ((demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
          || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA))
          && (fluxo.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
              || fluxo.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA))) {
        if (fluxo.getOrdem() >= 3) {
          return fluxoDemandaList.get(i - 2).getCaixaPostal().getSigla();
        } else {
          return demanda.getCaixaPostalDemandante().getSigla();
        }
      }

    }
    return null;
  }

  public String getCaixaProxima() {

    if (demanda.getFlagDemandaPai()) {
      return "VÁRIOS";
    }

    for (int i = 0; i < fluxoDemandaList.size(); i++) {
      FluxoDemanda fluxo = fluxoDemandaList.get(i);
      if (fluxoDemandaList.size() == 1) {
        return fluxo.getCaixaPostal().getSigla();

        // Estiver no demandante pega o proximo
      } else if (demanda.getCaixaPostalDemandante().equals(demanda.getCaixaPostalResponsavel())) {
        return fluxo.getCaixaPostal().getSigla();
      } else if (fluxo.getCaixaPostal().equals(demanda.getCaixaPostalResponsavel()) && i + 1 <= fluxoDemandaList.size()) {
        return fluxoDemandaList.get(i + 1).getCaixaPostal().getSigla();
      }

    }

    return null;
  }

  /**
   * Remove o arquivo anexado.
   */
  public void limparAnexo() {
    this.anexo = null;
  }

  /**
   * Seta o conteúdo do web based editor via remote command.
   */
  public void setEditorContentFromRemoteCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    this.editorContent = params.get(CONTENT);
  }

  /*
   * Inicio dos campos com logica do Dados da demanda
   */
  public String getNumeroDemandaTratado() {
    StringBuilder sb = new StringBuilder();
    if (this.demanda.getDemandaPai() != null && this.demanda.getDemandaPai().getId() != null) {
      sb.append(this.demanda.getDemandaPai().getId());
      sb.append("/");
    }
    sb.append(this.demanda.getId());
    return sb.toString();
  }

  public String getPrazoAtendimento() {

    String dataFormatada = "";
    int qtdPrazoDias = 0;

    if (demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
      for (FluxoDemanda fluxoDemanda : fluxoDemandaList) {
        if (!fluxoDemanda.getOrdem().equals(0)) {
          qtdPrazoDias += fluxoDemanda.getPrazo();
        }
      }
    }

    if (qtdPrazoDias == 0) {
      return "--";
    } else {
      Date dataPrevista = feriadoService.adicionarDiasUteis(demanda.getDataHoraAbertura(), qtdPrazoDias);
      dataFormatada = DateUtil.formatDataPadrao(dataPrevista);
      return dataFormatada + "(" + qtdPrazoDias + " dias úteis)";
    }
  }

  public String getDataAbertura() {
    String dataAbertura = "--";

    if (demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.FECHADA)
        || demanda.getSituacao().equals(SituacaoEnum.CANCELADA)) {
      dataAbertura = DateUtil.formatDataPadrao(demanda.getDataHoraAbertura());
    }

    return dataAbertura;
  }

  public String getDataEncerramento() {
    String dataEncerramento = "--";

    if (demanda.getSituacao().equals(SituacaoEnum.FECHADA) || demanda.getSituacao().equals(SituacaoEnum.CANCELADA)) {
      if (demanda.getDataHoraEncerramento() != null) {
        dataEncerramento = DateUtil.formatDataPadrao(demanda.getDataHoraEncerramento());
      }
    }

    return dataEncerramento;
  }

  public String getSituacaoDemanda() {

    String situacao = "";

    if (demanda.getSituacao().equals(SituacaoEnum.CANCELADA)) {
      situacao = SituacaoEnum.CANCELADA.getDescricao();
    } else if (demanda.getSituacao().equals(SituacaoEnum.FECHADA)) {
      situacao = SituacaoEnum.FECHADA.getDescricao();
    } else if (demanda.getSituacao().equals(SituacaoEnum.RASCUNHO)) {
      situacao = SituacaoEnum.RASCUNHO.getDescricao() + " (DEMANDA AINDA NÃO ENCAMINHADA)";
    } else if (demanda.getSituacao().equals(SituacaoEnum.MINUTA)) {
      situacao = SituacaoEnum.MINUTA.getDescricao() + " (DEMANDA AINDA NÃO ENCAMINHADA)";
    } else if (demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
      situacao = "Aguardando ação da " + demanda.getCaixaPostalResponsavel().getSigla();
    }

    return situacao;
  }

  public String getMinutadaPor() {
    if (StringUtils.isNotBlank(this.demanda.getMatriculaMinuta())) {
      return this.demanda.getMatriculaMinuta().toUpperCase() + " - " + demanda.getNomeUsuarioMinuta();
    } else {
      return "--";
    }
  }

  public String getRascunhadaPor() {
    if (StringUtils.isNotBlank(this.demanda.getMatriculaRascunho())) {
      return this.demanda.getMatriculaRascunho().toUpperCase() + " - " + demanda.getNomeUsuarioRascunho();
    } else {
      return "--";
    }
  }

  public String getAbertaPor() {
    if (demanda.getSituacao().equals(SituacaoEnum.ABERTA) || demanda.getSituacao().equals(SituacaoEnum.FECHADA)
        || demanda.getSituacao().equals(SituacaoEnum.CANCELADA)) {
      if (demanda.getMatriculaDemandante() != null) {
        return demanda.getMatriculaDemandante().toUpperCase() + " - " + demanda.getNomeUsuarioDemandante();
      } else {
        return demanda.getMatriculaDemandante();
      }
    } else {
      return "--";
    }

  }

  /** Retorna o Assunto em estrutura de arvore com seus pais, avos, etc... */
  public String getArvoreAssunto() {
    return assuntoService.obterArvoreAssuntos(this.demanda.getAssunto());
  }

  public boolean isCaixaResponsavel(FluxoDemanda fluxo) {
    // fluxo.caixaPostal.id eq tratarDemandaController.demanda.caixaPostalResponsavel.id}
    if (fluxo.getCaixaPostal().getId().equals(demanda.getCaixaPostalResponsavel().getId())) {
      return true;
    }

    // Se o responsavel é externa
    if (demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
        || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)) {
      if (fluxo.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
          || fluxo.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Retorna true se a a demanda estiver na @EXTERNA e a caixa selecionada for a anterior
   */
  private boolean isCaixaResponsavelPelaExterna() {

    FluxoDemanda fluxoExterna = null;
    FluxoDemanda fluxoAtual = null;

    for (FluxoDemanda fluxoDem : fluxoDemandaList) {
      if (fluxoDem.getCaixaPostal().equals(caixaPostalSelecionada)) {
        fluxoAtual = fluxoDem;
      }

      if (fluxoDem.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
          || fluxoDem.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)) {
        fluxoExterna = fluxoDem;
      }

    }

    // Tá no fluxo demanda
    if (fluxoAtual != null) {

      // Se está na externa
      if (demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
          || demanda.getCaixaPostalResponsavel().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)) {

        if (fluxoExterna != null && fluxoExterna.getOrdem().equals(fluxoAtual.getOrdem() + 1)) {
          return true;
        }
      }
    }

    return false;
  }

  /** Obtem se pode ou não carregar o field de contrato. */
  public boolean getHabilitaContrato() {
    boolean retorno = Boolean.FALSE;
    if (this.demanda.getAssunto() != null && this.demanda.getAssunto().getContrato()) {
      retorno = Boolean.TRUE;
    }
    return retorno;
  }

  /** Adicionar itens a lista de contratos */
  public void adicionarContrato() {
    if (validarAdicionarContrato()) {
      this.demanda.getContratosList().add(demandaContratoService.obterDemandaContrato(this.demanda, this.contratoTemp));
      Collections.sort(this.demanda.getContratosList());
      this.contratoTemp = "";
    }
  }

  private boolean validarAdicionarContrato() {

    if (StringUtils.isBlank(this.contratoTemp)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA036"));
      this.contratoTemp = "";
      return false;
    }

    if (this.contratoTemp.length() < 3) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA037"));
      this.contratoTemp = "";
      return false;
    }

    if (this.demanda.getContratosList() == null) {
      this.demanda.setContratosList(new ArrayList<DemandaContrato>());
    }
    this.contratoTemp = demandaContratoService.formatacaoNumeroContrato(this.contratoTemp);
    for (DemandaContrato dc : this.demanda.getContratosList()) {
      if (dc.getNumeroContrato().equals(this.contratoTemp)) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA038"));
        this.contratoTemp = "";
        return false;
      }
    }

    return true;
  }

  /** Remover itens da lista de contratos */
  public void removerContrato(DemandaContrato item) {
    Iterator<DemandaContrato> dc = this.demanda.getContratosList().iterator();
    while (dc.hasNext()) {
      DemandaContrato demandaContrato = dc.next();
      if (demandaContrato.getNumeroContrato().equals(item.getNumeroContrato())) {
        dc.remove();
      }
    }
  }

  /** Validar avançar demanda com contrato obrigatório */
  public Boolean validarContratosObrigatorios() {
    if (demanda.getAssunto().getContrato()) {
      if (this.demanda.getContratosList().isEmpty()) {
        return false;
      }
      return true;
    } else {
      return true;
    }
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public Demanda getDemanda() {
    return demanda;
  }

  public void setDemanda(Demanda demanda) {
    this.demanda = demanda;
  }

  public List<FluxoDemanda> getFluxoDemandaList() {
    return fluxoDemandaList;
  }

  public List<FluxoDemanda> getFluxoDemandaListAtivos(Demanda demanda) {
    List<FluxoDemanda> fluxoDemandaList = new ArrayList<FluxoDemanda>();

    for (FluxoDemanda listaFluxo : demanda.getFluxosDemandasList()) {
      if (listaFluxo.isAtivo()) {
        fluxoDemandaList.add(listaFluxo);
      }
    }
    return fluxoDemandaList;
  }

  public List<FluxoDemanda> getFluxoDemandaListAtivos() {
    List<FluxoDemanda> fluxoDemandaList = new ArrayList<FluxoDemanda>();
    for (FluxoDemanda listaFluxo : this.fluxoDemandaList) {
      if (listaFluxo.isAtivo()) {
        fluxoDemandaList.add(listaFluxo);
      }
    }
    return fluxoDemandaList;
  }

  public List<FluxoDemanda> getFluxoDemandaListSemDemandante() {
    List<FluxoDemanda> fluxoDemandaListSemDemandante = new ArrayList<FluxoDemanda>();
    fluxoDemandaListSemDemandante = getFluxoDemandaListAtivos();
    fluxoDemandaListSemDemandante.remove(0);
    return fluxoDemandaListSemDemandante;
  }

  public void setFluxoDemandaList(List<FluxoDemanda> fluxoDemandaList) {
    this.fluxoDemandaList = fluxoDemandaList;
  }

  public List<DemandaHistoricoDTO> getHistoricoList() {
    return historicoList;
  }

  public void setHistoricoList(List<DemandaHistoricoDTO> historicoList) {
    this.historicoList = historicoList;
  }

  public List<CaixaPostal> getObservadorAssuntoList() {
    return observadorAssuntoList;
  }

  public void setObservadorAssuntoList(List<CaixaPostal> observadorAssuntoList) {
    this.observadorAssuntoList = observadorAssuntoList;
  }

  public List<CaixaPostal> getObservadorDemandaList() {
    return observadorDemandaList;
  }

  public void setObservadorDemandaList(List<CaixaPostal> observadorDemandaList) {
    this.observadorDemandaList = observadorDemandaList;
  }

  public List<CaixaPostal> getCaixaPostalObservadorDisponivesList() {
    return caixaPostalObservadorDisponivesList;
  }

  public void setCaixaPostalObservadorDisponivesList(List<CaixaPostal> caixaPostalObservadorDisponivesList) {
    this.caixaPostalObservadorDisponivesList = caixaPostalObservadorDisponivesList;
  }

  public Long getIdDemanda() {
    return idDemanda;
  }

  public void setIdDemanda(Long idDemanda) {
    this.idDemanda = idDemanda;
  }

  public List<DemandaVinculadaDTO> getDemandasVinculadasList() {
    return demandasVinculadasList;
  }

  public void setDemandasVinculadasList(List<DemandaVinculadaDTO> demandasVinculadasList) {
    this.demandasVinculadasList = demandasVinculadasList;
  }

  public CaixaPostal getCaixaPostalSelecionada() {
    return caixaPostalSelecionada;
  }

  public Atendimento getAtendimento() {
    return atendimento;
  }

  public void setAtendimento(Atendimento atendimento) {
    this.atendimento = atendimento;
  }

  public String getEditorContent() {
    return editorContent;
  }

  public void setEditorContent(String editorContent) {
    this.editorContent = editorContent;
  }

  public List<CaixaPostal> getCaixaPostalObservadorSelecionadosList() {
    return caixaPostalObservadorSelecionadosList;
  }

  public void setCaixaPostalObservadorSelecionadosList(List<CaixaPostal> caixaPostalObservadorSelecionadosList) {
    this.caixaPostalObservadorSelecionadosList = caixaPostalObservadorSelecionadosList;
  }

  public UploadedFile getAnexo() {
    return anexo;
  }

  /**
   * Seta o anexo vindo da tela somente se ele tiver sido selecionado.
   */
  public void setAnexo(UploadedFile anexo) {
    if (!ObjectUtils.isNullOrEmpty(anexo) && !ObjectUtils.isNullOrEmpty(anexo.getFileName())) {
      this.anexo = anexo;
    }
  }

  public MotivoReaberturaEnum getMotivoReaberturaEnum() {
    return motivoReaberturaEnum;
  }

  public void setMotivoReaberturaEnum(MotivoReaberturaEnum motivoReaberturaEnum) {
    this.motivoReaberturaEnum = motivoReaberturaEnum;
  }

  public Integer getPrazoConsulta() {
    return prazoConsulta;
  }

  public void setPrazoConsulta(Integer prazoConsulta) {
    this.prazoConsulta = prazoConsulta;
  }

  public List<Unidade> getUnidadeExternaList() {
    return unidadeExternaList;
  }

  public void setUnidadeExternaList(List<Unidade> unidadeExternaList) {
    this.unidadeExternaList = unidadeExternaList;
  }

  public Unidade getUnidadeExternaSelecionada() {
    return unidadeExternaSelecionada;
  }

  public void setUnidadeExternaSelecionada(Unidade unidadeExternaSelecionada) {
    this.unidadeExternaSelecionada = unidadeExternaSelecionada;
  }

  public boolean isProximaCaixaExterna() {

    proximaCaixaExterna = false;

    FluxoDemanda fluxoExterna = null;
    FluxoDemanda fluxoAtual = null;

    for (FluxoDemanda fluxoDem : fluxoDemandaList) {
      if (fluxoDem.getCaixaPostal().equals(caixaPostalSelecionada)) {
        fluxoAtual = fluxoDem;
      }

      if (fluxoDem.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)
          || fluxoDem.getCaixaPostal().getUnidade().getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)) {
        fluxoExterna = fluxoDem;
      }

    }

    // Tá no fluxo demanda
    if (fluxoAtual != null && fluxoExterna != null) {
      if (fluxoExterna.getOrdem().equals(fluxoAtual.getOrdem() + 1)) {
        proximaCaixaExterna = true;
      }
      // Se está na externa
    }

    return proximaCaixaExterna;
  }

  /**
   * Realiza a validação do anexo.
   * 
   * @return True se o anexo é válido e False caso contrário.
   */
  private boolean isAnexoValido() {

    if (ObjectUtils.isNullOrEmpty(this.anexo)) {
      return true;
    }

    if (this.anexo.getSize() > TAM_MAX_ANEXO) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA013"));
      return false;
    } else if (!this.anexo.getFileName().toLowerCase().endsWith(EXT_ZIP)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA012"));
      return false;
    }

    return true;
  }

  public void setProximaCaixaExterna(boolean proximaCaixaExterna) {
    this.proximaCaixaExterna = proximaCaixaExterna;
  }

  public Date getDataAtual() {
    return new Date();
  }

  public List<RelatorioConsolidadoDTO> getRelatorioConsolidado() {
    List<RelatorioConsolidadoDTO> relatorio = new ArrayList<RelatorioConsolidadoDTO>();

    for (Demanda filha : demandasFilhasList) {

      if (filha.getSituacao().equals(SituacaoEnum.FECHADA) && (demanda.getFlagDemandaPai()
          || (!demanda.getFlagDemandaPai() && filha.getCaixaPostalResponsavel().equals(this.caixaPostalSelecionada)))) {

        List<Atendimento> atendimentoList = atendimentoService.obterAtendimentosPorDemanda(filha.getId());
        Collections.sort(atendimentoList);
        Collections.reverse(atendimentoList);

        Atendimento ultimo = atendimentoList.iterator().next();

        RelatorioConsolidadoDTO dto = new RelatorioConsolidadoDTO();
        dto.setDemanda(this.demanda.getId() + "/" + filha.getId());
        dto.setRespostaDe(ultimo.getFluxoDemanda().getCaixaPostal().getSigla());
        dto.setData(ultimo.getDataHoraAtendimento());
        dto.setDetalhe(ultimo.getDescricao());

        relatorio.add(dto);
      }
    }

    return relatorio;
  }

  public boolean getFlagExibirBotaoConsolidar() {
    for (Demanda filha : demandasFilhasList) {
      if (filha.getSituacao().equals(SituacaoEnum.FECHADA)) {
        return true;
      }
    }

    return false;
  }

  public String getConteudoJsonGrafico() {
    return conteudoJsonGrafico;
  }

  public void setConteudoJsonGrafico(String conteudoJsonGrafico) {
    this.conteudoJsonGrafico = conteudoJsonGrafico;
  }

  public String getNomeUsuarioLogado() {
    return nomeUsuarioLogado;
  }

  public void setNomeUsuarioLogado(String nomeUsuarioLogado) {
    this.nomeUsuarioLogado = nomeUsuarioLogado;
  }

  public String getContratoTemp() {
    return contratoTemp;
  }

  public void setContratoTemp(String contratoTemp) {
    this.contratoTemp = contratoTemp;
  }

public CaixaPostal getCaixaPostalOrdemUm() {
	return caixaPostalOrdemUm;
}

public void setCaixaPostalOrdemUm(CaixaPostal caixaPostalOrdemUm) {
	this.caixaPostalOrdemUm = caixaPostalOrdemUm;
}

}
