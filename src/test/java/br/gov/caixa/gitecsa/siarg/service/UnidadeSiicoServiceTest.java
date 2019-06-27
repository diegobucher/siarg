package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.dao.SiicoDAOImpl;
import br.gov.caixa.gitecsa.siico.dao.UnidadeSiicoDAO;
import br.gov.caixa.gitecsa.siico.dao.impl.UnidadeSiicoDAOImpl;
import br.gov.caixa.gitecsa.siico.service.UnidadeSiicoService;
import br.gov.caixa.gitecsa.siico.vo.UnidadeSiicoVO;

public class UnidadeSiicoServiceTest {
	
	@InjectMocks
	UnidadeSiicoService unidadeSiicoService;
	
	@Mock
	UnidadeSiicoDAO unidadeSiicoDAO;
	
	//@Mock
	UnidadeSiicoVO unidadeSiicoVo;
	
	private Integer numeroCGC = 7470;
	private Integer id = 33;
    private String nome = "Teste";
    private String sigla = "T";
	private String siglaTipo = "GI";
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		unidadeSiicoVo = new UnidadeSiicoVO();		
		this.unidadeSiicoVo.setId(id);
		this.unidadeSiicoVo.setNome(nome);
		this.unidadeSiicoVo.setSigla(sigla);
		this.unidadeSiicoVo.setSiglaTipo(siglaTipo);
		
		
	}
	
	@Test
	public void obterUnidadePorCGCTest() {
	 Mockito.when(this.unidadeSiicoDAO.obterUnidadePorCGC(numeroCGC)).thenReturn(unidadeSiicoVo);
	 UnidadeSiicoVO unidadeSiicoVO = this.unidadeSiicoService.obterUnidadePorCGC(this.numeroCGC);
	 
	 
	 assertNotNull(unidadeSiicoVO);	 
 
	}

}
