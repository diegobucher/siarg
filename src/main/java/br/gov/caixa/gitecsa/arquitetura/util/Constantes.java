package br.gov.caixa.gitecsa.arquitetura.util;

public class Constantes {

  public static final Long ABRANGENCIA_VIGOV_ID = 1L;
	
  // LDAP
  public static final String URL_LDAP = "configuracao.ldap.url";
  public static final String NOME_SISTEMA = "configuracao.ldap.grupo.sistema";
  public static final String SECURITY_DOMAIN = "configuracao.ldap.security.domain";

  // FILE SYSTEM
  public static final String DIRETORIO_UPLOAD_ANEXOS = "siarg.configuracao.anexos";
  public static final String DIRETORIO_UPLOAD_ANEXOS_ABRANGENCIAS = "siarg.configuracao.anexos.abrangencias";
  
  public static final String PREFIXO_DIRETORIO_UPLOAD_ANEXOS = "Abrangencia";
  public static final String FORMATO_NOME_ANEXO_DEMANDA = "ANEXO_%d.ZIP";
  public static final String FORMATO_NOME_ANEXO_DEMANDA_ATENDIMENTO = "ANEXO_%d_%d.ZIP";
  public static final String SYSTEM_FILE_SEPARATOR = System.getProperty("file.separator");

  // FORMATAÇÃO PADRÃO
  public static final String DATA_FORMATACAO = "dd/MM/yyyy";
  public static final String DATA_HORA_FORMATACAO = "dd/MM/yyyy HH:mm";

  // LOCAL DO REPORT
  public static final String REPORT_BASE_DIR = "/reports/";
  public static final String REPORT_IMG_DIR = "/reports/imagens/";

  // DEMANDA
  public static final String COR_PADRAO_DEMANDA = "#fff";
  public static final String FLASH_SUCCESS_MESSAGE = "FLASH_SESSION_MESSAGE";
  public static final String FLASH_ERROR_MESSAGE = "FLASH_SESSION_ERROR_MESSAGE";

  /** Tempo de Refresh da pagina de Monitoramento - Tronco. */
  public static final String SET_TIME_REFRESH = "siarg.configuracao.setTimeRefresh";
  
  public static final String CALLBACK_URL = "CALLBACK_URL";
  
  public static final String VIEW_ID_INCLUSAO_DEMANDA = "/demanda/inclusao/inclusao";
  
  /** Jobs com parâmetros no Jboss para balanceamento de carga. */
  public static final String JOB_EXTRATO_SCHEDULE = "siarg.configuracao.jobExtratoSchedule";
  public static final String JOB_QUESTIONAMENTO_SCHEDULE = "siarg.configuracao.jobQuestionamentoSchedule";

}
