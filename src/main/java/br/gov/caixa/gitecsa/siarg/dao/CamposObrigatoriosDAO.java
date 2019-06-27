package br.gov.caixa.gitecsa.siarg.dao;

import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;

/**
 * Interface DAO para CamposObrigatorios.
 * @author f763644
 */

public interface CamposObrigatoriosDAO extends BaseDAO<CamposObrigatorios> {

	CamposObrigatorios obterCamposObrigatorioPorNome(String nome);

	List<CamposObrigatorios> obterCamposObrigatoriosPorTipo(TipoCampoEnum tipo);

	List<CamposObrigatorios> obterCamposObrigatoriosPorMascara(String mascara);

	List<CamposObrigatorios> obterCamposObrigatoriosPorNome(String nome);

	CamposObrigatorios obterCamposObrigatorioPorDescricao(String descricao);

	List<CamposObrigatorios> obterObrigatoriosPorDescricao(String descricao);

	CamposObrigatorios obterListaCamposObrigatoriosUsuarioLogado(Long id);

	List<CamposObrigatorios> obterCamposObrigatoriosPorTamanho(Integer tamanho);

	List<CamposObrigatorios> carregarTodosCamposCheckbox(Boolean ativo);

	List<CamposObrigatorios> obterCamposObrigatoriosPorAssunto(Long idAssunto);
		

}
