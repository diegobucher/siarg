package br.gov.caixa.gitecsa.siarg.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dto.DemandasEncaminhadasExternasDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class DemandasEncaminhadasExternasController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 2751732340908789790L;

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private FeriadoService feriadoService;

  @Inject
  private DemandaService demandaService;

  /** Variável de Classe. */
  private Date dataInicial;
  
  private String dataInicialString;

  private Date dataFinal;
  
  private String dataFinalString;

  private Abrangencia abrangenciaSelecionada;

  private UnidadeDTO unidadeSelecionadaDTO;

  private List<Assunto> listaTodosAssuntos;

  private String tamanhoDTOList;

  private List<DemandasEncaminhadasExternasDTO> deeDTOList;

  private List<UnidadeDTO> listaUnidadesDTO;

  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    this.unidadeSelecionadaDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.listaUnidadesDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");

    this.deeDTOList = new ArrayList<DemandasEncaminhadasExternasDTO>();
    dataInicial = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.MONTH);
    dataFinal = new Date();
    
    dataInicialString = DateUtil.formatDataPadrao(dataInicial);
    dataFinalString = DateUtil.formatDataPadrao(dataFinal);
  }

  public String pesquisar() {

    if (validarCampos()) {
      
      this.deeDTOList = new ArrayList<DemandasEncaminhadasExternasDTO>();
      
      Map<String, DemandasEncaminhadasExternasDTO> mapDeeDTO = new HashMap<String, DemandasEncaminhadasExternasDTO>();
      String chaveTeste = "";
      
      List<Demanda> demandaList =
          demandaService.obterDemandasEncaminhadosExternasNoPeriodo(abrangenciaSelecionada, dataInicial, dataFinal);
      
      for (Demanda demanda : demandaList) {
        List<Atendimento> atendimentoList = demanda.getAtendimentosList();
        Collections.sort(atendimentoList);
        
        for (int i = 0; i < atendimentoList.size(); i++) {
          Atendimento atendimento = atendimentoList.get(i);
          
          // Esse atendimento é o encaminhamento pra externa
          if (atendimento.getUnidadeExterna() != null) {
            Atendimento atendimentoFeitoPelaExterna = null;
            
            Unidade unidadeExterna = atendimento.getUnidadeExterna();
            
            // Passa pro proximo
            i++;
            while (atendimentoFeitoPelaExterna == null && i < atendimentoList.size()) {
              
              // Pega o proximo
              atendimento = atendimentoList.get(i);
              
              if (atendimento.getAcaoEnum() == null || !(atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.ATUALIZAR)
                  && atendimento.getAcaoEnum().equals(AcaoAtendimentoEnum.RASCUNHO))) {
                atendimentoFeitoPelaExterna = atendimento;
              }
              
              // Passa pro proximo
              i++;
            }
            
            if (atendimentoFeitoPelaExterna != null) {
              
              chaveTeste = (unidadeExterna.getId() + "#" + atendimento.getDemanda().getAssunto().getId());
              
              DemandasEncaminhadasExternasDTO dtoPorExternaAssunto = mapDeeDTO.get(chaveTeste);
              
              // Não tem no map, inicializa os campos
              if (dtoPorExternaAssunto == null) {
                dtoPorExternaAssunto = new DemandasEncaminhadasExternasDTO();
                dtoPorExternaAssunto.setUnidadeExterna(unidadeExterna.getSigla() + " - " + unidadeExterna.getNome());
                dtoPorExternaAssunto
                .setAssunto(this.assuntoService.obterArvoreAssuntos(demanda.getAssunto(), this.listaTodosAssuntos));
                dtoPorExternaAssunto.setResponsavel(demanda.getAssunto().getCaixaPostal().getSigla());
                
                dtoPorExternaAssunto.setQtdEcaminhadas(0);
                dtoPorExternaAssunto.setSomatorioQtdEcaminhadas(0);
                dtoPorExternaAssunto.setTmDemandasEncaminhadas(0.00);
                
                dtoPorExternaAssunto.setQtAguardaAcao(0);
                dtoPorExternaAssunto.setSomatorioQtAguardaAcao(0);
                dtoPorExternaAssunto.setTmAguardandoAcao(0.00);
                
                mapDeeDTO.put(chaveTeste, dtoPorExternaAssunto);
              }
              
              
              int dias = 0;
              Date dataFimAtendimento = atendimentoFeitoPelaExterna.getDataHoraAtendimento();
              
              if(dataFimAtendimento == null) {
                dataFimAtendimento = new Date();
              }
              
              dias = feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), dataFimAtendimento);
              
              dtoPorExternaAssunto.setQtdEcaminhadas(dtoPorExternaAssunto.getQtdEcaminhadas() + 1);
              dtoPorExternaAssunto.setSomatorioQtdEcaminhadas(dtoPorExternaAssunto.getSomatorioQtdEcaminhadas() + dias);
              //Calcula dividindo qtd de dias pela qtd de encaminhadas para fazer a media
              dtoPorExternaAssunto.setTmDemandasEncaminhadas(dtoPorExternaAssunto.getSomatorioQtdEcaminhadas().doubleValue() / dtoPorExternaAssunto.getQtdEcaminhadas().doubleValue());
              
              //Tá na externa e não foi atendida
              if(atendimentoFeitoPelaExterna.getDataHoraAtendimento() == null) {
                dtoPorExternaAssunto.setQtAguardaAcao(dtoPorExternaAssunto.getQtAguardaAcao() + 1);
                dtoPorExternaAssunto.setSomatorioQtAguardaAcao(dtoPorExternaAssunto.getSomatorioQtAguardaAcao() + dias);
                //Calcula dividindo qtd de dias pela qtd de encaminhadas para fazer a media
                dtoPorExternaAssunto.setTmAguardandoAcao(dtoPorExternaAssunto.getSomatorioQtAguardaAcao().doubleValue() / dtoPorExternaAssunto.getQtAguardaAcao().doubleValue());
              }
              
            }
            
          }
          
        }
      
      }
      //Transforma o map na lista esperada
      for (String key : mapDeeDTO.keySet()) {
        deeDTOList.add(mapDeeDTO.get(key));
      }
    }
    return null;
  }

  private boolean validarCampos() {
    
    if (StringUtils.isBlank(this.dataInicialString)) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }
    
    if (StringUtils.isBlank(this.dataFinalString)) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }
    
    try {
      this.dataFinal = DateUtils.parseDateStrictly(dataFinalString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    try {
      this.dataInicial = DateUtils.parseDateStrictly(dataInicialString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    if (this.dataInicial == null) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }
    
    if (this.dataFinal == null) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }
    
    if (this.dataFinal.before(this.dataInicial)) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }

  /**
   * Geração do Relatório em PDF paisagem.
   */
  public StreamedContent downloadPdf() throws JRException {
    HashMap<String, Object> params = new HashMap<String, Object>();

    params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
    params.put("DATA_INICIO", dataInicial);
    params.put("DATA_FIM", dataFinal);
    params.put("NOME_SISTEMA",
        MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
    params.put("NOME_UNIDADE", this.unidadeSelecionadaDTO.getSigla());

    InputStream jasper =
        this.getClass().getClassLoader()
            .getResourceAsStream(String.format("%sDemandasEncaminhadasExternas.jasper", Constantes.REPORT_BASE_DIR));
    Collections.sort(this.deeDTOList);
    return JasperReportUtils.run(jasper, "DemandasEncExternas.pdf", params, this.deeDTOList);
  }

  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() throws JRException {
    try {
      HashMap<String, Object> params = new HashMap<String, Object>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("DATA_INICIO", dataInicial);
      params.put("DATA_FIM", dataFinal);
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
      params.put("NOME_UNIDADE", this.unidadeSelecionadaDTO.getSigla());

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sDemandasEncaminhadasExternasXLS.jrxml", Constantes.REPORT_BASE_DIR));

      Collections.sort(this.deeDTOList);
      return JasperReportUtils.runXLS(jasper, "DemandasEncaminhadasExternas", params, this.deeDTOList);

    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public Logger getLogger() {
    return null;
  }

  /**
   * @return the listaUnidadesDTO
   */
  public List<UnidadeDTO> getListaUnidadesDTO() {
    return listaUnidadesDTO;
  }

  /**
   * @param listaUnidadesDTO
   *          the listaUnidadesDTO to set
   */
  public void setListaUnidadesDTO(List<UnidadeDTO> listaUnidadesDTO) {
    this.listaUnidadesDTO = listaUnidadesDTO;
  }

  /**
   * @return the abrangenciaSelecionada
   */
  public Abrangencia getAbrangenciaSelecionada() {
    return abrangenciaSelecionada;
  }

  /**
   * @param abrangenciaSelecionada
   *          the abrangenciaSelecionada to set
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
   * @param unidadeSelecionadaDTO
   *          the unidadeSelecionadaDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  /**
   * @return the listaTodosAssuntos
   */
  public List<Assunto> getListaTodosAssuntos() {
    return listaTodosAssuntos;
  }

  /**
   * @param listaTodosAssuntos
   *          the listaTodosAssuntos to set
   */
  public void setListaTodosAssuntos(List<Assunto> listaTodosAssuntos) {
    this.listaTodosAssuntos = listaTodosAssuntos;
  }

  /**
   * @return the tamanhoDTOList
   */
  public String getTamanhoDTOList() {
    this.tamanhoDTOList = "0";
    if ((this.deeDTOList != null) && !this.deeDTOList.isEmpty()) {
      this.tamanhoDTOList = String.valueOf((this.deeDTOList.size()));
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
   * @return the dataInicial
   */
  public Date getDataInicial() {
    return dataInicial;
  }

  /**
   * @param dataInicial
   *          the dataInicial to set
   */
  public void setDataInicial(Date dataInicial) {
    this.dataInicial = dataInicial;
  }

  /**
   * @return the dataFinal
   */
  public Date getDataFinal() {
    return dataFinal;
  }

  /**
   * @param dataFinal
   *          the dataFinal to set
   */
  public void setDataFinal(Date dataFinal) {
    this.dataFinal = dataFinal;
  }

  /**
   * @return the deeDTOList
   */
  public List<DemandasEncaminhadasExternasDTO> getDeeDTOList() {
    return deeDTOList;
  }

  /**
   * @param deeDTOList
   *          the deeDTOList to set
   */
  public void setDeeDTOList(List<DemandasEncaminhadasExternasDTO> deeDTOList) {
    this.deeDTOList = deeDTOList;
  }

  /**
   * @return the dataInicialString
   */
  public String getDataInicialString() {
    return dataInicialString;
  }

  /**
   * @param dataInicialString the dataInicialString to set
   */
  public void setDataInicialString(String dataInicialString) {
    this.dataInicialString = dataInicialString;
  }

  /**
   * @return the dataFinalString
   */
  public String getDataFinalString() {
    return dataFinalString;
  }

  /**
   * @param dataFinalString the dataFinalString to set
   */
  public void setDataFinalString(String dataFinalString) {
    this.dataFinalString = dataFinalString;
  }

}
