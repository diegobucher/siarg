package br.gov.caixa.gitecsa.siarg.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.comparator.RelatorioPeriodoPorAssuntoDTOComparator;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class RelatorioComparativoPorPeriodoController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1l;

  @Inject
  private AssuntoService assuntoService;
  
  @Inject
  private DemandaService demandaService;

  /** Variável de Classe. */
  private List<UnidadeDTO> listaUnidadesDTOCombo;
  
  private List<String> listaUnidadesStringCombo;

  private Abrangencia abrangenciaSelecionada;

  private UnidadeDTO unidadeSelecionadaDTO;
  
  private String unidadeSelecionadaString;
  
  private String tamanhoList;
  
  private List<RelatorioPeriodoPorAssuntoDTO> relatorioPeriodoList;
  
  private String dtIniPerInicial;
  private String dtFimPerInicial;

  private String dtIniPerFinal;
  private String dtFimPerFinal;
  
  private RelatorioPeriodoPorAssuntoDTO assuntoSelecionadoDetalhe;
  private String periodoDetalhe;
  private String periodoDetalheDatas;
  private String qtdeDetalhe;
  
  private List<DemandaDTO> demandasDetalheList;
  
  private Date dataIniPerInicial;
  private Date dataFimPerInicial;
  private Date dataIniPerFinal;
  private Date dataFimPerFinal;
  
  private static final String INICIAL = "Inicial";
  private static final String FINAL = "Final";
  
  private Boolean flagPesquisado;
  
  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    this.listaUnidadesStringCombo = new ArrayList<String>(); 
    flagPesquisado = false;
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    List<UnidadeDTO>  listaUnidadesSessaoDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");

    this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia(listaUnidadesSessaoDTO);

    if(this.listaUnidadesDTOCombo.size() > 1) {
      listaUnidadesStringCombo.add("TODAS");
    }
    
    for (UnidadeDTO unidade : listaUnidadesDTOCombo) {
      this.listaUnidadesStringCombo.add(unidade.getSigla());
    }
    
    inicializarDatas();
  }
  
  private void inicializarDatas() {
    
    Date hoje = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.dtFimPerFinal = sdf.format(hoje);

    //-15
    this.dtIniPerFinal = sdf.format(DateUtils.addDays(hoje, -15));
    
    //-16
    this.dtFimPerInicial = sdf.format(DateUtils.addDays(hoje, -16));
    
    //-31
    this.dtIniPerInicial = sdf.format(DateUtils.addDays(hoje, -31));
    
  }

  /**
   * Método obter lista de unidades por abrangencia.
   */
  private List<UnidadeDTO> obterListaUnidadePorAbrangencia(List<UnidadeDTO> listaUnidadesSessaoDTO) {
    List<UnidadeDTO> temp = new ArrayList<UnidadeDTO>();
    for (UnidadeDTO dto : listaUnidadesSessaoDTO) {
      if (dto.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
        temp.add(dto);
      }
    }
    return temp;
  }

  public void pesquisar() {
    
      
      if(validarCampos()){
        try {
          assuntoSelecionadoDetalhe = null;
          flagPesquisado = true;
          
          dataIniPerInicial = DateUtils.parseDateStrictly(this.dtIniPerInicial,"dd/MM/yyyy");
          dataFimPerInicial = DateUtils.parseDateStrictly(this.dtFimPerInicial,"dd/MM/yyyy");
          dataIniPerFinal = DateUtils.parseDateStrictly(this.dtFimPerInicial,"dd/MM/yyyy");
          dataFimPerFinal = DateUtils.parseDateStrictly(this.dtFimPerFinal,"dd/MM/yyyy");
          
          Long idUnidade = null;
          for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
            if(this.unidadeSelecionadaString.equals(unidadeDTO.getSigla())) {
              this.unidadeSelecionadaDTO = unidadeDTO;
              idUnidade = unidadeSelecionadaDTO.getId();
              break;
            }else {
              this.unidadeSelecionadaDTO = null;
              idUnidade = null;
            }
          }
          
          relatorioPeriodoList =
              assuntoService.findRelatorioPeriodoPorAssunto(abrangenciaSelecionada.getId(), idUnidade, dataIniPerInicial,
                  dataFimPerInicial, dataIniPerFinal, dataFimPerFinal);
          
        } catch (ParseException e) {
          this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
        }
      }
  }
  
  private String definirPeriodoDetalheDatas(String periodoDetalhe) {
    if(INICIAL.equals(periodoDetalhe)) {
      return this.dtIniPerInicial + " à " + this.dtFimPerInicial;
    } else {
      return this.dtIniPerFinal + " à " + this.dtFimPerFinal;
    }
  }
  
  public void detalharAssuntoPeriodoInicialQtdTotal(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = INICIAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(INICIAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdTotalPeriodoInicial().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerInicial , dataFimPerInicial, SituacaoEnum.ABERTA, SituacaoEnum.FECHADA);
    
  }
  public void detalharAssuntoPeriodoInicialQtdAberta(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = INICIAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(INICIAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdAbertaPeriodoInicial().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerInicial , dataFimPerInicial, SituacaoEnum.ABERTA);
  }
  public void detalharAssuntoPeriodoInicialQtdFechada(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = INICIAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(INICIAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdFechadaPeriodoInicial().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerInicial , dataFimPerInicial, SituacaoEnum.FECHADA);
  }

  public void detalharAssuntoPeriodoFinalQtdTotal(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = FINAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(FINAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdTotalPeriodoFinal().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerFinal, dataFimPerFinal, SituacaoEnum.ABERTA, SituacaoEnum.FECHADA);
  }
  public void detalharAssuntoPeriodoFinalQtdAberta(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = FINAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(FINAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdAbertaPeriodoFinal().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerFinal, dataFimPerFinal, SituacaoEnum.ABERTA);
  }
  public void detalharAssuntoPeriodoFinalQtdFechada(RelatorioPeriodoPorAssuntoDTO assuntoDto) {
    this.assuntoSelecionadoDetalhe = assuntoDto;
    this.periodoDetalhe = FINAL;
    this.periodoDetalheDatas = definirPeriodoDetalheDatas(FINAL);
    this.qtdeDetalhe= assuntoSelecionadoDetalhe.getQtdFechadaPeriodoFinal().toString();
    this.demandasDetalheList = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(assuntoDto.getAssunto().getId(), dataIniPerFinal, dataFimPerFinal, SituacaoEnum.FECHADA);
  }
  
  public String obterAssuntoConcatenado(String assuntoCompleto) {
    int tamanho = 35;
    if (assuntoCompleto.length() > tamanho) {
      String textoConcatenado = "...";
      textoConcatenado += assuntoCompleto.substring(assuntoCompleto.length() - tamanho-3, assuntoCompleto.length());
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

  private boolean validarCampos() {
    
    if(StringUtils.isBlank(this.dtIniPerInicial)
        || StringUtils.isBlank(this.dtFimPerInicial)
        || StringUtils.isBlank(this.dtFimPerFinal)
        ) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }
    
    
    try {
      Date dtIniPerInicial = DateUtils.parseDateStrictly(this.dtIniPerInicial,"dd/MM/yyyy");
      Date dtFimPerInicial = DateUtils.parseDateStrictly(this.dtFimPerInicial,"dd/MM/yyyy");
      Date dtIniPerFinal = DateUtils.parseDateStrictly(this.dtIniPerFinal,"dd/MM/yyyy");
      Date dtFimPerFinal = DateUtils.parseDateStrictly(this.dtFimPerFinal,"dd/MM/yyyy");
      
      if (dtFimPerInicial.before(dtIniPerInicial)) {
        this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA073", "Periodo Inicial")));
        return Boolean.FALSE;
      } else if(dtFimPerFinal.before(dtIniPerFinal)) {
        this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA073", "Periodo Final")));
        return Boolean.FALSE;
      } else if (!dtFimPerInicial.before(dtIniPerFinal)) {
        this.facesMessager.addMessageError("Período Inicial deve ser anterior ao Período Final");
        return Boolean.FALSE;
      }
    } catch (ParseException e) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }

  public void alterarUnidadeSelecionada() {
    this.pesquisar();
  }
  
  public StreamedContent downloadExcelResultado() throws JRException, ParseException {
    try {
      
        String nomeUnidade = "";
        if (this.unidadeSelecionadaDTO != null) {
          nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
        } else {
          nomeUnidade = "Todas";
        }
        
      HashMap<String, Object> params = new HashMap<>();
      
      Date dtIniPerInicial = DateUtils.parseDateStrictly(this.dtIniPerInicial,"dd/MM/yyyy");
      Date dtFimPerInicial = DateUtils.parseDateStrictly(this.dtFimPerInicial,"dd/MM/yyyy");
      Date dtIniPerFinal = DateUtils.parseDateStrictly(this.dtIniPerFinal,"dd/MM/yyyy");
      Date dtFimPerFinal = DateUtils.parseDateStrictly(this.dtFimPerFinal,"dd/MM/yyyy");

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("DATA_INICIO", dtIniPerInicial);
      params.put("DATA_FIM", dtFimPerInicial);
      params.put("DATA_INICIO_PER_FINAL", dtIniPerFinal);
      params.put("DATA_FIM_PER_FINAL", dtFimPerFinal);
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sRelatorioPeriodoPorAssuntoResultadoXLS.jrxml", Constantes.REPORT_BASE_DIR));

      RelatorioPeriodoPorAssuntoDTOComparator relatorioComparator = new RelatorioPeriodoPorAssuntoDTOComparator();
      
      Collections.sort(this.relatorioPeriodoList, relatorioComparator);
      
      return JasperReportUtils.runXLS(jasper, "RelatorioPeriodoPorAssunto", params, this.relatorioPeriodoList);

    } catch (IOException e) {
      LOGGER.info(e);
      return null;
    }
  }
  
  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() throws JRException {
    try {
      HashMap<String, Object> params = new HashMap<String, Object>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      if(this.periodoDetalhe.equals(INICIAL)) {
        params.put("DATA_INICIO", this.dataIniPerInicial);
        params.put("DATA_FIM", this.dataFimPerInicial);
      } else {
        params.put("DATA_INICIO", this.dataIniPerFinal);
        params.put("DATA_FIM", this.dataFimPerFinal);
      }
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sRelatorioPeriodoPorAssuntoXLS.jrxml", Constantes.REPORT_BASE_DIR));

      return JasperReportUtils.runXLS(jasper, "RelatorioPeriodoPorAssunto", params, this.demandasDetalheList);

    } catch (IOException e) {
      LOGGER.info(e);
      return null;
    }
  }

  public String getTamanhoList() {
    this.tamanhoList = "0";
    if ((this.relatorioPeriodoList != null) && !this.relatorioPeriodoList.isEmpty()) {
      this.tamanhoList = String.valueOf((this.relatorioPeriodoList.size()));
    }
    return this.tamanhoList;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the unidadeSelecionadaDTO
   */
  public UnidadeDTO getUnidadeSelecionadaDTO() {
    return this.unidadeSelecionadaDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param unidadeSelecionadaDTO
   *          the unidadeSelecionadaDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the abrangenciaSelecionada
   */
  public Abrangencia getAbrangenciaSelecionada() {
    return this.abrangenciaSelecionada;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param abrangenciaSelecionada
   *          the abrangenciaSelecionada to set
   */
  public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
    this.abrangenciaSelecionada = abrangenciaSelecionada;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaUnidadesDTOCombo
   */
  public List<UnidadeDTO> getListaUnidadesDTOCombo() {
    return this.listaUnidadesDTOCombo;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaUnidadesDTOCombo
   *          the listaUnidadesDTOCombo to set
   */
  public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
    this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
  }

  public List<RelatorioPeriodoPorAssuntoDTO> getRelatorioPeriodoList() {
    return relatorioPeriodoList;
  }

  public void setRelatorioPeriodoList(List<RelatorioPeriodoPorAssuntoDTO> relatorioPeriodoList) {
    this.relatorioPeriodoList = relatorioPeriodoList;
  }

  public String getDtIniPerInicial() {
    return dtIniPerInicial;
  }

  public void setDtIniPerInicial(String dtIniPerInicial) {
    this.dtIniPerInicial = dtIniPerInicial;
  }

  public String getDtFimPerInicial() {
    return dtFimPerInicial;
  }

  public void setDtFimPerInicial(String dtFimPerInicial) {
    this.dtFimPerInicial = dtFimPerInicial;
  }

  public String getDtIniPerFinal() {
    return dtIniPerFinal;
  }

  public void setDtIniPerFinal(String dtIniPerFinal) {
    this.dtIniPerFinal = dtIniPerFinal;
  }

  public String getDtFimPerFinal() {
    return dtFimPerFinal;
  }

  public void setDtFimPerFinal(String dtFimPerFinal) {
    this.dtFimPerFinal = dtFimPerFinal;
  }

  public RelatorioPeriodoPorAssuntoDTO getAssuntoSelecionadoDetalhe() {
    return assuntoSelecionadoDetalhe;
  }

  public void setAssuntoSelecionadoDetalhe(RelatorioPeriodoPorAssuntoDTO assuntoSelecionadoDetalhe) {
    this.assuntoSelecionadoDetalhe = assuntoSelecionadoDetalhe;
  }

  public String getPeriodoDetalhe() {
    return periodoDetalhe;
  }

  public void setPeriodoDetalhe(String periodoDetalhe) {
    this.periodoDetalhe = periodoDetalhe;
  }

  public String getQtdeDetalhe() {
    return qtdeDetalhe;
  }

  public void setQtdeDetalhe(String qtdeDetalhe) {
    this.qtdeDetalhe = qtdeDetalhe;
  }

  public List<DemandaDTO> getDemandasDetalheList() {
    return demandasDetalheList;
  }

  public void setDemandasDetalheList(List<DemandaDTO> demandasDetalheList) {
    this.demandasDetalheList = demandasDetalheList;
  }

  public String getPeriodoDetalheDatas() {
    return periodoDetalheDatas;
  }

  public void setPeriodoDetalheDatas(String periodoDetalheDatas) {
    this.periodoDetalheDatas = periodoDetalheDatas;
  }

  public Boolean getFlagPesquisado() {
    return flagPesquisado;
  }

  public void setFlagPesquisado(Boolean flagPesquisado) {
    this.flagPesquisado = flagPesquisado;
  }

  /**
   * @return the listaUnidadesStringCombo
   */
  public List<String> getListaUnidadesStringCombo() {
    return listaUnidadesStringCombo;
  }

  /**
   * @param listaUnidadesStringCombo the listaUnidadesStringCombo to set
   */
  public void setListaUnidadesStringCombo(List<String> listaUnidadesStringCombo) {
    this.listaUnidadesStringCombo = listaUnidadesStringCombo;
  }

  /**
   * @return the unidadeSelecionadaString
   */
  public String getUnidadeSelecionadaString() {
    return unidadeSelecionadaString;
  }

  /**
   * @param unidadeSelecionadaString the unidadeSelecionadaString to set
   */
  public void setUnidadeSelecionadaString(String unidadeSelecionadaString) {
    this.unidadeSelecionadaString = unidadeSelecionadaString;
  }

}
