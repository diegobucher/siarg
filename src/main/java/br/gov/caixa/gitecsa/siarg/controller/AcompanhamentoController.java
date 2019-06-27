/**
 * AcompanhamentoController.java
 * Versão: 1.0.0.00
 * Data de Criação : 29-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.arquitetura.util.Util;
import br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.DemandaCampo;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

/**
 * Classe Controller para tela de acomnpanhamento.
 * @author f520296
 */
@Named
@ViewScoped
public class AcompanhamentoController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 2367798259069926030L;

  /** Parâmetro Remote Command para o conteúdo do editor */
  private static final String IDS_CORES = "ids_cores";

  /** Service que contém as regras de negócio da entidade unidade. */
  @Inject
  private UnidadeService unidadeService;

  /** Service que contém as regras de negócio da entidade demanda. */
  @Inject
  private DemandaService demandaService;

  /** Service que contém as regras de negócio da entidade CAixa Postal. */
  @Inject
  private CaixaPostalService caixaPostalService;

  /** Service que contém as regras de negócio da entidade Feriado. */
  @Inject
  private FeriadoService feriadoService;

  /** Service que contém as regras de negócio da entidade Feriado. */
  @Inject
  private AssuntoService assuntoService;

  /** Variável de Classe. */
  private Long idUnidadeSelecionada;

  /** Variável de Classe. */
  private Unidade unidadeSelecionada;

  /** Variável de Classe. */
  private UnidadeDTO unidadeSelecionadaDTO;

  /** Variável de Classe. */
  private String responsavelFiltro;

  /** Variável de Classe. */
  private EnvolvimentoEnum envolvimentoFiltro;

  /** Variável de Classe. */
  private String corDemanda;

  /** Variável de Classe. */
  private String corDemandaFiltroPriori;

  /** Variável de Classe. */
  private String corDemandaFiltroDemais;

  /** Variável de Classe. */
  private SituacaoEnum situacaoPrioriFiltro;

  /** Variável de Classe. */
  private SituacaoEnum situacaoDemaisFiltro;

  /** Variável de Classe. */
  private CaixaPostal caixaPostalSelecionada;

  /** Variável de Classe. */
  private List<CaixaPostal> caixasPostaisList;

  /** Variável de Classe. */
  private List<DemandaDTO> demandasPrioridadesList;

  /** Variável de Classe. */
  private List<DemandaDTO> demaisDemandasList;

  /** Variável de Classe. */
  private List<UnidadeDTO> listaUnidades;

  /** Variável de Classe. */
  private List<Assunto> listaAssuntos;

  /** Variável de Classe. */
  private List<Date> datasFeriadosList;

  /** Variável de Classe. */
  private Date dataHoraAtual;

  /** Variáveis globais. */
  private Integer setTimeRefresh;

  /**
   * Método: Inicializar com a pagina.
   */
  @PostConstruct
  public void init() {
    this.limpar();
    this.setTimeRefresh = Integer.parseInt(System.getProperty(Constantes.SET_TIME_REFRESH));
    this.unidadeSelecionadaDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    if (this.unidadeSelecionadaDTO != null) {
      this.unidadeSelecionada = this.unidadeService.obterUnidadePorChaveFetch(this.unidadeSelecionadaDTO.getId());
      if (this.unidadeSelecionada != null) {
        this.listaAssuntos = this.assuntoService.obterAssuntosFetchPai();
        this.caixasPostaisList = this.unidadeSelecionada.getCaixasPostaisList();
        if ((this.caixasPostaisList != null) && !this.caixasPostaisList.isEmpty()) {
          Collections.sort(this.caixasPostaisList);
          this.demandaService.obterCaixaPostalComValoresDemandas(this.caixasPostaisList);
          
          //TODO MANTER CAIXA
          CaixaPostal caixaPostalNaSessao = (CaixaPostal) JSFUtil.getSessionMapValue("caixaPostal");
          this.caixaPostalSelecionada = null;

          //Caso tenha caixa postal na sessão busca na lista de caixas
          if(caixaPostalNaSessao != null) {
            for (CaixaPostal caixaPostal : caixasPostaisList) {
              if(caixaPostal.getId().equals(caixaPostalNaSessao.getId())) {
                caixaPostalSelecionada = caixaPostalNaSessao;
              }
            }
          }
          
          if(this.caixaPostalSelecionada == null) {
            this.caixaPostalSelecionada = this.caixasPostaisList.get(0);
          }
          JSFUtil.setSessionMapValue("caixaPostal", this.caixaPostalSelecionada);
          //TODO MANTER CAIXA
          
          this.datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();
          this.pesquisar();
        }
      }
      // Exibe mensagem de sucesso que tenha sido passada por outras telas do sistema.
      String message = (String) RequestUtils.getFlashData(Constantes.FLASH_SUCCESS_MESSAGE);
      String messageError = (String) RequestUtils.getFlashData(Constantes.FLASH_ERROR_MESSAGE);
      if (!ObjectUtils.isNullOrEmpty(message)) {
        this.facesMessager.addMessageInfo(message);
      } else if (!ObjectUtils.isNullOrEmpty(messageError)) {
    	  this.facesMessager.addMessageError(messageError);
      }
    }
  }

  /**
   * Método: Pesquisar os DTOs das Grids.
   */
  public void pesquisar() {
    this.dataHoraAtual = new Date();
    this.pesquisarPrioridade();
    this.pesquisarDemais();

  }

  public void pesquisarPrioridade() {
    this.demandasPrioridadesList =
        this.demandaService.obterListaDemandasPrioridadesDTO(this.caixaPostalSelecionada.getId(), this.responsavelFiltro,
            this.situacaoPrioriFiltro, this.datasFeriadosList, this.corDemandaFiltroPriori, this.listaAssuntos);
  }

  public void pesquisarDemais() {
    this.demaisDemandasList =
        this.demandaService.obterListaDemaisDemandasDTO(this.caixaPostalSelecionada.getId(), this.envolvimentoFiltro,
            this.situacaoDemaisFiltro, this.datasFeriadosList, this.corDemandaFiltroDemais, this.listaAssuntos);
  }
  
  public String maskCampoObrigtorio(DemandaCampo campoFormatar){
       
	  Boolean isCampoEmail = Util.validarEmail(campoFormatar.getDemandaCampo());
      if(!isCampoEmail 
    		  && campoFormatar != null
    		  && campoFormatar.getCamposObrigatorios() !=null
    		  && campoFormatar.getCamposObrigatorios().getMascara() !=null) {
    	  
    	  String valorMaskCampo = campoFormatar.getCamposObrigatorios().getMascara().replaceAll("x", "#"); 
    	  return format(valorMaskCampo, campoFormatar.getDemandaCampo());
      }
      
	return campoFormatar.getDemandaCampo();  
       
  }
  
	  private static String format(String pattern, Object value) {
	      try {
	    	  MaskFormatter mask = new MaskFormatter(pattern);
	          mask.setValueContainsLiteralCharacters(false);
	          return mask.valueToString(value);
	      } catch (ParseException e) {
	          throw new RuntimeException(e);
	      }
	  }

  /**
   * Método: Trocar a Unidade no COMBOBOX.
   */
  public void alterarUnidadeSelecionada() {
    this.init();
  }

  /**
   * Método: Trocar a Aba de Caixa Postal Atual.
   */
  public void alterarAbaCaixaPostalAtual(Long idCaixaPostal) {
    this.dataHoraAtual = new Date();
    this.responsavelFiltro = null;
    this.situacaoPrioriFiltro = null;
    this.situacaoDemaisFiltro = SituacaoEnum.ABERTA;
    this.envolvimentoFiltro = EnvolvimentoEnum.DEMANDANTE;
    this.corDemandaFiltroPriori = Constantes.COR_PADRAO_DEMANDA;
    this.corDemandaFiltroDemais = Constantes.COR_PADRAO_DEMANDA;
    this.caixaPostalSelecionada = this.caixaPostalService.findById(idCaixaPostal);
    JSFUtil.setSessionMapValue("caixaPostal", this.caixaPostalSelecionada);
    this.pesquisar();
  }

  /**
   * Método: Trocar a Situação no COMBOBOX Prioridade.
   */
  public void alterarSituacaoComboPrioridade() {
    this.pesquisarPrioridade();
  }

  /**
   * Método: Trocar o responsável no COMBOBOX Prioridade.
   */
  public void alterarResponsavelComboPrioridade() {
    this.pesquisarPrioridade();
  }

  /**
   * Método: Trocar a Situação no COMBOBOX Demais demandas.
   */
  public void alterarSituacaoComboDemaisDemandas() {
    this.pesquisarDemais();
  }

  /**
   * Método: Trocar o Envolvimento no COMBOBOX Demais demandas.
   */
  public void alterarEnvolvimentoComboDemaisDemandas() {
    this.pesquisarDemais();
  }

  public void consultar() {

  }

  private void limpar() {
    this.dataHoraAtual = new Date();
    this.responsavelFiltro = null;
    this.situacaoPrioriFiltro = null;
    this.situacaoDemaisFiltro = SituacaoEnum.ABERTA;
    this.envolvimentoFiltro = EnvolvimentoEnum.DEMANDANTE;
    this.corDemandaFiltroPriori = Constantes.COR_PADRAO_DEMANDA;
    this.corDemandaFiltroDemais = Constantes.COR_PADRAO_DEMANDA;

    this.unidadeSelecionadaDTO = new UnidadeDTO();
    this.unidadeSelecionada = new Unidade();
    this.caixaPostalSelecionada = new CaixaPostal();

    this.caixasPostaisList = new ArrayList<CaixaPostal>();
    this.demandasPrioridadesList = new ArrayList<DemandaDTO>();
    this.demaisDemandasList = new ArrayList<DemandaDTO>();
  }

  /**
   * Método: Alterar a cor na tabela de demanda.
   * @param demandaDTO demandaDTO
   */
  public void alterarCorDemanda() {

    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    Type type = new TypeToken<List<String>>() {
    }.getType();
    List<String> caixasPostaisMarcadas = new Gson().fromJson(params.get(IDS_CORES), type);

    Long idDemanda = Long.valueOf(caixasPostaisMarcadas.get(0));
    this.corDemanda = caixasPostaisMarcadas.get(1);
    this.demandaService.setColorInDemanda(idDemanda, this.corDemanda);
    this.pesquisarDemais();

  }

  /**
   * Método: Alterar o Filtro da cor na GRID de demanda.
   * @param demandaDTO demandaDTO
   */
  public void alterarFiltroCorDemandaPriori() {

    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    Type type = new TypeToken<List<String>>() {
    }.getType();
    List<String> caixasPostaisMarcadas = new Gson().fromJson(params.get(IDS_CORES), type);
    this.corDemandaFiltroPriori = caixasPostaisMarcadas.get(1);

    this.pesquisarPrioridade();

  }

  public void alterarFiltroCorDemandaDemais() {

    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    Type type = new TypeToken<List<String>>() {
    }.getType();
    List<String> caixasPostaisMarcadas = new Gson().fromJson(params.get(IDS_CORES), type);
    this.corDemandaFiltroDemais = caixasPostaisMarcadas.get(1);

    this.pesquisarDemais();
  }

  @Override
  public Logger getLogger() {
    return null;
  }

  public String obterAssuntoConcatenado(String assuntoCompleto) {
    if (assuntoCompleto.length() > 35) {
      String textoConcatenado = "...";
      textoConcatenado += assuntoCompleto.substring(assuntoCompleto.length() - 32, assuntoCompleto.length());
      return textoConcatenado;
    } else {
      return assuntoCompleto;
    }
  }

  public String obterTituloConcatenado(String tituloCompleto) {
    if (tituloCompleto.length() > 35) {
      String textoConcatenado = tituloCompleto.substring(0, 32) + "...";
      return textoConcatenado;
    } else {
      return tituloCompleto;
    }
  }

  /** Grid Demais demandas */
  public List<SituacaoEnum> getListaSituacoes() {
    List<SituacaoEnum> retorno = new ArrayList<SituacaoEnum>();
    retorno.add(SituacaoEnum.ABERTA);
    retorno.add(SituacaoEnum.FECHADA);
    retorno.add(SituacaoEnum.CANCELADA);
    return retorno;
  }

  /** Grid prioridade de demandas */
  public List<SituacaoEnum> getListaSituacoesFiltrada() {
    List<SituacaoEnum> retorno = new ArrayList<SituacaoEnum>();
    retorno.add(SituacaoEnum.RASCUNHO);
    retorno.add(SituacaoEnum.ABERTA);
    retorno.add(SituacaoEnum.FECHADA);
    retorno.add(SituacaoEnum.MINUTA);
    return retorno;
  }

  public List<EnvolvimentoEnum> getListaEnvolvimentos() {
    return Arrays.asList(EnvolvimentoEnum.values());
  }

  public List<String> getListaPaletaCores() {
    List<String> retorno = new ArrayList<String>();
    retorno.add(Constantes.COR_PADRAO_DEMANDA);
    retorno.add("#006400");
    retorno.add("#32cd32");
    retorno.add("#1e90ff");
    retorno.add("#ffd700");
    retorno.add("#ff8c00");
    retorno.add("#ff69ff");
    retorno.add("#ff0000");
    retorno.add("#60C");
    retorno.add("#6FF");
    return retorno;
  }

  /**
   * Obter o valor padrão.
   * @return the unidadeSelecionada
   */
  public Unidade getUnidadeSelecionada() {
    return this.unidadeSelecionada;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param unidadeSelecionada the unidadeSelecionada to set
   */
  public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
    this.unidadeSelecionada = unidadeSelecionada;
  }

  /**
   * Obter o valor padrão.
   * @return the demandasPrioridadesList
   */
  public List<DemandaDTO> getDemandasPrioridadesList() {
    return this.demandasPrioridadesList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demandasPrioridadesList the demandasPrioridadesList to set
   */
  public void setDemandasPrioridadesList(List<DemandaDTO> demandasPrioridadesList) {
    this.demandasPrioridadesList = demandasPrioridadesList;
  }

  /**
   * Obter o valor padrão.
   * @return the demaisDemandasList
   */
  public List<DemandaDTO> getDemaisDemandasList() {
    return this.demaisDemandasList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param demaisDemandasList the demaisDemandasList to set
   */
  public void setDemaisDemandasList(List<DemandaDTO> demaisDemandasList) {
    this.demaisDemandasList = demaisDemandasList;
  }

  /**
   * Obter o valor padrão.
   * @return the listaUnidades
   */
  public List<UnidadeDTO> getListaUnidades() {
    return this.listaUnidades;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param listaUnidades the listaUnidades to set
   */
  public void setListaUnidades(List<UnidadeDTO> listaUnidades) {
    this.listaUnidades = listaUnidades;
  }

  /**
   * Obter o valor padrão.
   * @return the caixasPostaisList
   */
  public List<CaixaPostal> getCaixasPostaisList() {
    return this.caixasPostaisList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixasPostaisList the caixasPostaisList to set
   */
  public void setCaixasPostaisList(List<CaixaPostal> caixasPostaisList) {
    this.caixasPostaisList = caixasPostaisList;
  }

  /**
   * Obter o valor padrão.
   * @return the caixaPostalSelecionada
   */
  public CaixaPostal getCaixaPostalSelecionada() {
    return this.caixaPostalSelecionada;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param caixaPostalSelecionada the caixaPostalSelecionada to set
   */
  public void setCaixaPostalSelecionada(CaixaPostal caixaPostalSelecionada) {
    this.caixaPostalSelecionada = caixaPostalSelecionada;
  }

  /**
   * Obter o valor padrão.
   * @return the dataHoraAtual
   */
  public Date getDataHoraAtual() {
    return this.dataHoraAtual;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param dataHoraAtual the dataHoraAtual to set
   */
  public void setDataHoraAtual(Date dataHoraAtual) {
    this.dataHoraAtual = dataHoraAtual;
  }

  /**
   * Obter o valor padrão.
   * @return the unidadeSelecionadaDTO
   */
  public UnidadeDTO getUnidadeSelecionadaDTO() {
    return this.unidadeSelecionadaDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param unidadeSelecionadaDTO the unidadeSelecionadaDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  /**
   * Obter o valor padrão.
   * @return the idUnidadeSelecionada
   */
  public Long getIdUnidadeSelecionada() {
    return this.idUnidadeSelecionada;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param idUnidadeSelecionada the idUnidadeSelecionada to set
   */
  public void setIdUnidadeSelecionada(Long idUnidadeSelecionada) {
    this.idUnidadeSelecionada = idUnidadeSelecionada;
  }

  /**
   * Obter o valor padrão.
   * @return the responsavelFiltro
   */
  public String getResponsavelFiltro() {
    return this.responsavelFiltro;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param responsavelFiltro the responsavelFiltro to set
   */
  public void setResponsavelFiltro(String responsavelFiltro) {
    this.responsavelFiltro = responsavelFiltro;
  }

  /**
   * Obter o valor padrão.
   * @return the situacaoPrioriFiltro
   */
  public SituacaoEnum getSituacaoPrioriFiltro() {
    return this.situacaoPrioriFiltro;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param situacaoPrioriFiltro the situacaoPrioriFiltro to set
   */
  public void setSituacaoPrioriFiltro(SituacaoEnum situacaoPrioriFiltro) {
    this.situacaoPrioriFiltro = situacaoPrioriFiltro;
  }

  /**
   * Obter o valor padrão.
   * @return the situacaoDemaisFiltro
   */
  public SituacaoEnum getSituacaoDemaisFiltro() {
    return this.situacaoDemaisFiltro;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param situacaoDemaisFiltro the situacaoDemaisFiltro to set
   */
  public void setSituacaoDemaisFiltro(SituacaoEnum situacaoDemaisFiltro) {
    this.situacaoDemaisFiltro = situacaoDemaisFiltro;
  }

  /**
   * Obter o valor padrão.
   * @return the corDemanda
   */
  public String getCorDemanda() {
    return this.corDemanda;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param corDemanda the corDemanda to set
   */
  public void setCorDemanda(String corDemanda) {
    this.corDemanda = corDemanda;
  }

  /**
   * Obter o valor padrão.
   * @return the envolvimentoFiltro
   */
  public EnvolvimentoEnum getEnvolvimentoFiltro() {
    return this.envolvimentoFiltro;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param envolvimentoFiltro the envolvimentoFiltro to set
   */
  public void setEnvolvimentoFiltro(EnvolvimentoEnum envolvimentoFiltro) {
    this.envolvimentoFiltro = envolvimentoFiltro;
  }

  /**
   * Obter o valor padrão.
   * @return the datasFeriadosList
   */
  public List<Date> getDatasFeriadosList() {
    return this.datasFeriadosList;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param datasFeriadosList the datasFeriadosList to set
   */
  public void setDatasFeriadosList(List<Date> datasFeriadosList) {
    this.datasFeriadosList = datasFeriadosList;
  }

  /**
   * Obter o valor padrão.
   * @return the setTimeRefresh
   */
  public Integer getSetTimeRefresh() {
    return this.setTimeRefresh;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param setTimeRefresh the setTimeRefresh to set
   */
  public void setSetTimeRefresh(Integer setTimeRefresh) {
    this.setTimeRefresh = setTimeRefresh;
  }

  /**
   * Obter o valor padrão.
   * @return the corDemandaFiltroPriori
   */
  public String getCorDemandaFiltroPriori() {
    return this.corDemandaFiltroPriori;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param corDemandaFiltroPriori the corDemandaFiltroPriori to set
   */
  public void setCorDemandaFiltroPriori(String corDemandaFiltroPriori) {
    this.corDemandaFiltroPriori = corDemandaFiltroPriori;
  }

  /**
   * Obter o valor padrão.
   * @return the corDemandaFiltroDemais
   */
  public String getCorDemandaFiltroDemais() {
    return this.corDemandaFiltroDemais;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param corDemandaFiltroDemais the corDemandaFiltroDemais to set
   */
  public void setCorDemandaFiltroDemais(String corDemandaFiltroDemais) {
    this.corDemandaFiltroDemais = corDemandaFiltroDemais;
  }

  public String redirectTratarDemanda(Long idDemanda) {
    JSFUtil.setSessionMapValue(TratarDemandaController.PARAM_ID_DEMANDA, idDemanda);
    return "/paginas/demanda/tratar/tratar.xhtml?faces-redirect=true";
  }

  /**
   * Obter o valor padrão.
   * @return the listaAssuntos
   */
  public List<Assunto> getListaAssuntos() {
    return this.listaAssuntos;
  }

  /**
   * Gravar o valor do parâmetro.
   * @param listaAssuntos the listaAssuntos to set
   */
  public void setListaAssuntos(List<Assunto> listaAssuntos) {
    this.listaAssuntos = listaAssuntos;
  }

}
