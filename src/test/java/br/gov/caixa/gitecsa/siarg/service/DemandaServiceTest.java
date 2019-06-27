package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.UploadedFile;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dto.AnaliticoDemandantesDemandadosDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandantePorSubrodinacaoDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandasAguardandoUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.dto.DemandasEmAbertoDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoDemandaDTO;
import br.gov.caixa.gitecsa.siarg.dto.KeyGroupValues;
import br.gov.caixa.gitecsa.siarg.dto.KeyGroupValuesCollection;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioGeralVisaoSuegPorUnidadesDTO;
import br.gov.caixa.gitecsa.siarg.email.EmailService;
import br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Parametro;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AbrangenciaService;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.AtendimentoService;
import br.gov.caixa.gitecsa.siarg.service.CaixaPostalService;
import br.gov.caixa.gitecsa.siarg.service.DemandaContratoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import br.gov.caixa.gitecsa.siarg.service.FluxoDemandaService;
import br.gov.caixa.gitecsa.siarg.service.ParametroService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;
import br.gov.caixa.gitecsa.siarg.util.Auxiliar;
import br.gov.caixa.gitecsa.siarg.util.ByteArrayUploadedFile;
import br.gov.caixa.gitecsa.siarg.ws.model.DemandaAbertaDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

public class DemandaServiceTest extends Auxiliar {

	@InjectMocks
	private DemandaService demandaService;
	@Mock
	private AtendimentoService atendimentoService;
	@Mock
	private AssuntoService assuntoService;
	@Mock
	private FeriadoService feriadoService;
	@Mock
	private DemandaContratoService demandaContratoService;
	@Mock
	private EmailService emailService;
	@Mock
	private AbrangenciaService abrangenciaService;
	@Mock
	private CaixaPostalService caixaPostalService;
	@Mock
	private FluxoDemandaService fluxoDemandaService;
	@Mock
	private UnidadeService unidadeService;
	@Mock
	private ParametroService parametroService;

	@Mock
	private DemandaDAO demandaDAO;

	@Mock
	private Demanda demanda;
	@Mock
	private Atendimento atendimento;
	@Mock
	private CaixaPostal caixaPostal;
	@Mock
	private Abrangencia abrangencia;
	@Mock
	private Parametro parametro;
	@Mock
	private Assunto assunto;
	@Mock
	private UnidadeDTO unidadeDTO;

	@Mock
	private Logger logger;

	private List<Demanda> demandas;
	private List<CaixaPostal> caixasPostais;
	private List<Assunto> assuntos;
	private List<Unidade> unidades;
	private List<UnidadeDTO> unidadeDTOs;
	private List<SituacaoEnum> situacaoEnums;
	private List<Date> dates;
	private UploadedFile uploadedFile;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.demanda = criarDemanda(1L);
		this.atendimento = criarAtendimento(1L);
		this.caixaPostal = criarCaixaPostal(1L);
		this.abrangencia = criarAbrangencia(1L);
		this.unidadeDTO = criarUnidadeDTO(1L);

		this.demandas = new ArrayList<Demanda>();
		demandas.add(this.demanda);
		demandas.add(criarDemanda(2L));
		demandas.add(criarDemanda(3L));

		this.caixasPostais = new ArrayList<CaixaPostal>();
		caixasPostais.add(this.caixaPostal);
		caixasPostais.add(criarCaixaPostal(4L));
		caixasPostais.add(criarCaixaPostal(5L));

		this.assuntos = new ArrayList<Assunto>();
		assuntos.add(criarAssunto(1L));
		assuntos.add(criarAssunto(2L));

		this.unidades = new ArrayList<Unidade>();
		unidades.add(criarUnidade(1L));
		unidades.add(criarUnidade(2L));

		this.unidadeDTOs = new ArrayList<UnidadeDTO>();
		unidadeDTOs.add(this.unidadeDTO);
		unidadeDTOs.add(criarUnidadeDTO(2L));
		unidadeDTOs.add(criarUnidadeDTO(3L));

