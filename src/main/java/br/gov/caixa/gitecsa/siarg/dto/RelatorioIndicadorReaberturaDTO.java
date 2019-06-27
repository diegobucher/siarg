package br.gov.caixa.gitecsa.siarg.dto;

import java.io.Serializable;

import br.gov.caixa.gitecsa.arquitetura.util.Util;

public class RelatorioIndicadorReaberturaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1601839286475731992L;
	
	private String caixaPostalEnvolvida;
	private Long qtdDemandasAbertas;
	private Long qtdDemandasReabertas;
	private String indicador;
	
	public RelatorioIndicadorReaberturaDTO(String caixaPostalEnvolvida, Long qtdDemandasAbertas, Long qtdDemandasReabertas) {
		this.caixaPostalEnvolvida = caixaPostalEnvolvida;
		this.qtdDemandasAbertas = qtdDemandasAbertas;
		this.qtdDemandasReabertas = qtdDemandasReabertas;
	}
	
	public RelatorioIndicadorReaberturaDTO() {
		this.qtdDemandasAbertas = 0L;
		this.qtdDemandasReabertas = 0L;
	}
	
	public String getCaixaPostalEnvolvida() {
		return caixaPostalEnvolvida;
	}
	public void setCaixaPostalEnvolvida(String caixaPostalEnvolvida) {
		this.caixaPostalEnvolvida = caixaPostalEnvolvida;
	}
	public Long getQtdDemandasAbertas() {
		return qtdDemandasAbertas;
	}
	public void setQtdDemandasAbertas(Long qtdDemandasAbertas) {
		this.qtdDemandasAbertas = qtdDemandasAbertas;
	}
	public Long getQtdDemandasReabertas() {
		return qtdDemandasReabertas;
	}
	public void setQtdDemandasReabertas(Long qtdDemandasReabertas) {
		this.qtdDemandasReabertas = qtdDemandasReabertas;
	}
	
	public String getIndicadorPercentualReabertura() {
//		if (this.qtdDemandasAbertas != null && this.qtdDemandasReabertas != null && this.qtdDemandasAbertas > 0) {
//			return (double)((qtdDemandasReabertas * 100) / qtdDemandasAbertas);
//		}
//		return (double) 0;
		return Util.converterDecimalFormatado(this.qtdDemandasReabertas.doubleValue(), this.qtdDemandasAbertas.doubleValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caixaPostalEnvolvida == null) ? 0 : caixaPostalEnvolvida.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelatorioIndicadorReaberturaDTO other = (RelatorioIndicadorReaberturaDTO) obj;
		if (caixaPostalEnvolvida == null) {
			if (other.caixaPostalEnvolvida != null)
				return false;
		} else if (!caixaPostalEnvolvida.equals(other.caixaPostalEnvolvida))
			return false;
		return true;
	}

	public String getIndicador() {
		return indicador;
	}

	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}

}
