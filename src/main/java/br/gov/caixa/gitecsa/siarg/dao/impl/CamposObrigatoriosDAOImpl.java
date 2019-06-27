package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.CamposObrigatoriosDAO;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoCampoEnum;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;

public class CamposObrigatoriosDAOImpl extends BaseDAOImpl<CamposObrigatorios> implements CamposObrigatoriosDAO {

	/**
	 * Método para obter os campos obrigatorios do usuário cadastrado como excessão.
	 */
	@Override
	public CamposObrigatorios obterListaCamposObrigatoriosUsuarioLogado(Long id) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.nu_campo = :id ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		query.setParameter("id", id);

		return null;
	}
	
	@Override
	public List<CamposObrigatorios> carregarTodosCamposCheckbox(Boolean ativo){
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.ativo = :ativo ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		
		query.setParameter("ativo", Boolean.TRUE);

		return query.getResultList();
	}
	
	@Override
	public CamposObrigatorios obterCamposObrigatorioPorNome(String nome) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT campo.no_campo "); 
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.no_campo = :nome ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);

		query.setParameter("nome", nome);

		return null;
	}
	
	@Override
	public List<CamposObrigatorios> obterObrigatoriosPorDescricao(String descricao){
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT DISTINCT campo.de_campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.de_campo = :descricao ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		
		query.setParameter("descricao", descricao);

		return query.getResultList();
	}
	
	@Override
	public CamposObrigatorios obterCamposObrigatorioPorDescricao(String descricao) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.de_campo = :descricao ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);

		query.setParameter("descricao", descricao);

		List<CamposObrigatorios> camposObrigatoriosList = query.getResultList();

		if (!camposObrigatoriosList.isEmpty()) {
			return camposObrigatoriosList.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<CamposObrigatorios> obterCamposObrigatoriosPorNome(String nome){
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.no_campo = :nome ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		query.setParameter("nome", nome);

		return query.getResultList();
	}

	@Override
	public List<CamposObrigatorios> obterCamposObrigatoriosPorTipo(TipoCampoEnum tipo) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.ic_tipo_campo = :tipo ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		query.setParameter("tipo", tipo);

		return query.getResultList();
	}
	
	@Override
	public List<CamposObrigatorios> obterCamposObrigatoriosPorTamanho(Integer tamanho){
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT campo ");
		hql.append(" From CamposObrigatorios campo ");
		hql.append(" WHERE campo.nu_tamanho_campo = :tamanho ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(),
				CamposObrigatorios.class);
		query.setParameter("tamanho", tamanho);

		return query.getResultList();
	}
	
	@Override
	public List<CamposObrigatorios> obterCamposObrigatoriosPorMascara(String mascara){
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT campo ");
		hql.append(" FROM CamposObrigatorios campo ");
		hql.append(" WHERE campo.de_mascara_campo = :mascara");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		query.setParameter("mascara", mascara);

		return query.getResultList();
	}
	
	public List<CamposObrigatorios> obterCamposObrigatoriosPorAssunto(Long IdAssunto) {
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT DISTINCT NEW CamposObrigatorios(campo.id, campo.nome, campo.descricao, campo.tipo, campo.tamanho, campo.mascara, assuntoCampo.quantidade)  ");
		hql.append(" FROM AssuntoCampo assuntoCampo ");
		hql.append(" JOIN assuntoCampo.camposObrigatorios campo ");
		hql.append(" JOIN assuntoCampo.assunto assunto ");
		hql.append(" WHERE assunto.id = :idAssunto");
		hql.append(" AND campo.ativo = :ativo ");

		TypedQuery<CamposObrigatorios> query = this.getEntityManager().createQuery(hql.toString(), CamposObrigatorios.class);
		query.setParameter("idAssunto", IdAssunto);
		query.setParameter("ativo", Boolean.TRUE);

		return query.getResultList();
	}

}
