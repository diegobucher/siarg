package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO;
import br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeIteracoesDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class AtendimentoServiceTest {
	
	@InjectMocks
	private AtendimentoService atendimentoService;
	
	@Mock
	private FeriadoService feriadoService;
	
	@Mock
	private AtendimentoDAO atendimentoDAO;
	
	@Mock
	private FeriadoDAO feriadoDAO;
	
	private Unidade unidade;
	
	private CaixaPostal caixaPostal1;
	
	private CaixaPostal caixaPostal2;
	
	private CaixaPostal caixaPostal3;
	
	private CaixaPostal caixaPostal4;
	
	private FluxoDemanda fluxoDemanda1;
	
	private FluxoDemanda fluxoDemanda2;
	
	private FluxoDemanda fluxoDemanda3;
	
	private FluxoDemanda fluxoDemanda4;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private List<Atendimento> atendimentoList;
	
	private List<QuantidadeIteracoesDTO> quantidadeIteracoesDTOList;
	
	private List<Date> datasFeriadosList;
	
	private Double diasTeste;
	
	private Abrangencia abrangencia;
	

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		unidade = new Unidade();
		unidade.setId(1L);
		dataInicial = new Date();
		dataFinal = new Date();
		
		caixaPostal1 = new CaixaPostal();
		caixaPostal1.setSigla("GEGOV");
		
		caixaPostal2 = new CaixaPostal();
		caixaPostal2.setSigla("GEGOV1");
		
		caixaPostal3 = new CaixaPostal();
		caixaPostal3.setSigla("GEGOV2");
		
		caixaPostal4 = new CaixaPostal();
		caixaPostal4.setSigla("GEGOV3");
		
		fluxoDemanda1 = new FluxoDemanda();
		fluxoDemanda1.setCaixaPostal(caixaPostal1);
		
		fluxoDemanda2 = new FluxoDemanda();
		fluxoDemanda2.setCaixaPostal(caixaPostal2);
		
		fluxoDemanda3 = new FluxoDemanda();
		fluxoDemanda3.setCaixaPostal(caixaPostal3);
		
		fluxoDemanda4 = new FluxoDemanda();
		fluxoDemanda4.setCaixaPostal(caixaPostal4);
		
		atendimentoList = new ArrayList<>();
		Atendimento atendimento = new Atendimento();
		atendimento.setAcaoEnum(AcaoAtendimentoEnum.RESPONDER);
		atendimento.setFluxoDemanda(fluxoDemanda1);
		atendimento.setDataHoraAtendimento(new Date());
		atendimento.setDataHoraRecebimento(new Date());
		atendimentoList.add(atendimento);
		
		Atendimento atendimento2 = new Atendimento();
		atendimento2.setAcaoEnum(AcaoAtendimentoEnum.QUESTIONAR);
		atendimento2.setFluxoDemanda(fluxoDemanda2);
		atendimento2.setDataHoraAtendimento(new Date());
		atendimento2.setDataHoraRecebimento(new Date());
		atendimentoList.add(atendimento2);
		
		Atendimento atendimento3 = new Atendimento();
		atendimento3.setAcaoEnum(AcaoAtendimentoEnum.ENCAMINHAR);
		atendimento3.setFluxoDemanda(fluxoDemanda3);
		atendimento3.setDataHoraAtendimento(new Date());
		atendimento3.setDataHoraRecebimento(new Date());
		atendimentoList.add(atendimento3);
		
		Atendimento atendimento4 = new Atendimento();
		atendimento4.setAcaoEnum(AcaoAtendimentoEnum.CONSULTAR);
		atendimento4.setFluxoDemanda(fluxoDemanda4);
		atendimento4.setDataHoraAtendimento(new Date());
		atendimento4.setDataHoraRecebimento(new Date());
		atendimentoList.add(atendimento4);
		
		Date data1 = new Date();
		Date date2 = new Date();
		datasFeriadosList = new ArrayList<>();
		datasFeriadosList.add(data1);
		datasFeriadosList.add(date2);
		
		abrangencia = new Abrangencia();
		abrangencia.setId(1L);
		abrangencia.setNome("Abrangencia Teste");
		
		diasTeste = 2.0;
	}

	@Test
	public void testObterListaAtendimentosPorUnidade() {
		
		Mockito.when(atendimentoDAO.obterListaAtendimentosPorUnidade(unidade.getId(), abrangencia, dataInicial, dataFinal)).thenReturn(atendimentoList);
		
		Mockito.when(feriadoDAO.obterListaDeDatasDosFeriados()).thenReturn(datasFeriadosList);
		
		Mockito.doReturn(diasTeste).when(this.feriadoService).calcularQtdDiasUteis(dataInicial, dataFinal, datasFeriadosList);
		
		atendimentoService.obterListaAtendimentosPorUnidade(unidade.getId(), abrangencia, dataInicial, dataFinal);
		
		this.quantidadeIteracoesDTOList = 
				 this.atendimentoService.obterListaAtendimentosPorUnidade(unidade.getId(), abrangencia, dataInicial, dataFinal);
		
		
	}

}
