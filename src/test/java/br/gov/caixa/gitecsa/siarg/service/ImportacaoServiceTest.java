package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessRollbackException;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.AtendimentoDAO;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dao.FluxoAssuntoDAO;
import br.gov.caixa.gitecsa.siarg.dao.FluxoDemandaDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dto.ExportacaoAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.dto.UnidadeCadastroDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

/**
 * @author f763644
 
public class ImportacaoServiceTest {

	private static final String SIARG_BYTES = "SIARG";

	@InjectMocks
	private ImportacaoService importacaoService;

	@Mock
	private AssuntoDAO assuntoDao;

	@Mock
	private CaixaPostalDAO caixaPostalDao;

	@Mock
	private UnidadeDAO unidadeDao;

	@Mock
	private DemandaDAO demandaDao;

	@Mock
	private FluxoDemandaDAO fluxoDemandaDao;

	@Mock
	private FluxoAssuntoDAO fluxoAssuntoDAO;

	@Mock
	private AtendimentoDAO atendimentoDAO;

	private Abrangencia abrangencia = new Abrangencia();

	private CaixaPostal caixaPostal = new CaixaPostal();

	private Assunto assuntoPai = new Assunto();
	
    private Unidade unidadeSubordinacao = new Unidade();
    
	List<FluxoAssunto> fluxoAssuntoList = new ArrayList<>();
	List<Unidade> assuntoUnidadeDemandanteList = new ArrayList<>();
	List<CaixaPostal> caixasPostaisObservadorList = new ArrayList<>();
	List<UsuarioUnidade> usuarioUnidadeList = new ArrayList<>();
	List<Demanda> demandasObservadasList = new ArrayList<>();
	List<FluxoDemanda> fluxosDemandasList = new ArrayList<>();
	List<Unidade> unidadesSubordinadasList = new ArrayList<>();

	@Before
	public void setUpBeforeClass() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void importarAssuntosTest() throws BusinessRollbackException {

		// Cenário

		InputStream stream = getClass().getResourceAsStream("/MigracaoAssunto.xls");

		List<Assunto> assuntoList = new ArrayList<>();
		List<Unidade> unidadeList = new ArrayList<>();
		List<CaixaPostal> caixaPostalList = new ArrayList<>();
		List<ExportacaoAssuntoDTO> listRegistros = new ArrayList<>();
		
		abrangencia.setId(1l);

		Assunto assu = new Assunto();
		assu.setId(1L);
		assu.setFlagAssunto(true);
		assu.setExcluido(true);
		assu.setContrato(true);
		assu.setAtivo(true);
		assu.setFlagAssunto(true);
		assu.setAbrangencia(abrangencia);
		assu.setNome("Teste");
		assu.setPrazo(3);
		assu.setTextoPreDefinido("Texto assunto");
		assu.setPermissaoAbertura(true);
		assu.setAssuntosList(assuntoList);
		assu.setFluxosAssuntosList(fluxoAssuntoList);
		assu.setArvoreCompleta("Arvore teste assunto");
		assu.setAssuntoUnidadeDemandanteList(assuntoUnidadeDemandanteList);
		assu.setCaixasPostaisObservadorList(caixasPostaisObservadorList);
		assu.setAssuntoPai(assuntoPai);
		assu.setCaixaPostal(caixaPostal);
		assuntoList.add(assu);
		
		Unidade unida = new Unidade();
		unida.setAbrangencia(abrangencia);
		unida.setAtivo(true);
		unida.setCaixasPostaisList(caixaPostalList);
		unida.setCgcUnidade(4);
		unida.setId(1L);
		unida.setNome("unidade teste");
		unida.setSigla("y");
		unida.setTipoUnidade(TipoUnidadeEnum.EXTERNA);
		unida.setIsRelConsolidadoAssunto(true);
		unida.setUnidadeSubordinacao(unidadeSubordinacao);
		unida.setUsuarioUnidadeList(usuarioUnidadeList);
		unida.setAssuntoUnidadeDemandanteList(assuntoList);
		unida.setUnidadesSubordinadasList(unidadesSubordinadasList);
		unidadeList.add(unida);
		

		CaixaPostal cx = new CaixaPostal();
		cx.setId(1L);
		cx.setRecebeEmail(true);
		cx.setSigla("P");
		cx.setUnidade(unida);
		cx.setAssuntosList(assuntoList);
		cx.setAtivo(true);
		cx.setEmail("testecaixa@teste.com.br");
		cx.setFluxosAssuntosList(fluxoAssuntoList);
		cx.setAssuntosObservadosList(assuntoList);
		cx.setDemandasObservadasList(demandasObservadasList);
		cx.setFluxosDemandasList(fluxosDemandasList);
		cx.setTotalDemandasDaCaixaPostal(1L);
		caixaPostalList.add(cx);
		
		ExportacaoAssuntoDTO exp = new ExportacaoAssuntoDTO();
		exp.setAtivo("Verda");
		exp.setCategoria1("Apoio Operacional - B. Família e B. Sociais");
		exp.setCategoria2("Benefícios Sociais");
		exp.setCategoria3("Alteração Contratual");
		exp.setDemandantesPreDefinidos("Exportacao teste");
		exp.setNomeAssunto("Garantia Safra");
		exp.setResponsavel("GEFAB02");
		exp.setPrazoResponsavel(3);
		exp.setFluxoAssunto("Fluxo Assunto teste");
		exp.setNumeroAssunto(1L);
		exp.setObservadoresAssunto("Testando Observadores");
		
		listRegistros.add(exp);
		

		Mockito.when(this.assuntoDao.findAllByAbrangencia(Matchers.any(Abrangencia.class))).thenReturn(assuntoList);
		Mockito.when(this.unidadeDao.obterUnidadesAtivas(Matchers.any(Abrangencia.class),Matchers.any(TipoUnidadeEnum.class), Matchers.any(TipoUnidadeEnum.class))).thenReturn(unidadeList);
		Mockito.when(this.caixaPostalDao.findByAbrangenciaTipoUnidade(Matchers.any(Abrangencia.class),Matchers.any(TipoUnidadeEnum.class), Matchers.any(TipoUnidadeEnum.class), Matchers.any(TipoUnidadeEnum.class))).thenReturn(caixaPostalList);

		for (ExportacaoAssuntoDTO exportacaoAssuntoDTO : listRegistros) {
			   this.importacaoService.equals(assuntoList);
		   }
		
		Integer resultado = this.importacaoService.importarAssuntos(stream);
		
		// Verificação


		
	}
	
	
	

}
*/