		this.situacaoEnums = new ArrayList<SituacaoEnum>();
		situacaoEnums.add(SituacaoEnum.ABERTA);
		situacaoEnums.add(SituacaoEnum.CANCELADA);

		this.parametro = criarParametro(1L);
		this.assunto = criarAssunto(1L);

		this.dates = new ArrayList<Date>();
		Calendar c = Calendar.getInstance();

		c.setTime(new Date());
		dates.add(c.getTime());
		c.add(Calendar.DATE, -5);
		dates.add(c.getTime());
		c.add(Calendar.DATE, -3);
		dates.add(c.getTime());

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		for (int i = 0; i < 10; i++) {
			output.write((byte) (Math.random() * 100));
		}
		this.uploadedFile = new ByteArrayUploadedFile(output.toByteArray(), "TestFile", "text/plain");

		try {
			when(demandaDAO.update(any(Demanda.class))).thenReturn(demanda);
			when(abrangenciaService.obterAbrangenciaPorDemanda(anyLong())).thenReturn(abrangencia);
			when(caixaPostalService.update(any(CaixaPostal.class))).thenReturn(caixaPostal);
			when(feriadoService.adicionarDiasUteis(any(Date.class), anyInt())).thenReturn(new Date());
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testObterListaDemandasPrioridadesDTO() {
		when(demandaDAO.obterListaDemandasPrioridades(anyLong(), any(SituacaoEnum.class))).thenReturn(demandas);
		when(demandaDAO.obterListaDemandasExternasPorAendimentoECaixaPostal(anyLong(), any(SituacaoEnum.class))).thenReturn(demandas);
		when(assuntoService.obterArvoreAssuntos(any(Assunto.class), anyList())).thenReturn(demanda.getAssunto().getArvoreCompleta());
		when(atendimentoService.obterUltimoAtendimentoPorDemandaCaixaPostalExterna(anyLong())).thenReturn(atendimento);
		when(feriadoService.calcularNumeroDiasUteisPorDataEPrazo(any(Date.class), anyInt(), anyList())).thenReturn(demanda.getFluxosDemandasList().get(0).getPrazo());

		List<DemandaDTO> demandaDTOs = demandaService.obterListaDemandasPrioridadesDTO(caixaPostal.getId(), null, demanda.getSituacao(), anyList(), demanda.getCor(), anyList());
		assertNotNull(demandaDTOs);
		for (DemandaDTO demandaDTO : demandaDTOs) {
			assertTrue(demandaDTO.getFlagConsulta());
			assertFalse(demandaDTO.getFlagDemandaFilha());
			assertFalse(demandaDTO.getFlagRascunho());
		}
	}

	@Test
	public void testObterListaDemaisDemandasDTO() {
		when(demandaDAO.obterListaDemaisDemandas(anyLong(), any(EnvolvimentoEnum.class), any(SituacaoEnum.class))).thenReturn(demandas);
		when(atendimentoService.obterUltimoAtendimentoPorDemandaCaixaPostalExterna(anyLong())).thenReturn(atendimento);

		List<DemandaDTO> demandaDTOs = demandaService.obterListaDemaisDemandasDTO(caixaPostal.getId(), EnvolvimentoEnum.DEMANDANTE, demanda.getSituacao(), new ArrayList<Date>(),
				demanda.getCor(), new ArrayList<Assunto>());
		assertNotNull(demandaDTOs);
		for (DemandaDTO demandaDTO : demandaDTOs) {
			assertTrue(demandaDTO.getFlagConsulta());
			assertFalse(demandaDTO.getFlagDemandaFilha());
			assertFalse(demandaDTO.getFlagRascunho());
		}
	}

	@Test
	public void testIsDemandaFechadaNoPrazoSemConsulta() {
		when(feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), new ArrayList<Date>())).thenReturn(5);
		assertTrue(demandaService.isDemandaFechadaNoPrazoSemConsulta(atendimento, new ArrayList<Date>()));

