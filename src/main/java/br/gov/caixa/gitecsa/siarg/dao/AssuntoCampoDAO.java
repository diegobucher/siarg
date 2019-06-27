package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.AssuntoCampo;


/**
 * Interface DAO para CamposObrigatorios.
 * @author f763644
 */
public interface AssuntoCampoDAO extends BaseDAO<AssuntoCampo> {
	
	AssuntoCampo salvarAssuntoCampo(Long id);

	List<AssuntoCampo> obterAssuntoCampoPorAssunto(Long idAssunto);

	List<AssuntoCampo> obterAssuntoCampoPorAssuntoCampoObrigatorio(Long idAssunto, Long idCampoObrigatorio);


}
