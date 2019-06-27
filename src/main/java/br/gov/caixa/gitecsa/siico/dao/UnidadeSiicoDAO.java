package br.gov.caixa.gitecsa.siico.dao;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siico.vo.UnidadeSiicoVO;

public interface UnidadeSiicoDAO extends BaseDAO<UnidadeSiicoVO> {

  UnidadeSiicoVO obterUnidadePorCGC(Integer numeroCGC);

}
