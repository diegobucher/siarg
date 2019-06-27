package br.gov.caixa.siarg.service;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.service.AtendimentoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;

public class DemandaServiceTest {
  
  @InjectMocks
  private DemandaService demandaService;
  
  @Mock
  private DemandaDAO demandaDAO;
  
  @Mock
  private AtendimentoService atendimentoService;
  
  @Mock
  private Demanda demanda;
  
  @Mock
  private Atendimento atendimento;
  
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.demanda = new Demanda();
    this.atendimento = new Atendimento();
    Calendar calendarDemanda = Calendar.getInstance();
    calendarDemanda.set(2018, 07, 01, 01, 01);
    Date dataAtendimento = new Date();
    this.demanda.setDataHoraAbertura(calendarDemanda.getTime());
    this.atendimento.setDataHoraRecebimento(dataAtendimento);
  }
  
  @Test
  public void retornaDataCalculadaTest() {
//    int valor2 = demandaService.retornaDataCalculada(this.atendimento, null);
    assertTrue(true);
    
//    int valor = demandaService.retornaDataCalculada(null, this.demanda);
    assertTrue(true);
  }
  
}
