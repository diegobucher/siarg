package br.gov.caixa.gitecsa.siarg.enumerator;

public enum TipoCampoEnum {
	
	TEXTO(0, "Texto", "text"), NUMERICO(1, "Numérico", "number"), DATA(2, "Data", "date");
	
	private Integer value;
	
	private String label;
	
	private String typeHtml;
	
	private TipoCampoEnum(Integer value, String label, String typeHtml) {
		this.value = value;
		this.label = label;
		this.typeHtml = typeHtml;
	}

	public Integer getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}
	
	public String getTypeHtml() {
		return typeHtml;
	}
}
