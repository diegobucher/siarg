package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.siarg.dao.FluxoDemandaDAO;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;

public class FluxoDemandaServiceTest {

  @InjectMocks
  private FluxoDemandaService fluxoDemandaService;
  @Mock
  private FluxoDemandaDAO fluxoDemandaDAO;
  private List<FluxoDemanda> fluxoDemandasList = new ArrayList<>();
  private FluxoDemanda fluxoDemanda = new FluxoDemanda();
  private Long id = 1L;
  private Integer resultadoNumInteiro;
  private Demanda demanda = new Demanda();
  private CaixaPostal caixaPostal = new CaixaPostal();
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.fluxoDemanda.setId(1L);
    this.fluxoDemandasList.add(this.fluxoDemanda);
    this.demanda.setId(1L);
    this.caixaPostal.setId(1L);
  }

  @Test
  public void findByIdDemandaTest() {
    Mockito.when(this.fluxoDemandaDAO.findByIdDemanda(this.id)).thenReturn(this.fluxoDemandasList);
    List<FluxoDemanda> resultado = this.fluxoDemandaService.findByIdDemanda(this.id);
    assertNotNull(resultado);
  }
  
  @Test
  public void findAtivosByIdDemandaTest() {
    Mockito.when(this.fluxoDemandaDAO.findAtivosByIdDemanda(this.id)).thenReturn(this.fluxoDemandasList);
    List<FluxoDemanda> resultado = this.fluxoDemandaService.findAtivosByIdDemanda(this.id);
    assertNotNull(resultado);
  }
  
  @Test
  public void obterFluxoDemandaComCaixaPostalUnidadeTest() {
    Mockito.when(this.fluxoDemandaDAO.obterFluxoDemandaComCaixaPostalUnidade(this.id)).thenReturn(this.fluxoDemanda);
    FluxoDemanda resultado = this.fluxoDemandaService.obterFluxoDemandaComCaixaPostalUnidade(this.id);
    assertNotNull(resultado);
  }
  
  @Test
  public void obterPrazoDaCaixaPostalAtualEncaminhadaExternaTest() {
    this.resultadoNumInteiro = 1;
    Mockito.when(this.fluxoDemandaDAO.obterPrazoDaCaixaPostalAtualEncaminhadaExterna(this.id)).thenReturn(this.resultadoNumInteiro);
    Integer resultado = this.fluxoDemandaService.obterPrazoDaCaixaPostalAtualEncaminhadaExterna(this.id);
    assertTrue(resultado == this.resultadoNumInteiro);
  }
  
  @Test
  public void obterPrazoDaCaixaPostalAtualTest() {
    this.resultadoNumInteiro = 1;
    Mockito.when(this.fluxoDemandaDAO.obterPrazoDaCaixaPostalAtual(this.id, this.id)).thenReturn(this.resultadoNumInteiro);
    Integer resultado = this.fluxoDemandaService.obterPrazoDaCaixaPostalAtual(this.id, this.id);
    assertTrue(resultado == this.resultadoNumInteiro);
  }
  
  @Test
  public void obterCaixaPostalFluxoAnteriorExternaTest() {
    Mockito.when(this.fluxoDemandaDAO.obterCaixaPostalFluxoAnteriorExterna(this.demanda)).thenReturn(this.caixaPostal);
    CaixaPostal resultado = this.fluxoDemandaService.obterCaixaPostalFluxoAnteriorExterna(this.demanda);
    assertTrue(resultado.getId() == caixaPostal.getId());
  }

}
