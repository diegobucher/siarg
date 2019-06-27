package br.gov.caixa.gitecsa.siarg.dto;

public class DemandaVinculadaDTO implements Comparable<DemandaVinculadaDTO> {

  private Long id;
  
  private String unidadeDemandada;
  
  private String situacao;
  
  private String responsavelAtual;

  private String classePrazo;

  @Override
  public int compareTo(DemandaVinculadaDTO o) {
    return this.id.compareTo(o.getId());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUnidadeDemandada() {
    return unidadeDemandada;
  }

  public void setUnidadeDemandada(String unidadeDemandada) {
    this.unidadeDemandada = unidadeDemandada;
  }

  public String getSituacao() {
    return situacao;
  }

  public void setSituacao(String situacao) {
    this.situacao = situacao;
  }

  public String getResponsavelAtual() {
    return responsavelAtual;
  }

  public void setResponsavelAtual(String responsavelAtual) {
    this.responsavelAtual = responsavelAtual;
  }

  public String getClassePrazo() {
    return classePrazo;
  }

  public void setClassePrazo(String classePrazo) {
    this.classePrazo = classePrazo;
  }


}
