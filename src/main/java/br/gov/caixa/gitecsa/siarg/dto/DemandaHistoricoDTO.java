package br.gov.caixa.gitecsa.siarg.dto;

import java.util.Date;

public class DemandaHistoricoDTO implements Comparable<DemandaHistoricoDTO> {

  private Long id;

  private Integer ordem;

  private String acao;

  private String origem;

  private String destino;

  private String matricula;
  
  private String nomeUsuario;

  private String email;

  private String descricao;

  private Date dataRecebimento;

  private Date dataAtendimento;
  
  private String classeCor;
  
  private String anexo;

  public String getAcao() {
    return acao;
  }

  public void setAcao(String acao) {
    this.acao = acao;
  }

  public String getOrigem() {
    return origem;
  }

  public void setOrigem(String origem) {
    this.origem = origem;
  }

  public String getDestino() {
    return destino;
  }

  public void setDestino(String destino) {
    this.destino = destino;
  }

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDataRecebimento() {
    return dataRecebimento;
  }

  public void setDataRecebimento(Date dataRecebimento) {
    this.dataRecebimento = dataRecebimento;
  }

  public Date getDataAtendimento() {
    return dataAtendimento;
  }

  public void setDataAtendimento(Date dataAtendimento) {
    this.dataAtendimento = dataAtendimento;
  }

  @Override
  public int compareTo(DemandaHistoricoDTO o) {
    return this.id.compareTo(o.getId());
  }

  public String getClasseCor() {
    return classeCor;
  }

  public void setClasseCor(String classeCor) {
    this.classeCor = classeCor;
  }

  public Integer getOrdem() {
    return ordem;
  }

  public void setOrdem(Integer ordem) {
    this.ordem = ordem;
  }

  public String getAnexo() {
    return anexo;
  }

  public void setAnexo(String anexo) {
    this.anexo = anexo;
  }

  public String getNomeUsuario() {
    return nomeUsuario;
  }

  public void setNomeUsuario(String nomeUsuario) {
    this.nomeUsuario = nomeUsuario;
  }

}
