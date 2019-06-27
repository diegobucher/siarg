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

import br.gov.caixa.gitecsa.siarg.dao.DemandaContratoDAO;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;

public class DemandaContratoServiceTest {
  
  @InjectMocks
  private DemandaContratoService demandaContratoService;
  @Mock
  private DemandaContratoDAO demandaContratoDAO;
  private Demanda demanda = new Demanda();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testObterDemandaContrato() {
    DemandaContrato result = this.demandaContratoService.obterDemandaContrato(this.demanda, "123456");
    assertTrue(result != null);
  }
  
  @Test
  public void testFormatacaoNumeroContrato() {
    String result = this.demandaContratoService.formatacaoNumeroContrato("123456");
    assertTrue(result != null);
  }
  
  @Test
  public void testRemoverContrato() {
    DemandaContrato demandaContrato = new DemandaContrato();
    demandaContrato.setId(1L);
    try {
      this.demandaContratoService.removerContrato(demandaContrato);
    }catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testObterContratosPorIdDemanda() {
    DemandaContrato demandaContrato = new DemandaContrato();
    demandaContrato.setId(1L);
    List<DemandaContrato> demandaContratosList = new ArrayList<>();
    demandaContratosList.add(demandaContrato);
    Mockito.when(this.demandaContratoDAO.obterContratosPorIdDemanda(1L)).thenReturn(demandaContratosList);
    List<DemandaContrato> result = this.demandaContratoService.obterContratosPorIdDemanda(1L);
    assertTrue(result != null);
  }

}
