/**
 * MenuService.java
 * Versão: 1.0.0.00
 * Data de Criação : 27-11-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;

import br.gov.caixa.gitecsa.siarg.dao.MenuDAO;
import br.gov.caixa.gitecsa.siarg.dao.PerfilDAO;
import br.gov.caixa.gitecsa.siarg.model.Menu;
import br.gov.caixa.gitecsa.siarg.model.Perfil;

/**
 * Classe de serviços de menu.
 * @author f520296
 */
@Stateless
public class MenuService {

  @Inject
  private MenuDAO menuDAO;

  @Inject
  private PerfilDAO perfilDAO;

  public List<Menu> obterListaMenusPais(Long idPperfil, Long idAbrangencia) {

    List<Menu> listaMenusPais = menuDAO.obterListaMenusPaisPorPerfil(idPperfil, idAbrangencia);
    Perfil perfil = perfilDAO.findById(idPperfil);

    List<Menu> menuSistemaList = new ArrayList<>();
    Menu menuPaiTemp;
    List<Menu> menuFilhoTemp;

    for (Menu menu : listaMenusPais) {
      Hibernate.initialize(menu.getMenuFilhosList());
      menuPaiTemp = menu;
      menuFilhoTemp = new ArrayList<>();
      for (Menu filhoTemp : menuPaiTemp.getMenuFilhosList()) {
        Menu temp = menuDAO.findByIdEager(filhoTemp.getId(), idAbrangencia);
        
        if(temp != null && temp.getListaPerfis().contains(perfil)) {
            menuFilhoTemp.add(temp);
        }
        
      }
      menuPaiTemp.setMenuFilhosList(menuFilhoTemp);
      menuSistemaList.add(menuPaiTemp);
    }
    return menuSistemaList;
  }

}
