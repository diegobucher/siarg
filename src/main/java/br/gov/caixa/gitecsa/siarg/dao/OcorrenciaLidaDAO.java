package br.gov.caixa.gitecsa.siarg.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.OcorrenciaLida;

public interface OcorrenciaLidaDAO extends BaseDAO<OcorrenciaLida> {

  OcorrenciaLida obterOcorrenciaLida(String matricula, Long idAbrangencia);

}
