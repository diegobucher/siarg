package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.OcorrenciaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

@Named
@ViewScoped
public class OcorrenciaController  extends BaseController implements Serializable {

  /** Serail. */
  private static final long serialVersionUID = -4183537407038606380L;
  
  @Inject
  private OcorrenciaService ocorrenciaService;

  @Inject
  private UnidadeService unidadeService;

  private boolean pesquisaAtiva;

  private boolean modoEdicao;
  
  private Abrangencia abrangenciaSelecionada;
  
  private UnidadeDTO unidadeSelecionadaDTO;

  private List<UnidadeDTO> listaUnidadesDTO;

  private List<UnidadeDTO> listaUnidadesDTOCombo;

  private List<Ocorrencia> listaOcorrencias;
  
  private String tamanhoDTOList;
  
  private Ocorrencia ocorrenciaSelecionada;
  
  private Boolean unidadeMatriz;

  private Boolean pesquisaDireta;
  
  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    limpar();
    this.listaUnidadesDTO = new ArrayList<UnidadeDTO>();
    this.listaUnidadesDTOCombo = new ArrayList<UnidadeDTO>();
    this.listaUnidadesDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");

    alterarAbrangencia();
    if (listaUnidadesDTO.size() == 1) {
      this.unidadeSelecionadaDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
      this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();
      this.pesquisaDireta = Boolean.TRUE;
    }
    pesquisar();
  }
  
  public void limpar() {
    this.pesquisaDireta = Boolean.FALSE;
    this.unidadeMatriz = Boolean.FALSE;
    this.pesquisaAtiva = Boolean.TRUE;
    this.modoEdicao = Boolean.FALSE;
    this.ocorrenciaSelecionada = new Ocorrencia();
  }

  public void carregarModalNovo() {
    this.ocorrenciaSelecionada = new Ocorrencia();
    ocorrenciaSelecionada.setDhPublicacao(new Date());
    Calendar expirar = Calendar.getInstance();
    expirar.add(Calendar.DAY_OF_MONTH, 15);
    ocorrenciaSelecionada.setDhExpiracao(expirar.getTime());
    this.modoEdicao = Boolean.FALSE;
    RequestContext.getCurrentInstance().execute("$('#modalNovo').modal('show');");
  }

  public String adicionar() {
    try {
      if (validarCamposModal()) {
        MensagemUtil.setKeepMessages(true);
        this.ocorrenciaSelecionada.setAtivo(Boolean.TRUE);
        this.ocorrenciaSelecionada.setMatricula((String) JSFUtil.getSessionMapValue("loggedMatricula"));
        this.ocorrenciaSelecionada.setNomeUsuario((String) JSFUtil.getSessionMapValue("loggedUserNomeCompleto"));
        this.ocorrenciaSelecionada.setUnidade(unidadeService.obterUnidadePorChaveFetch(unidadeSelecionadaDTO.getId()));
        
        Date hojeTruncado = DateUtils.truncate(new Date(), Calendar.DATE);
        if(hojeTruncado.equals(ocorrenciaSelecionada.getDhPublicacao())) {
          this.ocorrenciaSelecionada.setDhPublicacao(new Date());
        }
        
        Calendar dataExpiracao = Calendar.getInstance();
        dataExpiracao.setTime(ocorrenciaSelecionada.getDhExpiracao());
        dataExpiracao.set(Calendar.HOUR, 23);
        dataExpiracao.set(Calendar.MINUTE, 59);
        dataExpiracao.set(Calendar.SECOND, 59);
        ocorrenciaSelecionada.setDhExpiracao(dataExpiracao.getTime());
        
        this.ocorrenciaSelecionada = ocorrenciaService.adicionar(this.ocorrenciaSelecionada);
        pesquisar();
        this.facesMessager.addMessageWarn(MensagemUtil.obterMensagem("MA025"));
        RequestContext.getCurrentInstance().execute("$('#modalNovo').modal('hide');");
      } else {
        return null;
      }
    } catch ( RequiredException | BusinessException  | DataBaseException e) {
      LOGGER.error(e.getMessage());
    }
    return redirect();
  }

  public void carregarAlteracao(Ocorrencia ocorrencia) {
    this.modoEdicao = Boolean.TRUE;
    this.ocorrenciaSelecionada = ocorrenciaService.findById(ocorrencia.getId());
    RequestContext.getCurrentInstance().execute("$('#modalNovo').modal('show');");
  }

  public String alterar() {
    
    try {
      if (validarCamposModal()) {
        MensagemUtil.setKeepMessages(true);
        
        //Caso se alterou a data publicação e for hoje dá um new Date para ter as horas
        Ocorrencia ocorrencaNaBase = ocorrenciaService.findById(ocorrenciaSelecionada.getId());
        Date dataPublicacaoTruncadaBase = DateUtils.truncate(ocorrencaNaBase.getDhPublicacao(), Calendar.DATE);
        Date dataPublicacaoOcorrencia = DateUtils.truncate(ocorrenciaSelecionada.getDhPublicacao(), Calendar.DATE);
        Date hojeTruncado = DateUtils.truncate(new Date(), Calendar.DATE);
        
        //Teve Alteracao de Data Publicacao
        if (!dataPublicacaoOcorrencia.equals(dataPublicacaoTruncadaBase)) {
          //Alterou para hoje, então atualiza com DataHora atual
          if (dataPublicacaoOcorrencia.equals(hojeTruncado)) {
            this.ocorrenciaSelecionada.setDhPublicacao(new Date());
          }
        }
        
        Calendar dataExpiracao = Calendar.getInstance();
        dataExpiracao.setTime(ocorrenciaSelecionada.getDhExpiracao());
        dataExpiracao.set(Calendar.HOUR, 23);
        dataExpiracao.set(Calendar.MINUTE, 59);
        dataExpiracao.set(Calendar.SECOND, 59);
        ocorrenciaSelecionada.setDhExpiracao(dataExpiracao.getTime());
        
        this.ocorrenciaSelecionada = ocorrenciaService.alterar(this.ocorrenciaSelecionada);
        pesquisar();
        this.facesMessager.addMessageWarn(MensagemUtil.obterMensagem("MA043"));
        RequestContext.getCurrentInstance().execute("$('#modalNovo').modal('hide');");
        this.modoEdicao = Boolean.FALSE;
      } else {
        return null;
      }
    } catch ( RequiredException | BusinessException  | DataBaseException e) {
      LOGGER.error(e.getMessage());
    }
    return redirect();
  }
  
  private boolean validarCamposModal() {
    if (this.ocorrenciaSelecionada != null) {
      
      if (!StringUtils.isNotBlank(this.ocorrenciaSelecionada.getTitulo())) {
        this.facesMessager.addMessageError(
            MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
      
      if (!StringUtils.isNotBlank(this.ocorrenciaSelecionada.getConteudo())) {
        this.facesMessager.addMessageError(
            MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
      
      if (this.ocorrenciaSelecionada.getDhPublicacao() == null) {
        this.facesMessager.addMessageError(
            MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
      
      if (this.ocorrenciaSelecionada.getDhExpiracao() == null) {
        this.facesMessager.addMessageError(
            MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
   
      
      Date dataHojeTruncada = DateUtils.truncate(new Date(), Calendar.DATE);
      Date dataPublicacaoTruncada = DateUtils.truncate(ocorrenciaSelecionada.getDhPublicacao(), Calendar.DATE);
      
      if (this.modoEdicao) {
        Ocorrencia temp = ocorrenciaService.findById(ocorrenciaSelecionada.getId());
        Date dataPublicacaoTruncadaTemp = DateUtils.truncate(temp.getDhPublicacao(), Calendar.DATE);

        if (!dataPublicacaoTruncada.equals(dataPublicacaoTruncadaTemp)) {
          if (dataPublicacaoTruncada.before(dataHojeTruncada)) {
            this.facesMessager
            .addMessageError(MensagemUtil.obterMensagem("MA042"));
            return Boolean.FALSE;
          }
        }
       //Modo inserção
      } else {
        if (dataPublicacaoTruncada.before(dataHojeTruncada)) {
          this.facesMessager
          .addMessageError(MensagemUtil.obterMensagem("MA042"));
          return Boolean.FALSE;
        }
      }

      if(this.ocorrenciaSelecionada.getDhExpiracao() != null) {
        
        if (dataPublicacaoTruncada.after(this.ocorrenciaSelecionada.getDhExpiracao())) {
          this.facesMessager
          .addMessageError(MensagemUtil.obterMensagem("MA040"));
          return Boolean.FALSE;
        }
      
        if (dataPublicacaoTruncada.equals(this.ocorrenciaSelecionada.getDhExpiracao())) {
          this.facesMessager
          .addMessageError(MensagemUtil.obterMensagem("MA041"));
          return Boolean.FALSE;
        }
        
        /**  Validação de 15 dias - Máximo da notícia. */
        Calendar maxExpiracao = Calendar.getInstance();
        maxExpiracao.setTime(dataPublicacaoTruncada);
        maxExpiracao.add(Calendar.DAY_OF_MONTH, 15);
        if (this.ocorrenciaSelecionada.getDhExpiracao().after(maxExpiracao.getTime())) {
          this.facesMessager
          .addMessageError(MensagemUtil.obterMensagem("MA045"));
          return Boolean.FALSE;
        }
      }      
    }
    
    return Boolean.TRUE;
  }

  public void carregarExclusao(Ocorrencia ocorrencia) {
    this.ocorrenciaSelecionada = ocorrenciaService.findById(ocorrencia.getId());
    RequestContext.getCurrentInstance().execute("$('#modalExclusao').modal('show');");
  }

  public String excluir() {
    try {
      MensagemUtil.setKeepMessages(true);
      this.ocorrenciaSelecionada = ocorrenciaService.excluir(this.ocorrenciaSelecionada);
      pesquisar();
      this.facesMessager.addMessageWarn(MensagemUtil.obterMensagem("MA027"));
      RequestContext.getCurrentInstance().execute("$('#modalExclusao').modal('hide');");
    } catch ( RequiredException | BusinessException  | DataBaseException e) {
      LOGGER.error(e.getMessage());
    }
    return redirect();
  }

  /**
   * Método obter lista de unidades por abrangencia.
   * @return list
   */
  private List<UnidadeDTO> obterListaUnidadePorAbrangencia() {
    List<UnidadeDTO> temp = new ArrayList<UnidadeDTO>();
    for (UnidadeDTO dto : this.listaUnidadesDTO) {
      if (dto.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
        temp.add(dto);
      }
    }
    return temp;
  }
  
  public void pesquisar() {
    this.pesquisaAtiva = Boolean.TRUE;
    
    if (unidadeSelecionadaDTO != null && TipoUnidadeEnum.MATRIZ.equals(unidadeService.obterUnidadePorChaveFetch(unidadeSelecionadaDTO.getId()).getTipoUnidade())) {
      this.unidadeMatriz = Boolean.TRUE;
    } else {
      this.unidadeMatriz = Boolean.FALSE;
    }
    this.listaOcorrencias = ocorrenciaService.obterListaOcorrenciasPorUnidade(unidadeSelecionadaDTO);
  }
  
  public void alterarAbrangencia() {
    this.unidadeSelecionadaDTO = null;
    if (abrangenciaSelecionada != null) {
      this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();
    } else {
      this.listaUnidadesDTOCombo = null; 
    }
  }
  
  @Override
  public Logger getLogger() {
    return LOGGER;
  }
  
  private String redirect() {
    return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true";
  }
  
  /**
   * @return the tamanhoDTOList
   */
  public String getTamanhoDTOList() {
    this.tamanhoDTOList = "0";
    if ((this.listaOcorrencias != null) && !this.listaOcorrencias.isEmpty()) {
      this.tamanhoDTOList = String.valueOf((this.listaOcorrencias.size()));
    }
    return tamanhoDTOList;
  }

  /**
   * @param tamanhoDTOList
   *          the tamanhoDTOList to set
   */
  public void setTamanhoDTOList(String tamanhoDTOList) {
    this.tamanhoDTOList = tamanhoDTOList;
  }
  
  /**
   * @return the pesquisaAtiva
   */
  public boolean isPesquisaAtiva() {
    return pesquisaAtiva;
  }

  /**
   * @param pesquisaAtiva the pesquisaAtiva to set
   */
  public void setPesquisaAtiva(boolean pesquisaAtiva) {
    this.pesquisaAtiva = pesquisaAtiva;
  }

  /**
   * @return the abrangenciaSelecionada
   */
  public Abrangencia getAbrangenciaSelecionada() {
    return abrangenciaSelecionada;
  }

  /**
   * @param abrangenciaSelecionada the abrangenciaSelecionada to set
   */
  public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
    this.abrangenciaSelecionada = abrangenciaSelecionada;
  }

  /**
   * @return the unidadeSelecionadaDTO
   */
  public UnidadeDTO getUnidadeSelecionadaDTO() {
    return unidadeSelecionadaDTO;
  }

  /**
   * @param unidadeSelecionadaDTO the unidadeSelecionadaDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  /**
   * @return the listaUnidadesDTO
   */
  public List<UnidadeDTO> getListaUnidadesDTO() {
    return listaUnidadesDTO;
  }

  /**
   * @param listaUnidadesDTO the listaUnidadesDTO to set
   */
  public void setListaUnidadesDTO(List<UnidadeDTO> listaUnidadesDTO) {
    this.listaUnidadesDTO = listaUnidadesDTO;
  }

  /**
   * @return the listaUnidadesDTOCombo
   */
  public List<UnidadeDTO> getListaUnidadesDTOCombo() {
    return listaUnidadesDTOCombo;
  }

  /**
   * @param listaUnidadesDTOCombo the listaUnidadesDTOCombo to set
   */
  public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
    this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
  }

  /**
   * @return the listaOcorrencias
   */
  public List<Ocorrencia> getListaOcorrencias() {
    return listaOcorrencias;
  }

  /**
   * @param listaOcorrencias the listaOcorrencias to set
   */
  public void setListaOcorrencias(List<Ocorrencia> listaOcorrencias) {
    this.listaOcorrencias = listaOcorrencias;
  }

  /**
   * @return the ocorrenciaSelecionada
   */
  public Ocorrencia getOcorrenciaSelecionada() {
    return ocorrenciaSelecionada;
  }

  /**
   * @param ocorrenciaSelecionada the ocorrenciaSelecionada to set
   */
  public void setOcorrenciaSelecionada(Ocorrencia ocorrenciaSelecionada) {
    this.ocorrenciaSelecionada = ocorrenciaSelecionada;
  }

  /**
   * @return the modoEdicao
   */
  public boolean isModoEdicao() {
    return modoEdicao;
  }

  /**
   * @param modoEdicao the modoEdicao to set
   */
  public void setModoEdicao(boolean modoEdicao) {
    this.modoEdicao = modoEdicao;
  }

  /**
   * @return the unidadeMatriz
   */
  public Boolean getUnidadeMatriz() {
    return unidadeMatriz;
  }

  /**
   * @param unidadeMatriz the unidadeMatriz to set
   */
  public void setUnidadeMatriz(Boolean unidadeMatriz) {
    this.unidadeMatriz = unidadeMatriz;
  }

  /**
   * @return the pesquisaDireta
   */
  public Boolean getPesquisaDireta() {
    return pesquisaDireta;
  }

  /**
   * @param pesquisaDireta the pesquisaDireta to set
   */
  public void setPesquisaDireta(Boolean pesquisaDireta) {
    this.pesquisaDireta = pesquisaDireta;
  }

}
