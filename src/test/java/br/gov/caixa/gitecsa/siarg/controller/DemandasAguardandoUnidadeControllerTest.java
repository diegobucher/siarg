package br.gov.caixa.gitecsa.siarg.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.web.FacesMessager;

public class DemandasAguardandoUnidadeControllerTest {

  @InjectMocks
  private DemandasAguardandoUnidadeController demandasController;
  
  @Mock
  private FacesMessager facesMessager;
  
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    String dataInicialString, dataFinalString;
    dataInicialString = "01/01/2018";
    dataFinalString = "01/02/2018";
    demandasController.setDataInicialString(dataInicialString);
    demandasController.setDataFinalString(dataFinalString);
  }
  
  @Test
  public void validarCamposTest() {
    Boolean validade = demandasController.validarCampos();
    assertTrue(validade);
  }
  
  @Test
  public void validarCamposDataNulaTest() {
    demandasController.setDataInicialString("");
    Boolean validade = demandasController.validarCampos();
    assertFalse(validade);
  }
  
  @Test
  public void validarCamposDataInicioMaiorQueFinalTest() {
    demandasController.setDataInicialString("01/03/2018");
    Boolean validade = demandasController.validarCampos();
    assertFalse(validade);
  }
  
  @Test
  public void validarCamposDataInicioInvalidaTest() {
    demandasController.setDataInicialString("32/01/2018");
    Boolean validade = demandasController.validarCampos();
    assertFalse(validade);
  }

  @Test
  public void validarCamposDataFimInvalidaTest() {
    demandasController.setDataFinalString("35/01/2018");
    Boolean validade = demandasController.validarCampos();
    assertFalse(validade);
  }
}
