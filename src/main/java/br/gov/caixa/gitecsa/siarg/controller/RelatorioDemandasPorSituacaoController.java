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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioDemandasPorSituacaoDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.comparator.RelatorioDemandasPorSituacaoDTOComparator;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.DemandaPorSituacaoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class RelatorioDemandasPorSituacaoController extends BaseController implements Serializable {

	/** Serial. */
	private static final long serialVersionUID = 1l;

	@Inject
	private DemandaService demandaService;

	@Inject
	private DemandaPorSituacaoService demandaPorSituacaoService;

	@Inject
	private FeriadoService feriadoService;

	/** Variável de Classe. */
	private List<UnidadeDTO> listaUnidadesDTOCombo;

	private List<String> listaUnidadesStringCombo;

	private Abrangencia abrangenciaSelecionada;

	private UnidadeDTO unidadeSelecionadaDTO;

	private String unidadeSelecionadaString;

	private List<RelatorioPeriodoPorAssuntoDTO> relatorioPeriodoList;

	private List<RelatorioDemandasPorSituacaoDTO> relatorioDemandasPorSituacaoList;

	HashMap<String, RelatorioDemandasPorSituacaoDTO> mapDemandasPorUnidade;

	private String dtInicial;
	private String dtFinal;

	private RelatorioDemandasPorSituacaoDTO demandaSelecionadaDetalhe;

	private List<DemandaDTO> demandasDetalheList;

	private Date dataIniPerInicial;
	private Date dataFimPerFinal;

	private List<Demanda> demandasPesquisadasList;

	private String tamanhoList;

	private List<DemandaDTO> demandasPesquisadasDetalhe;

	private Long totalAbertas;
	private Long totalAbertasDentroPrazo;
	private Long totalAbertasPrazoVencido;
	private Long totalFechadas;
	private Long totalFechadasForaPrazo;
	private Long totalReabertas;
	private Long totalCanceladas;
	private Long totalGeral;

	private static final String SITUACAO_ABERTA = "Aberta";
	private static final String SITUACAO_ABERTA_NO_PRAZO = "Aberta dentro do prazo";
	private static final String SITUACAO_ABERTA_FORA_PRAZO = "Aberta com prazo vencido";
	private static final String SITUACAO_FECHADA = "Fechada";
	private static final String SITUACAO_FECHADA_FORA_PRAZO = "Fechada fora do prazo";
	private static final String SITUACAO_CANCELADA = "Cancelada";
	private static final String SITUACAO_REABERTA = "Reaberta";

	private String detalheSituacao;
	private String detalheCaixaPostal;
	private String detalheQuantidade;

	private Boolean flagPesquisado;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		this.listaUnidadesStringCombo = new ArrayList<>();
		flagPesquisado = false;
		this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
		List<UnidadeDTO> listaUnidadesSessaoDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");

		this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia(listaUnidadesSessaoDTO);

		if (this.listaUnidadesDTOCombo.size() > 1) {
			listaUnidadesStringCombo.add("TODAS");
		}

		for (UnidadeDTO unidade : listaUnidadesDTOCombo) {
			this.listaUnidadesStringCombo.add(unidade.getSigla());
		}

		limparTotalizadores();

		inicializarDatas();
	}

	private void limparTotalizadores() {
		this.totalAbertas = 0L;
		this.totalAbertasDentroPrazo = 0L;
		this.totalAbertasPrazoVencido = 0L;
		this.totalFechadas = 0L;
		this.totalFechadasForaPrazo = 0L;
		this.totalReabertas = 0L;
		this.totalCanceladas = 0L;
		this.totalGeral = 0L;
	}

	private void inicializarDatas() {

		Date hoje = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMATO_DATA);
		this.dtFinal = sdf.format(hoje);
		this.dtInicial = sdf.format(DateUtils.addDays(hoje, -15));
	}

	/**
	 * Método obter lista de unidades por abrangencia.
	 */
	private List<UnidadeDTO> obterListaUnidadePorAbrangencia(List<UnidadeDTO> listaUnidadesSessaoDTO) {
		List<UnidadeDTO> temp = new ArrayList<>();
		for (UnidadeDTO dto : listaUnidadesSessaoDTO) {
			if (dto.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
				temp.add(dto);
			}
		}
		return temp;
	}

	public void pesquisar() {

		if (validarCampos()) {
			try {

				limparTotalizadores();
				demandaSelecionadaDetalhe = null;
				flagPesquisado = true;

				dataIniPerInicial = DateUtils.parseDateStrictly(this.dtInicial, DateUtil.FORMATO_DATA);
				dataFimPerFinal = DateUtils.parseDateStrictly(this.dtFinal, DateUtil.FORMATO_DATA);

				Long idUnidade = null;
				for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
					if (this.unidadeSelecionadaString.equals(unidadeDTO.getSigla())) {
						this.unidadeSelecionadaDTO = unidadeDTO;
						idUnidade = unidadeSelecionadaDTO.getId();
						break;
					} else {
						this.unidadeSelecionadaDTO = null;
						idUnidade = null;
					}
				}

				demandasPesquisadasList = demandaPorSituacaoService.obterDemandasPorUnidadePeriodo(idUnidade,
						abrangenciaSelecionada, dataIniPerInicial, dataFimPerFinal);
				mapDemandasPorUnidade = montarMapPorUnidade(demandasPesquisadasList, idUnidade);
				relatorioDemandasPorSituacaoList = new ArrayList<>();

				for (Map.Entry<String, RelatorioDemandasPorSituacaoDTO> hashDemandasPorUnidade : mapDemandasPorUnidade
						.entrySet()) {
					RelatorioDemandasPorSituacaoDTO dto = hashDemandasPorUnidade.getValue();
					relatorioDemandasPorSituacaoList.add(dto);
				}

			} catch (ParseException e) {
				this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
			} catch (AppException e) {
				getLogger().info(e);
			}
		}
	}

	private HashMap<String, RelatorioDemandasPorSituacaoDTO> montarMapPorUnidade(List<Demanda> demandasPesquisadasList,
			Long idUnidade) throws ParseException {

		List<Date> datasFeriadosList = this.feriadoService.obterListaDeDatasDosFeriados();
		//List<Demanda> demandasReabertasList = this.demandaService.obterDemandasReabertasPorUnidadePeriodo(idUnidade,abrangenciaSelecionada, dataIniPerInicial, dataFimPerFinal);
		List<Demanda> demandasFechadas = this.demandaService.obterDemandasFechadasTotal(idUnidade,abrangenciaSelecionada.getId(), dataIniPerInicial,dataFimPerFinal);
		List<Demanda> demandasReabertas = this.demandaService.obterListaDemandasReabertasTotal(idUnidade,dataIniPerInicial, dataFimPerFinal);

		Iterator<Demanda> listaSemFechadas = demandasPesquisadasList.iterator();

		/** Remove demandas fechadas da lista principal */
		while (listaSemFechadas.hasNext()) {
			if (listaSemFechadas.next().getSituacao().equals(SituacaoEnum.FECHADA)) {
				listaSemFechadas.remove();
			}
		}

		/** Adiciona demandas fechadas de uma consulta auxiliar */
		demandasPesquisadasList.addAll(demandasFechadas);
		mapDemandasPorUnidade = new HashMap<>();
		for (Demanda demanda : demandasPesquisadasList) {

			if (demanda.getSituacao().equals(SituacaoEnum.ABERTA)) {
				// CaixaPostal caixaPostal = demanda.getCaixaPostalResponsavel();

				String caixaPostalResponsavel = demanda.getCaixaPostalResponsavel().getSigla();

				if (!mapDemandasPorUnidade.containsKey(caixaPostalResponsavel)) {
					RelatorioDemandasPorSituacaoDTO relatorioDemandas = new RelatorioDemandasPorSituacaoDTO();
					relatorioDemandas.setCaixaPostalEnvolvida(caixaPostalResponsavel);
					mapDemandasPorUnidade.put(caixaPostalResponsavel, relatorioDemandas);
				}

				RelatorioDemandasPorSituacaoDTO relatorioDemandas = mapDemandasPorUnidade.get(caixaPostalResponsavel);

				relatorioDemandas.setQtdAbertas(relatorioDemandas.getQtdAbertas() + 1);
				demanda.setSituacaoDemandaRelatorio(SITUACAO_ABERTA);
				// totalizador no tfoot
				this.totalAbertas += 1;
				relatorioDemandas.getDemandasList().add(demanda);

				Integer situacaoPrazo = demandaService.getPrazoSituacaoVencimento(demanda, datasFeriadosList);

				// Criar um clone dessa demanda antes de adicionar na lista
				Demanda demandaCopia = SerializationUtils.clone(demanda);

				// Dentro do prazo
				if (situacaoPrazo <= 0) {
					demandaCopia.setSituacaoDemandaRelatorio(SITUACAO_ABERTA_NO_PRAZO);
					relatorioDemandas.setQtdAbertasDentroPrazo(relatorioDemandas.getQtdAbertasDentroPrazo() + 1);
					this.totalAbertasDentroPrazo++;
				} else {
					demandaCopia.setSituacaoDemandaRelatorio(SITUACAO_ABERTA_FORA_PRAZO);
					relatorioDemandas.setQtdAbertasPrazoVencido(relatorioDemandas.getQtdAbertasPrazoVencido() + 1);
					this.totalAbertasPrazoVencido++;
				}
				relatorioDemandas.getDemandasList().add(demandaCopia);

			} else if (demanda.getSituacao().equals(SituacaoEnum.FECHADA)) {
				// CaixaPostal caixaPostal =
				// demanda.getFluxosDemandasAtivoSemFluxoDemandanteList().iterator().next().getCaixaPostal();

				String caixaPostalResponsavel = demanda.getFluxosDemandasList().get(0).getCaixaPostal().getSigla();

				if (!mapDemandasPorUnidade.containsKey(caixaPostalResponsavel)) {
					RelatorioDemandasPorSituacaoDTO relatorioDemandas = new RelatorioDemandasPorSituacaoDTO();
					relatorioDemandas.setCaixaPostalEnvolvida(caixaPostalResponsavel);
					mapDemandasPorUnidade.put(caixaPostalResponsavel, relatorioDemandas);
				}
				RelatorioDemandasPorSituacaoDTO relatorioDemandas = mapDemandasPorUnidade.get(caixaPostalResponsavel);

				relatorioDemandas.setQtdFechadas(relatorioDemandas.getQtdFechadas() + 1);
				demanda.setSituacaoDemandaRelatorio(SITUACAO_FECHADA);
				// totalizador no tfoot
				this.totalFechadas += 1;
				relatorioDemandas.getDemandasList().add(demanda);
				Atendimento atendimento = new Atendimento();
				atendimento = demandaService.consultaAtendimento(demanda.getId());

				if (!demandaService.isDemandaFechadaNoPrazoSemConsulta(atendimento, datasFeriadosList)) {

					Demanda demandaCopia = SerializationUtils.clone(demanda);

					relatorioDemandas.setQtdFechadasForaDoPrazo(relatorioDemandas.getQtdFechadasForaDoPrazo() + 1);

					// totalizador no tfoot
					this.totalFechadasForaPrazo += 1;

					demandaCopia.setSituacaoDemandaRelatorio(SITUACAO_FECHADA_FORA_PRAZO);
					relatorioDemandas.getDemandasList().add(demandaCopia);

				}

			}

			else if (demanda.getSituacao().equals(SituacaoEnum.CANCELADA)) {

				String caixaPostalResponsavel = demanda.getCaixaPostalResponsavel().getSigla();

				if (!mapDemandasPorUnidade.containsKey(caixaPostalResponsavel)) {
					RelatorioDemandasPorSituacaoDTO relatorioDemandas = new RelatorioDemandasPorSituacaoDTO();
					relatorioDemandas.setCaixaPostalEnvolvida(caixaPostalResponsavel);
					mapDemandasPorUnidade.put(caixaPostalResponsavel, relatorioDemandas);
				}

				RelatorioDemandasPorSituacaoDTO relatorioDemandas = mapDemandasPorUnidade.get(caixaPostalResponsavel);

				relatorioDemandas.setQtdCanceladas(relatorioDemandas.getQtdCanceladas() + 1);
				demanda.setSituacaoDemandaRelatorio(SITUACAO_CANCELADA);
				// totalizador no tfoot
				this.totalCanceladas += 1;

				relatorioDemandas.getDemandasList().add(demanda);

			}

		}

		/**Setando demandas reabertas*/
		for (Demanda reaberta : demandasReabertas) {
			String caixaPostalResponsavel = reaberta.getFluxosDemandasList().get(0).getCaixaPostal().getSigla();

			if (!mapDemandasPorUnidade.containsKey(caixaPostalResponsavel)) {
				RelatorioDemandasPorSituacaoDTO relatorioDemandas = new RelatorioDemandasPorSituacaoDTO();
				relatorioDemandas.setCaixaPostalEnvolvida(caixaPostalResponsavel);
				mapDemandasPorUnidade.put(caixaPostalResponsavel, relatorioDemandas);
			}

			RelatorioDemandasPorSituacaoDTO relatorioDemandas = mapDemandasPorUnidade.get(caixaPostalResponsavel);

			relatorioDemandas.setQtdReabertas(relatorioDemandas.getQtdReabertas() + 1);
			// totalizador no tfoot
			this.totalReabertas += 1;

			Demanda demandaCopia = new Demanda();

			demandaCopia = SerializationUtils.clone(reaberta);

			// Criar um clone dessa demanda antes de adicionar na lista
			demandaCopia.setSituacaoDemandaRelatorio(SITUACAO_REABERTA);
			relatorioDemandas.getDemandasList().add(demandaCopia);

		}

		this.totalGeral = this.totalAbertas + this.totalCanceladas + this.totalFechadas;
		return mapDemandasPorUnidade;
	}

	public void exibeDemandasSelecionadas(RelatorioDemandasPorSituacaoDTO item, String situacao) {
		demandaSelecionadaDetalhe = item;
		List<Demanda> demandaAuxList = demandaSelecionadaDetalhe.getDemandasList();
		List<Demanda> listASerEnviada = new ArrayList<>();
		for (Demanda demanda : demandaAuxList) {
			if (situacao.equals(demanda.getSituacaoDemandaRelatorio())) {
				listASerEnviada.add(demanda);
			}
		}

		this.detalheSituacao = situacao;
		this.detalheCaixaPostal = item.getCaixaPostalEnvolvida();
		this.detalheQuantidade = ((Integer) listASerEnviada.size()).toString();

		montarDemandaDTO(listASerEnviada);
	}

	private List<DemandaDTO> montarDemandaDTO(List<Demanda> demandaAuxList) {
		List<DemandaDTO> demandaDTOList = new ArrayList<>();
		demandasPesquisadasDetalhe = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(demandaAuxList);
		return demandaDTOList;
	}

	private boolean validarCampos() {

		if (StringUtils.isBlank(this.dtInicial) || StringUtils.isBlank(this.dtFinal)) {
			this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
			return Boolean.FALSE;
		}

		try {
			Date dtIniPerInicial = DateUtils.parseDateStrictly(this.dtInicial, DateUtil.FORMATO_DATA);
			Date dtFimPerFinal = DateUtils.parseDateStrictly(this.dtFinal, DateUtil.FORMATO_DATA);

			if (this.unidadeSelecionadaString.equals("TODAS")) {

				if (DateUtil.calculaDiferencaEntreDatasEmDias(dtFimPerFinal, dtIniPerInicial) > 30) {
					this.facesMessager.addMessageError(MensagemUtil.obterMensagem("relatorio.demanda.por.situacao"));
					return Boolean.FALSE;
				}

			}

			if (dtFimPerFinal.before(dtIniPerInicial)) {
				this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
				return Boolean.FALSE;
			}
		} catch (ParseException e) {
			this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	public void alterarUnidadeSelecionada() {
		this.pesquisar();
	}

	public String obterAssuntoConcatenado(String assuntoCompleto) {
		int tamanho = 35;
		if (assuntoCompleto.length() > tamanho) {
			String textoConcatenado = "...";
			textoConcatenado += assuntoCompleto.substring(assuntoCompleto.length() - tamanho - 3,
					assuntoCompleto.length());
			return textoConcatenado;
		} else {
			return assuntoCompleto;
		}
	}

	public String obterTituloConcatenado(String tituloCompleto) {
		if (tituloCompleto.length() > 35) {
			return tituloCompleto.substring(0, 32) + "...";
		} else {
			return tituloCompleto;
		}
	}

	/**
	 * Geração do Relatório em Excel - paisagem.
	 */
	public StreamedContent downloadExcelResultado() throws JRException {
		try {

			String nomeUnidade = "";
			if (this.unidadeSelecionadaDTO != null) {
				nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
			} else {
				nomeUnidade = "Todas";
			}

			HashMap<String, Object> params = new HashMap<>();

			params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
			params.put("DATA_INICIO", this.dataIniPerInicial);
			params.put("DATA_FIM", this.dataFimPerFinal);
			params.put("NOME_SISTEMA", MensagemUtil.obterMensagem("geral.nomesistema") + " - "
					+ MensagemUtil.obterMensagem("geral.subnomesistema"));

			InputStream jasper = this.getClass().getClassLoader().getResourceAsStream(
					String.format("%sRelatorioDemandaPorSituacaoResultadoXLS.jrxml", Constantes.REPORT_BASE_DIR));

			RelatorioDemandasPorSituacaoDTOComparator relatorioDemandasComparator = new RelatorioDemandasPorSituacaoDTOComparator();

			Collections.sort(this.relatorioDemandasPorSituacaoList, relatorioDemandasComparator);

			return JasperReportUtils.runXLS(jasper, "RelatorioDemandaPorSituacao", params,
					this.relatorioDemandasPorSituacaoList);

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
			HashMap<String, Object> params = new HashMap<>();

			params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
			params.put("DATA_INICIO", this.dataIniPerInicial);
			params.put("DATA_FIM", this.dataFimPerFinal);
			params.put("NOME_SISTEMA", MensagemUtil.obterMensagem("geral.nomesistema") + " - "
					+ MensagemUtil.obterMensagem("geral.subnomesistema"));

			InputStream jasper = this.getClass().getClassLoader().getResourceAsStream(
					String.format("%sRelatorioDemandaPorSituacaoXLS.jrxml", Constantes.REPORT_BASE_DIR));

			return JasperReportUtils.runXLS(jasper, "RelatorioDemandaPorSituacao", params,
					this.demandasPesquisadasDetalhe);

		} catch (IOException e) {
			LOGGER.info(e);
			return null;
		}
	}

	public String getTamanhoList() {
		this.tamanhoList = "0";
		if ((this.relatorioDemandasPorSituacaoList != null) && !this.relatorioDemandasPorSituacaoList.isEmpty()) {
			this.tamanhoList = String.valueOf((this.relatorioDemandasPorSituacaoList.size()));
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
	 *            the unidadeSelecionadaDTO to set
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
	 *            the abrangenciaSelecionada to set
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
	 *            the listaUnidadesDTOCombo to set
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
		return dtInicial;
	}

	public void setDtIniPerInicial(String dtIniPerInicial) {
		this.dtInicial = dtIniPerInicial;
	}

	public String getDtFimPerFinal() {
		return dtFinal;
	}

	public void setDtFimPerFinal(String dtFimPerFinal) {
		this.dtFinal = dtFimPerFinal;
	}

	public RelatorioDemandasPorSituacaoDTO getDemandaSelecionadaDetalhe() {
		return demandaSelecionadaDetalhe;
	}

	public void setDemandaSelecionadaDetalhe(RelatorioDemandasPorSituacaoDTO demandaSelecionadaDetalhe) {
		this.demandaSelecionadaDetalhe = demandaSelecionadaDetalhe;
	}

	public List<DemandaDTO> getDemandasDetalheList() {
		return demandasDetalheList;
	}

	public void setDemandasDetalheList(List<DemandaDTO> demandasDetalheList) {
		this.demandasDetalheList = demandasDetalheList;
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
	 * @param listaUnidadesStringCombo
	 *            the listaUnidadesStringCombo to set
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
	 * @param unidadeSelecionadaString
	 *            the unidadeSelecionadaString to set
	 */
	public void setUnidadeSelecionadaString(String unidadeSelecionadaString) {
		this.unidadeSelecionadaString = unidadeSelecionadaString;
	}

	/**
	 * @return the dtInicial
	 */
	public String getDtInicial() {
		return dtInicial;
	}

	/**
	 * @param dtInicial
	 *            the dtInicial to set
	 */
	public void setDtInicial(String dtInicial) {
		this.dtInicial = dtInicial;
	}

	/**
	 * @return the dtFinal
	 */
	public String getDtFinal() {
		return dtFinal;
	}

	/**
	 * @param dtFinal
	 *            the dtFinal to set
	 */
	public void setDtFinal(String dtFinal) {
		this.dtFinal = dtFinal;
	}

	/**
	 * @return the relatorioDemandasPorAssuntoList
	 */
	public List<RelatorioDemandasPorSituacaoDTO> getRelatorioDemandasPorSituacaoList() {
		return relatorioDemandasPorSituacaoList;
	}

	/**
	 * @param relatorioDemandasPorAssuntoList
	 *            the relatorioDemandasPorAssuntoList to set
	 */
	public void setRelatorioDemandasPorSituacaoList(
			List<RelatorioDemandasPorSituacaoDTO> relatorioDemandasPorSituacaoList) {
		this.relatorioDemandasPorSituacaoList = relatorioDemandasPorSituacaoList;
	}

	/**
	 * @return the demandasPesquisadasList
	 */
	public List<Demanda> getDemandasPesquisadasList() {
		return demandasPesquisadasList;
	}

	/**
	 * @param demandasPesquisadasList
	 *            the demandasPesquisadasList to set
	 */
	public void setDemandasPesquisadasList(List<Demanda> demandasPesquisadasList) {
		this.demandasPesquisadasList = demandasPesquisadasList;
	}

	/**
	 * @return the mapDemandasPorUnidade
	 */
	public Map<String, RelatorioDemandasPorSituacaoDTO> getMapDemandasPorUnidade() {
		return mapDemandasPorUnidade;
	}

	/**
	 * @param mapDemandasPorUnidade
	 *            the mapDemandasPorUnidade to set
	 */
	public void setMapDemandasPorUnidade(Map<String, RelatorioDemandasPorSituacaoDTO> mapDemandasPorUnidade) {
		this.mapDemandasPorUnidade = (HashMap<String, RelatorioDemandasPorSituacaoDTO>) mapDemandasPorUnidade;
	}

	/**
	 * @return the demandasPesquisadasDetalhe
	 */
	public List<DemandaDTO> getDemandasPesquisadasDetalhe() {
		return demandasPesquisadasDetalhe;
	}

	/**
	 * @param demandasPesquisadasDetalhe
	 *            the demandasPesquisadasDetalhe to set
	 */
	public void setDemandasPesquisadasDetalhe(List<DemandaDTO> demandasPesquisadasDetalhe) {
		this.demandasPesquisadasDetalhe = demandasPesquisadasDetalhe;
	}

	/**
	 * @return the totalAbertas
	 */
	public Long getTotalAbertas() {
		return totalAbertas;
	}

	/**
	 * @param totalAbertas
	 *            the totalAbertas to set
	 */
	public void setTotalAbertas(Long totalAbertas) {
		this.totalAbertas = totalAbertas;
	}

	/**
	 * @return the totalFechadas
	 */
	public Long getTotalFechadas() {
		return totalFechadas;
	}

	/**
	 * @param totalFechadas
	 *            the totalFechadas to set
	 */
	public void setTotalFechadas(Long totalFechadas) {
		this.totalFechadas = totalFechadas;
	}

	/**
	 * @return the totalCanceladas
	 */
	public Long getTotalCanceladas() {
		return totalCanceladas;
	}

	/**
	 * @param totalCanceladas
	 *            the totalCanceladas to set
	 */
	public void setTotalCanceladas(Long totalCanceladas) {
		this.totalCanceladas = totalCanceladas;
	}

	/**
	 * @return the totalGeral
	 */
	public Long getTotalGeral() {
		return totalGeral;
	}

	/**
	 * @param totalGeral
	 *            the totalGeral to set
	 */
	public void setTotalGeral(Long totalGeral) {
		this.totalGeral = totalGeral;
	}

	/**
	 * @return the totalReabertas
	 */
	public Long getTotalReabertas() {
		return totalReabertas;
	}

	/**
	 * @param totalReabertas
	 *            the totalReabertas to set
	 */
	public void setTotalReabertas(Long totalReabertas) {
		this.totalReabertas = totalReabertas;
	}

	public String getDetalheSituacao() {
		return detalheSituacao;
	}

	public void setDetalheSituacao(String detalheSituacao) {
		this.detalheSituacao = detalheSituacao;
	}

	public String getDetalheCaixaPostal() {
		return detalheCaixaPostal;
	}

	public void setDetalheCaixaPostal(String detalheCaixaPostal) {
		this.detalheCaixaPostal = detalheCaixaPostal;
	}

	public String getDetalheQuantidade() {
		return detalheQuantidade;
	}

	public void setDetalheQuantidade(String detalheQuantidade) {
		this.detalheQuantidade = detalheQuantidade;
	}

	public Long getTotalFechadasForaPrazo() {
		return totalFechadasForaPrazo;
	}

	public void setTotalFechadasForaPrazo(Long totalFechadasForaPrazo) {
		this.totalFechadasForaPrazo = totalFechadasForaPrazo;
	}

	public Long getTotalAbertasDentroPrazo() {
		return totalAbertasDentroPrazo;
	}

	public void setTotalAbertasDentroPrazo(Long totalAbertasDentroPrazo) {
		this.totalAbertasDentroPrazo = totalAbertasDentroPrazo;
	}

	public Long getTotalAbertasPrazoVencido() {
		return totalAbertasPrazoVencido;
	}

	public void setTotalAbertasPrazoVencido(Long totalAbertasPrazoVencido) {
		this.totalAbertasPrazoVencido = totalAbertasPrazoVencido;
	}

}
