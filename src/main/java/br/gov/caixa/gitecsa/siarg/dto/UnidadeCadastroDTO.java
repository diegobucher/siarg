package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;

public class UnidadeCadastroDTO implements Serializable{
  
  private static final long serialVersionUID = 1L;

  /** Identificador da unidade. Chave primária. */
  private Long id;

  /** Campo que armazena o CGC da unidade. */
  private Integer cgcUnidade;

  /** Campo que armazena a sigla da unidade. */
  private String sigla;

  /** Campo que armazena o nome da unidade. */
  private String nome;

  /** Campo que armazena o tipo da unidade. */
  private TipoUnidadeEnum tipoUnidade;

  /** Campo que armazena o status da unidade. Ativa - 1. Inativa - 0. */
  private Boolean ativo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getCgcUnidade() {
    return cgcUnidade;
  }

  public void setCgcUnidade(Integer cgcUnidade) {
    this.cgcUnidade = cgcUnidade;
  }

  public String getSigla() {
    return sigla;
  }

  public void setSigla(String sigla) {
    this.sigla = sigla;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public TipoUnidadeEnum getTipoUnidade() {
    return tipoUnidade;
  }

  public void setTipoUnidade(TipoUnidadeEnum tipoUnidade) {
    this.tipoUnidade = tipoUnidade;
  }

  public String getTipoUnidadeStr() {
    
    if(TipoUnidadeEnum.MATRIZ.equals(tipoUnidade)) {
      return "Matriz";
    } else if(TipoUnidadeEnum.FILIAL.equals(tipoUnidade)) {
      return "Rede";
    } else if(TipoUnidadeEnum.EXTERNA.equals(tipoUnidade)) {
      return "Unidade Externa";
    }
    
    return "";
  }

  public Boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }

}