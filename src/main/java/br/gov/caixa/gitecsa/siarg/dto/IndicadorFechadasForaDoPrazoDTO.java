package br.gov.caixa.gitecsa.siarg.dto;

public class IndicadorFechadasForaDoPrazoDTO {
	
	private String caixaPostal;
	private Long qtFechadaPrazo;
	private Long qtFechadaForaPrazo;
	private String indicador;
	
	public IndicadorFechadasForaDoPrazoDTO() {
		qtFechadaPrazo = 0L;
		qtFechadaForaPrazo = 0L;
	}


	public String getCaixaPostal() {
		return caixaPostal;
	}
	
	public void setCaixaPostal(String caixaPostal) {
		this.caixaPostal = caixaPostal;
	}

	public Long getQtFechadaPrazo() {
		return qtFechadaPrazo;
	}

	public void setQtFechadaPrazo(Long qtFechadaPrazo) {
		this.qtFechadaPrazo = qtFechadaPrazo;
	}

	public Long getQtFechadaForaPrazo() {
		return qtFechadaForaPrazo;
	}

	public void setQtFechadaForaPrazo(Long qtFechadaForaPrazo) {
		this.qtFechadaForaPrazo = qtFechadaForaPrazo;
	}


	public String getIndicador() {
		return indicador;
	}


	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}


	

	
	

}
