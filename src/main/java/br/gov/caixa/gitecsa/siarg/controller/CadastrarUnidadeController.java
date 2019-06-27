package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
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
import br.gov.caixa.gitecsa.siarg.dto.UnidadeCadastroDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.service.UsuarioService;
import br.gov.caixa.gitecsa.siico.service.UnidadeSiicoService;
import br.gov.caixa.gitecsa.siico.vo.UnidadeSiicoVO;

/**
 * Classe Controller para tela de cadastro de unidade
 */
@Named
@ViewScoped
public class CadastrarUnidadeController extends BaseController implements Serializable {

  private static final long serialVersionUID = 1L;

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
  private UnidadeSiicoService unidadeSiicoService;

  /** Abrangência selecionada */
  private Abrangencia abrangencia;

  /** Lista do combo de abrangência */
  private List<Abrangencia> abrangenciaList;

  /** Grid de unidades */
  private List<UnidadeCadastroDTO> unidadeList;
  
  /** Campos do Modal*/
  private Unidade unidade;
  
  private CaixaPostal caixaPostal;
  
  private List<CaixaPostal> caixaPostalList;
  
  private List<Unidade> unidadeSubordinacaoList;
  
  private Boolean isEdicaoUnidade;
  
  private Boolean isUnidadeMatrizOuFilial;

  @PostConstruct
  public void init() {
    
    abrangenciaList = abrangenciaService.findAll();
    unidadeList = new ArrayList<UnidadeCadastroDTO>();
    unidadeSubordinacaoList = new ArrayList<Unidade>();
    caixaPostalList = new ArrayList<CaixaPostal>();
    isEdicaoUnidade = false;
    isUnidadeMatrizOuFilial = false;
    this.carregarUnidadesPorAbrangencia();
  }
  
  public void carregarUnidadesPorAbrangencia() {
    unidadeList = new ArrayList<UnidadeCadastroDTO>();
    this.abrangencia = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    if(this.abrangencia != null) {
      unidadeList = unidadeService.obterUnidadesDTOPor(this.abrangencia, TipoUnidadeEnum.MATRIZ, TipoUnidadeEnum.FILIAL, TipoUnidadeEnum.EXTERNA);
      unidadeSubordinacaoList = unidadeService.obterListaUnidadesSUEG(this.abrangencia.getId());
    }
  }
  
  public void selecionarTipoUnidade() {
    if(TipoUnidadeEnum.EXTERNA.equals(this.unidade.getTipoUnidade())) {
      this.unidade.setUnidadeSubordinacao(null);
      this.unidade.setIsRelConsolidadoAssunto(false);
    }
  }
  
  public void handlerModalNovaUnidade() {
    isEdicaoUnidade = false;
    isUnidadeMatrizOuFilial = false;
    unidade = new Unidade();
    unidade.setAtivo(true);
    unidade.setAbrangencia(this.abrangencia);
    caixaPostal = new CaixaPostal();
    caixaPostalList = new ArrayList<CaixaPostal>();
    isUnidadeMatrizOuFilial = false;
  }

  public void handlerModalEditarUnidade(UnidadeCadastroDTO unidadeDTO) {
    unidade = unidadeService.obterUnidadePorChaveFetch(unidadeDTO.getId());
    if(unidade.getTipoUnidade().equals(TipoUnidadeEnum.EXTERNA)) {
      isEdicaoUnidade = true;
      isUnidadeMatrizOuFilial = false;
    }else {
      isEdicaoUnidade = false;
      isUnidadeMatrizOuFilial = true;
    }
    caixaPostal = new CaixaPostal();
    
    caixaPostalList = unidade.getCaixasPostaisAtivasList();
  }
  
