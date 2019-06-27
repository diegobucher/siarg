package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
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
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.dto.CamposObrigatoriosDTO;
import br.gov.caixa.gitecsa.siarg.service.CamposObrigatoriosService;

/**
 * Classe Controller para tela de cadastro de campos obrigatórios
 * 
 * @author f763644
 */
@Named
@ViewScoped
public class CadastrarCamposObrigatoriosController extends BaseController implements Serializable {

	private static final String PAG_PRINCIPAL = "/paginas/cadastro/camposobrigatorios/campos_obrigatorios.xhtml?faces-redirect=true";

	private static final long serialVersionUID = 1L;

	/*
	 * Injections necessários
	 */
	@Inject
	@ConfigurationRepository("configuracoes.properties")
	private Properties configuracoes;

	@EJB
	private CamposObrigatoriosService camposObrigatoriosService;
	
	/** Campos do Modal*/
	private CamposObrigatorios camposObrigatorios = new CamposObrigatorios();

	private List<CamposObrigatorios> camposObrigatoriosList;
	
	/** Grid de campos */
	private List<CamposObrigatoriosDTO> campoObrigatorioList;
	
	private Long id;

	private String nome;
	
	private String descricao;

	private TipoCampoEnum tipo;

	private Integer tamanho;

	private String mascara;
	
	private boolean isMascaraHabilitada;
	
	public static final String CAMPOS_OBRIGATORIOS = "camposobrigatorios";

	@PostConstruct
	public void init() {
		this.isMascaraHabilitada=false;
		carregaLista();
	}


	private void carregaLista() {
		limpar();
		List<CamposObrigatorios> lista = camposObrigatoriosService.findAll();

		/**
		 * Só carrega os itens ativos devido a exclusão logica
		 * */
		for (CamposObrigatorios camposObrigatorios : lista) {
			if(camposObrigatorios.getAtivo()) {
				this.camposObrigatoriosList.add(camposObrigatorios);
			}
		}
	}
	

	private void limpar() {
		id = null;
		nome = null;
		tipo = null;
		tamanho = null;
		mascara = null;
		this.isMascaraHabilitada=false;
		this.camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		this.camposObrigatorios = new CamposObrigatorios();
	}

	public void carregarCamposObrigatorios() {
		
		
		if (nome != null) {
			camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorNome(nome);
		} else {
			camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		}
		
		if(descricao != null) {
			camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorDescricao(descricao);
		}else {
			camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		}

		if (tipo != null) {
			camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorTipo(tipo);
		} else {
			camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		}

		if (tamanho != null) {
			camposObrigatoriosList = camposObrigatoriosService.obterCampoObrigatorioPorTamanho(tamanho);
		} else {
			camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		}

		if (mascara != null) {
			camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorMascara(mascara);
		} else {
			camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
		}

	}

	public void detalharCamposObrigatorios(CamposObrigatorios camposObrigatorios) throws DataBaseException {
		
		this.camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorNome(camposObrigatorios.getNome());
		
		this.camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorDescricao(camposObrigatorios.getDescricao());
		 
        this.camposObrigatorios.getTipo();
         
		this.camposObrigatoriosList = camposObrigatoriosService.obterCampoObrigatorioPorTamanho(tamanho);
		
		this.camposObrigatoriosList = camposObrigatoriosService.obterCamposObrigatoriosPorMascara(getMascara());
	}

	public String abrirConfigurarCamposObrigatorios() {
		
	    JSFUtil.setSessionMapValue("nome", this.nome);
	    JSFUtil.setSessionMapValue(descricao, this.descricao);
	    JSFUtil.setSessionMapValue("tipo", this.tipo);
	    JSFUtil.setSessionMapValue("tamanho", this.tamanho);
	    JSFUtil.setSessionMapValue("mascara", this.mascara);	    
		JSFUtil.setSessionMapValue(CadastrarCamposObrigatoriosController.CAMPOS_OBRIGATORIOS, null);

		String viewId = PAG_PRINCIPAL;
		return viewId;
	}

