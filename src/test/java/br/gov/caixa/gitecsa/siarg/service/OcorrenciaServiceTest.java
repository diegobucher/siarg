package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
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
import br.gov.caixa.gitecsa.siarg.dao.OcorrenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public class OcorrenciaServiceTest {

  @InjectMocks
  private OcorrenciaService ocorrenciaService;
  @Mock
  private OcorrenciaDAO ocorrenciaDAO;
  private List<Ocorrencia> ocorrenciasList = new ArrayList<>();
  private UnidadeDTO unidadeDTO = new UnidadeDTO();
  private Ocorrencia ocorrencia = new Ocorrencia();
  private Ocorrencia ocorrenciaSemId = new Ocorrencia();
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.ocorrencia.setId(1L);
    this.ocorrenciasList.add(this.ocorrencia);
  }

  @Test
  public void testObterListaOcorrenciasPorUnidade() {
    Mockito.when(this.ocorrenciaDAO.obterListaOcorrenciasPorUnidade(this.unidadeDTO)).thenReturn(ocorrenciasList);
    List<Ocorrencia> result = this.ocorrenciaService.obterListaOcorrenciasPorUnidade(this.unidadeDTO);
    assertTrue(result.size() > 0);
  }
  
  @Test
  public void testSalvar() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(this.ocorrenciaService.save(this.ocorrenciaSemId)).thenReturn(this.ocorrencia);
    Ocorrencia result = this.ocorrenciaService.salvar(this.ocorrenciaSemId);
    assertTrue(result != null);
  }
  
  @Test
  public void testSalvarUpdate() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(this.ocorrenciaService.update(this.ocorrencia)).thenReturn(this.ocorrencia);
    Ocorrencia result = this.ocorrenciaService.salvar(this.ocorrencia);
    assertTrue(result.getId() != null);
  }
  
  @Test
  public void testAdicionar() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(this.ocorrenciaService.save(this.ocorrenciaSemId)).thenReturn(this.ocorrencia);
    Ocorrencia result = this.ocorrenciaService.adicionar(this.ocorrenciaSemId);
    assertTrue(result != null);
  }
  
  @Test
  public void testAlterar() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(this.ocorrenciaService.update(this.ocorrencia)).thenReturn(this.ocorrencia);
    Ocorrencia result = this.ocorrenciaService.alterar(this.ocorrencia);
    assertTrue(result != null);
  }
  
  @Test
  public void testExcluir() throws RequiredException, BusinessException, DataBaseException {
    Mockito.when(this.ocorrenciaService.update(this.ocorrencia)).thenReturn(this.ocorrencia);
    Ocorrencia result = this.ocorrenciaService.excluir(this.ocorrencia);
    assertTrue(result != null);
  }
  
  @Test
  public void testObterListaOcorrenciasPor() {
    Abrangencia abrangencia = new Abrangencia();
    Calendar cal = Calendar.getInstance();
    cal.set(2019, Calendar.JANUARY, 20);
    Mockito.when(ocorrenciaDAO.obterListaOcorrenciasPor(abrangencia, cal.getTime())).thenReturn(this.ocorrenciasList);
    List<Ocorrencia> result = this.ocorrenciaService.obterListaOcorrenciasPor(abrangencia, cal.getTime());
    assertTrue(result != null);
  }

}