  public void pesquisarPorCGC() {
    
    if(this.unidade.getCgcUnidade() != null) {
      //PESQUISAR NA MESMA ABRANGENCIA
      Unidade unidadeDB = unidadeService.obterUnidadePorAbrangenciaCGC(this.abrangencia, this.unidade.getCgcUnidade());
      
      if(unidadeDB != null) {
        if(!unidadeDB.getId().equals(this.unidade.getId())) {
          facesMessager.addMessageError(MensagemUtil.obterMensagem("MA069"));
          this.unidade.setCgcUnidade(null);
        }
      } else {
        //PESQUISAR NA VIEW
        UnidadeSiicoVO unidadeSiicoVO = unidadeSiicoService.obterUnidadePorCGC(this.unidade.getCgcUnidade());
        
        if(unidadeSiicoVO != null) {
          
          this.unidade.setNome("");
          this.unidade.setSigla("");
          
          if(StringUtils.isNotBlank(unidadeSiicoVO.getNome())) {
            this.unidade.setNome(unidadeSiicoVO.getNome().trim());
          }
          
          if(StringUtils.isNotBlank(unidadeSiicoVO.getSigla())) {
            this.unidade.setSigla(unidadeSiicoVO.getSigla().trim());
          }
          
        } else {
          facesMessager.addMessageError(MensagemUtil.obterMensagem("MA072"));
        }
      }
    }
  }

  public void pesquisarPorSigla() {
    
    //PESQUISAR NA MESMA ABRANGENCIA
    if(StringUtils.isNotBlank(this.unidade.getSigla())) {
      Unidade unidadeDB = unidadeService.obterUnidadePorAbrangenciaSigla(this.abrangencia, this.unidade.getSigla());
      
      if(unidadeDB != null) {
        if(!unidadeDB.getId().equals(this.unidade.getId())) {
          facesMessager.addMessageError(MensagemUtil.obterMensagem("MA069"));
          this.unidade.setSigla("");
        }
      }
    }
  }
  

  public void pesquisarPorSiglaCaixaPostal() {
    
    //PESQUISAR NA MESMA ABRANGENCIA
    if(StringUtils.isNotBlank(this.caixaPostal.getSigla()) && caixaPostalList != null && !caixaPostalList.isEmpty()) {

      CaixaPostal caixaPostal = null;
      for (CaixaPostal caixa : caixaPostalList) {
        
        if(this.caixaPostal.getSigla().toUpperCase().trim().equals(caixa.getSigla().toUpperCase().trim())) {
          caixaPostal = caixa;
        }
      }
      
      if(caixaPostal != null) {
        facesMessager.addMessageError(MensagemUtil.obterMensagem("MA071"));
        this.caixaPostal.setSigla("");
      }
    }
  }
  
  
  public List<SelectItem> getTipoUnidadeList() {
    List<SelectItem> tiposUnidadeList = new ArrayList<SelectItem>();

    tiposUnidadeList.add(new SelectItem(TipoUnidadeEnum.MATRIZ, "Matriz"));
    tiposUnidadeList.add(new SelectItem(TipoUnidadeEnum.FILIAL, "Rede"));
    tiposUnidadeList.add(new SelectItem(TipoUnidadeEnum.EXTERNA, "Unidade Externa"));

    return tiposUnidadeList;
  }
  
  public List<SelectItem> getTipoUnidadeListFiltrada() {
    List<SelectItem> tiposUnidadeList = new ArrayList<SelectItem>();

    tiposUnidadeList.add(new SelectItem(TipoUnidadeEnum.MATRIZ, "Matriz"));
    tiposUnidadeList.add(new SelectItem(TipoUnidadeEnum.FILIAL, "Rede"));

    return tiposUnidadeList;
  }
  
  public Boolean ehUnidadeExterna() {
    if(this.unidade != null && TipoUnidadeEnum.EXTERNA.equals(this.unidade.getTipoUnidade())) {
      return true;
    } else {
      return false;
    }
  }
  
  
  public void salvarCaixaPostal() {
    if(validarCaixaPostal()){
      CaixaPostal caixa = new CaixaPostal();
      caixa.setUnidade(this.unidade);
      caixa.setSigla(this.caixaPostal.getSigla());
      caixa.setEmail(this.caixaPostal.getEmail());
      caixa.setRecebeEmail(this.caixaPostal.getRecebeEmail());
      caixa.setAtivo(true);
      caixa.setUnidade(this.unidade);
      
      boolean caixaAdicionada = false;

      for (CaixaPostal cx : caixaPostalList) {
        if(cx == this.caixaPostal.getReferenciaTransiente()) {
          caixaAdicionada = true;
          
          cx.setSigla(this.caixaPostal.getSigla());
          cx.setEmail(this.caixaPostal.getEmail());
          cx.setRecebeEmail(this.caixaPostal.getRecebeEmail());
        }
      }
      
      if(!caixaAdicionada) {
        this.caixaPostalList.add(caixa);
      }
      
      limparCaixaPostal();
    }
  }
  
