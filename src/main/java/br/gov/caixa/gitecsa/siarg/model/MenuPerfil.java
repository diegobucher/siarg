package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "argtb06_menu_perfil", schema = "argsm001")
public class MenuPerfil implements Serializable {

  private static final long serialVersionUID = 7928568929987709028L;
  
  private MenuPerfilPK id;

  private Menu menu;
  
  private Perfil perfil;
  
  private Abrangencia abrangencia;
  
  public MenuPerfil() {
    super();
  }
  
  @Id
  public MenuPerfilPK getId() {
    return id;
  }

  public void setId(MenuPerfilPK id) {
    this.id = id;
  }
  

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "nu_menu", insertable=false, updatable=false)
  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "nu_perfil", insertable=false, updatable=false)
  public Perfil getPerfil() {
    return perfil;
  }

  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "nu_abrangencia", insertable=false, updatable=false)
  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }


}
