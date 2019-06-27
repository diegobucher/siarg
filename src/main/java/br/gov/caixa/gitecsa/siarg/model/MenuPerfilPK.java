package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MenuPerfilPK implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1L;

  /** Chave primária. */
  private Long nuMenu;

  /** Chave primária. */
  private Long nuPerfil;

  /** Chave primária. */
  private Long nuAbrangencia;

  /** Construtor. */
  public MenuPerfilPK() {
    super();
  }

  @Column(name = "nu_menu")
  public Long getNuMenu() {
    return nuMenu;
  }

  public void setNuMenu(Long nuMenu) {
    this.nuMenu = nuMenu;
  }

  @Column(name = "nu_perfil")
  public Long getNuPerfil() {
    return nuPerfil;
  }

  public void setNuPerfil(Long nuPerfil) {
    this.nuPerfil = nuPerfil;
  }

  @Column(name = "nu_abrangencia")
  public Long getNuAbrangencia() {
    return nuAbrangencia;
  }

  public void setNuAbrangencia(Long nuAbrangencia) {
    this.nuAbrangencia = nuAbrangencia;
  }

}
