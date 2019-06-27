package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.ws.model.DemandaAbertaDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

public class DemandaRestServiceTest {
	
	@InjectMocks
	DemandaService demandaService;
	
	@Mock
	AssuntoService assuntoService;
	
	@Mock
	FeriadoService feriadoService;
	
	@Mock
	private Logger logger;
	
	@Mock
	private DemandaDAO demandaDAO;
	
	@Mock
	private AssuntoDAO assuntoDAO;
	
	@Mock
	private FeriadoDAO feriadoDAO;
	
	private FiltrosConsultaDemandas filtro;
	
	public List<Demanda> demandaList;
	
	private Demanda demanda;
	
	private CaixaPostal caixaPostalDemandante;
	
	private CaixaPostal caixaPostalResponsavel;
	
	private Unidade unidade;
	
	private DemandaContrato demandaContrato;
	
	List<DemandaContrato> demandaContratoList; 
	
	List<FluxoDemanda> fluxoList;
	
	List<Assunto> assuntoList;
	
	List<Atendimento> atendimentoList;
	
	List<Date> dateList;
	
	private Assunto assunto;
	
	private FluxoDemanda fluxoDemanda;
	
	private Atendimento atendimento;
	
	private Atendimento atendimento2;
	
	private Long idAbrangencia = 10L;
	private Date dataInicial = new Date();
	private Date dataFinal = new Date();
	private Integer aberta = 0;
	private Integer reaberta = 0;
	private Integer externa = 1;
	private Integer tipoConsulta = 1;
	private Integer conteudo = 60;

	private Long coAssunto = 1L;
	private String cpDemandante = "Teste";
	private String cpDemandada;

	private String cpResponsavelAssunto = "Teste1";
	private String cpResponsavelAtual = "Teste2";
	private Integer coUnidadeDemandante = 2;
	private Integer coUnidadeRespAssunto = 4;
	private Integer coUnidadeRespAtual = 5;

	@Before
	public void setUpBeforeClass() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		filtro = new FiltrosConsultaDemandas();

		this.filtro.setIdAbrangencia(idAbrangencia);
		this.filtro.setDataInicial(dataInicial);
		this.filtro.setDataFinal(dataFinal);
		this.filtro.setAberta(aberta);
		this.filtro.setReaberta(reaberta);
		this.filtro.setExterna(externa);
		this.filtro.setTipoConsulta(tipoConsulta);
		this.filtro.setConteudo(conteudo);
		this.filtro.setCoAssunto(coAssunto);
		this.filtro.setCpDemandada(cpDemandada);
		this.filtro.setCpDemandante(cpDemandante);
		this.filtro.setCpResponsavelAssunto(cpResponsavelAssunto);
		this.filtro.setCpResponsavelAtual(cpResponsavelAtual);
		this.filtro.setCoUnidadeDemandante(coUnidadeDemandante);
		this.filtro.setCoUnidadeRespAssunto(coUnidadeRespAssunto);
		this.filtro.setCoUnidadeRespAtual(coUnidadeRespAtual);
		this.filtro.setCpResponsavelAtual(cpResponsavelAtual);
		this.filtro.setPrazoDemanda(10);
		
		unidade = new Unidade();
		unidade.setCgcUnidade(12);
		unidade.setTipoUnidade(TipoUnidadeEnum.ARROBA_EXTERNA);
		
		dateList = new ArrayList<>();
		dateList.add(new Date());
		
		caixaPostalDemandante = new CaixaPostal();
		caixaPostalDemandante.setSigla("CEGOV");
		caixaPostalDemandante.setUnidade(unidade);
		
		caixaPostalResponsavel = new CaixaPostal();
		caixaPostalResponsavel.setSigla("GIGOV");
		caixaPostalResponsavel.setUnidade(unidade);
		
		fluxoDemanda = new FluxoDemanda();
		fluxoDemanda.setAtivo(true);
		fluxoDemanda.setDemanda(demanda);
		fluxoDemanda.setOrdem(1);
		fluxoDemanda.setId(1L);
		fluxoDemanda.setPrazo(10);
		fluxoDemanda.setCaixaPostal(caixaPostalDemandante);
		
		fluxoList = new ArrayList<FluxoDemanda>();
		fluxoList.add(fluxoDemanda);
		
		atendimento = new Atendimento();
		atendimento.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
		atendimento.setId(1L);
		atendimento.setFluxoDemanda(fluxoDemanda);
		
		atendimento2 = new Atendimento();
		atendimento2.setAcaoEnum(AcaoAtendimentoEnum.INCLUIR);
		atendimento2.setId(2L);
		atendimento2.setFluxoDemanda(fluxoDemanda);
		
		atendimentoList = new ArrayList<>();
		atendimentoList.add(atendimento);
		atendimentoList.add(atendimento2);
		
		demanda = new Demanda();
		demanda.setAnexoDemanda("Teste1");
		demanda.setDataHoraAbertura(new Date());
		demanda.setSituacao(SituacaoEnum.FECHADA);
		demanda.setCaixaPostalDemandante(caixaPostalDemandante);
		demanda.setCaixaPostalResponsavel(caixaPostalResponsavel);
		demanda.setTitulo("Titulo");
		demanda.setTipoDemanda(TipoDemandaEnum.CONSULTA);
		demanda.setAtendimentosList(atendimentoList);
		demanda.setMatriculaDemandante("Fteste");
		demanda.setNomeUsuarioDemandante("Usuario Teste");
		
		demanda.setFluxosDemandasList(fluxoList);
		
		demandaContrato = new DemandaContrato();
		demandaContrato.setId(1L);
		demandaContrato.setNumeroContrato("53453");
		
		demandaContratoList = new ArrayList<>();
		demandaContratoList.add(demandaContrato);
			
		demanda.setContratosList(demandaContratoList);
		
		assunto = new Assunto();
		assunto.setId(1L);
		assunto.setAtivo(true);
		assunto.setNome("Teste");
		assunto.setCaixaPostal(caixaPostalDemandante);
		assunto.setContrato(true);
		
		demanda.setAssunto(assunto);
		
		assuntoList = new ArrayList<>();
		assuntoList.add(assunto);
		
		demandaList = new ArrayList<>();
		demandaList.add(demanda);
		
	}

	@Test
	public void obterDemandasWSConsultaTest() {
		
	Mockito.when(this.demandaDAO.obterDemandasWSConsulta(filtro)).thenReturn(demandaList);
	
	Mockito.when(this.assuntoService.obterAssuntosFetchPai()).thenReturn(assuntoList);
	
	Mockito.when(this.feriadoService.obterListaDeDatasDosFeriados()).thenReturn(dateList);
	
	List<DemandaAbertaDTO> demandaAbertaDTOs =   this.demandaService.obterDemandasWSConsulta(filtro);
	
	assertFalse(demandaAbertaDTOs.isEmpty());
	
	}
}
