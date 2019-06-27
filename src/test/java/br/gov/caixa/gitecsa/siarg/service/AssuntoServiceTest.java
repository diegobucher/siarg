package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public class AssuntoServiceTest {
  
  @InjectMocks
  private AssuntoService assuntoService;
  @Mock
  private AssuntoDAO assuntoDAO;
  private Abrangencia abrangencia = new Abrangencia();
  private Unidade unidade = new Unidade();
  private List<Unidade> unidadesList = new ArrayList<>();
  private Assunto assunto = new Assunto();
  private Assunto assuntoPai = new Assunto();
  private List<Assunto> assuntosList = new ArrayList<>();
  private CaixaPostal caixaPostal = new CaixaPostal();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.abrangencia.setId(1L);
    this.abrangencia.setNome("Abrangencia");
    this.unidade.setId(1L);
    this.unidadesList.add(this.unidade);
    this.abrangencia.setUnidadesList(this.unidadesList);
    this.assunto.setId(2L);
    this.assunto.setNome("Nome Assunto");
    this.assuntosList.add(this.assunto);
    this.assuntoPai.setNome("Nome Assunto");
    this.caixaPostal.setId(1L);
    this.caixaPostal.setUnidade(this.unidade);
    this.assunto.setCaixaPostal(this.caixaPostal);
    this.assuntosList.add(this.assuntoPai);
  }

  @Test
  public void testGetRelacionamentoAssuntos() {
    Mockito.when(this.assuntoDAO.findAtivos(this.abrangencia)).thenReturn(this.assuntosList);
    List<Assunto> result = this.assuntoService.getRelacionamentoAssuntos(this.abrangencia);
    assertTrue(result != null);
    this.assunto.setAssuntoPai(this.assunto);
    result = this.assuntoService.getRelacionamentoAssuntos(this.abrangencia);
    assertTrue(result != null);
  }
  
  @Test
  public void testGetRelacionamentoAssuntosIncluindoInativos() {
    Mockito.when(this.assuntoDAO.findAllBy(this.abrangencia)).thenReturn(this.assuntosList);
    List<Assunto> result = this.assuntoService.getRelacionamentoAssuntosIncluindoInativos(this.abrangencia);
    assertTrue(result != null);
    this.assunto.setAssuntoPai(this.assunto);
    result = this.assuntoService.getRelacionamentoAssuntosIncluindoInativos(this.abrangencia);
    assertTrue(result != null);
  }
  
  @Test
  public void testDeterminarTipoFluxoAssunto() throws DataBaseException {
    TipoFluxoEnum result = this.assuntoService.determinarTipoFluxoAssunto(this.assunto, this.caixaPostal);
    assertTrue(result.equals(TipoFluxoEnum.DEMANDANTE_RESPONSAVEL));
  }
  
  @Test
  public void testDeterminarTipoFluxoAssunto2() throws DataBaseException {
    this.assunto.setCaixaPostal(new CaixaPostal());
    Mockito.when(this.assuntoDAO.isDemandanteAssunto(this.assunto, this.caixaPostal.getUnidade())).thenReturn(true);
    TipoFluxoEnum result = this.assuntoService.determinarTipoFluxoAssunto(this.assunto, this.caixaPostal);
    assertTrue(result.equals(TipoFluxoEnum.DEMANDANTE_DEFINIDO));
  }
  
  @Test
  public void testDeterminarTipoFluxoAssunto3() throws DataBaseException {
    this.assunto.setCaixaPostal(new CaixaPostal());
    Mockito.when(this.assuntoDAO.isDemandanteAssunto(this.assunto, this.caixaPostal.getUnidade())).thenReturn(false);
    TipoFluxoEnum result = this.assuntoService.determinarTipoFluxoAssunto(this.assunto, this.caixaPostal);
    assertTrue(result.equals(TipoFluxoEnum.OUTROS_DEMANDANTES));
  }
  
  @Test
  public void testPesquisarRelacaoAssuntos() {
    UnidadeDTO unidadeDTO = new UnidadeDTO();
    List<UnidadeDTO> unidadeDTOList = new ArrayList<>();
    Mockito.when(this.assuntoDAO.pesquisarRelacaoAssuntos(this.abrangencia, unidadeDTO, unidadeDTOList)).thenReturn(this.assuntosList);
    List<Assunto> result = this.assuntoService.pesquisarRelacaoAssuntos(this.abrangencia, unidadeDTO, unidadeDTOList);
    assertTrue(result != null);
  }
  
  @Test
  public void testPesquisarAssuntosPorAbrangenciaEUnidade() {
    UnidadeDTO unidadeDTO = new UnidadeDTO();
    Mockito.when(this.assuntoDAO.pesquisarAssuntosPorAbrangenciaEUnidade(this.abrangencia, unidadeDTO)).thenReturn(this.assuntosList);
    List<Assunto> result = this.assuntoService.pesquisarAssuntosPorAbrangenciaEUnidade(this.abrangencia, unidadeDTO);
    assertTrue(result != null);
  }
  
  @Test
  public void testFindCategoriasNaoExcluidos() {
    Mockito.when(this.assuntoDAO.findCategoriasNaoExcluidos(this.abrangencia)).thenReturn(this.assuntosList);
    List<Assunto> result = this.assuntoService.findCategoriasNaoExcluidos(this.abrangencia);
    assertTrue(result != null);
  }
}
