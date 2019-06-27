package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Demanda;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DemandaPorSituacaoService {
  
  @Resource
  private UserTransaction transaction;
  
  private static final int TRANSACTION_TIMEOUT = 7200;


  @Inject
  private DemandaDAO demandaDAO;

  public List<Demanda> obterDemandasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada, Date dataInicial,
      Date dataFinal) throws AppException {
    
    List<Demanda> demandas = new ArrayList<>();
    try {
      this.transaction.setTransactionTimeout(TRANSACTION_TIMEOUT);
      this.transaction.begin();
      demandas = demandaDAO.obterDemandasPorUnidadePeriodo(idUnidade, abrangenciaSelecionada, dataInicial, dataFinal);
      this.transaction.commit();
    } catch (Exception e) {
      throw new AppException(e);
    }
    
    return demandas;
  }

}
