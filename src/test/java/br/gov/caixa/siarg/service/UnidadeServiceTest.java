package br.gov.caixa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dto.UnidadeCadastroDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

public class UnidadeServiceTest {

  @InjectMocks
  private UnidadeService unidadeService;

  @Mock
  private UnidadeDAO unidadeDAO;

  @Mock
  private CaixaPostalDAO caixaPostalDAO;

  private Long idAbrangencia;

  private List<Unidade> unidadeList;
  private List<UnidadeCadastroDTO> unidadeCadastroDTOList;
  private List<CaixaPostal> caixaPostalList;

  private Unidade unidade;

  private Abrangencia abrangencia;

  @Before
  public void setUpBeforeClass() throws Exception {
    MockitoAnnotations.initMocks(this);
    idAbrangencia = 1L;
    unidadeList = new ArrayList<>();
    unidadeList.add(new Unidade());

    unidade = new Unidade();
    List<CaixaPostal> caixaList = new ArrayList<>();
    caixaList.add(new CaixaPostal());
    unidade.setCaixasPostaisList(caixaList);
    abrangencia = new Abrangencia();

    unidadeCadastroDTOList = new ArrayList<>();
    unidadeCadastroDTOList.add(new UnidadeCadastroDTO());

    caixaPostalList = new ArrayList<>();
    caixaPostalList.add(new CaixaPostal());
  }

  @Test
  public void testObterUnidadePorChaveFetch() {

    Mockito.when(unidadeDAO.obterUnidadePorChaveFetch(idAbrangencia)).thenReturn(unidade);

    Unidade uni = unidadeService.obterUnidadePorChaveFetch(idAbrangencia);

    assertTrue(uni != null);

  }

  @Test
  public void testObterListaUnidadesPorAbrangencia() {

    Mockito.when(unidadeDAO.obterListaUnidadesPorAbrangencia(idAbrangencia)).thenReturn(unidadeList);

    List<Unidade> uniList = unidadeService.obterListaUnidadesPorAbrangencia(idAbrangencia);

    assertTrue(uniList.size() > 0);

  }

  @Test
  public void testObterUnidadesECaixasPostais() {

    Mockito.when(unidadeDAO.obterUnidadesECaixasPostais(TipoUnidadeEnum.values())).thenReturn(unidadeList);

    List<Unidade> uniList = unidadeService.obterUnidadesECaixasPostaisPorTipo(TipoUnidadeEnum.values());

    assertTrue(uniList.size() > 0);

  }

  @Test
  public void testObterUnidadesDTOPor() {

    Mockito.when(this.unidadeDAO.obterUnidadesECaixasPostais(abrangencia, TipoUnidadeEnum.values())).thenReturn(unidadeList);

    List<UnidadeCadastroDTO> uniList = unidadeService.obterUnidadesDTOPor(abrangencia, TipoUnidadeEnum.values());

    assertTrue(uniList.size() > 0);

  }

  @Test
  public void testUpdate() {

    Mockito.when(unidadeDAO.update(unidade)).thenReturn(unidade);

    Unidade unidade = unidadeService.update(this.unidade, caixaPostalList);

    assertTrue(unidade != null);

  }

  @Test
  public void testObterListaUnidadesSUEG() {

    Mockito.when(unidadeDAO.obterListaUnidadesSUEG(idAbrangencia)).thenReturn(unidadeList);

    List<Unidade> uniList = unidadeService.obterListaUnidadesSUEG(idAbrangencia);

    assertTrue(uniList.size() > 0);

  }

  @Test
  public void testObterUnidadePorAbrangenciaCGC() {
    Mockito.when(unidadeDAO.obterUnidadePorAbrangenciaCGC(abrangencia, 1)).thenReturn(this.unidade);

    Unidade unidade = unidadeService.obterUnidadePorAbrangenciaCGC(abrangencia, 1);

    assertTrue(unidade != null);
  }

  @Test
  public void testObterUnidadePorAbrangenciaSigla() {
    Mockito.when(unidadeDAO.obterUnidadePorAbrangenciaSigla(abrangencia, "GITEC")).thenReturn(this.unidade);

    Unidade unidade = unidadeService.obterUnidadePorAbrangenciaSigla(abrangencia, "GITEC");

    assertTrue(unidade != null);
  }

  @Test
  public void testExcluirLogicamente() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(unidadeService.findById(10l)).thenReturn(this.unidade);

    unidadeService.excluirLogicamente(10l);
    assertTrue(true);
  }
  
  @Test
  public void testObterListaUnidadeUsuarioLogado()  {
    Mockito.when(unidadeDAO.obterListaUnidadeUsuarioLogado(7470)).thenReturn(this.unidadeList);

    List<Unidade> unidadeList = unidadeService.obterListaUnidadeUsuarioLogado(7470);
    assertTrue(!unidadeList.isEmpty());
  }
 
  @Test
  public void testObterListaUnidadesRelatorioAnaliticoPorAssunto()  {
    Mockito.when(unidadeDAO.obterListaUnidadesRelatorioAnaliticoPorAssunto(1L)).thenReturn(this.unidadeList);
    
    List<Unidade> unidadeList = unidadeService.obterListaUnidadesRelatorioAnaliticoPorAssunto(1L);
    assertTrue(!unidadeList.isEmpty());
  }
  
  @Test
  public void testConsultar() throws Exception  {
    
    List<Unidade> unidadeList = unidadeService.consultar(unidade);
    assertTrue(unidadeList == null);
  }
  
  @Test
  public void testValidaRegrasExcluir() throws Exception  {
    unidadeService.validaRegrasExcluir(unidade);
    assertTrue(true);
  }
  
}
