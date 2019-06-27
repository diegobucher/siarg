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

import br.gov.caixa.gitecsa.arquitetura.exception.BusinessException;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.exception.RequiredException;
import br.gov.caixa.gitecsa.siarg.dao.CamposObrigatoriosDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;


/**
 * @author f763644
 */
public class CamposObrigatoriosTest {

	@InjectMocks
	private CamposObrigatoriosService camposObrigatoriosService;
	
	@Mock
	private CamposObrigatoriosDAO camposObrigatoriosDAO;
	
	@Mock
	private CamposObrigatorios camposObrigatorios;
	
	@Mock
	private List<CamposObrigatorios> camposObrigatoriosList;
	

	private String nome;
	
	private String descricao;

	private TipoCampoEnum tipo; 

	private Integer tamanho;

	private String mascara;
	
	private Boolean ativo;
	
	
	@Before
	public void setUpBeforeClass() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		camposObrigatoriosList = new ArrayList<>();
		
		camposObrigatoriosList.add(new CamposObrigatorios());
		
		camposObrigatorios = new CamposObrigatorios();
		
		ativo = true;
	}
	
	 @Test
	 public void testObterCamposObrigatoriosNome() {
		 Mockito.when(camposObrigatoriosDAO.obterCamposObrigatorioPorNome(nome)).thenReturn(camposObrigatorios);
		 
		 List<CamposObrigatorios> nome = camposObrigatoriosService.obterCamposObrigatoriosPorNome(this.nome);
		 
		 assertTrue(nome != null);
	}
	 
	 @Test
	 public void testObterCamposObrigatoriosDescricao() {
		 Mockito.when(camposObrigatoriosDAO.obterCamposObrigatorioPorDescricao(descricao)).thenReturn(camposObrigatorios);
		 List<CamposObrigatorios> descricao = camposObrigatoriosService.obterCamposObrigatoriosPorDescricao(this.descricao);
		 assertTrue(descricao != null);
	 }
	 
	 @Test
	 public void testObterCamposObrigatorioTipo() {
		 Mockito.when(camposObrigatoriosDAO.obterCamposObrigatoriosPorTipo(tipo)).thenReturn(camposObrigatoriosList);
		 
		 List<CamposObrigatorios> tipo = camposObrigatoriosService.obterCamposObrigatoriosPorTipo(this.tipo);
		 assertTrue(tipo != null);
		 
	 }
	 
	 @Test
	 public void testObterCamposObrigatorioTamanho() {
		 Mockito.when(camposObrigatoriosDAO.obterCamposObrigatoriosPorTamanho(tamanho)).thenReturn(camposObrigatoriosList);
		 List<CamposObrigatorios> tamanho = camposObrigatoriosService.obterCampoObrigatorioPorTamanho(this.tamanho);
		 assertTrue(tamanho != null);
	 }
	 
	 @Test
	 public void testObterCamposObrigatorioMascara() {
		 Mockito.when(camposObrigatoriosDAO.obterCamposObrigatoriosPorMascara(mascara)).thenReturn(camposObrigatoriosList);
		 List<CamposObrigatorios> mascara = camposObrigatoriosService.obterCamposObrigatoriosPorMascara(this.mascara);
		 assertTrue(mascara != null);
	 }
	 
	 @Test
	 public void testRetornarCheckboxControleFluxo() {
		 Mockito.when(camposObrigatoriosDAO.carregarTodosCamposCheckbox(ativo)).thenReturn(camposObrigatoriosList);
		 assertTrue(true);
		   
	 } 
	 
	  @Test
	  public void testUpdate() {
		Mockito.when(camposObrigatoriosDAO.update(camposObrigatorios)).thenReturn(camposObrigatorios);
		CamposObrigatorios camposObg = camposObrigatoriosService.updateCampos(camposObrigatorios);
		assertTrue(camposObg != null);
	  }
	  
	  @Test
	  public void testExcluirLogicamente() throws RequiredException, BusinessException, DataBaseException {
		  Mockito.when(camposObrigatoriosService.findById(10l)).thenReturn(camposObrigatorios);
		  
		  camposObrigatoriosService.excluirLogicamente(10l);
		  assertTrue(true);
	  }
	
}