		when(feriadoService.calcularQtdDiasUteisEntreDatas(atendimento.getDataHoraRecebimento(), atendimento.getDataHoraAtendimento(), new ArrayList<Date>())).thenReturn(16);
		assertFalse(demandaService.isDemandaFechadaNoPrazoSemConsulta(atendimento, new ArrayList<Date>()));
	}

	@Test
	public void testSetColorInDemanda() {
		when(demandaDAO.findById(anyLong())).thenReturn(demanda);

		demandaService.setColorInDemanda(anyLong(), "#000000");
	}

	@Test
	public void testSalvar() throws Exception {
		when(feriadoService.adicionarDiasUteis(any(Date.class), anyInt())).thenReturn(new Date());
		when(assuntoService.obterArvoreAssuntos(any(Assunto.class))).thenReturn(demanda.getAssunto().getArvoreCompleta());
		when(abrangenciaService.obterAbrangenciaPorDemanda(anyLong())).thenReturn(abrangencia);
		System.setProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS, "src/test/resources/files/");

		demandaService.salvar(demanda, uploadedFile);
	}

	@Test
	public void testObterDiretorioAnexosPorAbrangencia() {
		when(abrangenciaService.obterAbrangenciaPorDemanda(anyLong())).thenReturn(criarAbrangencia(2L));
		System.setProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS_ABRANGENCIAS, "src/test/resources/files/");

		String dir = demandaService.obterDiretorioAnexosPorAbrangencia(demanda);
		if (StringUtils.isNotBlank(dir))
			assertEquals(dir, System.getProperty(Constantes.DIRETORIO_UPLOAD_ANEXOS_ABRANGENCIAS) + "Abrangencia2\\");
		else
			fail("Não encontrou diretório");
	}

	@Test
	public void testObterCaixaPostalComValoresDemandas() {
		demandaService.obterCaixaPostalComValoresDemandas(demanda.getCaixasPostaisObservadorList());
		for (CaixaPostal caixaPostal : demanda.getCaixasPostaisObservadorList()) {
			assertEquals("0", String.valueOf(caixaPostal.getTotalDemandasDaCaixaPostal()));
		}
	}

	@Test
	public void testSalvarRascunho() throws Exception {
		demandaService.salvarRascunho(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(),
				atendimento.getNomeUsuarioAtendimento());

		demanda.setSituacao(SituacaoEnum.RASCUNHO);
		demandaService.salvarRascunho(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(),
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testAtualizarRascunhoParaMinuta() throws Exception {
		demandaService.atualizarRascunhoParaMinuta(demanda, uploadedFile, caixasPostais, demanda.getMatriculaMinuta(), demanda.getNomeUsuarioMinuta());
	}

	@Test
	public void testEncaminharDemanda() throws Exception {
		when(assuntoService.obterArvoreAssuntos(any(Assunto.class))).thenReturn(demanda.getAssunto().getArvoreCompleta());

		demandaService.encaminharDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(),
				atendimento.getNomeUsuarioAtendimento());

		demanda.setSituacao(SituacaoEnum.MINUTA);
		demandaService.encaminharDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(),
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testEncaminharDemandaExterna() throws Exception {
		demandaService.encaminharDemandaExterna(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(),
				atendimento.getUnidadeExterna(), atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testQuestionarDemanda() throws Exception {
		demandaService.questionarDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal,
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testResponderDemanda() throws Exception {
		demandaService.responderDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal,
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testAtualizarDemanda() throws Exception {
		demandaService.atualizarDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal,
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testCancelar() throws Exception {
		demandaService.cancelar(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), caixaPostal, atendimento.getDescricao(), atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testReabrirDemanda() throws Exception {
		demandaService.reabrirDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal, atendimento.getMotivoReabertura(),
				atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testFecharDemanda() throws Exception {
		when(demandaDAO.findFilhosByIdPaiFetch(anyLong())).thenReturn(demandas);

		demandaService.fecharDemanda(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal,
				atendimento.getNomeUsuarioAtendimento());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConsultarDemandas() throws Exception {
		when(caixaPostalService.findByRangeId(anyList())).thenReturn(caixasPostais);

		List<Long> idsCaixaPostal = new ArrayList<Long>();
		for (CaixaPostal caixaPostal : caixasPostais) {
			idsCaixaPostal.add(caixaPostal.getId());
		}

		demandaService.consultarDemandas(demanda, uploadedFile, caixasPostais, atendimento.getMatricula(), atendimento.getDescricao(), caixaPostal,
				atendimento.getFluxoDemanda().getPrazo(), idsCaixaPostal, atendimento.getNomeUsuarioAtendimento());
	}

	@Test
	public void testRelatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas() {
		when(demandaDAO.relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(any(Date.class), any(Date.class), any(Unidade.class))).thenReturn(demandas);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<AnaliticoDemandantesDemandadosDTO> addDTOs = demandaService.relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(c.getTime(), new Date(), caixaPostal.getUnidade(),
				assuntos);
		for (AnaliticoDemandantesDemandadosDTO addDTO : addDTOs) {
			assertNotNull(addDTO);
			assertNotNull(addDTO.getUnidadeDemandada());
			assertNotNull(addDTO.getUnidadeDemandante());
			assertEquals("3", String.valueOf(addDTO.getQtdAberta()));
			assertEquals("0", String.valueOf(addDTO.getQtdFechada()));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRelatorioUnidadesDemandantesPorSubordinacao() throws Exception {
		when(demandaDAO.relatorioUnidadesDemandantesPorSubordinacao(any(Date.class), any(Date.class), any(Unidade.class), anyList(), any(Abrangencia.class))).thenReturn(demandas);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<DemandantePorSubrodinacaoDTO> dpsDTOs = demandaService.relatorioUnidadesDemandantesPorSubordinacao(c.getTime(), new Date(), caixaPostal.getUnidade(), assuntos,
				unidades, abrangencia);
		for (DemandantePorSubrodinacaoDTO dpsDTO : dpsDTOs) {
			assertNotNull(dpsDTO);
			assertNotNull(dpsDTO.getSubordinacao());
			assertNotNull(dpsDTO.getUnidadeDemandante());
			assertEquals("D", dpsDTO.getLetraSueg());
			assertEquals("3", String.valueOf(dpsDTO.getQtdDemandas()));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRelatorioGeralSuegConsolidadoPorUnidade() {
		when(demandaDAO.obterListaDemandasAbertasPorSuegPeriodoATratar(any(Abrangencia.class), any(Unidade.class), any(Date.class), any(Date.class), anyList()))
				.thenReturn(demandas);
		when(demandaDAO.obterListaDemandasAbertasPorSuegPeriodoRealizadas(any(Abrangencia.class), any(Unidade.class), any(Date.class), any(Date.class), anyList()))
				.thenReturn(demandas);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<RelatorioGeralVisaoSuegPorUnidadesDTO> rgvsuDTOs = demandaService.relatorioGeralSuegConsolidadoPorUnidade(abrangencia, caixaPostal.getUnidade(), c.getTime(),
				new Date(), unidades);
		for (RelatorioGeralVisaoSuegPorUnidadesDTO rgvsuDTO : rgvsuDTOs) {
			assertNotNull(rgvsuDTO);
			assertNotNull(rgvsuDTO.getUnidadeDemandante());
			assertEquals("3", String.valueOf(rgvsuDTO.getDemandasAbertasRealizadas()));
			assertEquals("0", String.valueOf(rgvsuDTO.getDemandasFechadasRealizadas()));
			assertEquals("1", String.valueOf(rgvsuDTO.getIdUnidadeDemandante()));
			assertEquals("3", String.valueOf(rgvsuDTO.getTotalDemandasRealizadas()));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testObterListaDemandasPorUnidadesResponsavelPeloAssunto() {
		demandas.get(0).getFluxosDemandasAtivoList().get(0).setOrdem(1);
		when(demandaDAO.obterListaDemandasPorUnidadesResponsavelPeloAssunto(anyList(), anyList())).thenReturn(demandas);

		List<ExportacaoDemandaDTO> exportacaoDemandaDTOs = demandaService.obterListaDemandasPorUnidadesResponsavelPeloAssunto(abrangencia, unidadeDTOs, situacaoEnums);
		int nrDemanda = 1;
		for (ExportacaoDemandaDTO exportacaoDemandaDTO : exportacaoDemandaDTOs) {
			assertEquals(Long.valueOf(nrDemanda), exportacaoDemandaDTO.getNumeroDemanda());
			nrDemanda += 1;
		}
		assertEquals(3, exportacaoDemandaDTOs.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExisteDemandasPorUnidadesResponsavelPeloAssunto() {
		when(demandaDAO.existeDemandasPorUnidadesResponsavelPeloAssunto(anyList(), anyList())).thenReturn(Boolean.TRUE);

		Boolean isExisteDemandas = demandaService.existeDemandasPorUnidadesResponsavelPeloAssunto(unidadeDTOs, situacaoEnums);
		assertTrue(isExisteDemandas);
	}

	@Test
	public void testAdicionarObservadorDemanda() {
		when(demandaDAO.findById(anyLong())).thenReturn(demanda);

		demandaService.adicionarObservadorDemanda(demanda.getId(), caixaPostal);
	}

	@Test
	public void testGetAgrupamentoCaixaPostal() {
		when(unidadeService.obterUnidadesECaixasPostaisPorTipo(any(Abrangencia.class), any(TipoUnidadeEnum.class), any(TipoUnidadeEnum.class))).thenReturn(unidades);
		when(parametroService.obterParametroByNome(anyString())).thenReturn(parametro);

		KeyGroupValuesCollection<CaixaPostal> caixaPostalCollection = demandaService.getAgrupamentoCaixaPostal(abrangencia, caixasPostais);
		for (KeyGroupValues<CaixaPostal> caixaPostalKeyGroupValues : caixaPostalCollection.getOrderedValues()) {
			assertEquals("Rede SIGLA", caixaPostalKeyGroupValues.getKey());
			for (CaixaPostal caixaPostal : caixaPostalKeyGroupValues.getValues()) {
				assertNotNull(caixaPostal.getId());
			}
		}
	}

	@Test
	public void testObterListaDemandasRelatorioAssuntoPeriodo() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<Date> dates = new ArrayList<Date>();
		dates.add(c.getTime());
		dates.add(new Date());

		when(demandaDAO.obterListaDemandasRelatorioAssuntoPeriodo(anyLong(), any(Date.class), any(Date.class), any(SituacaoEnum.class))).thenReturn(demandas);
		when(feriadoService.obterListaDeDatasDosFeriados()).thenReturn(dates);
		when(assuntoService.obterAssuntosFetchPai()).thenReturn(assuntos);
		when(atendimentoService.obterUltimoAtendimentoPorDemandaCaixaPostalExterna(anyLong())).thenReturn(atendimento);
		when(demandaDAO.findByIdFetchList(demandas)).thenReturn(demandas);

		List<DemandaDTO> demandaDTOs = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(demanda.getAssunto().getId(), c.getTime(), new Date(), demanda.getSituacao());
		testDemandaDTOs(demandaDTOs);

		demandaDTOs = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(demandas);
		testDemandaDTOs(demandaDTOs);
	}

	private void testDemandaDTOs(List<DemandaDTO> demandaDTOs) {
		for (DemandaDTO demandaDTO : demandaDTOs) {
			assertNotNull(demandaDTO.getNumero());
			assertEquals(demanda.getCor(), demandaDTO.getColorStatus());
			assertEquals(demanda.getSituacao(), demandaDTO.getSituacao());
		}

	}

	@Test
	public void testObterDemandasEmAbertoPorAssunto() {
		when(demandaDAO.obterDemandasEmAbertoPorAssunto(any(UnidadeDTO.class), any(Assunto.class), any(Date.class), any(Date.class), any(Abrangencia.class))).thenReturn(demandas);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<DemandasEmAbertoDTO> demandasEmAbertoDTOs = demandaService.obterDemandasEmAbertoPorAssunto(unidadeDTO, assunto, c.getTime(), new Date(), abrangencia, assuntos);
		for (DemandasEmAbertoDTO demandasEmAbertoDTO : demandasEmAbertoDTOs) {
			assertNotNull(demandasEmAbertoDTO.getNumeroDemanda());
			assertEquals(caixaPostal.getSigla(), demandasEmAbertoDTO.getAreaDemandada());
			assertEquals(DateUtil.formatDataPadrao(new Date()), demandasEmAbertoDTO.getDtPrazoVencimento());
		}
	}

	@Test
	public void testObterDemandasAguardandoUnidade() {
		when(demandaDAO.obterDemandasAguardandoUnidade(any(Date.class), any(Date.class), any(Abrangencia.class), anyLong())).thenReturn(demandas);
		when(atendimentoService.obterUltimoAtendimentoPorDemanda(anyLong())).thenReturn(atendimento);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -5);

		List<DemandasAguardandoUnidadeDTO> dauDTOs = demandaService.obterDemandasAguardandoUnidade(c.getTime(), new Date(), abrangencia, caixaPostal.getUnidade().getId(),
				assuntos);
		for (DemandasAguardandoUnidadeDTO dauDTO : dauDTOs) {
			assertEquals(demanda.getTitulo(), dauDTO.getTitulo());
			assertEquals(atendimento.getDataHoraRecebimento(), dauDTO.getUltimoEncaminhamento());
			assertEquals(demanda.getMatriculaDemandante(), dauDTO.getMatriculaDemandante());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPrazoSituacaoVencimento() {
		when(feriadoService.adicionarDiasUteis(any(Date.class), anyInt(), anyList())).thenReturn(new Date());

		Integer qtPrazo = demandaService.getPrazoSituacaoVencimento(demanda, dates);
		assertEquals("0", qtPrazo.toString());

		demanda.getFluxosDemandasAtivoList().get(0).setOrdem(3);
		qtPrazo = demandaService.getPrazoSituacaoVencimento(demanda, dates);
		assertEquals("1", qtPrazo.toString());
	}

	@Test
	public void testObterDemandasWSConsulta() {
		for (Demanda demanda : demandas) {
			for (FluxoDemanda fluxoDemanda : demanda.getFluxosDemandasList()) {
				fluxoDemanda.setOrdem(2);
			}
		}
		when(demandaDAO.obterDemandasWSConsulta(any(FiltrosConsultaDemandas.class))).thenReturn(demandas);
		when(assuntoService.obterAssuntosFetchPai()).thenReturn(assuntos);
		when(feriadoService.obterListaDeDatasDosFeriados()).thenReturn(dates);

		FiltrosConsultaDemandas filtrosConsultaDemandas = criarFiltrosConsultaDemandas();
		List<DemandaAbertaDTO> demandaAbertaDTOs = demandaService.obterDemandasWSConsulta(filtrosConsultaDemandas);
		for (DemandaAbertaDTO demandaAbertaDTO : demandaAbertaDTOs) {
			assertNotNull(demandaAbertaDTO.getAtendimentoList());
			assertNotNull(demandaAbertaDTO.getNumeroContratoList());
			assertEquals(filtrosConsultaDemandas.getCoUnidadeDemandada(), demandaAbertaDTO.getCgcUnidadeDemandante());
			assertEquals(demandas.get(0).getTextoDemanda(), demandaAbertaDTO.getConteudo());
		}
	}

}
