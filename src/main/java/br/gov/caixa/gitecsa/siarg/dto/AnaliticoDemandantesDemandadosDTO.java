package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class AnaliticoDemandantesDemandadosDTO implements Serializable, Comparable<AnaliticoDemandantesDemandadosDTO> {

  /** Serial */
  private static final long serialVersionUID = 8141072410783647124L;

  private Unidade unidadeDemandante;

  private String assunto;

  private String sigla;

  private String cgc;

  private String aberta;

  private String fechada;

  private Long qtdAberta;

  private Long qtdFechada;

  private Unidade unidadeDemandada;
  
  private String demandadas;

  /**
   * @return the unidadeDemandante
   */
  public Unidade getUnidadeDemandante() {
    return unidadeDemandante;
  }

  /**
   * @param unidadeDemandante the unidadeDemandante to set
   */
  public void setUnidadeDemandante(Unidade unidadeDemandante) {
    this.unidadeDemandante = unidadeDemandante;
  }

  /**
   * @return the assunto
   */
  public String getAssunto() {
    return assunto;
  }

  /**
   * @param assunto the assunto to set
   */
  public void setAssunto(String assunto) {
    this.assunto = assunto;
  }

  /**
   * @return the qtdAberta
   */
  public Long getQtdAberta() {
    return qtdAberta;
  }

  /**
   * @param qtdAberta the qtdAberta to set
   */
  public void setQtdAberta(Long qtdAberta) {
    this.qtdAberta = qtdAberta;
  }

  /**
   * @return the qtdFechada
   */
  public Long getQtdFechada() {
    return qtdFechada;
  }

  /**
   * @param qtdFechada the qtdFechada to set
   */
  public void setQtdFechada(Long qtdFechada) {
    this.qtdFechada = qtdFechada;
  }

  /**
   * @return the unidadeDemandada
   */
  public Unidade getUnidadeDemandada() {
    return unidadeDemandada;
  }

  /**
   * @param unidadeDemandada the unidadeDemandada to set
   */
  public void setUnidadeDemandada(Unidade unidadeDemandada) {
    this.unidadeDemandada = unidadeDemandada;
  }

  /**
   * @return the sigla
   */
  public String getSigla() {
    this.sigla = this.unidadeDemandante.getSigla();
    return this.sigla;
  }

  /**
   * @param sigla the sigla to set
   */
  public void setSigla(String sigla) {
    this.sigla = sigla;
  }

  /**
   * @return the cgc
   */
  public String getCgc() {
    this.cgc = String.format("%04d", this.unidadeDemandante.getCgcUnidade());
    return this.cgc;
  }

  /**
   * @param cgc the cgc to set
   */
  public void setCgc(String cgc) {
    this.cgc = cgc;
  }

  /**
   * @return the aberta
   */
  public String getAberta() {
    this.aberta = this.qtdAberta.toString();
    return this.aberta;
  }

  /**
   * @param aberta the aberta to set
   */
  public void setAberta(String aberta) {
    this.aberta = aberta;
  }

  /**
   * @return the fechada
   */
  public String getFechada() {
    this.fechada = this.qtdFechada.toString();
    return this.fechada;
  }

  /**
   * @param fechada the fechada to set
   */
  public void setFechada(String fechada) {
    this.fechada = fechada;
  }
  
  /**
   * @return the demandadas
   */
  public String getDemandadas() {
    this.demandadas = "";
    if (this.unidadeDemandada.getCgcUnidade() != null) {
      this.demandadas += this.unidadeDemandada.getCgcUnidade();
    }
    if (this.unidadeDemandada.getCgcUnidade() != null && this.unidadeDemandada.getSigla() != null) {
      this.demandadas += " - ";
    }
    if (this.unidadeDemandada.getSigla() != null) {
      this.demandadas += this.unidadeDemandada.getSigla();
    }
    return demandadas;
  }

  /**
   * @param demandadas the demandadas to set
   */
  public void setDemandadas(String demandadas) {
    this.demandadas = demandadas;
  }

  public String getUnidadeDemandanteCompleta() {
    String retorno = "";
    if (this.unidadeDemandante != null) {
      if (this.unidadeDemandante.getCgcUnidade() != null) {
        retorno += String.format("%04d", this.unidadeDemandante.getCgcUnidade());
      }
      if (this.unidadeDemandante.getCgcUnidade() != null && this.unidadeDemandante.getSigla() != null) {
        retorno += " - ";
      }
      if (this.unidadeDemandante.getSigla() != null) {
        retorno += this.unidadeDemandante.getSigla();
      }
    }
    return retorno;
  }

  public String getUnidadeDemandadaCompleta() {
    String retorno = "";
    if (this.unidadeDemandada != null) {
      if (this.unidadeDemandada.getCgcUnidade() != null) {
        retorno += String.format("%04d", this.unidadeDemandada.getCgcUnidade());
      }
      if (this.unidadeDemandada.getCgcUnidade() != null && this.unidadeDemandada.getSigla() != null) {
        retorno += " - ";
      }
      if (this.unidadeDemandada.getSigla() != null) {

        retorno += this.unidadeDemandada.getSigla();
      }
    }
    return retorno;
  }

  @Override
  public int compareTo(AnaliticoDemandantesDemandadosDTO dto) {
    return this.unidadeDemandante.getSigla().compareTo(dto.getUnidadeDemandante().getSigla());
  }

}
