package br.gov.caixa.gitecsa.siarg.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.arquitetura.service.AbstractService;
import br.gov.caixa.gitecsa.siarg.dao.DemandaContratoDAO;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;

@Stateless
public class DemandaContratoService  extends AbstractService<DemandaContrato>{

  /** Serial. */
  private static final long serialVersionUID = -9171853705698861739L;
  
  @Inject
  private DemandaContratoDAO demandaContratoDAO;

  @Override
  public List<DemandaContrato> consultar(DemandaContrato entity) throws Exception {
    return null;
  }

  @Override
  protected void validaCamposObrigatorios(DemandaContrato entity) {
    
  }

  @Override
  protected void validaRegras(DemandaContrato entity) {
    
  }

  @Override
  protected void validaRegrasExcluir(DemandaContrato entity) {
    
  }

  @Override
  protected BaseDAO<DemandaContrato> getDAO() {
    return this.demandaContratoDAO;
  }
  
  public DemandaContrato obterDemandaContrato(Demanda demanda, String contratoTemp) {
    DemandaContrato contrato = new DemandaContrato();
    contrato.setDemanda(demanda);
    contrato.setDataInclusao(new Date());
    contrato.setNumeroContrato(contratoTemp);
    return contrato;
  }

  public String formatacaoNumeroContrato(String contratoTemp) {
    String temp = StringUtils.replace(contratoTemp, "-", "");
    if (temp.length() > 2) {
      String valor = StringUtils.substring(temp, 0, temp.length() -2);  
      String dv = StringUtils.substring(temp, temp.length() - 2, temp.length());
      valor = String.format("%07d", Integer.valueOf(valor));
      temp = valor + "-"+ dv;
    }
    return temp;
  }

  public void removerContrato(DemandaContrato dc) {
    demandaContratoDAO.delete(dc);    
  }

  public List<DemandaContrato> obterContratosPorIdDemanda(Long idDemanda) {
    return demandaContratoDAO.obterContratosPorIdDemanda(idDemanda);
  }

}
