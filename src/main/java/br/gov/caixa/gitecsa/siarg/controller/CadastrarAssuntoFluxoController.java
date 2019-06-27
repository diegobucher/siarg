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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.repository.ConfigurationRepository;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.AssuntoCampo;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.AuditoriaService;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.CamposObrigatoriosService;
import br.gov.caixa.gitecsa.siarg.service.FluxoAssuntoService;
import br.gov.caixa.gitecsa.siarg.service.PerfilService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.service.UsuarioService;

/**
 * Classe Controller para tela de cadastro de usuario
 */
@Named
@ViewScoped
public class CadastrarAssuntoFluxoController extends BaseController implements Serializable {

  private static final String PAGINA_ASSUNTO_REDIRECT_TRUE = "/paginas/cadastro/assunto/assunto.xhtml?faces-redirect=true";

  private static final String MA002 = "MA002";

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
  private AssuntoService assuntoService;

  @EJB
  private CaixaPostalService caixaPostalService;

  @EJB
  private FluxoAssuntoService fluxoAssuntoService;
  
  @EJB
  private CamposObrigatoriosService camposObrigatoriosService;
  

  /** Abrangência selecionada */
  private Abrangencia abrangencia;

  private Unidade unidadeLogada;

  private Boolean flagMaster;

  private String matriculaLogado;

  private Assunto assunto;

  /** Lista das caixas que são exibidos para seleção. */
  private List<CaixaPostal> caixaPostalResponsavelList;

  private List<CaixaPostal> caixaPostalDestinoList;

  private List<CaixaPostal> caixaPostalObservadorList;

  /** Lista dos Observadores que foram selecionados na tela. */
  private List<CaixaPostal> caixaPostalObservadorSelecionadosList;

  /** Lista dos Observadores do Assunto. */
  private List<CaixaPostal> caixaPostalFluxoSelecionavelList;

  private List<Unidade> unidadeDemandanteDisponivelList;

  private List<Unidade> unidadeDemandanteSelecionadaList;

  /** Os campos Obrigatorios carregado */
  private CamposObrigatorios camposObrigatorios;
  
  /**Lista de campos obrigatorios */
  private List<CamposObrigatorios> camposObrigatoriosList;
  
  private Assunto categoria;

  private String arvoreAssuntoStr;

  /** Conteudo do HTML Editor */
  private String editorContent;

  /** Parâmetro Remote Command para o conteúdo do editor */
  private static final String CONTENT = "content";

  public static final String CATEGORIA_SELECIONADA = "categoriaSelecionada";
  public static final String ASSUNTO_SELECIONADO = "assuntoSelecionado";
  public static final String ABRANGENCIA_SELECIONADA = "abrangenciaSelecionada";
  
  public static final String CAMPOS_OBRIGATORIOS = "camposObrigatorios";
  
  private List<FluxoAssunto> fluxoAssuntoPreDefinido;

  private FluxoAssunto fluxoEdicao;

  private List<Integer> ordemFluxoList;
  
  private List<AssuntoCampo> listaAssuntosCampo;




private Integer ordemFluxo;

  @PostConstruct
  public void init() {
    carregarDados();
    
  }
  

  private void carregarDados() {

    categoria = (Assunto) JSFUtil.getSessionMapValue(CATEGORIA_SELECIONADA);
    assunto = (Assunto) JSFUtil.getSessionMapValue(ASSUNTO_SELECIONADO);
    abrangencia = (Abrangencia) JSFUtil.getSessionMapValue(ABRANGENCIA_SELECIONADA);
    camposObrigatorios = (CamposObrigatorios) JSFUtil.getSessionMapValue(CAMPOS_OBRIGATORIOS);

    UnidadeDTO unidadeDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.unidadeLogada = this.unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());
    UsuarioLdap usuario = (UsuarioLdap) RequestUtils.getSessionValue("usuario");
    matriculaLogado = usuario.getNuMatricula();
    
    
    if (assunto == null) {
      assunto = new Assunto();
      assunto.setAbrangencia(abrangencia);
      assunto.setAssuntoPai(categoria);
      assunto.setExcluido(false);
      assunto.setFlagAssunto(true);
    } else {
      try {

        assunto = assuntoService.findByIdEager(assunto.getId());
        this.editorContent = assunto.getTextoPreDefinido();

        this.caixaPostalObservadorSelecionadosList = caixaPostalService.findObservadoresByAssunto(assunto.getId());
        this.unidadeDemandanteSelecionadaList = unidadeService.obterUnidadesDemandateByAssunto(assunto);

        this.fluxoAssuntoPreDefinido =
            fluxoAssuntoService.obterFluxoAssuntoByAssuntoETipo(assunto, TipoFluxoEnum.DEMANDANTE_DEFINIDO);
        this.listaAssuntosCampo = assuntoService.obterAssuntoCampoPorAssunto(assunto.getId());
        

      } catch (DataBaseException e) {
        getLogger().info(e);
      }
    }