	public List<CamposObrigatorios> getCamposObrigatoriosList() {
		return camposObrigatoriosList;
	}

	public void setCamposObrigatoriosList(List<CamposObrigatorios> camposObrigatoriosList) {
		this.camposObrigatoriosList = camposObrigatoriosList;
	}

	public void excluirCamposObrigatorios(CamposObrigatorios camposObrigatorios) throws RequiredException, BusinessException, DataBaseException {

		camposObrigatoriosService.excluirLogicamente(camposObrigatorios.getId());

		facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", "Campos Obrigatorios"));
		RequestContext.getCurrentInstance().update("formListagem");
		this.init();
		
	}
	
	  public void salvarCamposObrigatorios() {
	   
	   
	  if(validarCamposObrigatorios()) {
		  boolean isModal = true;
		  
	      try {
		        if(this.camposObrigatorios.getId() == null) {
		        	
		        	if (!jaCadastrado(this.camposObrigatorios)){
		        		
		        		this.camposObrigatorios.setAtivo(true);
				        facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.salvo", "Campos Obrigatorios"));
				        camposObrigatoriosService.save(this.camposObrigatorios);
				        limpar();
				
		        	}else {
		        		
		   		     	facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.crud.duplicidade"));
		   		     	RequestContext.getCurrentInstance().execute("$('#modal_cadastro_campo_obrig').modal('show')");
		   		     	RequestContext.getCurrentInstance().update("message");
		   		     	isModal = false;
		        	}
		          

		        } else {
		          camposObrigatoriosService.update(this.camposObrigatorios);
		          facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.atualizado", "Campos Obrigatorios"));		      
		          this.init();
		          
		          
		        }
		        	if(isModal) {
		        		RequestContext.getCurrentInstance().execute("$('#modal_cadastro_campo_obrig').modal('hide')");
		        		RequestContext.getCurrentInstance().update("formListagem");
		        		RequestContext.getCurrentInstance().execute("carregarDataTableCamposObrigatorios()");
		        	}
			        this.init();
			        
			        
			      } catch (Exception e) {
			        LOGGER.error(e);
			      }
	          
	         }
	  		
		  }
	  
	  private boolean validarCamposObrigatorios() {
		    
		    boolean valido = true;
		    
		    if(this.camposObrigatorios.getNome() == null) {
		      valido = false;
		    }
		    
		    if(this.camposObrigatorios.getDescricao() == null) {
		    	valido = false;
		    }

		    if(this.camposObrigatorios.getTipo() == null) {
		    	valido = false;
		    }
		    
		    if((this.camposObrigatorios.getTamanho() == null) && (this.camposObrigatorios.getMascara() == null)) {
		    	valido = false;
		    }
		    
		    if(!valido) {
		     facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"));
		     RequestContext.getCurrentInstance().execute("$('#modal_cadastro_campo_obrig').modal('show')");
		     RequestContext.getCurrentInstance().update("message");
		    }
		    return valido;
		  }
	  
	 public void pesquisarPorCamposObrigatorios() {
		if(this.camposObrigatorios.getNome() != null && this.camposObrigatorios.getDescricao() != null && this.camposObrigatorios.getTipo() != null && this.camposObrigatorios.getTamanho() != null 
				&& this.camposObrigatorios.getMascara() != null) {
			//PESQUISA POR NOME 
			camposObrigatoriosService.obterCamposObrigatoriosPorNome(nome);
			//PESQUISA POR DESCRIÇÃO
			camposObrigatoriosService.obterCamposObrigatoriosPorDescricao(descricao);
			//PESQUISA POR TIPO
			camposObrigatoriosService.obterCamposObrigatoriosPorTipo(tipo);
			//PESQUISAR POR TAMANHO
			camposObrigatoriosService.obterCampoObrigatorioPorTamanho(tamanho);
			//PESQUISA POR MASCARA
			camposObrigatoriosService.obterCamposObrigatoriosPorMascara(mascara);
         }
	 }
	  
