package br.gov.caixa.gitecsa.siarg.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import br.gov.caixa.gitecsa.siarg.dto.RelatorioGeralVisaoSuegPorUnidadesDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioVisaoGeralPorSUEGConsolidadoXls;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

@Named
@ViewScoped
public class RelatorioQtdDemandasVsUnidadeController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = -4767771666514798174L;

  /** Constante. */
  public static final Logger LOGGER = Logger.getLogger(RelatorioQtdDemandasVsUnidadeController.class);

  @Inject
  private DemandaService demandaService;

  @Inject
  private UnidadeService unidadeService;

  private Date dataInicio;

  private Date datatFim;

  private String dataInicialString;

  private String dataFinalString;

  private Unidade unidadeSelecionada;

  private String tamanhoList;
  
  private List<Unidade> listaUnidades;

  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> relatorio;

  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> suega;
  
  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegb;
  
  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegc;
  
  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegd;
  
  private List<RelatorioGeralVisaoSuegPorUnidadesDTO> suege;
  
  private Abrangencia abrangenciaSelecionada;
  
  private Long totalDemandasAtratar;
  
  private Long totalDemandasRealizadas;
  
  private Boolean pesquisaAtiva;

  @PostConstruct
  public void init() {
    limparCampos();
    carregarUnidadesCombo();
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    this.datatFim = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.dataFinalString = sdf.format(datatFim).toString();
        
    this.dataInicio = DateUtils.addDays(datatFim, -6);
    this.dataInicialString = sdf.format(dataInicio).toString(); 
  }

  public String pesquisar() {
    limparPesquisa();
    if (validarCampos()) {
      this.pesquisaAtiva = Boolean.TRUE;
      this.relatorio = demandaService.relatorioGeralSuegConsolidadoPorUnidade(abrangenciaSelecionada, unidadeSelecionada, dataInicio, datatFim, listaUnidades);
      if (!this.relatorio.isEmpty()) {
        preencherListasSUEGs();
      } else {
        this.pesquisaAtiva = Boolean.FALSE;
      }
    }
    return null;
  }

  private void preencherListasSUEGs() {
    for (RelatorioGeralVisaoSuegPorUnidadesDTO rel : this.relatorio) {
      switch (rel.getUnidadeDemandante().getUnidadeSubordinacao().getSigla()) {
        case "SUEGA" : 
          this.suega.add(rel);
          break;
        case "SUEGB" : 
          this.suegb.add(rel);
          break;
        case "SUEGC" :
          this.suegc.add(rel);
          break;
        case "SUEGD" :
          this.suegd.add(rel);
          break;
        case "SUEGE" : 
          this.suege.add(rel);
          break;
      }
    }
  }

  private boolean validarCampos() {

    if (StringUtils.isBlank(this.dataInicialString)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (StringUtils.isBlank(this.dataFinalString)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    try {
      this.datatFim = DateUtils.parseDateStrictly(dataFinalString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    try {
      this.dataInicio = DateUtils.parseDateStrictly(dataInicialString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    if (this.dataInicio == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (this.datatFim == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (this.datatFim.before(this.dataInicio)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
      return Boolean.FALSE;
    }

    if (this.datatFim.after(new Date())) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA035")));
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  private void limparCampos() {
    limparPesquisa();
    this.dataInicio = null;
    this.datatFim = null;
    this.dataInicialString = null;
    this.dataFinalString = null;
  }

  private void limparPesquisa() {
    this.pesquisaAtiva = Boolean.FALSE;
    this.relatorio = new ArrayList<>();
    this.suega = new ArrayList<>();
    this.suegb = new ArrayList<>();
    this.suegc = new ArrayList<>();
    this.suegd = new ArrayList<>();
    this.suege = new ArrayList<>();
  }

  private void carregarUnidadesCombo() {
    listaUnidades = new ArrayList<Unidade>();
    listaUnidades.add(unidadeService.findBySigla("SUEGA"));
    listaUnidades.add(unidadeService.findBySigla("SUEGB"));
    listaUnidades.add(unidadeService.findBySigla("SUEGC"));
    listaUnidades.add(unidadeService.findBySigla("SUEGD"));
    listaUnidades.add(unidadeService.findBySigla("SUEGE"));
  }
  
  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() {
    try {
      HashMap<String, Object> params = new HashMap<String, Object>();
      String nomeSistema = MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema");
      String logoCaixa = String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR);
      
      params.put("PARAM_LOGO_CAIXA", logoCaixa );
      params.put("PARAM_NOME_SISTEMA", nomeSistema);
      params.put("PARAM_DATA_INICIO", dataInicio);
      params.put("PARAM_DATA_FIM", datatFim);
      params.put("PARAM_TOTAL_TRATAR", getTotalDemandasAtratar());
      params.put("PARAM_TOTAL_REALIZADA", getTotalDemandasRealizadas());
      params.put("SUBREPORT_DIR", String.format("%sRelatorioVisãoGeralSUEG_SubReport.jasper", Constantes.REPORT_BASE_DIR));
      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sRelatorioVisãoGeralSUEG.jrxml", Constantes.REPORT_BASE_DIR));

      List<RelatorioVisaoGeralPorSUEGConsolidadoXls> lista = montagemListaXls();
      
      return JasperReportUtils.runXLS(jasper, "RelatorioVisãoGeralSUEG", params, lista);
      
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  private List<RelatorioVisaoGeralPorSUEGConsolidadoXls> montagemListaXls() {
    List<RelatorioVisaoGeralPorSUEGConsolidadoXls> lista = new ArrayList<RelatorioVisaoGeralPorSUEGConsolidadoXls>();
    RelatorioVisaoGeralPorSUEGConsolidadoXls rel = new RelatorioVisaoGeralPorSUEGConsolidadoXls();
    if(!suega.isEmpty()) {
      Long countTratar = 0L;
      Long countRealizado = 0L;
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suega) {
        countTratar = countTratar + dto.getTotalDemandasTratar();
        countRealizado = countRealizado + dto.getTotalDemandasRealizadas();
      }
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suega) {
        dto.setTotalDemandasTratarSUEG(countTratar);
        dto.setTotalDemandasRealizadasSUEG(countRealizado);
      }
      rel.setRelatorios(suega);
      lista.add(rel);      
    }
    if(!suegb.isEmpty()) {
      rel = new RelatorioVisaoGeralPorSUEGConsolidadoXls();
      Long countTratar = 0L;
      Long countRealizado = 0L;
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegb) {
        countTratar = countTratar + dto.getTotalDemandasTratar();
        countRealizado = countRealizado + dto.getTotalDemandasRealizadas();
      }
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegb) {
        dto.setTotalDemandasTratarSUEG(countTratar);
        dto.setTotalDemandasRealizadasSUEG(countRealizado);
      }
      rel.setRelatorios(suegb);
      lista.add(rel);
    }
    if(!suegc.isEmpty()) {
      rel = new RelatorioVisaoGeralPorSUEGConsolidadoXls();
      Long countTratar = 0L;
      Long countRealizado = 0L;
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegc) {
        countTratar = countTratar + dto.getTotalDemandasTratar();
        countRealizado = countRealizado + dto.getTotalDemandasRealizadas();
      }
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegc) {
        dto.setTotalDemandasTratarSUEG(countTratar);
        dto.setTotalDemandasRealizadasSUEG(countRealizado);
      }
      rel.setRelatorios(suegc);
      lista.add(rel);
    }
    if(!suegd.isEmpty()) {
      rel = new RelatorioVisaoGeralPorSUEGConsolidadoXls();
      Long countTratar = 0L;
      Long countRealizado = 0L;
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegd) {
        countTratar = countTratar + dto.getTotalDemandasTratar();
        countRealizado = countRealizado + dto.getTotalDemandasRealizadas();
      }
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suegd) {
        dto.setTotalDemandasTratarSUEG(countTratar);
        dto.setTotalDemandasRealizadasSUEG(countRealizado);
      }
      rel.setRelatorios(suegd);
      lista.add(rel);
    }
    if(!suege.isEmpty()) {
      rel = new RelatorioVisaoGeralPorSUEGConsolidadoXls();
      Long countTratar = 0L;
      Long countRealizado = 0L;
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suege) {
        countTratar = countTratar + dto.getTotalDemandasTratar();
        countRealizado = countRealizado + dto.getTotalDemandasRealizadas();
      }
      for (RelatorioGeralVisaoSuegPorUnidadesDTO dto : suege) {
        dto.setTotalDemandasTratarSUEG(countTratar);
        dto.setTotalDemandasRealizadasSUEG(countRealizado);
      }
      rel.setRelatorios(suege);
      lista.add(rel);
    }
    return lista;
  }

  /**
   * Obter o valor padrão.
   * @return the tamanhoList
   */
  public String getTamanhoList() {
    this.tamanhoList = "0";
    if ((this.relatorio != null) && !this.relatorio.isEmpty()) {
      this.tamanhoList = String.valueOf((this.relatorio.size()));
    }
    return this.tamanhoList;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  public Long getTotalDemandasTratarSUEGA() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suega) {
      total += temp.getTotalDemandasTratar();
    }    
    return total;
  }

  public Long getTotalDemandasRealizadasSUEGA() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suega) {
      total += temp.getTotalDemandasRealizadas();
    }    
    return total;
  }
  
  public Long getTotalDemandasTratarSUEGB() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegb) {
      total += temp.getTotalDemandasTratar();
    }    
    return total;
  }
  
  public Long getTotalDemandasRealizadasSUEGB() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegb) {
      total += temp.getTotalDemandasRealizadas();
    }    
    return total;
  }
  
  public Long getTotalDemandasTratarSUEGC() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegc) {
      total += temp.getTotalDemandasTratar();
    }    
    return total;
  }
  
  public Long getTotalDemandasRealizadasSUEGC() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegc) {
      total += temp.getTotalDemandasRealizadas();
    }    
    return total;
  }
  
  public Long getTotalDemandasTratarSUEGD() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegd) {
      total += temp.getTotalDemandasTratar();
    }    
    return total;
  }
  
  public Long getTotalDemandasRealizadasSUEGD() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suegd) {
      total += temp.getTotalDemandasRealizadas();
    }    
    return total;
  }
  
  public Long getTotalDemandasTratarSUEGE() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suege) {
      total += temp.getTotalDemandasTratar();
    }    
    return total;
  }
  
  public Long getTotalDemandasRealizadasSUEGE() {
    Long total = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : this.suege) {
      total += temp.getTotalDemandasRealizadas();
    }    
    return total;
  }
  
    /**
   * @return the dataInicio
   */
  public Date getDataInicio() {
    return dataInicio;
  }

  /**
   * @param dataInicio the dataInicio to set
   */
  public void setDataInicio(Date dataInicio) {
    this.dataInicio = dataInicio;
  }

  /**
   * @return the datatFim
   */
  public Date getDatatFim() {
    return datatFim;
  }

  /**
   * @param datatFim the datatFim to set
   */
  public void setDatatFim(Date datatFim) {
    this.datatFim = datatFim;
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

  /**
   * @return the unidadeSelecionada
   */
  public Unidade getUnidadeSelecionada() {
    return unidadeSelecionada;
  }

  /**
   * @param unidadeSelecionada the unidadeSelecionada to set
   */
  public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
    this.unidadeSelecionada = unidadeSelecionada;
  }

  /**
   * @param tamanhoList the tamanhoList to set
   */
  public void setTamanhoList(String tamanhoList) {
    this.tamanhoList = tamanhoList;
  }

  /**
   * @return the listaUnidades
   */
  public List<Unidade> getListaUnidades() {
    return listaUnidades;
  }

  /**
   * @param listaUnidades the listaUnidades to set
   */
  public void setListaUnidades(List<Unidade> listaUnidades) {
    this.listaUnidades = listaUnidades;
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
   * @return the relatorio
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getRelatorio() {
    return relatorio;
  }

  /**
   * @param relatorio the relatorio to set
   */
  public void setRelatorio(List<RelatorioGeralVisaoSuegPorUnidadesDTO> relatorio) {
    this.relatorio = relatorio;
  }

  /**
   * @return the totalDemandasAtratar
   */
  public Long getTotalDemandasAtratar() {
    totalDemandasAtratar = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : relatorio) {
      totalDemandasAtratar += temp.getTotalDemandasTratar();
    }
    return totalDemandasAtratar;
  }

  /**
   * @return the totalDemandasRealizadas
   */
  public Long getTotalDemandasRealizadas() {
    totalDemandasRealizadas = 0L;
    for (RelatorioGeralVisaoSuegPorUnidadesDTO temp : relatorio) {
      totalDemandasRealizadas += temp.getTotalDemandasRealizadas();
    }
    return totalDemandasRealizadas;
  }

  /**
   * @return the pesquisaAtiva
   */
  public Boolean getPesquisaAtiva() {
    return pesquisaAtiva;
  }

  /**
   * @param pesquisaAtiva the pesquisaAtiva to set
   */
  public void setPesquisaAtiva(Boolean pesquisaAtiva) {
    this.pesquisaAtiva = pesquisaAtiva;
  }

  /**
   * @return the suega
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getSuega() {
    return suega;
  }

  /**
   * @param suega the suega to set
   */
  public void setSuega(List<RelatorioGeralVisaoSuegPorUnidadesDTO> suega) {
    this.suega = suega;
  }

  /**
   * @return the suegb
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getSuegb() {
    return suegb;
  }

  /**
   * @param suegb the suegb to set
   */
  public void setSuegb(List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegb) {
    this.suegb = suegb;
  }

  /**
   * @return the suegc
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getSuegc() {
    return suegc;
  }

  /**
   * @param suegc the suegc to set
   */
  public void setSuegc(List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegc) {
    this.suegc = suegc;
  }

  /**
   * @return the suegd
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getSuegd() {
    return suegd;
  }

  /**
   * @param suegd the suegd to set
   */
  public void setSuegd(List<RelatorioGeralVisaoSuegPorUnidadesDTO> suegd) {
    this.suegd = suegd;
  }

  /**
   * @return the suege
   */
  public List<RelatorioGeralVisaoSuegPorUnidadesDTO> getSuege() {
    return suege;
  }

  /**
   * @param suege the suege to set
   */
  public void setSuege(List<RelatorioGeralVisaoSuegPorUnidadesDTO> suege) {
    this.suege = suege;
  }

}
