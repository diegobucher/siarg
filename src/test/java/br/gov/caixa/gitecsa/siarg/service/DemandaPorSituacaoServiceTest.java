package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Demanda;

public class DemandaPorSituacaoServiceTest {

	@InjectMocks
	DemandaPorSituacaoService demandaPorSituacaoService;

	@Mock
	private DemandaDAO demandaDAO;

	@Mock
	private Demanda demanda;

	@Mock
	private List<Demanda> demandaList;

	private Long idUnidade;
	private Abrangencia abrangenciaSelecionada;
	private Date dataInicial;
	private Date dataFinal;

	@Before
	public void setUpBeforeClass() throws Exception {
		MockitoAnnotations.initMocks(this);

		demandaList = new ArrayList<>();

		demandaList.add(new Demanda());

	}

	@Test
	public void testObterDemandasPorUnidadePeriodo() throws AppException {
		Mockito.when(demandaDAO.obterDemandasPorUnidadePeriodo(idUnidade, abrangenciaSelecionada, dataInicial, dataFinal)).thenReturn(demandaList);
		assertTrue(demandaList != null);

	}

}
