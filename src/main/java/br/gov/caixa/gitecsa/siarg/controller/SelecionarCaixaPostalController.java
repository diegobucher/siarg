package br.gov.caixa.gitecsa.siarg.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.OcorrenciaLida;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.security.ControleAcesso;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.OcorrenciaLidaService;
import br.gov.caixa.gitecsa.siarg.service.OcorrenciaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

@Named
@ViewScoped
public class SelecionarCaixaPostalController extends BaseController implements Serializable {

  private static final long serialVersionUID = 1L;

  @EJB
  private UnidadeService unidadeService;

  @EJB
  private CaixaPostalService caixaPostalService;

  @EJB
  private OcorrenciaService ocorrenciaService;
  
  @EJB
  private OcorrenciaLidaService ocorrenciaLidaService;

  private List<UnidadeDTO> unidadeList;

  private List<CaixaPostal> caixaPostalList;

  private List<Abrangencia> abrangenciaList;

  private UnidadeDTO unidade;

  private CaixaPostal caixaPostal;

  private Abrangencia abrangencia;

  private List<Ocorrencia> ocorrenciaList;

  private boolean flagExisteOcorrenciaNaoLida;
  
  private boolean checkboxLida;

  /** Constante. */
  private static final String REDIRECT_ACOMPANHAMENTO_DEMANADAS =
      "/paginas/demanda/acompanhamento/acompanhamento.xhtml?faces-redirect=true";

  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    caixaPostalList = new ArrayList<CaixaPostal>();
    
