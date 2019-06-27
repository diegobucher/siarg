/**
 * InclusaoDemandaController.java
 * Versão: 1.0.0.00
 * Data de Criação : 01-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.UploadedFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessRollbackException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.JavaScriptUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.arquitetura.web.FacesMessager;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.dto.KeyGroupValuesCollection;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaCampo;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.security.ControleAcesso;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.CamposObrigatoriosService;
import br.gov.caixa.gitecsa.siarg.service.DemandaCampoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

/**
 * Classe Controller para tela de inclusão de demandas.
 * 
 * @author f757783
 */
@Named
@ViewScoped
public class InclusaoDemandaController extends BaseController implements Serializable {
  
  private static final String PARAM_NUM_DEMANDA = "XXX";

  /** Serial. */
  private static final long serialVersionUID = 1L;

  /** Parâmetro Remote Command de Assunto */
  private static final String ID_ASSUNTO = "id-assunto";

  /** Parâmetro Remote Command para o conteúdo do editor */
  private static final String CONTENT = "content";
  
  /** Parâmetro Remote Command que contém a lista de caixas-postais marcadas */
  private static final String IDS_CAIXAS_POSTAIS = "ids_caixas_postais";
  
  /** Parâmetro Remote Command para prazo */
  private static final String DIAS_PRAZO = "dias_prazo";

  /** Formatação do nome do agrupamento de rede */
  private static final String PREFIXO_AGRUPAMENTO_REDE = "Rede %s";
  
  /** Arquivos zip */
  private static final String EXT_ZIP = ".zip";
  
  /** Número de caracteres para o substring da Rede */
  private static final int NUM_CHARS_SUBSTRING = 5;
  
  /** Caixa relativa ao demandante (ordem 0) */
  private static final int CAIXA_ORDEM_ZERO = 0;
  
  /** Primeira caixa depois da demandante (ordem 1) */
  private static final int CAIXA_ORDEM_UM = 1;
    
  /** Indica que foram marcadas várias caixas-postais de destino */
  private static final String VARIAS_CAIXAS_POSTAIS_DESTINO = "VÁRIAS";
  
  /** Tamanho máximo do anexo em bytes (5MB) */
  private static final long TAM_MAX_ANEXO = 5242880;
  
  /** View ID da tela de acompanhamento de demanda */
  private static final String VIEWID_ACOMPANHAMENTO = "/paginas/demanda/acompanhamento/acompanhamento.xhtml";
  
  private static final String VIEWID_INCLUSAO = "/paginas/demanda/inclusao/inclusao.xhtml";
  
  /** Service que contém as regras de negócio da entidade assunto */
  @Inject
  private AssuntoService assuntoService;
  
  /** Mensagens */
  @Inject
  private FacesMessager facesMessager;
  
  /** Service que contém as regras de negócio da entidade unidade */
  @Inject
  private UnidadeService unidadeService;
  
  /** Service que contém as regras de negócio da entidade feriado */ 
  @Inject
  private FeriadoService feriadoService;
  
  /** Service que contém as regras de negócio da entidade caixa-postal */
  @Inject
  private CaixaPostalService caixaPostalService;
  
  /** Service que contém as regras de negócio da entidade demanda */
  @Inject
  private DemandaService demandaService;
  
  /** Service que contém as regras de negócio da entidade campos obrigatorios */
  @Inject
  private CamposObrigatoriosService camposObrigatoriosService;
  
  /** Service que contém as regras de negócio da entidade campos obrigatorios */
  @Inject
  private DemandaCampoService demandaCampoService;
  
  /** Lista de assuntos de uma demanda */
  private List<Assunto> arvoreAssunto;
  
  /** Lista de unidades/caixas-postais */
  private KeyGroupValuesCollection<CaixaPostal> agrupamentoCaixaPostal;

  /** Assunto selecionado */
  private Assunto assuntoSelecionado;

  /** HTML Editor */
  private String editorContent;

  /** Anexo */
  private UploadedFile anexo;

  /** Instância da demanda */
  private Demanda instance = new Demanda();
  
  /** Caixas-postais marcadas */
  private List<Long> caixasPostaisMarcadas;
  
  /** Prazo */
  private Integer prazo = 0;
  
  /** Vencimento do prazo */
  private Date dataLimite;
  
  /** Árvore genealógica do assunto selecionado */
  private List<Assunto> hierarquiaAssuntoSelecionado;
  
  /** Destino da demanda */
  private String destino;
  
  /** Caixas-postais da unidade do usuário */
  private List<CaixaPostal> caixaPostalList;
  
  /** Demandante da demanda */
  private CaixaPostal demandante;
  
  /** Tipo de fluxo definido */
  private TipoFluxoEnum tipoFluxoAssunto;
  
  /** Lista de fluxos de assunto da demanda */
  private List<FluxoAssunto> fluxoList;
  
  /** Lista de fluxos de assunto da demanda sem o demandante */
  private List<FluxoAssunto> fluxoListSemDemandante;
  
  /** Caixas-postais pré-definidas pelo responsável */
  private List<CaixaPostal> caixaPostalPredefinidaList;
  
  /** Caixas-postais autorizadas a visualizar esta demanda */
  private List<CaixaPostal> caixaPostalObservadorList;
  
  /** Caixas-postais selecionadas pelo usuário para visualizar esta demanda */
  private List<CaixaPostal> caixaPostalAutorizadaList;
  
  /** Lista de campos obrigatorios */
  private List<CamposObrigatorios> camposObrigatoriosList;
  
  /** Destinos para fluxos dinâmicos do tipo 2 */
  private String atoresDemanda;
  
  private Unidade unidadeSelecionada;
  
  private Map<Long, List<DemandaCampo>> mapaCamposPreenchidosDemanda;
  
