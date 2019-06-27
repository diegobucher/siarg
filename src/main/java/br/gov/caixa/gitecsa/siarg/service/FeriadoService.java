/**
 * FeriadoService.java
 * Versão: 1.0.0.00
 * Data de Criação : 13-12-2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.FeriadoDAO;
import br.gov.caixa.gitecsa.siarg.model.Feriado;

/**
 * Classe de serviços de feriados.
 * 
 * @author f757783
 */
@Stateless
public class FeriadoService extends AbstractService<Feriado> {

  /** Serial */
  private static final long serialVersionUID = 1L;

  @Inject
  private FeriadoDAO dao;

  /**
   * Método não implementado.
   * 
   * @param entity
   *          Feriado
   */
  @Override
  public List<Feriado> consultar(Feriado entity) throws Exception {
    return new ArrayList<>();
  }

  /**
   * Método não implementado.
   * 
   * @param entity
   *          Feriado
   */
  @Override
  protected void validaCamposObrigatorios(Feriado entity) {

  }

  /**
   * Método não implementado.
   * 
   * @param entity
   *          Feriado
   */
  @Override
  protected void validaRegras(Feriado entity) {

  }

  /**
   * Método não implementado.
   * 
   * @param entity
   *          Feriado
   */
  @Override
  protected void validaRegrasExcluir(Feriado entity) {

  }

  /**
   * Obtém a instância do DAO de Feriados.
   * 
   * @return A instância de FeriadoDAOImpl.
   */
  @Override
  protected BaseDAO<Feriado> getDAO() {
    return null;
  }

  /**
   * Calcula a data final do prazo levando em consideração os feriados.
   * 
   * @param data
   *          Data de inicio
   * @param prazo
   *          Prazo em dias
   * @return Data de vencimento do prazo
   * @throws DataBaseException
   */
  public Date calcularDataVencimentoPrazo(final Date data, final Integer prazo) throws DataBaseException {
    int diasUteis = BigDecimal.ZERO.intValue();
    Calendar dia = Calendar.getInstance();
    dia.setTime(data);
    dia.setLenient(false);

    while (diasUteis < prazo) {
      dia.add(Calendar.DAY_OF_MONTH, BigDecimal.ONE.intValue());
      int diaSemana = dia.get(Calendar.DAY_OF_WEEK);

      if ((diaSemana != Calendar.SATURDAY) && (diaSemana != Calendar.SUNDAY) && !this.dao.isFeriado(dia.getTime())) {
        diasUteis++;
      }
    }

    return dia.getTime();
  }

  public Boolean isDiaUtil(Date data) throws DataBaseException {

    Calendar cal = Calendar.getInstance();
    cal.setTime(data);

    int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

    if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
      return false;
    }

    if (dao.isFeriado(data)) {
      return false;
    }