  private void limparCaixaPostal() {
    this.caixaPostal = new CaixaPostal();
  }
  

  private boolean validarUnidade() {
    
    boolean valido = true;
    
    if(!ehUnidadeExterna() && this.unidade.getCgcUnidade() == null) {
      valido = false;
    }
    
    if(StringUtils.isBlank(this.unidade.getSigla())) {
      valido = false;
    }

    if(StringUtils.isBlank(this.unidade.getNome())) {
      valido = false;
    }

    if(this.unidade.getTipoUnidade() == null) {
      valido = false;
    }
    
    if(this.caixaPostalList == null || this.caixaPostalList.isEmpty()) {
      valido = false;
    }
    

    if(!valido) {
     facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"));
     RequestContext.getCurrentInstance().update("message");
    }
    
    if(valido) {
      
      //PESQUISAR NA MESMA ABRANGENCIA
      if(StringUtils.isNotBlank(this.unidade.getSigla())) {
        Unidade unidadeDB = unidadeService.obterUnidadePorAbrangenciaSigla(this.abrangencia, this.unidade.getSigla());
        
        if(unidadeDB != null) {
          if(!unidadeDB.getId().equals(this.unidade.getId())) {
            facesMessager.addMessageError(MensagemUtil.obterMensagem("MA069"));
            RequestContext.getCurrentInstance().update("message");
            valido = false;
          }
        }
      }
    }
    
    
    return valido;
  }
  
  private boolean validarCaixaPostal() {
    
    boolean valido = true;
    if(StringUtils.isBlank(this.caixaPostal.getSigla())) {
      valido = false;
    }

    if(StringUtils.isBlank(this.caixaPostal.getEmail())) {
      valido = false;
    }
    

    if(!valido) {
     facesMessager.addMessageError(MensagemUtil.obterMensagem("geral.message.validation.dados.obrigatorios"));
     RequestContext.getCurrentInstance().update("message");
    }
    
    if(valido) {
      
      //PESQUISAR NA MESMA ABRANGENCIA
      if(StringUtils.isNotBlank(this.caixaPostal.getSigla()) && caixaPostalList != null && !caixaPostalList.isEmpty()) {

        CaixaPostal caixaPostal = null;
        for (CaixaPostal caixa : caixaPostalList) {
          
          if(this.caixaPostal.getSigla().toUpperCase().trim().equals(caixa.getSigla().toUpperCase().trim())
              && this.caixaPostal.getReferenciaTransiente() != caixa) {
            caixaPostal = caixa;
          }
        }
        
        if(caixaPostal != null) {
          facesMessager.addMessageError(MensagemUtil.obterMensagem("MA071"));
          RequestContext.getCurrentInstance().update("message");
          valido = false;
        }
      }
    }
    
    
    return valido;
  
  }

  public void editarCaixaPostal(CaixaPostal caixaPostalSelecionada) {
    this.caixaPostal = new CaixaPostal();
    try {
      BeanUtils.copyProperties(this.caixaPostal, caixaPostalSelecionada);
      this.caixaPostal.setReferenciaTransiente(caixaPostalSelecionada);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.info(e);
    }
  }

  public void excluirCaixaPostal(CaixaPostal caixaPostalSelecionada) {
    
    this.caixaPostal = new CaixaPostal();
    
    for (Iterator<CaixaPostal> iterator = caixaPostalList.iterator(); iterator.hasNext();) {
      CaixaPostal cx = (CaixaPostal) iterator.next();
      
      if(cx == caixaPostalSelecionada) {
        iterator.remove();
      }
    }
  }

  public void excluirUnidade(UnidadeCadastroDTO unidadeDTO) throws RequiredException, BusinessException, DataBaseException {

    unidadeService.excluirLogicamente(unidadeDTO.getId());
    
    facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.excluido", "Unidade"));
    RequestContext.getCurrentInstance().update("formListagem");
    carregarUnidadesPorAbrangencia();
  }
  
