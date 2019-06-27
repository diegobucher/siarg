/**
 * RelacaoAssuntosController.java
 * Versão: 1.0.0.00
 * 19/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.JavaScriptUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.RequestUtils;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoDemandaDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelacaoAssuntosDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.exporter.ExportarMigracaoAssuntoXLS;
import br.gov.caixa.gitecsa.siarg.exporter.ExportarMigracaoDemandaXLS;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FluxoAssuntoService;
import br.gov.caixa.gitecsa.siarg.service.ParametroService;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;
import net.sf.jasperreports.engine.JRException;

/**
 * Classe de Controller para tela de Relatório - Relacao de Assuntos.
 * 
 * @author f520296
 */
@Named
@ViewScoped
public class RelacaoAssuntosController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1427777816614033768L;

  /** Service que contém as regras de negócio da entidade Feriado. */

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private FluxoAssuntoService fluxoAssuntoService;

  @Inject
  private ParametroService parametroService;

  @Inject
  private DemandaService demandaService;

  /** Variável de Classe. */
  private List<UnidadeDTO> listaUnidadesDTO;

  private List<UnidadeDTO> listaUnidadesDTOCombo;

  private Abrangencia abrangenciaSelecionada;

  private UnidadeDTO unidadeSelecionadaDTO;

  private RelacaoAssuntosDTO relacaoAssuntosDTO;

  private List<RelacaoAssuntosDTO> relacaoAssuntosDTOList;

  /** Variável de Classe. */
  private List<Assunto> listaAssuntos;

  private List<Assunto> listaTodosAssuntos;

  private List<FluxoAssunto> fluxosAssuntosDemDefinList;

  private String tamanhoDTOList;

  private String situacaoTransacaoSelecionada;

  private final String ABERTAS = "ABERTAS";
  private final String FECHADAS = "FECHADAS";
  private final String CANCELADAS = "CANCELADAS";
  private final String RASCUNHADASMINUTADAS = "RASCUNHADASMINUTADAS";
  private final String TODAS = "TODAS";
  
  private boolean isListaDemandasCheia;

  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    this.unidadeSelecionadaDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.listaUnidadesDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();

    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    
    if(this.listaUnidadesDTO.size() > 1) {
      this.unidadeSelecionadaDTO = null;
    }

    this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();

    this.relacaoAssuntosDTO = new RelacaoAssuntosDTO();
    this.relacaoAssuntosDTOList = new ArrayList<RelacaoAssuntosDTO>();

    this.fluxosAssuntosDemDefinList = this.fluxoAssuntoService.obterTodosAssuntosFetch(TipoFluxoEnum.DEMANDANTE_DEFINIDO);

    this.pesquisar();
  }

  /**
   * Método obter lista de unidades por abrangencia.
   * 
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
    this.listaAssuntos =
        this.assuntoService.pesquisarRelacaoAssuntos(this.abrangenciaSelecionada, this.unidadeSelecionadaDTO,
            this.listaUnidadesDTOCombo);
    this.relacaoAssuntosDTOList = this.montarDTOExibicao();
  }

  public void alterarUnidadeSelecionada() {
    this.pesquisar();
  }

  public void alterarAbrangenciaSelecionada() {
    this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();
    if (this.listaUnidadesDTOCombo.isEmpty()) {
      this.unidadeSelecionadaDTO = null;
    } else {
      this.unidadeSelecionadaDTO = this.listaUnidadesDTOCombo.get(0);
    }
    this.pesquisar();
  }

  public List<RelacaoAssuntosDTO> montarDTOExibicao() {
    List<RelacaoAssuntosDTO> assuntoList = new ArrayList<RelacaoAssuntosDTO>();
    RelacaoAssuntosDTO assuntoDTO;
    for (Assunto assunto : this.listaAssuntos) {
      assuntoDTO = new RelacaoAssuntosDTO();
      assuntoDTO.setNumeroAssunto(assunto.getId());
      assuntoDTO.setNomeAssunto(this.assuntoService.obterArvoreAssuntos(assunto, this.listaTodosAssuntos));
      assuntoDTO.setResponsavelOrdenacao(assunto.getCaixaPostal().getSigla());
      assuntoDTO.setResponsavel(assunto.getCaixaPostal().getSigla() + "(" + assunto.getPrazo() + ")");
      assuntoDTO.setAtoresFluxo(this.obterAtoresFluxoAssunto(assunto));
      assuntoDTO.setObservadoresAutorizados(this.obterNomesObservadoresAutorizados(assunto));
      assuntoDTO.setDemandantesPreDefinidos(this.obTerListaDemandantesPreDefinidos(assunto));
      assuntoList.add(assuntoDTO);
    }
    return assuntoList;
  }

  /**
   * Método para obter os nomes dos observadores autorizados a visualizar o assunto.
   * 
   * @param assunto
   *          assunto
   * @return String
   */
  private String obterNomesObservadoresAutorizados(Assunto assunto) {
    return this.assuntoService.obterNomesObservadoresAutorizados(assunto);
  }

  /**
   * Método para obter a lista de demandantes pre-definidos.
   * 
   * @param assunto
   *          assunto
   * @return list String
   */
  private String obTerListaDemandantesPreDefinidos(Assunto assunto) {
    String temp = "";
    int count = 0;
    for (Unidade unidade : assunto.getAssuntoUnidadeDemandanteList()) {
      temp += unidade.getSigla();
      if (count < assunto.getAssuntoUnidadeDemandanteList().size()) {
        temp += "; ";
      }
    }
    return temp;
  }

  /**
   * Método para obter os atores do fluxo assunto.
   * 
   * @param assunto
   *          assunto
   * @return string
   */
  private String obterAtoresFluxoAssunto(Assunto assunto) {
    String atoresFluxoAssunto = "";
    List<FluxoAssunto> temp = new ArrayList<FluxoAssunto>();
    for (FluxoAssunto fluxoAssunto : this.fluxosAssuntosDemDefinList) {
      if (fluxoAssunto.getAssunto().getId().equals(assunto.getId())) {
        temp.add(fluxoAssunto);
      }
    }
    Collections.sort(temp);
    int count = 0;
    for (FluxoAssunto fa : temp) {
      count++;
      atoresFluxoAssunto += fa.getCaixaPostal().getSigla() + "(" + fa.getPrazo() + ")";
      if (count < temp.size()) {
        atoresFluxoAssunto += " «-» ";
      }
    }
    return atoresFluxoAssunto;
  }

  /*
   * (non-Javadoc)
   * 
   * @see br.gov.caixa.gitecsa.arquitetura.controller.BaseController#getLogger()
   */
  @Override
  public Logger getLogger() {
    return null;
  }

  /**
   * Geração do Relatório em PDF paisagem.
   */
  public StreamedContent downloadPdf() throws JRException {
    HashMap<String, Object> params = new HashMap<String, Object>();
    String nomeUnidade = "";
    if (this.unidadeSelecionadaDTO != null) {
      nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
    } else {
      nomeUnidade = "Todas";
    }

    params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
    params.put("NOME_SISTEMA",
        MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
    params.put("NOME_UNIDADE", nomeUnidade);

    InputStream jasper =
        this.getClass().getClassLoader()
            .getResourceAsStream(String.format("%sRelacaoAssuntosReport.jasper", Constantes.REPORT_BASE_DIR));
    Collections.sort(this.relacaoAssuntosDTOList);
    return JasperReportUtils.run(jasper, "RelacaoAssuntos.pdf", params, this.relacaoAssuntosDTOList);
  }

  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() throws JRException {
    try {
      String nomeUnidade = "";
      if (this.unidadeSelecionadaDTO != null) {
        nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
      } else {
        nomeUnidade = "Todas";
      }

      HashMap<String, Object> params = new HashMap<String, Object>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
      params.put("NOME_UNIDADE", nomeUnidade);

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sRelacaoAssuntosReportXLS.jrxml", Constantes.REPORT_BASE_DIR));

      Collections.sort(this.relacaoAssuntosDTOList);
      return JasperReportUtils.runXLS(jasper, "RelacaoAssuntos", params, this.relacaoAssuntosDTOList);

    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Geração da planiha de migração de assunto - paisagem.
   */
  public StreamedContent downloadPlanilhaMigracaoAssunto() {

    try {
      ExportarMigracaoAssuntoXLS exportador = new ExportarMigracaoAssuntoXLS();
      List<ExportacaoAssuntoDTO> assuntoList = assuntoService.buscarAssuntosExportacao(listaAssuntos, relacaoAssuntosDTOList);

      exportador.setData(assuntoList);

      String caminho = parametroService.obterParametroByNome(ParametroConstantes.CONFIG_RELATORIO_TEMP).getValor();

      String filename = "MigracaoAssunto.xls";
      File relatorio = exportador.export(caminho + filename);
      return RequestUtils.download(relatorio, relatorio.getName());
    } catch (Exception e) {
      LOGGER.error(mensagem());
    }
    return null;
  }

  /**
   * Geração da planiha de migração de assunto - paisagem.
   */
  public StreamedContent downloadPlanilhaMigracaoDemanda() {

    try {
      ExportarMigracaoDemandaXLS exportador = new ExportarMigracaoDemandaXLS();

      List<UnidadeDTO> listaUnidadesFiltro = new ArrayList<UnidadeDTO>();

      if (this.unidadeSelecionadaDTO != null) {
        listaUnidadesFiltro.add(this.unidadeSelecionadaDTO);
      } else {
        listaUnidadesFiltro.addAll(this.listaUnidadesDTOCombo);
      }

      List<SituacaoEnum> situacaoEnumList = new ArrayList<SituacaoEnum>();
      if (situacaoTransacaoSelecionada.equals(ABERTAS)) {
        situacaoEnumList.add(SituacaoEnum.ABERTA);
      } else if (situacaoTransacaoSelecionada.equals(FECHADAS)) {
        situacaoEnumList.add(SituacaoEnum.FECHADA);
      } else if (situacaoTransacaoSelecionada.equals(this.CANCELADAS)) {
        situacaoEnumList.add(SituacaoEnum.CANCELADA);
      } else if (situacaoTransacaoSelecionada.equals(this.RASCUNHADASMINUTADAS)) {
        situacaoEnumList.add(SituacaoEnum.MINUTA);
        situacaoEnumList.add(SituacaoEnum.RASCUNHO);
      } else if (situacaoTransacaoSelecionada.equals(this.TODAS)) {
        situacaoEnumList.add(SituacaoEnum.ABERTA);
        situacaoEnumList.add(SituacaoEnum.FECHADA);
        situacaoEnumList.add(SituacaoEnum.CANCELADA);
        situacaoEnumList.add(SituacaoEnum.MINUTA);
        situacaoEnumList.add(SituacaoEnum.RASCUNHO);
      }

      List<ExportacaoDemandaDTO> demandasList =
          demandaService.obterListaDemandasPorUnidadesResponsavelPeloAssunto(abrangenciaSelecionada, listaUnidadesFiltro, situacaoEnumList);

      exportador.setData(demandasList);
      
      String caminho = parametroService.obterParametroByNome(ParametroConstantes.CONFIG_RELATORIO_TEMP).getValor();

      String filename = "MigracaoDemanda.xls";
      File relatorio = exportador.export(caminho + filename);
      JavaScriptUtils.execute("$('#modalExportarDemanda').modal('hide')");
      return RequestUtils.download(relatorio, relatorio.getName());
    } catch (

    Exception e) {
      LOGGER.error(mensagem());
    }
    return null;
  }
  
  private void verificaDemandaList() {

    if (situacaoTransacaoSelecionada != null) {
      List<UnidadeDTO> listaUnidadesFiltro = new ArrayList<UnidadeDTO>();

      if (this.unidadeSelecionadaDTO != null) {
        listaUnidadesFiltro.add(this.unidadeSelecionadaDTO);
      } else {
        listaUnidadesFiltro.addAll(this.listaUnidadesDTOCombo);
      }

      List<SituacaoEnum> situacaoEnumList = new ArrayList<SituacaoEnum>();
      if (situacaoTransacaoSelecionada.equals(ABERTAS)) {
        situacaoEnumList.add(SituacaoEnum.ABERTA);
      } else if (situacaoTransacaoSelecionada.equals(FECHADAS)) {
        situacaoEnumList.add(SituacaoEnum.FECHADA);
      } else if (situacaoTransacaoSelecionada.equals(this.CANCELADAS)) {
        situacaoEnumList.add(SituacaoEnum.CANCELADA);
      } else if (situacaoTransacaoSelecionada.equals(this.RASCUNHADASMINUTADAS)) {
        situacaoEnumList.add(SituacaoEnum.MINUTA);
        situacaoEnumList.add(SituacaoEnum.RASCUNHO);
      } else if (situacaoTransacaoSelecionada.equals(this.TODAS)) {
        situacaoEnumList.add(SituacaoEnum.ABERTA);
        situacaoEnumList.add(SituacaoEnum.FECHADA);
        situacaoEnumList.add(SituacaoEnum.CANCELADA);
        situacaoEnumList.add(SituacaoEnum.MINUTA);
        situacaoEnumList.add(SituacaoEnum.RASCUNHO);
      }

      Boolean existeDemanda =
          demandaService.existeDemandasPorUnidadesResponsavelPeloAssunto(listaUnidadesFiltro, situacaoEnumList);

      if (!existeDemanda) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA010"));
        setListaDemandasCheia(false);
      } else {
        setListaDemandasCheia(true);
      }
      JavaScriptUtils.update("message");
      JavaScriptUtils.update("btnExportarDemanda");
    }
      JavaScriptUtils.update("message");
      JavaScriptUtils.update("btnExportarDemanda");
  }

  private String mensagem() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String texto = "";
    texto += "\n**************************************\n";
    texto += "Diretorio inexistente no servidor.\n";
    texto += "Data: " + sdf.format(new Date()) + "\n";
    texto += "**************************************\n";
    return texto;
  }

  public void handlerExibirModalSituacoes() {
    isListaDemandasCheia = true;
    this.situacaoTransacaoSelecionada = null;
    verificaDemandaList();
  }

  /**
   * Getters and Setters.
   */

  /**
   * Obter o valor padrão.
   * 
   * @return the relacaoAssuntosDTO
   */
  public RelacaoAssuntosDTO getRelacaoAssuntosDTO() {
    return this.relacaoAssuntosDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param relacaoAssuntosDTO
   *          the relacaoAssuntosDTO to set
   */
  public void setRelacaoAssuntosDTO(RelacaoAssuntosDTO relacaoAssuntosDTO) {
    this.relacaoAssuntosDTO = relacaoAssuntosDTO;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the relacaoAssuntosDTOList
   */
  public List<RelacaoAssuntosDTO> getRelacaoAssuntosDTOList() {
    return this.relacaoAssuntosDTOList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param relacaoAssuntosDTOList
   *          the relacaoAssuntosDTOList to set
   */
  public void setRelacaoAssuntosDTOList(List<RelacaoAssuntosDTO> relacaoAssuntosDTOList) {
    this.relacaoAssuntosDTOList = relacaoAssuntosDTOList;
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
   * @return the listaAssuntos
   */
  public List<Assunto> getListaAssuntos() {
    return this.listaAssuntos;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaAssuntos
   *          the listaAssuntos to set
   */
  public void setListaAssuntos(List<Assunto> listaAssuntos) {
    this.listaAssuntos = listaAssuntos;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaUnidadesDTO
   */
  public List<UnidadeDTO> getListaUnidadesDTO() {
    return this.listaUnidadesDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaUnidadesDTO
   *          the listaUnidadesDTO to set
   */
  public void setListaUnidadesDTO(List<UnidadeDTO> listaUnidadesDTO) {
    this.listaUnidadesDTO = listaUnidadesDTO;
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

  /**
   * Obter o valor padrão.
   * 
   * @return the listaTodosAssuntos
   */
  public List<Assunto> getListaTodosAssuntos() {
    return this.listaTodosAssuntos;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaTodosAssuntos
   *          the listaTodosAssuntos to set
   */
  public void setListaTodosAssuntos(List<Assunto> listaTodosAssuntos) {
    this.listaTodosAssuntos = listaTodosAssuntos;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the fluxosAssuntosDemDefinList
   */
  public List<FluxoAssunto> getFluxosAssuntosDemDefinList() {
    return this.fluxosAssuntosDemDefinList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param fluxosAssuntosDemDefinList
   *          the fluxosAssuntosDemDefinList to set
   */
  public void setFluxosAssuntosDemDefinList(List<FluxoAssunto> fluxosAssuntosDemDefinList) {
    this.fluxosAssuntosDemDefinList = fluxosAssuntosDemDefinList;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the tamanhoDTOList
   */
  public String getTamanhoDTOList() {
    this.tamanhoDTOList = "0";
    if ((this.relacaoAssuntosDTOList != null) && !this.relacaoAssuntosDTOList.isEmpty()) {
      this.tamanhoDTOList = String.valueOf((this.relacaoAssuntosDTOList.size()));
    }
    return this.tamanhoDTOList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param tamanhoDTOList
   *          the tamanhoDTOList to set
   */
  public void setTamanhoDTOList(String tamanhoDTOList) {
    this.tamanhoDTOList = tamanhoDTOList;
  }

  public List<SelectItem> getSituacaoTransacaoList() {
    List<SelectItem> situacoes = new ArrayList<SelectItem>();

    situacoes.add(new SelectItem(ABERTAS, "Abertas"));
    situacoes.add(new SelectItem(FECHADAS, "Fechadas"));
    situacoes.add(new SelectItem(CANCELADAS, "Canceladas"));
    situacoes.add(new SelectItem(RASCUNHADASMINUTADAS, "Rascunhadas/Minutadas"));
    situacoes.add(new SelectItem(TODAS, "Todas"));

    return situacoes;
  }

  public String getSituacaoTransacaoSelecionada() {
    return situacaoTransacaoSelecionada;
  }

  public void setSituacaoTransacaoSelecionada(String situacaoTransacaoSelecionada) {
    this.situacaoTransacaoSelecionada = situacaoTransacaoSelecionada;
  }

  public boolean getIsListaDemandasCheia() {
    return isListaDemandasCheia;
  }

  public void setListaDemandasCheia(boolean isListaDemandasCheia) {
    this.isListaDemandasCheia = isListaDemandasCheia;
  }

}
