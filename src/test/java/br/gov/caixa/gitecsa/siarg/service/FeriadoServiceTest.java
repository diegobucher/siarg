package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.siarg.model.Feriado;

public class FeriadoServiceTest {

  @InjectMocks
  private FeriadoService feriadoService;
  @Mock
  private FeriadoDAO feriadoDAO;
  private Feriado feriado = new Feriado();
  
  private List<Date> datasFeriadosList;

  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    Date data1 = new Date();
    Date date2 = new Date();
    datasFeriadosList = new ArrayList<>();
    datasFeriadosList.add(data1);
    datasFeriadosList.add(date2);
    
  }

  @Test
  public void consultarTest() throws Exception {
    List<Feriado> feriadoList = feriadoService.consultar(this.feriado);
    assertTrue(feriadoList.isEmpty());
  }
  
  @Test
  public void calcularDataVencimentoPrazoTest() throws DataBaseException {
    Calendar cal = Calendar.getInstance();
    cal.set(2019, 01, 23);
    Mockito.when(this.feriadoDAO.isFeriado(cal.getTime())).thenReturn(false);
    Date dataResultado = this.feriadoService.calcularDataVencimentoPrazo(cal.getTime(), 1);
    assertFalse(dataResultado.after(new Date()));
    
    Mockito.when(this.feriadoDAO.isFeriado(cal.getTime())).thenReturn(true);
    dataResultado = this.feriadoService.calcularDataVencimentoPrazo(cal.getTime(), 1);
    assertFalse(dataResultado.after(new Date()));
  }
  
  @Test
  public void isDiaUtilTest() throws DataBaseException {
    Calendar cal = Calendar.getInstance();
    cal.set(2019, 01, 23);
    Mockito.when(this.feriadoDAO.isFeriado(cal.getTime())).thenReturn(false);
    Boolean resultado = this.feriadoService.isDiaUtil(cal.getTime());
    assertFalse(resultado);
    
    cal.set(2019, 01, 27);
    resultado = this.feriadoService.isDiaUtil(cal.getTime());
    assertTrue(resultado);
  }
  
  @Test
  public void calcularNumeroDiasUteisPorDataEPrazoTest() {
    Calendar cal = Calendar.getInstance();
//    cal.set(2019, 01, 01);
    cal.add(Calendar.YEAR, 1);
    List<Date> datasFeriadosList = new ArrayList<>();
    datasFeriadosList.add(cal.getTime());
    Integer resultado = this.feriadoService.calcularNumeroDiasUteisPorDataEPrazo(cal.getTime(), 1, datasFeriadosList);
    assertTrue(resultado > 1);
    
    cal = Calendar.getInstance();
    cal.set(1980, 00, 01);
    datasFeriadosList = new ArrayList<>();
    datasFeriadosList.add(cal.getTime());
    resultado = this.feriadoService.calcularNumeroDiasUteisPorDataEPrazo(cal.getTime(), 1, datasFeriadosList);
    assertTrue(resultado < 1);
  }
  
  @Test
  public void adicionarDiasUteis() {
    Calendar cal = Calendar.getInstance();
    cal.set(2019, 00, 01);
    Date resultado = this.feriadoService.adicionarDiasUteis(cal.getTime(), 10);
    assertTrue(resultado.before(new Date()));
  }

  @Test
  public void calcularQtdDiasUteisEntreDatasTest() {
    Calendar dataInicial = Calendar.getInstance();
    dataInicial.set(2019, Calendar.JANUARY, 01);
    Calendar dataFinal = Calendar.getInstance();
    dataFinal.set(2019, Calendar.JANUARY, 10);
    
    Integer resultado = this.feriadoService.calcularQtdDiasUteisEntreDatas(dataInicial.getTime(), dataFinal.getTime());
    assertTrue(resultado == 7);
  }
  
  @Test
  public void calcularQtdDiasUteisTest() {
    Calendar dataInicial = Calendar.getInstance();
    dataInicial.set(2019, Calendar.JANUARY, 01);
    Calendar dataFinal = Calendar.getInstance();
    dataFinal.set(2019, Calendar.JANUARY, 10);
    
    Double resultado = this.feriadoService.calcularQtdDiasUteis(dataInicial.getTime(), dataFinal.getTime(), datasFeriadosList);
    assertTrue(resultado == 7);
  }
  
  @Test
  public void calcularQtdDiasUteisEntreDatasFeriadoTest() {
    Calendar dataInicial = Calendar.getInstance();
    dataInicial.set(2019, Calendar.JANUARY, 01);
    Calendar dataFinal = Calendar.getInstance();
    dataFinal.set(2019, Calendar.JANUARY, 10);
    List<Date> datasFeriadosList = new ArrayList<>();
    datasFeriadosList.add(dataInicial.getTime());
    Integer resultado = this.feriadoService.calcularQtdDiasUteisEntreDatas(dataInicial.getTime(), dataFinal.getTime(), datasFeriadosList);
    assertTrue(resultado > 1);
  }
 
}
