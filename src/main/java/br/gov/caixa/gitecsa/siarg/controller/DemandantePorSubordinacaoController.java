package br.gov.caixa.gitecsa.siarg.controller;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.siarg.dto.DemandantePorSubrodinacaoDTO;
import br.gov.caixa.gitecsa.siarg.exporter.ExportarVisaoSUEGAnaliticoXLS;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.ParametroService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;

@Named
@ViewScoped
public class DemandantePorSubordinacaoController extends BaseController implements Serializable{

  /** Serial. */
  private static final long serialVersionUID = 5490909190785083126L;
  
  /** Constante. */
  public static final Logger LOGGER = Logger.getLogger(DemandantePorSubordinacaoController.class);

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private DemandaService demandaService;

  @Inject
  private UnidadeService unidadeService;

  @Inject
  private ParametroService parametroService;

  private Date dataInicio;

  private Date datatFim;

  private String dataInicialString;

  private String dataFinalString;

  private List<Assunto> listaTodosAssuntos;
  
  private List<Unidade> listaUnidades;

  private Unidade unidadeSelecionada;
  
  private String tamanhoList;
 
  private List<DemandantePorSubrodinacaoDTO> relatorio;
  
  private List<String> siglaDemandadasList;
  
  private Abrangencia abrangenciaSelecionada;
  
  @PostConstruct
  public void init() {
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    limparCampos();
    carregarUnidadesCombo();
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();
    this.unidadeSelecionada = null;
    
    this.datatFim = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.dataFinalString = sdf.format(datatFim).toString();
        
    this.dataInicio = DateUtils.addDays(datatFim, -6);
    this.dataInicialString = sdf.format(dataInicio).toString(); 
    
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
    this.siglaDemandadasList = new ArrayList<>();
  }

  private void carregarUnidadesCombo() {
    listaUnidades = new ArrayList<Unidade>();
    listaUnidades.add(unidadeService.findBySigla("SUEGA"));
    listaUnidades.add(unidadeService.findBySigla("SUEGB"));
    listaUnidades.add(unidadeService.findBySigla("SUEGC"));
    listaUnidades.add(unidadeService.findBySigla("SUEGD"));
    listaUnidades.add(unidadeService.findBySigla("SUEGE"));
  }

  private void limparPesquisa() {
    this.relatorio = new ArrayList<>();    
  }
  
  public String pesquisar() {
    try {
      limparPesquisa();
      if (validarCampos()) {
        this.relatorio =
            demandaService.relatorioUnidadesDemandantesPorSubordinacao(dataInicio, datatFim, unidadeSelecionada, listaTodosAssuntos, listaUnidades, abrangenciaSelecionada);
        if (!this.relatorio.isEmpty()) {
          this.siglaDemandadasList = new ArrayList<>();
          Map<String,Integer> mapDTO = relatorio.get(0).getListaDemandadas();
          List<String> listaUnidadesDemandadas = new ArrayList<String>();
          for (String key : mapDTO.keySet()) {
            listaUnidadesDemandadas.add(key);
            this.siglaDemandadasList.add(StringUtils.substringAfter(key, "#"));
          }         
          List<Integer> listaValoresUnidadesDemandadas;
          for (DemandantePorSubrodinacaoDTO dto : relatorio) {
            listaValoresUnidadesDemandadas = new ArrayList<Integer>();
            for (String sgUnidade : listaUnidadesDemandadas) {
              listaValoresUnidadesDemandadas.add(dto.getListaDemandadas().get(sgUnidade));
            }
            dto.setNomeUnidadesList(listaUnidadesDemandadas);
            dto.setValorUnidadesList(listaValoresUnidadesDemandadas);
            dto.setSiglaUnidadesDemandasList(this.siglaDemandadasList);
          }
          
          Collections.sort(this.relatorio);
        } else {
          this.siglaDemandadasList = new ArrayList<>();
        }
      }
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() {

    try {
      ExportarVisaoSUEGAnaliticoXLS exportador = new ExportarVisaoSUEGAnaliticoXLS();
      exportador.setData(relatorio);
      
      String caminho = parametroService.obterParametroByNome(ParametroConstantes.CONFIG_RELATORIO_TEMP).getValor();
      
      String filename = "VisãoSUEGAnalitico.xls";
      File relatorio = exportador.exportComParametros(caminho + filename, dataInicio, datatFim);
      return RequestUtils.download(relatorio, relatorio.getName());
    } catch (Exception e) {
      LOGGER.error(mensagem());
    }
    return null;
  }
  
  private String mensagem() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String texto = "";
    texto += "\n**************************************\n";
    texto += "Diretorio inexistente no servidor.\n";
    texto += "Data: " + sdf.format(new Date())+ "\n";
    texto += "**************************************\n";
    return texto;
  }
  
  @Override
  public Logger getLogger() {
    return LOGGER;
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
   * @return the listaTodosAssuntos
   */
  public List<Assunto> getListaTodosAssuntos() {
    return listaTodosAssuntos;
  }

  /**
   * @param listaTodosAssuntos the listaTodosAssuntos to set
   */
  public void setListaTodosAssuntos(List<Assunto> listaTodosAssuntos) {
    this.listaTodosAssuntos = listaTodosAssuntos;
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
   * @return the relatorio
   */
  public List<DemandantePorSubrodinacaoDTO> getRelatorio() {
    return relatorio;
  }

  /**
   * @param relatorio the relatorio to set
   */
  public void setRelatorio(List<DemandantePorSubrodinacaoDTO> relatorio) {
    this.relatorio = relatorio;
  }

  /**
   * @return the siglaDemandadasList
   */
  public List<String> getSiglaDemandadasList() {
    return siglaDemandadasList;
  }

  /**
   * @param siglaDemandadasList the siglaDemandadasList to set
   */
  public void setSiglaDemandadasList(List<String> siglaDemandadasList) {
    this.siglaDemandadasList = siglaDemandadasList;
  }

  /**
   * @param tamanhoList the tamanhoList to set
   */
  public void setTamanhoList(String tamanhoList) {
    this.tamanhoList = tamanhoList;
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

}