    if (fluxoAssuntoPreDefinido == null) {
      this.fluxoAssuntoPreDefinido = new ArrayList<>();
    }

    this.fluxoEdicao = new FluxoAssunto();

    this.arvoreAssuntoStr = assuntoService.obterArvoreAssuntos(categoria, "/");
    this.unidadeDemandanteDisponivelList =
        unidadeService.obterUnidadesECaixasPostaisPorTipo(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);
    this.caixaPostalResponsavelList =
        this.caixaPostalService.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);
    this.caixaPostalObservadorList =
        this.caixaPostalService.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL);

    carregarCaixasPostaisDestino();

    caixaPostalFluxoSelecionavelList = new ArrayList<>();
    caixaPostalFluxoSelecionavelList.addAll(caixaPostalResponsavelList);
    
    carregarListaOrdem();
    carregaLista();

    limparFluxoAssunto();

  }

  private void carregarCaixasPostaisDestino() {
    this.caixaPostalDestinoList =
        this.caixaPostalService.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.ARROBA_EXTERNA);

    this.caixaPostalDestinoList.addAll(
        this.caixaPostalService.findByAbrangenciaTipoUnidade(abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL));

    for (FluxoAssunto fluxo : fluxoAssuntoPreDefinido) {
      if (fluxo.getTipoFluxo().equals(TipoFluxoEnum.DEMANDANTE_DEFINIDO)) {
        caixaPostalDestinoList.remove(fluxo.getCaixaPostal());
      }
    }

    Collections.sort(caixaPostalDestinoList);

  }

  private void carregarListaOrdem() {
    ordemFluxoList = new ArrayList<>();

    int ordem = 0;
    for (FluxoAssunto fluxo : fluxoAssuntoPreDefinido) {
      if (fluxo.getTipoFluxo().equals(TipoFluxoEnum.DEMANDANTE_DEFINIDO)) {
        ordem++;
        ordemFluxoList.add(ordem);
      }
    }

    ordem++;
    ordemFluxoList.add(ordem);
    ordemFluxo = ordem;

  }

  public String getClasseCaixaSelecionada(FluxoAssunto fluxo) {
    if (fluxoEdicao != null && fluxoEdicao.getCaixaPostal() != null
        && fluxo.getCaixaPostal().getId().equals(fluxoEdicao.getCaixaPostal().getId())) {
      return "active";
    } else {
      return "";
    }
  }

  private boolean validarFluxoAssunto() {

    if (fluxoEdicao.getCaixaPostal() == null || fluxoEdicao.getPrazo() == null || fluxoEdicao.getPrazo() == 0) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MA002));
      RequestContext.getCurrentInstance().addCallbackParam("naoValido", "true");
      return false;
    }

    return true;
  }

  public void adicionarFluxoAssunto() {

    if (validarFluxoAssunto()) {
      fluxoEdicao.setAssunto(this.assunto);
      fluxoEdicao.setTipoFluxo(TipoFluxoEnum.DEMANDANTE_DEFINIDO);
      fluxoEdicao.setOrdem(ordemFluxo);

      reordenarOrdens();

      this.fluxoAssuntoPreDefinido.add(fluxoEdicao);
      Collections.sort(fluxoAssuntoPreDefinido);

      limparFluxoAssunto();
      carregarCaixasPostaisDestino();
    }
  }

  private void reordenarOrdens() {

    boolean alterarOrdem = false;
    for (FluxoAssunto fluxo : fluxoAssuntoPreDefinido) {

      if (fluxo.getOrdem().equals(ordemFluxo)) {
        alterarOrdem = true;
        fluxoEdicao.setOrdem(ordemFluxo);
      }

      if (alterarOrdem) {
        fluxo.setOrdem(fluxo.getOrdem() + 1);
      }

    }

  }

  public void alterarFluxoAssunto() {
    if (validarFluxoAssunto()) {

      Iterator<FluxoAssunto> it = fluxoAssuntoPreDefinido.iterator();

      FluxoAssunto fluxoAssuntoEditado = null;
      while (it.hasNext()) {
        FluxoAssunto fluxoAssunto = it.next();

        // Acha o item em edição atualiza e guarda
        if (fluxoEdicao.getReferenciaTransiente() == fluxoAssunto) {
          fluxoAssunto.setCaixaPostal(fluxoEdicao.getCaixaPostal());
          fluxoAssunto.setPrazo(fluxoEdicao.getPrazo());
          // Se mudou a ordem remove
          if (fluxoAssunto.getOrdem() != ordemFluxo) {
            it.remove();
            // Guarda e remove da lista
            fluxoAssuntoEditado = fluxoAssunto;
          }
          fluxoAssunto.setOrdem(ordemFluxo);

        }
      }

      if (fluxoAssuntoEditado != null) {
        fluxoAssuntoPreDefinido.add(ordemFluxo - 1, fluxoAssuntoEditado);
      }

      int ordem = 1;
      for (FluxoAssunto fluxoAssunto : fluxoAssuntoPreDefinido) {
        fluxoAssunto.setOrdem(ordem);
        ordem++;
      }

      carregarCaixasPostaisDestino();

      limparFluxoAssunto();
      carregarCaixasPostaisDestino();

    }
  }

  public void excluirFluxoAssunto() {
    Iterator<FluxoAssunto> it = fluxoAssuntoPreDefinido.iterator();

    while (it.hasNext()) {
      FluxoAssunto fluxoAssunto = it.next();
      if (fluxoEdicao.getReferenciaTransiente() == fluxoAssunto) {
        it.remove();
        break;
      }
    }

    int ordem = 1;
    for (FluxoAssunto fluxoAssunto : fluxoAssuntoPreDefinido) {
      fluxoAssunto.setOrdem(ordem);
      ordem++;
    }
    limparFluxoAssunto();

    carregarCaixasPostaisDestino();

  }

  public void editarFluxoAssunto(FluxoAssunto fluxo) {
    this.fluxoEdicao = SerializationUtils.clone(fluxo);
    this.fluxoEdicao.setReferenciaTransiente(fluxo);
    carregarCaixasPostaisDestino();
    caixaPostalDestinoList.add(fluxo.getCaixaPostal());
    Collections.sort(caixaPostalDestinoList);
    carregarListaOrdem();

    ordemFluxo = fluxoEdicao.getOrdem();

    Collections.reverse(ordemFluxoList);
    Iterator<Integer> it = ordemFluxoList.iterator();
    it.next();
    it.remove();
    Collections.reverse(ordemFluxoList);

  }

  public void limparFluxoAssunto() {
    this.fluxoEdicao = new FluxoAssunto();
    fluxoEdicao.setOrdem(0);
    if (this.fluxoAssuntoPreDefinido != null && !this.fluxoAssuntoPreDefinido.isEmpty()) {
      for (FluxoAssunto fluxo : fluxoAssuntoPreDefinido) {
        if (fluxoEdicao.getOrdem() < fluxo.getOrdem()) {
          fluxoEdicao.setOrdem(fluxo.getOrdem());
        }
      }
    }
    carregarListaOrdem();
  }

  /**
   * Seta o conteúdo do web based editor via remote command.
   */
  public void setEditorContentFromRemoteCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();

    this.editorContent = params.get(CONTENT);
    this.assunto.setTextoPreDefinido(editorContent);
  }

  
	public void carregaLista() {
		this.camposObrigatoriosList = camposObrigatoriosService.retornarCheckboxControleFluxo(true);
		if(this.camposObrigatoriosList != null && this.listaAssuntosCampo != null ) {
			for (AssuntoCampo assuntoCampo : this.listaAssuntosCampo) {
				for (CamposObrigatorios campoObrigatorio : this.camposObrigatoriosList) {
					if(campoObrigatorio.getId() == assuntoCampo.getCamposObrigatorios().getId()) {
						campoObrigatorio.setMarcado(true);
						campoObrigatorio.setQuantidade(assuntoCampo.getQuantidade());
					}
				}
			}
		}
		
		for (CamposObrigatorios campoObrigatorio : this.camposObrigatoriosList) {
			if(campoObrigatorio.getMarcado()&& campoObrigatorio.getQuantidade() ==0) {
				campoObrigatorio.setQuantidade(1);
			}
		}
	
	}
	
	/**Remove itens não checados*/
	public void soMarcados() {
		if (this.camposObrigatoriosList != null) {
			Iterator<CamposObrigatorios> iterator = this.camposObrigatoriosList.iterator();
			while (iterator.hasNext()) {
				if (!iterator.next().getMarcado()) {
					iterator.remove();
				}

			}
		}
		
		
	}
  
	public void limparCheck(CamposObrigatorios campoObr) {
		
		
		List<AssuntoCampo> listAssuntoCampo = assuntoService.obterAssuntoCampoPorAssuntoCampoObrigatorio(this.assunto.getId(), campoObr.getId());
		
		if (this.assunto != null && campoObr != null && !campoObr.getMarcado() && !listAssuntoCampo.isEmpty() ) {
			assuntoService.removerAssuntoCampo(listAssuntoCampo.get(0));
			campoObr = new CamposObrigatorios();
			init();
		}
		

	}
  
	
  public String salvar() {
	  soMarcados();

    if (validarSalvar()) {
    	
    	for (AssuntoCampo assuntoCampo : listaAssuntosCampo) {
    		if(assuntoCampo != null) {
    			assuntoService.removerAssuntoCampo(assuntoCampo);
    		}
    		
		}
    	
      boolean novo = assunto.getId() == null;
      assuntoService.salvarAssunto(assunto, matriculaLogado, unidadeLogada, unidadeDemandanteSelecionadaList,
          caixaPostalObservadorSelecionadosList, fluxoAssuntoPreDefinido, camposObrigatoriosList);
      JSFUtil.setSessionMapValue(ASSUNTO_SELECIONADO, assunto);
      JSFUtil.setSessionMapValue(CAMPOS_OBRIGATORIOS, camposObrigatorios);
      
      if (novo) {
        this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.itemIncluido", "Assunto"));
      } else {
        this.facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.itemAtualizado", "Assunto"));
      }
      carregarDados();
//      camposObrigatoriosList.clear();
      
    }
    return null;
  }

  public boolean validarSalvar() {
    boolean valido = true;
    if (StringUtils.isBlank(assunto.getNome()) || assunto.getCaixaPostal() == null || assunto.getPrazo() == null
        || assunto.getPrazo() <= 0) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MA002));
      valido = false;
    } else {

      boolean temDemandante = unidadeDemandanteSelecionadaList != null && !unidadeDemandanteSelecionadaList.isEmpty();
      boolean temFluxoPreDefinido = fluxoAssuntoPreDefinido != null && !fluxoAssuntoPreDefinido.isEmpty();
      
      boolean ativo = camposObrigatoriosList != null && !camposObrigatoriosList.isEmpty();

      if ((temDemandante && !temFluxoPreDefinido) || (!temDemandante && temFluxoPreDefinido)) {
        this.facesMessager
            .addMessageError(MensagemUtil.obterMensagem("assunto.configuracao.fluxo.demandanteFluxo.nao.associado"));
        valido = false;
      } else {
        if (fluxoAssuntoPreDefinido != null) {
          for (FluxoAssunto fluxoAssunto : fluxoAssuntoPreDefinido) {
            Unidade unidade = caixaPostalService.findByIdFetch(fluxoAssunto.getCaixaPostal().getId()).getUnidade();
            if (unidade.getTipoUnidade().equals(TipoUnidadeEnum.ARROBA_EXTERNA)
                && !fluxoAssunto.getOrdem().equals(fluxoAssuntoPreDefinido.size())) {
              this.facesMessager.addMessageError("Só é permitida a configuração da caixa postal @Externa no final do fluxo.");
              valido = false;
            }

          }
        }
      }
    }

    return valido;
  }

  public String cancelar() {
    return PAGINA_ASSUNTO_REDIRECT_TRUE;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public Properties getConfiguracoes() {
    return configuracoes;
  }

  public void setConfiguracoes(Properties configuracoes) {
    this.configuracoes = configuracoes;
  }

  public void setPerfilService(PerfilService perfilService) {
    this.perfilService = perfilService;
  }

  public AuditoriaService getAuditoria() {
    return auditoria;
  }

  public void setAuditoria(AuditoriaService auditoria) {
    this.auditoria = auditoria;
  }

  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  public Unidade getUnidadeLogada() {
    return unidadeLogada;
  }

  public void setUnidadeLogada(Unidade unidadeLogada) {
    this.unidadeLogada = unidadeLogada;
  }

  public Boolean getFlagMaster() {
    return flagMaster;
  }

  public void setFlagMaster(Boolean flagMaster) {
    this.flagMaster = flagMaster;
  }

  public String getMatriculaLogado() {
    return matriculaLogado;
  }

  public void setMatriculaLogado(String matriculaLogado) {
    this.matriculaLogado = matriculaLogado;
  }

  public Assunto getAssunto() {
    return assunto;
  }

  public void setAssunto(Assunto assunto) {
    this.assunto = assunto;
  }

  public List<CaixaPostal> getCaixaPostalObservadorSelecionadosList() {
    return caixaPostalObservadorSelecionadosList;
  }

  public void setCaixaPostalObservadorSelecionadosList(List<CaixaPostal> caixaPostalObservadorSelecionadosList) {
    this.caixaPostalObservadorSelecionadosList = caixaPostalObservadorSelecionadosList;
  }

  public Assunto getCategoria() {
    return categoria;
  }

  public void setCategoria(Assunto categoria) {
    this.categoria = categoria;
  }

  public String getArvoreAssuntoStr() {
    return arvoreAssuntoStr;
  }

  public void setArvoreAssuntoStr(String arvoreAssuntoStr) {
    this.arvoreAssuntoStr = arvoreAssuntoStr;
  }

  public String getEditorContent() {
    return editorContent;
  }

  public void setEditorContent(String editorContent) {
    this.editorContent = editorContent;
  }

  public List<Unidade> getUnidadeDemandanteDisponivelList() {
    return unidadeDemandanteDisponivelList;
  }

  public void setUnidadeDemandanteDisponivelList(List<Unidade> unidadeDemandanteDisponivelList) {
    this.unidadeDemandanteDisponivelList = unidadeDemandanteDisponivelList;
  }

  public List<Unidade> getUnidadeDemandanteSelecionadaList() {
    return unidadeDemandanteSelecionadaList;
  }

  public void setUnidadeDemandanteSelecionadaList(List<Unidade> unidadeDemandanteSelecionadaList) {
    this.unidadeDemandanteSelecionadaList = unidadeDemandanteSelecionadaList;
  }

  public List<CaixaPostal> getCaixaPostalFluxoSelecionavelList() {
    return caixaPostalFluxoSelecionavelList;
  }

  public void setCaixaPostalFluxoSelecionavelList(List<CaixaPostal> caixaPostalFluxoSelecionavelList) {
    this.caixaPostalFluxoSelecionavelList = caixaPostalFluxoSelecionavelList;
  }

  public List<FluxoAssunto> getFluxoAssuntoPreDefinido() {
    return fluxoAssuntoPreDefinido;
  }

  public void setFluxoAssuntoPreDefinido(List<FluxoAssunto> fluxoAssuntoPreDefinido) {
    this.fluxoAssuntoPreDefinido = fluxoAssuntoPreDefinido;
  }

  public List<CaixaPostal> getCaixaPostalResponsavelList() {
    return caixaPostalResponsavelList;
  }

  public void setCaixaPostalResponsavelList(List<CaixaPostal> caixaPostalResponsavelList) {
    this.caixaPostalResponsavelList = caixaPostalResponsavelList;
  }

  public List<CaixaPostal> getCaixaPostalDestinoList() {
    return caixaPostalDestinoList;
  }

  public void setCaixaPostalDestinoList(List<CaixaPostal> caixaPostalDestinoList) {
    this.caixaPostalDestinoList = caixaPostalDestinoList;
  }

  public FluxoAssunto getFluxoEdicao() {
    return fluxoEdicao;
  }

  public void setFluxoEdicao(FluxoAssunto fluxoEdicao) {
    this.fluxoEdicao = fluxoEdicao;
  }

  public List<CaixaPostal> getCaixaPostalObservadorList() {
    return caixaPostalObservadorList;
  }

  public void setCaixaPostalObservadorList(List<CaixaPostal> caixaPostalObservadorList) {
    this.caixaPostalObservadorList = caixaPostalObservadorList;
  }

  public Integer getOrdemFluxo() {
    return ordemFluxo;
  }

  public void setOrdemFluxo(Integer ordemFluxo) {
    this.ordemFluxo = ordemFluxo;
  }

  public List<Integer> getOrdemFluxoList() {
    return ordemFluxoList;
  }

  public void setOrdemFluxoList(List<Integer> ordemFluxoList) {
    this.ordemFluxoList = ordemFluxoList;
  }



public CamposObrigatorios getCamposObrigatorios() {
	return camposObrigatorios;
}

public void setCamposObrigatorios(CamposObrigatorios camposObrigatorios) {
	this.camposObrigatorios = camposObrigatorios;
}

public List<CamposObrigatorios> getCamposObrigatoriosList() {
	return camposObrigatoriosList;
}

public void setCamposObrigatoriosList(List<CamposObrigatorios> camposObrigatoriosList) {
	this.camposObrigatoriosList = camposObrigatoriosList;
}


public List<AssuntoCampo> getListaAssuntosCampo() {
	return listaAssuntosCampo;
}


public void setListaAssuntosCampo(List<AssuntoCampo> listaAssuntosCampo) {
	this.listaAssuntosCampo = listaAssuntosCampo;
}



}
