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

import br.gov.caixa.gitecsa.arquitetura.exception.CaixaPostalException;
import br.gov.caixa.gitecsa.ldap.usuario.UsuarioLdap;
import br.gov.caixa.gitecsa.siarg.dao.AbrangenciaDAO;
import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Perfil;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.dto.UsuarioSistemaDTO;
import br.gov.caixa.gitecsa.siico.service.FuncaoEmpregadoGerenteService;

public class ControleAcessoServiceTest {
  
  private static final int _7470 = 7470;

  @InjectMocks
  private ControleAcessoService controleAcessoService;
  
  @Mock
  private UsuarioDAO usuarioDAO;
  
  @Mock
  private UnidadeDAO unidadeDAO;
  
  @Mock
  private PerfilDAO perfilDAO;

  @Mock
  private AbrangenciaDAO abrangenciaDAO;
  
  @Mock
  private FuncaoEmpregadoGerenteService funcaoEmpregadoGerenteService;

  private UsuarioLdap usuarioLdap1;
  private UsuarioLdap usuarioLdap2;
  
  private Usuario usuario1;

  private Usuario usuario2;
  
  private Perfil perfilId1;

  private Perfil perfilId2;

  private Unidade unidade;

  private List<Unidade> unidadeList;
  
  private List<Abrangencia> abrangenciaList;

  @Before
  public void setUp() throws Exception {
    
    MockitoAnnotations.initMocks(this);

    perfilId1 = new Perfil();
    perfilId1.setId(1l);

    perfilId2 = new Perfil();
    perfilId2.setId(2l);
    
    usuarioLdap1 = new UsuarioLdap();
    usuarioLdap1.setNuMatricula("1");
    usuarioLdap1.setCoUnidade(_7470);
    usuarioLdap1.setNuFuncao(1);

    usuarioLdap2 = new UsuarioLdap();
    usuarioLdap2.setNuMatricula("2");
    usuarioLdap2.setCoUnidade(_7470);
    usuarioLdap2.setNuFuncao(1);
    
    unidade = new Unidade();
    unidade.setId(1l);
    
    usuario1 = new Usuario();
    usuario1.setId(1l);
    usuario1.setPerfil(perfilId1);

    usuario2 = new Usuario();
    usuario2.setId(2l);
    usuario2.setPerfil(perfilId2);
    
    abrangenciaList=new ArrayList<>();
    Abrangencia abrangencia = new Abrangencia();
    abrangencia.setId(1l);
    abrangenciaList.add(abrangencia);
    
    unidadeList = new ArrayList<>();
    Unidade unidade = new Unidade();
    unidade.setAbrangencia(abrangencia);
    unidade.setId(1L);
    CaixaPostal cx = new CaixaPostal();
    cx.setAtivo(true);
    cx.setId(1l);
    unidade.setCaixasPostaisList(new ArrayList());
    unidade.getCaixasPostaisList().add(cx);
    unidadeList.add(unidade);
  }

  /**/
  @Test
  public void testObterPerfilCompletoDoUsuarioLogado1() throws CaixaPostalException {
    
    Mockito.when(perfilDAO.obterPerfilPorId(1l)).thenReturn(perfilId1);
    
    Mockito.when(abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(1l)).thenReturn(abrangenciaList);
    
    Mockito.when(usuarioDAO.obterUsuarioExcessaoPorMatricula("1")).thenReturn(usuario1);
    
    Mockito.when(unidadeDAO.obterUnidadeUsuarioLogado(_7470)).thenReturn(unidade);

    Mockito.when(unidadeDAO.obterListaUnidadeUsuarioLogado(_7470)).thenReturn(unidadeList);
    Mockito.when(unidadeDAO.obterListaUnidadesUsuarioExcessao(1L)).thenReturn(unidadeList);

//    Mockito.when(unidadeDAO.obterListaUnidadesPorAbrangencia(1l)).thenReturn(unidadeList);

    UsuarioSistemaDTO usuarioSistemaDTO = controleAcessoService.obterPerfilCompletoDoUsuarioLogado(usuarioLdap1);
    assertTrue(usuarioSistemaDTO != null); 
  }

  
  /**/
  @Test
  public void testObterPerfilCompletoDoUsuarioLogado2() throws CaixaPostalException {
    
    Mockito.when(perfilDAO.obterPerfilPorId(2l)).thenReturn(perfilId2);
    
    Mockito.when(abrangenciaDAO.obterListaAbrangeciaDasUnidadesUsuarioExcessao(2l)).thenReturn(abrangenciaList);
    
    Mockito.when(usuarioDAO.obterUsuarioExcessaoPorMatricula("2")).thenReturn(usuario2);
    
    Mockito.when(unidadeDAO.obterUnidadeUsuarioLogado(7470)).thenReturn(unidade);
    
    Mockito.when(unidadeDAO.obterListaUnidadeUsuarioLogado(2)).thenReturn(unidadeList);
    Mockito.when(unidadeDAO.obterListaUnidadesUsuarioExcessao(2L)).thenReturn(unidadeList);
    
//    Mockito.when(unidadeDAO.obterListaUnidadesPorAbrangencia(2l)).thenReturn(unidadeList);
    
    
    UsuarioSistemaDTO usuarioSistemaDTO = controleAcessoService.obterPerfilCompletoDoUsuarioLogado(usuarioLdap2);
    assertTrue(usuarioSistemaDTO != null); 
  }
   
  
  @Test
  public void testObterPerfilCompletoDoUsuarioLogadoSemExcecao() throws CaixaPostalException {
    
    Mockito.when(usuarioDAO.obterUsuarioExcessaoPorMatricula("2")).thenReturn(null);
    Mockito.when(unidadeDAO.obterListaUnidadeUsuarioLogado(_7470)).thenReturn(unidadeList);

    UsuarioSistemaDTO usuarioSistemaDTO = controleAcessoService.obterPerfilCompletoDoUsuarioLogado(usuarioLdap2);
    assertTrue(usuarioSistemaDTO != null); 
  }
//  
//  @Test
//  public void  @Test
//  public void testObterPerfilCompletoDoUsuarioLogado2() throws CaixaPostalException {() throws CaixaPostalException {
//    
//  }
//  
}
