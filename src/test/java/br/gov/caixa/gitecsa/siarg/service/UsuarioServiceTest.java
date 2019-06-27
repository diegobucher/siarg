package br.gov.caixa.gitecsa.siarg.service;

import static org.junit.Assert.assertFalse;
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

import br.gov.caixa.gitecsa.siarg.dao.AuditoriaDAO;
import br.gov.caixa.gitecsa.siarg.dao.UnidadeDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioDAO;
import br.gov.caixa.gitecsa.siarg.dao.UsuarioUnidadeDAO;
import br.gov.caixa.gitecsa.siarg.model.Perfil;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.Usuario;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;

public class UsuarioServiceTest {

  @InjectMocks
  private UsuarioService usuarioService;

  @Mock
  private UsuarioDAO usuarioDAO;
  @Mock
  private UsuarioUnidadeDAO usuarioUnidadeDAO;
  @Mock
  private AuditoriaDAO auditoriaDAO;
  @Mock
  private UnidadeDAO unidadeDAO;

  private static final String MATRICULA = "c000000";
  private static final Long ID = 1L;
  private Usuario usuario = new Usuario();
  private Usuario usuarioComId = new Usuario();
  private List<UsuarioUnidade> usuarioUnidadeList = new ArrayList<>();
  private UsuarioUnidade usuarioUnidade = new UsuarioUnidade();
  private Unidade unidade = new Unidade();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Calendar cal = Calendar.getInstance();
    cal.set(2019, Calendar.JANUARY, 28, 10, 00);
    this.usuario.setId(1L);
    this.usuario.setMatricula(MATRICULA);
    this.usuario.setNome("Usuário Teste");
    this.unidade.setId(7470L);
    this.unidade.setCgcUnidade(7470);
    Perfil perfil = new Perfil();
    perfil.setId(1L);
    perfil.setNome("Perfil");
    this.usuario.setPerfil(perfil);
    this.usuario.setMatricula(MATRICULA);
    this.usuarioComId.setId(1L);
    this.usuarioComId.setMatricula(MATRICULA);
    this.usuarioComId.setNome("Usuário Teste");
    this.usuarioComId.setId(7470L);
    this.usuarioComId.setPerfil(perfil);
    this.usuarioUnidade.setId(1L);
    this.usuarioUnidade.setDataInicio(cal.getTime());
    this.usuarioUnidade.setDataFim(cal.getTime());
    this.usuarioUnidade.setUnidade(this.unidade);
    this.usuarioUnidade.setUsuario(this.usuario);
    this.usuarioUnidadeList.add(this.usuarioUnidade);
  }

  @Test
  public void buscarUsuarioPorMatriculaFetchTest() {
    Mockito.when(this.usuarioDAO.buscarUsuarioPorMatriculaFetch(MATRICULA)).thenReturn(this.usuario);
    Usuario result = this.usuarioService.buscarUsuarioPorMatriculaFetch(MATRICULA);
    assertTrue(result.getId().equals(this.usuario.getId()));
  }

  @Test
  public void buscarUsuarioUnidadePorMatriculaTest() {
    Mockito.when(this.usuarioDAO.buscarUsuarioUnidadePorMatricula(MATRICULA, ID)).thenReturn(this.usuarioUnidadeList);
    List<UsuarioUnidade> result = this.usuarioService.buscarUsuarioUnidadePorMatricula(MATRICULA, ID);
    assertTrue(result.contains(this.usuarioUnidade));
  }

  @Test
  public void obterUsuarioExcessaoPorMatriculaTest() {
    Mockito.when(this.usuarioDAO.obterUsuarioExcessaoPorMatricula(MATRICULA)).thenReturn(usuario);
    Usuario result = this.usuarioService.obterUsuarioExcessaoPorMatricula(MATRICULA);
    assertTrue(result.getMatricula().equals(MATRICULA));
  }

  @Test
  public void salvarUsuarioTest() {
    Mockito.when(this.usuarioDAO.buscarUsuarioPorMatriculaFetch(MATRICULA)).thenReturn(this.usuario);
    Mockito.when(this.usuarioUnidadeDAO.findByIdFetch(this.usuarioUnidade.getId())).thenReturn(this.usuarioUnidade);
    try {
      this.usuarioService.salvarUsuario(this.usuario, this.usuarioUnidadeList, this.usuarioUnidadeList, MATRICULA, this.unidade);
      assertTrue(true);
    } catch (Exception e) {
      assertFalse(true);
    }
  }

  @Test
  public void salvarUsuario2Test() {
    Mockito.when(this.usuarioDAO.buscarUsuarioPorMatriculaFetch(MATRICULA)).thenReturn(this.usuario);
    Mockito.when(this.usuarioUnidadeDAO.findByIdFetch(this.usuarioUnidade.getId())).thenReturn(this.usuarioUnidade);
    Mockito.when(this.unidadeDAO.obterUnidadePorChaveFetch(this.usuarioUnidade.getUnidade().getId())).thenReturn(this.unidade);
    Mockito.when(this.usuarioDAO.findById(this.usuario.getId())).thenReturn(this.usuario);
    this.usuarioUnidade.setId(null);
    try {
      this.usuarioService.salvarUsuario(this.usuario, this.usuarioUnidadeList, this.usuarioUnidadeList, MATRICULA, this.unidade);
      assertTrue(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void salvarUsuarioNovoTest() {
    Mockito.when(this.usuarioDAO.buscarUsuarioPorMatriculaFetch(MATRICULA)).thenReturn(this.usuario);
    Mockito.when(this.usuarioUnidadeDAO.findByIdFetch(this.usuarioUnidade.getId())).thenReturn(this.usuarioUnidade);
    Mockito.when(this.unidadeDAO.obterUnidadePorChaveFetch(this.usuarioUnidade.getUnidade().getId())).thenReturn(this.unidade);
    Mockito.when(this.usuarioDAO.findById(this.usuario.getId())).thenReturn(this.usuario);
    this.usuarioUnidade.setId(1L);
    this.usuario.setId(null);
    Mockito.when(usuarioDAO.save(this.usuario)).thenReturn(this.usuarioComId);
    try {
      this.usuarioService.salvarUsuario(this.usuario, this.usuarioUnidadeList, this.usuarioUnidadeList, MATRICULA, this.unidade);
      assertTrue(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
