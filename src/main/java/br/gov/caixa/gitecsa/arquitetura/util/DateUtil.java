package br.gov.caixa.gitecsa.arquitetura.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Classe com metodos utilitários que lidam com Datas/Horas.
 */
public class DateUtil {

  public static final String FORMATO_DATA  = "dd/MM/yyyy";
  public static final String FORMATO_DATA_HORA  = "dd/MM/yyyy HH:mm";
  public static final String FORMATO_DATA_HORA_COMPLETO = "dd/MM/yyyy HH:mm:ss";
  public static final String FORMATO_DATA_HORA_COMPLETO_ARQUIVO = "dd_MM_yyyy_HH_mm_ss";
  public static final String FORMATO_DATA_AMERICANO  = "yyyy-MM-dd";
  public static final String FORMATO_DATA_AMERICANO_SEM_SEPARADOR  = "yyyyMMdd";

  
  public static final String TIME_FORMAT = "HH:mm";
  
  public static final Logger logger = Logger.getLogger(DateUtil.class);

  private DateUtil() {
  }
  
  /**
   * Formata o Date informado usando o format informado.
   */
  public static String format(Date date, String format) {
    if (date != null) {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      return formatter.format(date);
    }

    return StringUtils.EMPTY;
  }

  public static String formatDataPadrao(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat(FORMATO_DATA);
    return sdate.format(data);
  }
  
  public static String formatData(Date data, String pFormato) {
    SimpleDateFormat sdate = new SimpleDateFormat(pFormato);
    return sdate.format(data);
  }

  public static String formatDataHoraCompletaNomeArquivo(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat(FORMATO_DATA_HORA_COMPLETO_ARQUIVO);
    return sdate.format(data);
  }

  public static String formatDataHora(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat(FORMATO_DATA_HORA);
    return sdate.format(data);
  }

  public static String formatDataHoraCompleto(Date data) {
    SimpleDateFormat sdate = new SimpleDateFormat(FORMATO_DATA_HORA_COMPLETO);
    return sdate.format(data);
  }

  /**
   * Método responsável por validar se a hora informada está entre 00:00 e 23:59.
   * 
   */
  public static boolean horaValida(String hora) {
    if (Util.isNullOuVazio(hora)) {
      return false;
    }
    final Integer HORA_MINIMA = 0;
    final Integer HORA_MAXIMA = 2359;
    final Integer horaInt = Integer.valueOf(hora.replace(":", ""));

    if (horaInt >= HORA_MINIMA && horaInt <= HORA_MAXIMA) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Calcula o tempo entre duas datas.
   */
  public static String calculaTempoEntreDatas(Date d1, Date d2) {

    if (!Util.isNullOuVazio(d1) && !Util.isNullOuVazio(d2)) {

      long segundos = (d2.getTime() / 1000) - (d1.getTime() / 1000);
      long segundo = segundos % 60;
      long minutos = segundos / 60;
      long minuto = minutos % 60;
      long hora = minutos / 60;
      return String.format("%02d:%02d:%02d", hora, minuto, segundo);
    }

    return "";
  }


  /**
   * Faz o parse de String para Date com o formato informado.
   */
  public static Date parseDate(String valor, String formato) {
    try {
      SimpleDateFormat formatoData = new SimpleDateFormat(formato);
      return formatoData.parse(valor);
    } catch (ParseException e) {
      logger.error(e);
    }
    return null;
  }
  
  public static Date parseDateThrowsException(String valor, String formato) throws ParseException {
      SimpleDateFormat formatoData = new SimpleDateFormat(formato);
      return formatoData.parse(valor);
  }

  /**
   * Faz o parse do Date para String com o formato "EEE MMM dd HH:mm:ss z yyyy" e Locale English.
   */
  public static Date parseDateEng(String valor) {
    try {
      DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
      return format.parse(valor);
    } catch (ParseException e) {
      logger.error(e);
    }
    return null;
  }

  /**
   * Faz o parse/format do Date para String com o formato informado.
   */
  public static String parseDateToString(Date vlr, String formato) {
    DateFormat format = new SimpleDateFormat(formato);
    return format.format(vlr);
  }

  /**
   * Calcula a diferenca entre datas em dias.
   */
  public static int calculaDiferencaEntreDatasEmDias(Date d1, Date d2) {
    final int MILLIS_IN_DAY = 86400000;

    Calendar c1 = Calendar.getInstance();
    c1.setTime(d1);
    c1.set(Calendar.MILLISECOND, 0);
    c1.set(Calendar.SECOND, 0);
    c1.set(Calendar.MINUTE, 0);
    c1.set(Calendar.HOUR_OF_DAY, 0);

    Calendar c2 = Calendar.getInstance();
    c2.setTime(d2);
    c2.set(Calendar.MILLISECOND, 0);
    c2.set(Calendar.SECOND, 0);
    c2.set(Calendar.MINUTE, 0);
    c2.set(Calendar.HOUR_OF_DAY, 0);
    return (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / MILLIS_IN_DAY);
  }

  
  /**
   * Retorna a data por extenso.
   */
  public static String dataPorExtenso(Calendar data) {
    String[] meses =
        new String[] { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
            "Novembro", "Dezembro" };
    return data.get(Calendar.DAY_OF_MONTH) + " de " + meses[data.get(Calendar.MONTH)] + " de " + data.get(Calendar.YEAR);
  }

  /**
   * Retorna a data informada truncando apartir da hora 00:00:00:000.
   */
  public static Date getDataInicial(Date data) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
  }

  /**
   * Retorna a data informada com a hora maxima 23:59:59:999.
   */
  public static Date getDataFinal(Date data) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data);
    calendar.set(Calendar.HOUR, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);

    return calendar.getTime();
  }
}
