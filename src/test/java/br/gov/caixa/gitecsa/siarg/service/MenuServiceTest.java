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

import br.gov.caixa.gitecsa.siarg.dao.MenuDAO;
import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.model.Menu;
import br.gov.caixa.gitecsa.siarg.model.Perfil;

public class MenuServiceTest {

  @InjectMocks
  private MenuService menuService;
  @Mock
  private MenuDAO menuDAO;
  @Mock
  private PerfilDAO perfilDAO;
  private Long idPperfil;
  private Long idAbrangencia;
  private Menu menu = new Menu();
  private Menu menuFilho = new Menu();
  private List<Menu> menuList = new ArrayList<>();
  private List<Menu> menuFilhosList = new ArrayList<>();
  private Perfil perfil = new Perfil();
  private List<Perfil> listaPerfis = new ArrayList<>();
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.idPperfil = 1L;
    this.idAbrangencia = 1L;
    this.menu.setId(1L);
    this.menu.setLink("www.caixa.gov.br");
    this.perfil.setId(1L);
    this.perfil.setNome("Perfil");
    this.listaPerfis.add(this.perfil);
    this.menu.setListaPerfis(this.listaPerfis);
    this.menuFilho.setId(1L);
    this.menuFilhosList.add(this.menuFilho);
    this.menu.setMenuFilhosList(this.menuFilhosList);
    this.menuList.add(this.menu);
  }

  @Test
  public void obterListaMenusPaisTest() {
    Mockito.when(this.menuDAO.obterListaMenusPaisPorPerfil(this.idPperfil, this.idAbrangencia)).thenReturn(this.menuList);
    Mockito.when(this.perfilDAO.findById(this.idPperfil)).thenReturn(this.perfil);
    Mockito.when(this.menuDAO.findByIdEager(this.menuFilho.getId(), this.idAbrangencia)).thenReturn(this.menu);
    List<Menu> listMenu = this.menuService.obterListaMenusPais(this.idPperfil, this.idAbrangencia);
    assertTrue(listMenu.size() > 0);
  }

}