    return true;
  }

  public Integer calcularNumeroDiasUteisPorDataEPrazo(final Date data, final Integer prazo, final List<Date> datasFeriadosList) {
    Integer prazoAtual = 0;
    Calendar dataFinalDefinida = Calendar.getInstance();
    Calendar dataAtual = Calendar.getInstance();

    dataFinalDefinida.setTime(data);

    int i = 0;
    i = 0;
    while (i < prazo) {
      dataFinalDefinida.add(Calendar.DAY_OF_MONTH, 1);
      int diaSemana = dataFinalDefinida.get(Calendar.DAY_OF_WEEK);
      if ((diaSemana != Calendar.SATURDAY) && (diaSemana != Calendar.SUNDAY)
          && !datasFeriadosList.contains(DateUtils.truncate(dataFinalDefinida.getTime(), Calendar.DAY_OF_MONTH))) {
        i++;
      }
    }

    dataFinalDefinida = DateUtils.truncate(dataFinalDefinida, Calendar.DAY_OF_MONTH);
    dataAtual = DateUtils.truncate(dataAtual, Calendar.DAY_OF_MONTH);

    /** Prazo estourado - Comportamento negativo */
    prazoAtual = isPrazoEstourado(datasFeriadosList, prazoAtual, dataFinalDefinida, dataAtual);

    return prazoAtual;
  }

  private Integer isPrazoEstourado(final List<Date> datasFeriadosList, Integer prazoAtual, Calendar dataFinalDefinida,
      Calendar dataAtual) {
    if (dataAtual.after(dataFinalDefinida)) {
      do {
        dataFinalDefinida.add(Calendar.DAY_OF_MONTH, 1);
        int diaSemana = dataFinalDefinida.get(Calendar.DAY_OF_WEEK);
        if ((diaSemana != Calendar.SATURDAY) && (diaSemana != Calendar.SUNDAY)
            && !datasFeriadosList.contains(DateUtils.truncate(dataFinalDefinida.getTime(), Calendar.DAY_OF_MONTH))) {
          prazoAtual--;
        }
      } while (dataAtual.after(dataFinalDefinida));
      /** Prazo ok - Comportamento positivo */
    } else if (dataAtual.before(dataFinalDefinida)) {
      do {
        dataAtual.add(Calendar.DAY_OF_MONTH, 1);
        int diaSemana = dataAtual.get(Calendar.DAY_OF_WEEK);
        if ((diaSemana != Calendar.SATURDAY) && (diaSemana != Calendar.SUNDAY)
            && !datasFeriadosList.contains(DateUtils.truncate(dataAtual.getTime(), Calendar.DAY_OF_MONTH))) {
          prazoAtual++;
        }
      } while (dataAtual.before(dataFinalDefinida));
    }
    return prazoAtual;
  }

  /**
   * Método: Obter Lista De Datas Dos Feriados.
   * 
   * @return list
   */
  public List<Date> obterListaDeDatasDosFeriados() {
    return this.dao.obterListaDeDatasDosFeriados();
  }

  public Date adicionarDiasUteis(final Date datainicial, final Integer prazoDiasUteis) {

    List<Date> datasFeriadosList = obterListaDeDatasDosFeriados();

    Calendar dataAtual = Calendar.getInstance();
    dataAtual.setTime(DateUtils.truncate(datainicial, Calendar.DAY_OF_MONTH));

    int qtdDiasAdicionar = prazoDiasUteis;
    while (qtdDiasAdicionar > 0) {
      dataAtual.add(Calendar.DAY_OF_MONTH, 1);
      int diaSemana = dataAtual.get(Calendar.DAY_OF_WEEK);
      boolean isFeriado = datasFeriadosList.contains(dataAtual.getTime());
      if (diaSemana != Calendar.SATURDAY && diaSemana != Calendar.SUNDAY && !isFeriado) {
        qtdDiasAdicionar--;
      }
    }

    return dataAtual.getTime();

  }
  
  public Date adicionarDiasUteis(final Date datainicial, final Integer prazoDiasUteis, final List<Date> datasFeriadosList) {
    
    Calendar dataAtual = Calendar.getInstance();
    dataAtual.setTime(DateUtils.truncate(datainicial, Calendar.DAY_OF_MONTH));
    
    int qtdDiasAdicionar = prazoDiasUteis;
    while (qtdDiasAdicionar > 0) {
      dataAtual.add(Calendar.DAY_OF_MONTH, 1);
      int diaSemana = dataAtual.get(Calendar.DAY_OF_WEEK);
      boolean isFeriado = datasFeriadosList.contains(dataAtual.getTime());
      if (diaSemana != Calendar.SATURDAY && diaSemana != Calendar.SUNDAY && !isFeriado) {
        qtdDiasAdicionar--;
      }
    }
    
    return dataAtual.getTime();
    
  }

  public Integer calcularQtdDiasUteisEntreDatas(final Date dataInicial, final Date dataFinal) {

    List<Date> datasFeriadosList = obterListaDeDatasDosFeriados();

    Calendar calAtual = Calendar.getInstance();
    Calendar calFinal = Calendar.getInstance();

    calAtual.setTime(DateUtils.truncate(dataInicial, Calendar.DAY_OF_MONTH));
    calFinal.setTime(DateUtils.truncate(dataFinal, Calendar.DAY_OF_MONTH));

    Integer qtdDiasUteis = 0;

    calAtual.add(Calendar.DAY_OF_MONTH, 1);

    while (!calAtual.getTime().after(calFinal.getTime())) {

      int diaSemana = calAtual.get(Calendar.DAY_OF_WEEK);
      boolean isFeriado = datasFeriadosList.contains(calAtual.getTime());
      if (diaSemana != Calendar.SATURDAY && diaSemana != Calendar.SUNDAY && !isFeriado) {
        qtdDiasUteis++;
      }

      calAtual.add(Calendar.DAY_OF_MONTH, 1);
    }

    return qtdDiasUteis;
  }
  
  public Double calcularQtdDiasUteis(final Date dataInicial, final Date dataFinal, final List<Date> datasFeriadosList) {

	    Calendar calAtual = Calendar.getInstance();
	    Calendar calFinal = Calendar.getInstance();

	    calAtual.setTime(DateUtils.truncate(dataInicial, Calendar.DAY_OF_MONTH));
	    calFinal.setTime(DateUtils.truncate(dataFinal, Calendar.DAY_OF_MONTH));

	    Double qtdDiasUteis = 0d;

	    calAtual.add(Calendar.DAY_OF_MONTH, 1);

	    while (!calAtual.getTime().after(calFinal.getTime())) {

	      int diaSemana = calAtual.get(Calendar.DAY_OF_WEEK);
	      boolean isFeriado = datasFeriadosList.contains(calAtual.getTime());
	      if (diaSemana != Calendar.SATURDAY && diaSemana != Calendar.SUNDAY && !isFeriado) {
	        qtdDiasUteis++;
	      }

	      calAtual.add(Calendar.DAY_OF_MONTH, 1);
	    }

	    return qtdDiasUteis;
  }

  public Integer calcularQtdDiasUteisEntreDatas(final Date dataInicial, final Date dataFinal, List<Date> datasFeriadosList) {

    Calendar calAtual = Calendar.getInstance();
    Calendar calFinal = Calendar.getInstance();

    calAtual.setTime(DateUtils.truncate(dataInicial, Calendar.DAY_OF_MONTH));
    calFinal.setTime(DateUtils.truncate(dataFinal, Calendar.DAY_OF_MONTH));

    Integer qtdDiasUteis = 0;

    calAtual.add(Calendar.DAY_OF_MONTH, 1);

    while (!calAtual.getTime().after(calFinal.getTime())) {

      int diaSemana = calAtual.get(Calendar.DAY_OF_WEEK);
      boolean isFeriado = datasFeriadosList.contains(calAtual.getTime());
      if (diaSemana != Calendar.SATURDAY && diaSemana != Calendar.SUNDAY && !isFeriado) {
        qtdDiasUteis++;
      }

      calAtual.add(Calendar.DAY_OF_MONTH, 1);
    }

    return qtdDiasUteis;
  }

}
