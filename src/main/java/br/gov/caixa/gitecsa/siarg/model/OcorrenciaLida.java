package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "argtb20_ocorrencias_lidas", schema = "argsm001")
public class OcorrenciaLida implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nu_ocorrencias_lidas")
  private Long id;

  @Column(name = "co_matricula", columnDefinition="bpchar")
  private String matricula;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dh_leitura")
  private Date dataHoraLeitura;
  
  @ManyToOne(targetEntity = Abrangencia.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "nu_abrangencia", nullable = false)
  private Abrangencia abrangencia;

  public OcorrenciaLida() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMatricula() {
    return this.matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public Date getDataHoraLeitura() {
    return dataHoraLeitura;
  }

  public void setDataHoraLeitura(Date dataHoraLeitura) {
    this.dataHoraLeitura = dataHoraLeitura;
  }

  public Abrangencia getAbrangencia() {
    return abrangencia;
  }

  public void setAbrangencia(Abrangencia abrangencia) {
    this.abrangencia = abrangencia;
  }

}