  @PostConstruct
  public void init() {    
    //** [RI7] Demandante padrão */
    this.demandante = (CaixaPostal)RequestUtils.getSessionValue("caixaPostal");
    
    UnidadeDTO unidadeDTO = (UnidadeDTO)RequestUtils.getSessionValue("unidadeSelecionadaDTO");

    this.caixaPostalList = this.caixaPostalService.findByUnidade(unidadeDTO);
    
    this.unidadeSelecionada = this.unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());

    this.demandante.setUnidade(this.unidadeSelecionada);
    
    this.mapaCamposPreenchidosDemanda = new HashMap<>();
    
  }
  
  /**
   * Obtém os assuntos organizados hierarquicamente.
   * 
   * @return Uma string JSON contendo todos os assuntos.
   */
  public List<Assunto> getArvoreAssunto() {
    if (ObjectUtils.isNullOrEmpty(this.arvoreAssunto)) {
      this.arvoreAssunto = this.assuntoService.getRelacionamentoAssuntos(unidadeSelecionada.getAbrangencia());
    }

    return this.arvoreAssunto;
  }

  /**
   * Constrói a lista de assuntos ordenada hierarquicamente.
   */
  private void contruirArvoreGenealogicaAssunto() {
    this.hierarquiaAssuntoSelecionado = new ArrayList<Assunto>();
    this.adicionarAssuntoArvoreGenealogica(this.assuntoSelecionado);
    Collections.reverse(this.hierarquiaAssuntoSelecionado); 
  }
  
  /**
   * Inclui cada assunto que faz parte da hierarquia do assunto selecionado.
   * 
   * @param assunto
   *          Assunto que possui um grau de parentesco com o assunto selecionado.
   */
  private void adicionarAssuntoArvoreGenealogica(final Assunto assunto) {
    if (!ObjectUtils.isNullOrEmpty(assunto)) {
      this.hierarquiaAssuntoSelecionado.add(assunto);
      this.adicionarAssuntoArvoreGenealogica(assunto.getAssuntoPai());
    }
  }
  
  /**
   * Seta o assunto selecionado na árvore via remote command.
   */
  public void selectAssuntoFromRemoteCommand() {
    try {
      FacesContext context = FacesContext.getCurrentInstance();
      Map<String, String> params = context.getExternalContext().getRequestParameterMap();
      
      Boolean isAlteracao = (ObjectUtils.isNullOrEmpty(this.assuntoSelecionado)) ? false : true;
      
      this.assuntoSelecionado = this.assuntoService.findByIdEager(Long.valueOf(params.get(ID_ASSUNTO)));
      this.editorContent = this.assuntoSelecionado.getTextoPreDefinido();
      
      this.contruirArvoreGenealogicaAssunto();
      this.carregarDadosDemanda();
      this.carregarCaixasPostaisEmCopia();
      this.verificarExistenciaUnidadeExternaFluxo();
      this.carregarCamposObrigatorios(Long.valueOf(params.get(ID_ASSUNTO)));
      
      if (isAlteracao) {
        this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("MA014"));
        JavaScriptUtils.update("message");
      }
      
    } catch (DataBaseException e) {
      LOGGER.error(e.getMessage(), e);
    }   
  }

  /**
   * Seta o conteúdo do web based editor via remote command.
   */
  public void setEditorContentFromRemoteCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    this.editorContent = params.get(CONTENT);
  }
  
  /**
   * Seta os ids das caixas-postais marcadas no modal de seleção via remote command.
   */
  public void markCaixasPostaisFromRemoteCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    
    Type type = new TypeToken<List<Long>>(){}.getType();
    this.caixasPostaisMarcadas = new Gson().fromJson(params.get(IDS_CAIXAS_POSTAIS), type);
    this.determinarDestinoDemanda();
  }

  /**
   * Seta o prazo da demanda em dias.
   */
  public void setPrazoFromRemoteCommand() {
    try {
      FacesContext context = FacesContext.getCurrentInstance();
      Map<String, String> params = context.getExternalContext().getRequestParameterMap();

      this.prazo = Integer.valueOf(params.get(DIAS_PRAZO));
      this.dataLimite = this.feriadoService.calcularDataVencimentoPrazo(new Date(), this.prazo);
    } catch (DataBaseException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /** Incluí uma nova demanda. 
 * @throws BusinessRollbackException */
  public void salvar() throws BusinessRollbackException {
    
    MensagemUtil.setKeepMessages(true);
    
    if (this.validarCampos()) {
      /* 
       * RN009 - Inclusão de demanda com a situação igual à ABERTA
       * 
       * Para inclusão de demanda com a situação igual à ABERTA, o sistema deve atender um dos itens abaixo:
       * 1. ASSUNTO com a permissão de abertura para todos os usuários;
       * 2. Usuários com a marcação de GESTOR;
       * 3. Tipo de Unidade do usuário logado ser igual à MATRIZ;
       * 
       * Caso não atenda os itens acima, o sistema deve incluir a situação da demanda igual à MINUTA.
       */
      String msg = StringUtils.EMPTY;
      UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
      
      if (this.isSituacaoAberta()) {
        msg = MensagemUtil.obterMensagem("MA006", PARAM_NUM_DEMANDA);
        
        this.instance.setMatriculaDemandante(usuario.getNuMatricula());
        this.instance.setNomeUsuarioDemandante(usuario.getNomeUsuario());
        this.instance.setSituacao(SituacaoEnum.ABERTA);
      } else {
        msg = MensagemUtil.obterMensagem("MA008", PARAM_NUM_DEMANDA);
        
        this.instance.setMatriculaMinuta(usuario.getNuMatricula());
        this.instance.setNomeUsuarioMinuta(usuario.getNomeUsuario());
        this.instance.setSituacao(SituacaoEnum.MINUTA);
      }
      
      this.setAtributosDemanda();
      
      try {
        this.demandaService.salvar(this.instance, this.anexo);
        
        msg = msg.replaceAll(PARAM_NUM_DEMANDA, this.instance.getId().toString());
        
        RequestUtils.setSessionValue(Constantes.FLASH_SUCCESS_MESSAGE, msg);        
        RequestUtils.redirect(VIEWID_ACOMPANHAMENTO);
        
      } catch (BusinessException e) {
        this.facesMessager.addMessageError(e.getMessage());
      } catch (BusinessRollbackException bre) {
        try {
        	RequestUtils.redirect(VIEWID_ACOMPANHAMENTO);
        	RequestUtils.setSessionValue(Constantes.FLASH_ERROR_MESSAGE, MensagemUtil.obterMensagem("demanda.mensagem.erro.persistencia.fluxoDemanda"));        
		} catch (IOException e) {
		}
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
      }
    }
  }
  
  public boolean validarCamposObrigatoriosDemandaCampo() {
    List<DemandaCampo> listaDemandaCamposPreenchidos = this.montarListaDemandaCampos();
    
    if (!ObjectUtils.isNullOrEmpty(camposObrigatoriosList)) {
        List<DemandaCampo> listaCamposPreenchidos = this.montarListaDemandaCampos();
        if (ObjectUtils.isNullOrEmpty(listaCamposPreenchidos)) {
          return false;
        }
        
        Set<Long> chavesCamposObrigatorios = new HashSet<>();
        for (CamposObrigatorios campos : camposObrigatoriosList) {
          chavesCamposObrigatorios.add(campos.getId());
        }
        
        Set<Long> chavesCamposPreenchidos = mapaCamposPreenchidosDemanda.keySet();
        
        if (!chavesCamposPreenchidos.containsAll(chavesCamposObrigatorios)) {
          return false;
        }
        
    }

    if (!ObjectUtils.isNullOrEmpty(listaDemandaCamposPreenchidos)) {
      for (DemandaCampo demandaCampo : listaDemandaCamposPreenchidos) {
        if (StringUtils.isBlank(demandaCampo.getDemandaCampo())) {
          return false;
        }
      }
    }

    return true;
  }  

  /** Grava a demanda em modo rascunho */
  public void salvarRascunho() {
    
    MensagemUtil.setKeepMessages(true);
    
    if (this.validarCampos()) {
      
      UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
      this.instance.setMatriculaRascunho(usuario.getNuMatricula());
      this.instance.setNomeUsuarioRascunho(usuario.getNomeUsuario());
      this.instance.setSituacao(SituacaoEnum.RASCUNHO);
      
      this.setAtributosDemanda();
      
      try {
        this.demandaService.salvar(this.instance, this.anexo);
        
        RequestUtils.setSessionValue(Constantes.FLASH_SUCCESS_MESSAGE, MensagemUtil.obterMensagem("MA007", this.instance.getId().toString()));        
        RequestUtils.redirect(VIEWID_ACOMPANHAMENTO);
        
      } catch (BusinessException e) {
        this.facesMessager.addMessageError(e.getMessage());
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.erro.desconhecido"));
      }
    }
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
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
  
  /**
   * Determina os dados da demanda que dependem diretamente do demandante: prazo, data limite, destino e fluxo.
   * 
   * @throws DataBaseException
   */
  private void carregarDadosDemanda() throws DataBaseException {
    
    this.atoresDemanda = "";
    
    this.tipoFluxoAssunto = this.assuntoService.determinarTipoFluxoAssunto(this.assuntoSelecionado, this.demandante);
    this.fluxoList = this.assuntoService.findFluxoAssunto(this.assuntoSelecionado, this.tipoFluxoAssunto);
    this.fluxoList = setDemandanteComoPrimeiroDoFluxo(this.demandante, this.fluxoList);
    this.fluxoListSemDemandante = this.fluxoList;
    //Removendo a unidade demandante
    this.fluxoListSemDemandante.remove(0);
    
    this.calcularPrazoDemanda();
    this.determinarDestinoDemanda();
  }
  
  /**
   * Adiciona o demandante como o primeiro item do fluxo de demanda
   */
  private List<FluxoAssunto> setDemandanteComoPrimeiroDoFluxo(CaixaPostal demandante, List<FluxoAssunto> fluxoList) {
    FluxoAssunto fluxoAssunto = new FluxoAssunto();
    fluxoAssunto.setCaixaPostal(this.demandante);
    fluxoAssunto.setAssunto(this.assuntoSelecionado);
    fluxoAssunto.setId(this.assuntoSelecionado.getId());
    fluxoAssunto.setOrdem(0);
    fluxoAssunto.setTipoFluxo(this.tipoFluxoAssunto);
    fluxoAssunto.setPrazo(1);
    
    this.fluxoList.add(0, fluxoAssunto);
    return this.fluxoList;
  }

  /**
   * Carrega a lista de caixas-postais observadoras e predefinidas pelo responsável.
   */
  private void carregarCaixasPostaisEmCopia() {
    this.caixaPostalPredefinidaList = this.assuntoService.findObservadores(this.assuntoSelecionado);
    
    if (ObjectUtils.isNullOrEmpty(this.caixaPostalObservadorList)) {
      this.caixaPostalObservadorList = this.caixaPostalService.findByAbrangenciaTipoUnidade(unidadeSelecionada.getAbrangencia(), TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);
      this.caixaPostalObservadorList.removeAll(this.caixaPostalPredefinidaList);
      this.caixaPostalObservadorList.remove(this.demandante);
    }
  }
  
  /**
   * Calcula o prazo (data limite) da demanda.
   * 
   * @throws DataBaseException 
   */
  private void calcularPrazoDemanda() throws DataBaseException {
    if (!TipoFluxoEnum.DEMANDANTE_RESPONSAVEL.equals(this.tipoFluxoAssunto)) {
      this.prazo = 0;
      for (FluxoAssunto fluxo : this.fluxoList) {
        this.prazo += fluxo.getPrazo();
      }
    } 
    
    this.dataLimite = this.feriadoService.calcularDataVencimentoPrazo(new Date(), this.prazo);
  }
  
  /**
   * Define qual o descritivo do destino da demanda a depender de seu tipo de fluxo.
   */
  private void determinarDestinoDemanda() {
    switch (this.tipoFluxoAssunto) {
      case DEMANDANTE_DEFINIDO:
        this.destino = this.fluxoList.get(CAIXA_ORDEM_ZERO).getCaixaPostal().getSigla();
        break;
      case OUTROS_DEMANDANTES:
        this.destino = this.assuntoSelecionado.getCaixaPostal().getSigla(); 
        break;
      case DEMANDANTE_RESPONSAVEL:
        if (!ObjectUtils.isNullOrEmpty(this.caixasPostaisMarcadas)) {
          this.destino =
              (this.caixasPostaisMarcadas.size() > 1) ? VARIAS_CAIXAS_POSTAIS_DESTINO : this.caixaPostalService.findById(
                  this.caixasPostaisMarcadas.get(0)).getSigla();
        
          List<CaixaPostal> caixaPostalDestinoList = this.caixaPostalService.findByRangeId(this.caixasPostaisMarcadas);
          String[] destinos = new String[caixaPostalDestinoList.size()];
          
          for (int i = 0; i < caixaPostalDestinoList.size(); i++) {
            destinos[i] = caixaPostalDestinoList.get(i).getSigla();
          }
          
          this.atoresDemanda = StringUtils.join(destinos, ", ");
        }
        break;
    }
  }
  
  /** 
   * Verifica se o fluxo estipulado pelo assunto selecionado, possui unidade Externa.
   */
  private void verificarExistenciaUnidadeExternaFluxo() {
    for (FluxoAssunto fluxo : this.fluxoList) {
      if (!ObjectUtils.isNullOrEmpty(fluxo.getCaixaPostal()) && !ObjectUtils.isNullOrEmpty(fluxo.getCaixaPostal().getUnidade()) 
          && TipoUnidadeEnum.ARROBA_EXTERNA.equals(fluxo.getCaixaPostal().getUnidade().getTipoUnidade())) {
        this.facesMessager.addMessageWarn(MensagemUtil.obterMensagem("MA005"));
        break;
      }
    }
  }
  
  /**
   * 
   */
  private void carregarCamposObrigatorios(Long IdAssunto) {
	  this.camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorAssunto(IdAssunto);
  }
  
  
  /**
   * Verifica se todos os campos foram preenchidos corretamente.
   */
  private boolean validarCampos() {
    boolean valido = true;
    
    /** VALIDAÇÃO ADICIONADA NA OS041 */
    /*if(!validarContratosObrigatorios()) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA036"));
      valido = false;
    }*/
    
    if (ObjectUtils.isNullOrEmpty(this.assuntoSelecionado) || ObjectUtils.isNullOrEmpty(this.prazo)
        || ObjectUtils.isNullOrEmpty(this.instance.getTitulo()) || ObjectUtils.isNullOrEmpty(this.editorContent) 
        || ObjectUtils.isNullOrEmpty(Jsoup.parse(this.editorContent).text()) || !validarCamposObrigatoriosDemandaCampo()) {
      valido = false;
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA002"));
    } else if (!this.isAnexoValido()) {
      this.anexo = null;
      valido = false;
    }
    
    return valido;
  }
  
  /**
   * Inicializa os dados da demanda.
   */
  private void setAtributosDemanda() {
    
    this.instance.setDataHoraAbertura(new Date());
    this.instance.setCor(Constantes.COR_PADRAO_DEMANDA);
    this.instance.setAssunto(this.assuntoSelecionado);
    this.instance.setCaixaPostalDemandante(this.demandante);
    this.instance.setTextoDemanda(this.editorContent);
    this.instance.setCaixasPostaisObservadorList(this.caixaPostalAutorizadaList);
    
    this.instance.setFlagDemandaPai(false);
    this.instance.setTipoDemanda(TipoDemandaEnum.NORMAL);
    
    List<DemandaCampo> listaDemandaCamposPreenchidos = this.montarListaDemandaCampos();
    if (!ObjectUtils.isNullOrEmpty(listaDemandaCamposPreenchidos)) {
      this.instance.setCamposList(listaDemandaCamposPreenchidos);
    }
    
    if(!ObjectUtils.isNullOrEmpty(this.fluxoList)) {
      this.fluxoList = setDemandanteComoPrimeiroDoFluxo(this.demandante, this.fluxoList);
    }
        
    // Cria uma cópia dos fluxos de assunto para fluxo demanda.
    if (!ObjectUtils.isNullOrEmpty(this.fluxoList) && !TipoFluxoEnum.DEMANDANTE_RESPONSAVEL.equals(this.tipoFluxoAssunto)) {
      this.instance.setCaixaPostalResponsavel(fluxoList.get(CAIXA_ORDEM_UM).getCaixaPostal());
      this.instance.setFluxosDemandasList(new ArrayList<FluxoDemanda>());
      for (FluxoAssunto fluxo : fluxoList) {
        FluxoDemanda copia = new FluxoDemanda(fluxo);
        copia.setDemanda(this.instance);
        this.instance.getFluxosDemandasList().add(copia);
      }
    } else if (!ObjectUtils.isNullOrEmpty(this.caixasPostaisMarcadas) && TipoFluxoEnum.DEMANDANTE_RESPONSAVEL.equals(this.tipoFluxoAssunto)) {
      // [RN010] Caso seja selecionada apenas uma CAIXA-POSTAL de destino da DEMANDA, o sistema deve abrir apenas uma
      // demanda e encaminhar para a CAIXA-POSTAL selecionada. Caso seja selecionada mais de uma CAIXA-POSTAL, o sistema
      // deve abrir uma DEMANDA PAI para a CAIXA-POSTAL DEMANDANTE e uma DEMANDA FILHA para cada unidade selecionada.
      if (this.caixasPostaisMarcadas.size() == 1) {
        this.instance.setCaixaPostalResponsavel(this.caixaPostalService.findById(this.caixasPostaisMarcadas.get(0)));
        
        this.instance.setFluxosDemandasList(new ArrayList<FluxoDemanda>());
        FluxoDemanda fluxoDemandante = gerarFluxoDemandante(this.instance);
        this.instance.getFluxosDemandasList().add(fluxoDemandante);
        
        this.instance.getFluxosDemandasList().add(new FluxoDemanda(this.instance, this.prazo));
        
      } else if (this.caixasPostaisMarcadas.size() > 1) {
        
        this.instance.setFlagDemandaPai(true);
        this.instance.setTipoDemanda(TipoDemandaEnum.CENTRALIZADORA);
        this.instance.setCaixaPostalResponsavel(this.instance.getCaixaPostalDemandante());
        
        // Fluxo Demanda: Demandante Pai => Responsável Pai
        this.instance.setFluxosDemandasList(new ArrayList<FluxoDemanda>());
        FluxoDemanda fluxoDemandante = gerarFluxoDemandante(this.instance);
        this.instance.getFluxosDemandasList().add(fluxoDemandante);
        this.instance.getFluxosDemandasList().add(new FluxoDemanda(this.instance, this.prazo));
        
        this.instance.setDemandaFilhosList(new ArrayList<Demanda>());
        
        for (Long id : this.caixasPostaisMarcadas) {
          CaixaPostal cxPostal = new CaixaPostal();
          cxPostal.setId(id);
          
          Demanda filha = new Demanda(this.instance);
          filha.setDemandaPai(this.instance);
          filha.setCaixaPostalResponsavel(cxPostal);
          filha.setFlagDemandaPai(false);
          filha.setTipoDemanda(TipoDemandaEnum.NORMAL);
          
          // Fluxo Demanda: Demandante Filha => Responsável Filha
          filha.setFluxosDemandasList(new ArrayList<FluxoDemanda>());
          FluxoDemanda fluxoDemandanteFilha = gerarFluxoDemandante(filha);
          filha.getFluxosDemandasList().add(fluxoDemandanteFilha);
          filha.getFluxosDemandasList().add(new FluxoDemanda(filha, this.prazo));
          
          this.instance.getDemandaFilhosList().add(filha);
        }
      }
    }
    
    // Caso a situação da demanda seja igual a aberta, deve-se criar um atendimento.
    if (SituacaoEnum.ABERTA.equals(this.instance.getSituacao())) {
      Atendimento atendimentoDemandante = new Atendimento();
      this.instance.setAtendimentosList(new ArrayList<Atendimento>());
      UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
      
      //Criando atendimento para o próprio demandante (Abertura da demanda)
      atendimentoDemandante.setDemanda(this.instance);
      atendimentoDemandante.setDescricao("Demanda incluida com sucesso.");
      atendimentoDemandante.setDataHoraRecebimento(new Date());
      atendimentoDemandante.setDataHoraAtendimento(new Date());
      atendimentoDemandante.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
      atendimentoDemandante.setMatricula(usuario.getNuMatricula());
      atendimentoDemandante.setNomeUsuarioAtendimento(usuario.getNomeUsuario());
      atendimentoDemandante.setFluxoDemanda(this.instance.getFluxosDemandasList().get(CAIXA_ORDEM_ZERO));
      
      this.instance.getAtendimentosList().add(atendimentoDemandante);
      
      Atendimento atendimentoProximaCaixa = new Atendimento();
      
      //Criando atendimento para a próxima caixa
      atendimentoProximaCaixa.setDemanda(this.instance);
      atendimentoProximaCaixa.setDataHoraRecebimento(new Date());
      atendimentoProximaCaixa.setFluxoDemanda(this.instance.getFluxosDemandasList().get(CAIXA_ORDEM_UM));
      
      this.instance.getAtendimentosList().add(atendimentoProximaCaixa);

      
      if (!ObjectUtils.isNullOrEmpty(this.instance.getDemandaFilhosList())) {
        for (Demanda filha : this.instance.getDemandaFilhosList()) {
          Atendimento atendimentoFilhaDemandante = new Atendimento();
          
          atendimentoFilhaDemandante.setDemanda(filha);
          atendimentoFilhaDemandante.setDataHoraRecebimento(new Date());
          atendimentoFilhaDemandante.setDataHoraAtendimento(new Date());
          atendimentoFilhaDemandante.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
          atendimentoFilhaDemandante.setMatricula(usuario.getNuMatricula());
          atendimentoFilhaDemandante.setNomeUsuarioAtendimento(usuario.getNomeUsuario());
          atendimentoFilhaDemandante.setFluxoDemanda(this.instance.getFluxosDemandasList().get(CAIXA_ORDEM_ZERO));

          Atendimento atendimentoFilha = new Atendimento();
          
          atendimentoFilha.setDemanda(filha);
          atendimentoFilha.setDataHoraRecebimento(new Date());
          atendimentoFilha.setFluxoDemanda(filha.getFluxosDemandasList().get(CAIXA_ORDEM_UM));
          
          filha.setAtendimentosList(new ArrayList<Atendimento>());
          filha.getAtendimentosList().add(atendimentoFilhaDemandante);
          filha.getAtendimentosList().add(atendimentoFilha);
        }
      }
      
    } else {
      // Solicitação: Definir a caixa postal responsável como demandante. 
      this.instance.setCaixaPostalResponsavel(this.instance.getCaixaPostalDemandante());
    }
  }
  
  private List<DemandaCampo> montarListaDemandaCampos() {
    if (!ObjectUtils.isNullOrEmpty(this.mapaCamposPreenchidosDemanda)) {
      Collection<List<DemandaCampo>> listasCampos = mapaCamposPreenchidosDemanda.values();
      List<DemandaCampo> listaRetorno = new ArrayList<>();
      for (List<DemandaCampo> lista : listasCampos) {
        if (!ObjectUtils.isNullOrEmpty(lista)) {
          listaRetorno.addAll(lista);
        }
      }
      return listaRetorno;
    }
    return new ArrayList<>();
  }

  private FluxoDemanda gerarFluxoDemandante(Demanda demanda) {
    FluxoDemanda fluxoDemandante = new FluxoDemanda();
    fluxoDemandante.setDemanda(demanda);
    fluxoDemandante.setCaixaPostal(demanda.getCaixaPostalDemandante());
    fluxoDemandante.setPrazo(1);
    fluxoDemandante.setOrdem(0);
    fluxoDemandante.setAtivo(true);
    fluxoDemandante.setTipoFluxoDemanda(this.tipoFluxoAssunto);
    return fluxoDemandante;
  }
  
  /**
   * Verifica se a demanda pode ser aberta com a situação "Aberta".
   * 
   * @return True se pode ter a situação aberta e False caso contrário.
   */
  private boolean isSituacaoAberta() {
    try {
      
      FacesContext context = FacesContext.getCurrentInstance();
      ControleAcesso controleAcesso =
          (ControleAcesso) context.getApplication().getExpressionFactory()
              .createValueExpression(context.getELContext(), "#{controleAcesso}", ControleAcesso.class)
              .getValue(context.getELContext());
      
      UnidadeDTO unidadeSessao = controleAcesso.getUnidadeSelecionadaDTO();
      Unidade unidade = this.unidadeService.obterUnidadePorChaveFetch(unidadeSessao.getId());
      
      if (this.assuntoSelecionado.getPermissaoAbertura() || controleAcesso.getGerente()
          || TipoUnidadeEnum.MATRIZ.equals(unidade.getTipoUnidade())) {
        return true;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    
    return false;
  }
  
  /**
   * Listener para o evento onChange do comobox de demandante.
   */
  public void onChangeDemandante() {
    try {
      this.carregarDadosDemanda();
      this.agrupamentoCaixaPostal = null;
      this.caixaPostalObservadorList = null;
      this.carregarCaixasPostaisEmCopia();
      
      if (TipoFluxoEnum.DEMANDANTE_RESPONSAVEL.equals(this.tipoFluxoAssunto)) {
        this.prazo = 0;
        this.dataLimite = null;
        this.destino = null;
        this.atoresDemanda = null;
        this.assuntoSelecionado = null;
        this.hierarquiaAssuntoSelecionado = new ArrayList<Assunto>();
      }
      
    } catch (DataBaseException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  /**
   * Obtém o agrupamento das unidades/caixas-postais ativas.
   * 
   * @return Unidades/caixas-postais ativas
   */
  public KeyGroupValuesCollection<CaixaPostal> getAgrupamentoCaixaPostal() {
    if (ObjectUtils.isNullOrEmpty(this.agrupamentoCaixaPostal)) {
      
      List<CaixaPostal> caixasPostaisEnvolvidas = new ArrayList<CaixaPostal>();
      caixasPostaisEnvolvidas.add(this.demandante);
      
      this.agrupamentoCaixaPostal = demandaService.getAgrupamentoCaixaPostal(unidadeSelecionada.getAbrangencia(), caixasPostaisEnvolvidas);
      
    }

    return this.agrupamentoCaixaPostal;
  }
  
  /**
   * Indica se a demanda pode ser salvar como rascunho.
   * 
   * @return True se a demanda pode ser salva como rascunho e False caso contrário.
   */
  public Boolean canSalvarRascunho() {
    return !TipoFluxoEnum.DEMANDANTE_RESPONSAVEL.equals(this.tipoFluxoAssunto);
  }
  
  /**
   * Remove o arquivo anexado.
   */
  public void limparAnexo() {
    this.anexo = null;
  }
  
  
  /** Obtém o assunto selecionado */
  public Assunto getAssuntoSelecionado() {
    return assuntoSelecionado;
  }

  /** Seta o assunto selecionado */
  public void setAssuntoSelecionado(Assunto assuntoSelecionado) {
    this.assuntoSelecionado = assuntoSelecionado;
  }

  /** Obtém o conteúdo do editor */
  public String getEditorContent() {
    return editorContent;
  }
  
  /** Obtem se pode ou não carregar o field de contrato. */
  public boolean getHabilitaContrato() {
    boolean retorno = Boolean.FALSE;
    if (this.assuntoSelecionado != null && this.assuntoSelecionado.getContrato()) {
      retorno = Boolean.TRUE;
    }
    return retorno ;
  }
  
  public List<DemandaCampo> obterListaCamposObrigatorios(Long idCampoObrigatorio){
    if (idCampoObrigatorio != null && idCampoObrigatorio > 0) {
      return mapaCamposPreenchidosDemanda.get(idCampoObrigatorio);
    }
    return new ArrayList<>();
  }

  /** Adicionar itens a lista de campos obrigatorios */
  public void adicionarCampo(CamposObrigatorios camposObrigatorios) {
    if (validarAdicionarCampos(camposObrigatorios)) {
      Long key = camposObrigatorios.getId();
      List<DemandaCampo> listaDemandaCampo = new ArrayList<>();      
      if (mapaCamposPreenchidosDemanda.containsKey(key)) {        
        List<DemandaCampo> listaDemandaCampoTemp = mapaCamposPreenchidosDemanda.get(key);        
        if (!ObjectUtils.isNullOrEmpty(listaDemandaCampoTemp)) {
          listaDemandaCampo = listaDemandaCampoTemp;
        }
      }
      
      listaDemandaCampo.add(demandaCampoService.obterDemandaCampos(this.instance, camposObrigatorios));
      mapaCamposPreenchidosDemanda.put(key, listaDemandaCampo);
    }
    camposObrigatorios.setDemandaCampo(null);
  }
  
  public void removerCampo(DemandaCampo campoPreenchido) {
    Long key = campoPreenchido.getCamposObrigatorios().getId();
    if (mapaCamposPreenchidosDemanda.containsKey(key)) {
      mapaCamposPreenchidosDemanda.get(key).remove(campoPreenchido);
    }
  }
  
  private boolean validarAdicionarCampos(CamposObrigatorios camposObrigatorios) {
    
    if(!demandaCampoService.verificarFormatacaoMascaraCampoObrigatorio(camposObrigatorios)) {
      return false;
    }
    
    List<DemandaCampo> listaCampos = this.mapaCamposPreenchidosDemanda.get(camposObrigatorios.getId());
    
    if (StringUtils.isBlank(camposObrigatorios.getDemandaCampo())) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA075", String.valueOf(camposObrigatorios.getNome())));
      return false;
    }
    
    if (!ObjectUtils.isNullOrEmpty(listaCampos)) {
      if (listaCampos.size() >= camposObrigatorios.getQuantidade()) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA077", String.valueOf(camposObrigatorios.getQuantidade()), String.valueOf(camposObrigatorios.getNome())));
        return false;
      } 
      
      for (DemandaCampo dc : listaCampos) {
        if (dc.getDescDemandaCampoFormatada().equals(camposObrigatorios.getDemandaCampo())) {
          this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA076", String.valueOf(camposObrigatorios.getNome())));
          return false;
        }
      }      
    }
    
    return true;
  }

  /** Remover itens da lista de contratos */
  public void removerContrato(DemandaContrato item) {
    Iterator<DemandaContrato> dc = this.instance.getContratosList().iterator();
    while (dc.hasNext()) {
      DemandaContrato demandaContrato = dc.next();
      if (demandaContrato.getNumeroContrato().equals(item.getNumeroContrato())) {
        dc.remove();
      }
    }
  }
  
  /** Validar avançar demanda com contrato obrigatório */
  public Boolean validarContratosObrigatorios() {
    if (assuntoSelecionado.getContrato()) {      
      if (this.instance.getContratosList() == null || this.instance.getContratosList().isEmpty()) {
        return false;
      } 
      return true;        
    } else {
      return true;
    }
  }
  
  
  /**
   * Seta o conteúdo do editor
   * 
   * @param content
   */
  public void setEditorContent(String content) {
    this.editorContent = content;
  }

  /** Obtém o anexo da demanda */
  public UploadedFile getAnexo() {
    return anexo;
  }

  /**
   * Seta o anexo da demanda
   * 
   * @param anexo
   */
  public void setAnexo(UploadedFile anexo) {
    if (!ObjectUtils.isNullOrEmpty(anexo) && !ObjectUtils.isNullOrEmpty(anexo.getFileName())) {
      this.anexo = anexo;
    }
  }

  /**
   * Obtém a instância da demanda.
   */
  public Demanda getInstance() {
    return instance;
  }

  /**
   * Seta a instância da demanda.
   * 
   * @param instance
   *          Demanda
   */
  public void setInstance(Demanda instance) {
    this.instance = instance;
  }
  
  /**
   * Obtém as caixas-postais de destino marcadas no modal de seleção.
   * 
   * @return Identificadores das caixas-postais
   */
  public List<Long> getCaixasPostaisMarcadas() {
    return caixasPostaisMarcadas;
  }
  
  /**
   * Obtém a definição do prazo.
   * 
   * @return Prazo em dias
   */
  public Integer getPrazo() {
    return prazo;
  }
  
  /**
   * Obtém lista de assuntos em ordem hierarquica.
   * 
   * @return Lista de assuntos
   */
  public List<Assunto> getHierarquiaAssuntoSelecionado() {
    return hierarquiaAssuntoSelecionado;
  }
  
  /**
   * Seta lista de assuntos em ordem hierarquica.
   * 
   * @param hierarquiaAssuntoSelecionado
   *          Lista de assuntos
   */
  public void setHierarquiaAssuntoSelecionado(List<Assunto> hierarquiaAssuntoSelecionado) {
    this.hierarquiaAssuntoSelecionado = hierarquiaAssuntoSelecionado;
  }
  
  /**
   * Obtém a data limite do prazo da demanda.
   * 
   * @return Data limite
   */
  public Date getDataLimite() {
    return dataLimite;
  }
  
  /**
   * Seta a data limite do prazo da demanda.
   * 
   * @param dataLimite
   *          Data limite
   */
  public void setDataLimite(Date dataLimite) {
    this.dataLimite = dataLimite;
  }
  
  /**
   * Obtém o destino da demanda.
   * 
   * @return Caixa-postal de destino
   */
  public String getDestino() {
    return destino;
  }
  
  /**
   * Seta o destino da demanda.
   * 
   * @param destino Caixa-postal de destino.
   */
  public void setDestino(String destino) {
    this.destino = destino;
  }
  
  /**
   * Obtém a lista de caixas-postais da unidade do usuário.
   * 
   * @return Lista de caixas-postais
   */
  public List<CaixaPostal> getCaixaPostalList() {
    return caixaPostalList;
  }
  
  /**
   * Seta a lista de caixas-postais da unidade do usuário.
   * 
   * @param caixaPostalList
   */
  public void setCaixaPostalList(List<CaixaPostal> caixaPostalList) {
    this.caixaPostalList = caixaPostalList;
  }
  
  /**
   * Obtém o demandante da demanda.
   * 
   * @return Demandante
   */
  public CaixaPostal getDemandante() {
    return demandante;
  }
  
  /**
   * Seta o demandante da demanda.
   * 
   * @param demandante
   *          Demandante
   */
  public void setDemandante(CaixaPostal demandante) {
    this.demandante = demandante;
  }

  /**
   * Obtém o tipo de fluxo de assunto determinado.
   * 
   * @return Tipo de fluxo de assunto
   */
  public TipoFluxoEnum getTipoFluxoAssunto() {
    return tipoFluxoAssunto;
  }

  /**
   * Seta o tipo de fluxo de assunto determinado para a demanda.
   * 
   * @param tipoFluxoAssunto
   *          Tipo de fluxo de assunto
   */
  public void setTipoFluxoAssunto(TipoFluxoEnum tipoFluxoAssunto) {
    this.tipoFluxoAssunto = tipoFluxoAssunto;
  }

  /**
   * Obtém os fluxos de assunto cadastrados para o assunto e tipo de fluxo.
   * 
   * @return Lista contendo os fluxos de assunto da demanda.
   */
  public List<FluxoAssunto> getFluxoList() {
    return fluxoList;
  }

  /**
   * Seta os fluxos de assunto cadastrados para o assunto e tipo de fluxo.
   * 
   * @param fluxoList
   *          Lista de fluxos de assunto
   */
  public void setFluxoList(List<FluxoAssunto> fluxoList) {
    this.fluxoList = fluxoList;
  }
  
  /**
   * Obtém a lista de caixas-postais predefinida pelo responsável (cópia da demanda).
   * 
   * @return Lista de caixas-postais
   */
  public List<CaixaPostal> getCaixaPostalPredefinidaList() {
    return caixaPostalPredefinidaList;
  }
  
  /**
   * Seta a lista de caixas-postais predefinida pelo responsável (cópia da demanda).
   * 
   * @param caixaPostalPredefinidaList
   *          Lista de caixas-postais
   */
  public void setCaixaPostalPredefinidaList(List<CaixaPostal> caixaPostalPredefinidaList) {
    this.caixaPostalPredefinidaList = caixaPostalPredefinidaList;
  }
  
  /**
   * Obtém a lista de caixas-postais observadoras.
   * 
   * @return Lista de caixas-postais.
   */
  public List<CaixaPostal> getCaixaPostalObservadorList() {
    return caixaPostalObservadorList;
  }
  
  /**
   * Seta a lista de caixas-postais observadoras.
   * 
   * @param caixaPostalObservadorList
   *          Lista de caixas-postais.
   */
  public void setCaixaPostalObservadorList(List<CaixaPostal> caixaPostalObservadorList) {
    this.caixaPostalObservadorList = caixaPostalObservadorList;
  }
  
  /**
   * Obtém a lista de caixas-postais observadoras selecionadas.
   * 
   * @return Lista de caixas-postais.
   */
  public List<CaixaPostal> getCaixaPostalAutorizadaList() {
    return caixaPostalAutorizadaList;
  }
  
  /**
   * Seta a lista de caixas-postais observadoras selecionadas.
   * 
   * @param caixaPostalAutorizadaList
   *          Lista de caixas-postais.
   */
  public void setCaixaPostalAutorizadaList(List<CaixaPostal> caixaPostalAutorizadaList) {
    this.caixaPostalAutorizadaList = caixaPostalAutorizadaList;
  }
  
  /**
   * Obtém a URL de retorno (callback URL). Esta URL é a última página visitada antes de acessar a funcionalidade de
   * inclusão de demandas.
   * 
   * @return URL anterior.
   */
  public String getCallbackUrl() {
    return "/paginas/demanda/acompanhamento/acompanhamento.xhtml?faces-redirect=true";
  }

  public String getAtoresDemanda() {
    return atoresDemanda;
  }

  public void setAtoresDemanda(String atoresDemanda) {
    this.atoresDemanda = atoresDemanda;
  }

  public List<FluxoAssunto> getFluxoListSemDemandante() {
    return fluxoListSemDemandante;
  }

  public void setFluxoListSemDemandante(List<FluxoAssunto> fluxoListSemDemandante) {
    this.fluxoListSemDemandante = fluxoListSemDemandante;
  }

  /**
   * Obtém a lista de campos obrigatorios
   * 
   * @return Lista de caixas-postais.
   */
  public List<CamposObrigatorios> getCamposObrigatoriosList() {
	return camposObrigatoriosList;
  }

  public void setCamposObrigatoriosList(List<CamposObrigatorios> camposObrigatoriosList) {
	this.camposObrigatoriosList = camposObrigatoriosList;
  }

  public Map<Long, List<DemandaCampo>> getMapaCamposPreenchidosDemanda() {
    return mapaCamposPreenchidosDemanda;
  }

  public void setMapaCamposPreenchidosDemanda(Map<Long, List<DemandaCampo>> mapaCamposPreenchidosDemanda) {
    this.mapaCamposPreenchidosDemanda = mapaCamposPreenchidosDemanda;
  }
  
  
  
}
