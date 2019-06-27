package br.gov.caixa.gitecsa.siarg.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Parametro;

public interface ParametroDAO extends BaseDAO<Parametro> {

  Parametro obterParametroByNome(String nome);

}