    abrangenciaList = (List<Abrangencia>) JSFUtil.getSessionMapValue("abrangenciaList");
    if (abrangenciaList.size() == 1) {
      abrangencia = abrangenciaList.get(0);
      unidadeList = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
      if (unidadeList != null && unidadeList.size() == 1) {
        onChangeAbrangencia();
        onChangeUnidade();
      }
      carregarOcorrencias();
    }
  }

  private void carregarOcorrencias() {
    
    this.flagExisteOcorrenciaNaoLida = false;
    this.checkboxLida = false;
    
    this.ocorrenciaList = new ArrayList<Ocorrencia>();
    
    if (abrangencia != null) {
      Date dataInicialBusca = DateUtils.addDays(new Date(), -15);

      this.ocorrenciaList = ocorrenciaService.obterListaOcorrenciasPor(abrangencia, dataInicialBusca);

      if (ocorrenciaList != null && !ocorrenciaList.isEmpty()) {

        OcorrenciaLida ocorrenciaLida =
            ocorrenciaLidaService.obterOcorrenciaLida((String) JSFUtil.getSessionMapValue("loggedUser"),
                this.abrangencia.getId());

        if (ocorrenciaLida == null) {
          this.flagExisteOcorrenciaNaoLida = true;
        } else {

          Date dataHoraUltimaLeitura = ocorrenciaLida.getDataHoraLeitura();

          for (Ocorrencia ocorrencia : ocorrenciaList) {
            if (ocorrencia.getDhPublicacao().after(dataHoraUltimaLeitura)) {
              flagExisteOcorrenciaNaoLida = true;
              break;
            }
          }
        }
      }
    }

    
  }

  public void onChangeAbrangencia() {
    unidadeList = new ArrayList<UnidadeDTO>();
    caixaPostalList = new ArrayList<CaixaPostal>();

    if (abrangencia != null) {
      ControleAcesso controleAcesso = (ControleAcesso) JSFUtil.getSessionMapValue("controleAcessoBean");
      JSFUtil.setSessionMapValue("abrangencia", abrangencia);
      controleAcesso.setAbrangencia(abrangencia);
      unidadeList = controleAcesso.getUnidadesPorAbrangenciaList();
      if (unidadeList != null && unidadeList.size() == 1) {
        this.unidade = unidadeList.get(0);
        onChangeUnidade();
      }
    }
    carregarOcorrencias();
  }

  public void onChangeUnidade() {
    caixaPostalList = new ArrayList<CaixaPostal>();

    if (unidade != null) {
      ControleAcesso controleAcesso = (ControleAcesso) JSFUtil.getSessionMapValue("controleAcessoBean");
      JSFUtil.setSessionMapValue("unidadeSelecionadaDTO", unidade);
      controleAcesso.setUnidadeSelecionadaDTO(unidade);
      caixaPostalList = caixaPostalService.findByUnidade(unidade);

    }

  }

  public void onChangeCaixaPostal() throws IOException {
    JSFUtil.setSessionMapValue("caixaPostal", caixaPostal);
  }

  public String redirectAcompanhar() {
    return REDIRECT_ACOMPANHAMENTO_DEMANADAS;
  }

  public boolean desabilitarComboAbrangencia() {

    if (abrangenciaList.size() == 1) {
      return true;
    }

    return false;
  }

  public boolean desabilitarComboUnidade() {

//    if (unidadeList != null && unidadeList.size() == 1) {
//      return true;
//    }

    return false;
  }

  public boolean desabilitarComboCaixaPostal() {

    // return desabilitarComboUnidade();

    // if (caixaPostalList == null || caixaPostalList.size() <= 1) {
    // return true;
    // }
    //
    return false;
  }
  
  public void confirmarLeituraOcorrencias() throws RequiredException, BusinessException, DataBaseException {
    
    OcorrenciaLida ocorrenciaLida = ocorrenciaLidaService.obterOcorrenciaLida((String)JSFUtil.getSessionMapValue("loggedUser"), this.abrangencia.getId());

    if(ocorrenciaLida == null) {
      ocorrenciaLida = new OcorrenciaLida();
      ocorrenciaLida.setMatricula((String)JSFUtil.getSessionMapValue("loggedUser"));
      ocorrenciaLida.setDataHoraLeitura(new Date());
      ocorrenciaLida.setAbrangencia(this.abrangencia);
      ocorrenciaLidaService.save(ocorrenciaLida);
    } else {
      ocorrenciaLida.setDataHoraLeitura(new Date());
      ocorrenciaLidaService.update(ocorrenciaLida);
    }
    
    
    this.flagExisteOcorrenciaNaoLida = false;
    this.checkboxLida = false;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public List<UnidadeDTO> getUnidadeList() {
    return unidadeList;
  }

  public void setUnidadeList(List<UnidadeDTO> unidadeList) {
    this.unidadeList = unidadeList;
  }

  public List<CaixaPostal> getCaixaPostalList() {
    return caixaPostalList;
  }

  public void setCaixaPostalList(List<CaixaPostal> caixaPostalList) {
    this.caixaPostalList = caixaPostalList;
  }

  public UnidadeDTO getUnidade() {
    return unidade;
  }

  public void setUnidade(UnidadeDTO unidade) {
    this.unidade = unidade;
  }

  public CaixaPostal getCaixaPostal() {
    return caixaPostal;
  }

  public void setCaixaPostal(CaixaPostal caixaPostal) {
    this.caixaPostal = caixaPostal;
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

  public List<Ocorrencia> getOcorrenciaList() {
    return ocorrenciaList;
  }

  public void setOcorrenciaList(List<Ocorrencia> ocorrenciaList) {
    this.ocorrenciaList = ocorrenciaList;
  }

  public boolean isFlagExisteOcorrenciaNaoLida() {
    return flagExisteOcorrenciaNaoLida;
  }

  public void setFlagExisteOcorrenciaNaoLida(boolean flagExisteOcorrenciaNaoLida) {
    this.flagExisteOcorrenciaNaoLida = flagExisteOcorrenciaNaoLida;
  }

  public boolean isCheckboxLida() {
    return checkboxLida;
  }

  public void setCheckboxLida(boolean checkboxLida) {
    this.checkboxLida = checkboxLida;
  }

}