   public void handlerModalNovosCamposObrigatorios() {
	   
	   carregarCamposObrigatorios();
	   camposObrigatoriosList = new ArrayList<CamposObrigatorios>();
	   CamposObrigatorios campos = new CamposObrigatorios();
	   campos.setAtivo(true);
	   campos.setNome(nome);
	   campos.setDescricao(descricao);
	   campos.setTamanho(tamanho);
	   campos.setTipo(tipo);
   }
   
   /**
    * Executa regras de validação dos campos mascara e tamanho. Só pode existir um.
    * */
   public void validaRegraMascara(CamposObrigatorios campo) {
	   
	   if(campo.getMascara() != null && !campo.getMascara().isEmpty()) {
		   campo.setTamanho(null);
		   this.isMascaraHabilitada = Boolean.TRUE;
	   }else {
		   this.isMascaraHabilitada = Boolean.FALSE;
		   campo.setMascara(null);
	   }
	   
   }
   
   public void handlerModalEditarCamposObrigatorios(CamposObrigatorios camposObrigatorios) {
	   this.camposObrigatorios = camposObrigatoriosService.obterCamposObrigatoriosPorId(camposObrigatorios.getId());
	  /*
	   camposObrigatorios.setNome(nome);
	   camposObrigatorios.setTamanho(tamanho);
	   camposObrigatorios.setDescricao(descricao);
	   camposObrigatorios.setTipo(tipo);
	   */
	   validaRegraMascara(this.camposObrigatorios);
	  
	   
   }
   
   public void editarCamposObrigatorios(CamposObrigatorios campObrigatorios) {
	    this.camposObrigatorios = new CamposObrigatorios();
	    try {
	      BeanUtils.copyProperties(this.camposObrigatorios, campObrigatorios);
	      this.camposObrigatorios.setId(id);
	    } catch (IllegalAccessException | InvocationTargetException e) {
	      LOGGER.info(e);
	    }
	  }
   
   public boolean jaCadastrado(CamposObrigatorios campObrigatorios) {
	   boolean retorno = false;
	   List<CamposObrigatorios> lista = camposObrigatoriosService.findAll();
	
	   for (CamposObrigatorios camposObrigatorios : lista) {
		   if(camposObrigatorios.getNome().equalsIgnoreCase(campObrigatorios.getNome())) {
			   retorno = true;
		   }
	   }
	   return retorno;
   }
   
   public TipoCampoEnum[] getTiposCampo() {
	   return TipoCampoEnum.values();
   }
	  
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public TipoCampoEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoCampoEnum tipo) {
		this.tipo = tipo;
	}

	public Integer getTamanho() {
		return tamanho;
	}

	public CamposObrigatorios getCamposObrigatorios() {
		return camposObrigatorios;
	}


	public void setCamposObrigatorios(CamposObrigatorios camposObrigatorios) {
		this.camposObrigatorios = camposObrigatorios;
	}


	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	public String getMascara() {
		return mascara;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
	}

	
	public List<CamposObrigatoriosDTO> getCampoObrigatorioList() {
		return campoObrigatorioList;
	}

	public void setCampoObrigatorioList(List<CamposObrigatoriosDTO> campoObrigatorioList) {
		this.campoObrigatorioList = campoObrigatorioList;
	}


	@Override
	public Logger getLogger() {
		return LOGGER;
	}


	public boolean getMascaraHabilitada() {
		return isMascaraHabilitada;
	}


	public void setMascaraHabilitada(boolean isMascaraHabilitada) {
		this.isMascaraHabilitada = isMascaraHabilitada;
		if(!this.isMascaraHabilitada) {
			this.camposObrigatorios.setMascara(null);
		}
		if(this.isMascaraHabilitada) {
			this.camposObrigatorios.setTamanho(null);
		}
	}
	

}
