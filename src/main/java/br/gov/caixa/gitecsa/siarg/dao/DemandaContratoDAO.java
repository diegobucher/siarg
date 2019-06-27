package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;

public interface DemandaContratoDAO extends BaseDAO<DemandaContrato>{

  List<DemandaContrato> obterContratosPorIdDemanda(Long idDemanda);

}