  public void salvarUnidade() {
    if(validarUnidade()) {
      
      try {
        if(this.unidade.getId() == null) {
          //ASSOCIAR essa lista na Unidade
          this.unidade.setCaixasPostaisList(this.caixaPostalList);
          unidadeService.save(this.unidade);
          facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.salva", "Unidade"));

        } else {

          for (CaixaPostal cxExistente : unidade.getCaixasPostaisList()) {
            boolean continuaExistindo = false;
            
            if(cxExistente.isAtivo()) {
              for (CaixaPostal cx : caixaPostalList) {
                if(cxExistente.getId().equals(cx.getId())) {
                  continuaExistindo = true;
                }
              }
            }
            //Inativa as excluidas
            if(!continuaExistindo) {
              cxExistente.setAtivo(false);
            }
          }
          
        
          for (CaixaPostal cx : caixaPostalList) {
            //Adicionar as novas
            if(cx.getId() == null) {
              this.unidade.getCaixasPostaisList().add(cx);
            }
          }
          
          unidadeService.update(this.unidade);
          facesMessager.addMessageInfo(MensagemUtil.obterMensagem("geral.crud.atualizada", "Unidade"));

        }
        
        RequestContext.getCurrentInstance().execute("$('#modal_cadastro_unidade').modal('hide')");
        carregarUnidadesPorAbrangencia();
        RequestContext.getCurrentInstance().update("formListagem");
        RequestContext.getCurrentInstance().execute("carregarDataTableUnidades()");
        
      } catch (Exception e) {
        LOGGER.error(e);
      }
        
      
    }
  }


  @Override
  public Logger getLogger() {
    return LOGGER;
  }


  public List<Abrangencia> getAbrangenciaList() {
    return abrangenciaList;
  }


  public void setAbrangenciaList(List<Abrangencia> abrangenciaList) {
    this.abrangenciaList = abrangenciaList;
  }


  public Abrangencia getAbrangencia() {
    return abrangencia;
  }


  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

  public Unidade getUnidade() {
    return unidade;
  }

  public void setUnidade(Unidade unidade) {
    this.unidade = unidade;
  }


  public CaixaPostal getCaixaPostal() {
    return caixaPostal;
  }

  public void setCaixaPostal(CaixaPostal caixaPostal) {
    this.caixaPostal = caixaPostal;
  }

  public List<CaixaPostal> getCaixaPostalList() {
    return caixaPostalList;
  }

  public void setCaixaPostalList(List<CaixaPostal> caixaPostalList) {
    this.caixaPostalList = caixaPostalList;
  }

  public List<UnidadeCadastroDTO> getUnidadeList() {
    return unidadeList;
  }

  public void setUnidadeList(List<UnidadeCadastroDTO> unidadeList) {
    this.unidadeList = unidadeList;
  }

  public List<Unidade> getUnidadeSubordinacaoList() {
    return unidadeSubordinacaoList;
  }

  public void setUnidadeSubordinacaoList(List<Unidade> unidadeSubordinacaoList) {
    this.unidadeSubordinacaoList = unidadeSubordinacaoList;
  }

  /**
   * @return the isEdicaoUnidade
   */
  public Boolean getIsEdicaoUnidade() {
    return isEdicaoUnidade;
  }

  /**
   * @param isEdicaoUnidade the isEdicaoUnidade to set
   */
  public void setIsEdicaoUnidade(Boolean isEdicaoUnidade) {
    this.isEdicaoUnidade = isEdicaoUnidade;
  }

  /**
   * @return the isUnidadeMatrizOuFilial
   */
  public Boolean getIsUnidadeMatrizOuFilial() {
    return isUnidadeMatrizOuFilial;
  }

  /**
   * @param isUnidadeMatrizOuFilial the isUnidadeMatrizOuFilial to set
   */
  public void setIsUnidadeMatrizOuFilial(Boolean isUnidadeMatrizOuFilial) {
    this.isUnidadeMatrizOuFilial = isUnidadeMatrizOuFilial;
  }


}
