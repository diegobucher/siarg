package br.gov.caixa.gitecsa.siarg.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;

/**
 * Entity implementation class for Entity: argsm001.argtb23_campo.
 * 
 * @author f763644
 */
@Entity
@Table(name = "argtb23_campo", schema = "argsm001")
public class CamposObrigatorios implements Serializable {

	/** Serial. */
	private static final long serialVersionUID = -996277596358105284L;

	/** Identificador dos campos obrigatorios. Chave primária. */
	private Long id;

	/** Nome dos campos obrigatorios */
	private String nome;
	
	/** Descrição dos campos obrigatorios */
	private String descricao;

	/** Tipo dos campos obrigatórios */
	private TipoCampoEnum tipo;

	/** Tamanho dos campos obrigatórios */
	private Integer tamanho;

	/** Mascara dos campos obrigatórios */
	private String mascara;
	
	/** Campo que armazena o status da unidade. Ativa - 1. Inativa - 0. */
	private Boolean ativo;

	/**Campo checado TRUE/FALSE */
	private boolean marcado;
	
	/**Nova coluna - quantidade*/
	private int quantidade;
	
	private String demandaCampo;
	
	public CamposObrigatorios() {
		super();
	}
	
	public CamposObrigatorios(Long id, String nome, String descricao, TipoCampoEnum tipo, Integer tamanho, String mascara, int quantidade) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.tipo = tipo;
		this.tamanho = tamanho;
		this.mascara = mascara;
		this.quantidade = quantidade;
	}


	/**
	 * Obter o valor do Atributo.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nu_campo", unique = true, nullable = false, columnDefinition = "serial")
	public Long getId() {
		return this.id;
	}

	/**
	 * Gravar o valor do Atributo.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	
	/**
	 * Obter o valor do Atributo.
	 * 
	 * @return the nome
	 */
	@Column(name = "no_campo", nullable = false, length = 50)
	public String getNome() {
		return this.nome;
	}

	
	public void setNome(final String no_campo) {
		this.nome = no_campo;
	}
	
	@Column(name = "de_campo", nullable = false, length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * Obter o valor 
	 * 
	 * @return the ic_tipo_campo
	 */
	@Column(name= "ic_tipo_campo", nullable = false, columnDefinition = "int2" )
	public TipoCampoEnum getTipo() {
		return tipo;
	}

	/**
	 * Gravar o valor do atributo
	 * 
	 * @param tipo
	 */
	
	public void setTipo(final TipoCampoEnum tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obter o valor do numero do tamanho do campo obrigatório
	 * 
	 * @return nu_tamanho_campo
	 */
	@Column(name ="nu_tamanho_campo", nullable = true, columnDefinition = "int2")
	public Integer getTamanho() {
		return this.tamanho;
	}
	
	/**
	 * Obter Numero que especifica o tamanho do campo obrigatório.
	 * 
	 * @param nu_tamanho_campo
	 */
	public void setTamanho(final Integer nu_tamanho_campo) {
		this.tamanho = nu_tamanho_campo;
	}


	/**
	 * 
	 * 
	 * @return Descricao da mascara do campo obrigatório.
	 */
	@Column(name = "de_mascara_campo", nullable = true, length = 30)
	public String getMascara() {
		return this.mascara;
	}
	
	/**
	 * Obter Descricao da mascara do campo obrigatório.
	 * 
	 * @param de_mascara_campo
	 */
	public void setMascara(final String de_mascara_campo) {
		this.mascara = de_mascara_campo;
	}
	
	  /**
	   * Obter o valor padrão.
	   * @return the ativo
	   */
	  @Column(name = "ic_ativo")
	  public Boolean getAtivo() {
	    return this.ativo;
	  }

	  /**
	   * Gravar o valor do parâmetro.
	   * @param ativo the ativo to set
	   */
	  public void setAtivo(Boolean ativo) {
	    this.ativo = ativo;
	  }

	@Transient
	public boolean getMarcado() {
		return marcado;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	@Transient
	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	@Transient
	public String getDemandaCampo() {
		return demandaCampo;
	}

	public void setDemandaCampo(String demandaCampo) {
		this.demandaCampo = demandaCampo;
	}
	
}
