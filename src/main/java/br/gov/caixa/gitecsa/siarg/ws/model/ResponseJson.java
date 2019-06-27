package br.gov.caixa.gitecsa.siarg.ws.model;

public class ResponseJson {

  private Integer returnCode;
  private String message;

  public ResponseJson(Integer returnCode, String message) {
    super();
    this.returnCode = returnCode;
    this.message = message;
  }

  public Integer getReturnCode() {
    return returnCode;
  }

  public String getMessage() {
    return message;
  }

  public void setReturnCode(Integer returnCode) {
    this.returnCode = returnCode;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
