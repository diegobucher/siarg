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

import br.gov.caixa.gitecsa.siarg.dao.AbrangenciaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;

public class AbrangenciaServiceTest {
  
  @InjectMocks
  private AbrangenciaService abrangenciaService;
  @Mock
  private AbrangenciaDAO abrangenciaDAO;
  private static final String MATRICULA = "c000000";
  private static final Long ID = 1L;
  private Abrangencia abrangencia = new Abrangencia();
  private List<Abrangencia> abrangenciasList = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.abrangencia.setId(ID);
    this.abrangenciasList.add(this.abrangencia);
  }

  @Test
  public void testObterListaAbrangeciaDasUnidadesUsuarioExcessaoString() {
    Mockito.when(this.abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(MATRICULA)).thenReturn(this.abrangenciasList);
    List<Abrangencia> result = this.abrangenciaService.obterListaAbrangeciaDasUnidadesUsuarioExcessao(MATRICULA);
    assertTrue(result.size() > 0);
  }

  @Test
  public void testObterListaAbrangeciaDasUnidadesUsuarioExcessaoLong() {
    Mockito.when(this.abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(ID)).thenReturn(this.abrangenciasList);
    List<Abrangencia> result = this.abrangenciaService.obterListaAbrangeciaDasUnidadesUsuarioExcessao(ID);
    assertTrue(result.size() > 0);
  }

  @Test
  public void testObterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao() {
    Mockito.when(this.abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(MATRICULA)).thenReturn(this.abrangenciasList);
    List<Abrangencia> result = this.abrangenciaService.obterListaAbrangeciaDasUnidadesUsuarioComCaixaPostalExcessao(MATRICULA);
    assertTrue(result.size() > 0);
  }

  @Test
  public void testObterAbrangenciaPorDemanda() {
    Mockito.when(this.abrangenciaDAO.obterAbrangenciaPorDemanda(ID)).thenReturn(this.abrangencia);
    Abrangencia result = this.abrangenciaService.obterAbrangenciaPorDemanda(ID);
    assertTrue(result != null);
  }

}
