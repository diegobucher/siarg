package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.AssuntoCampoDAO;
import br.gov.caixa.gitecsa.siarg.model.AssuntoCampo;


public class AssuntoCampoDAOimpl  extends BaseDAOImpl<AssuntoCampo> implements AssuntoCampoDAO {

	
    @Override   
	public AssuntoCampo salvarAssuntoCampo(Long id){
		
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT campo ");
		hql.append(" FROM AssuntoCampo campo ");
		hql.append(" WHERE campo.nu_assunto = :id ");
		hql.append(" AND campo.nu_campo = :id ");

		TypedQuery<AssuntoCampo> query = this.getEntityManager().createQuery(hql.toString(), AssuntoCampo.class);
		
		query.setParameter("id", id);
		
		return null;
    	
	}
    
    @Override   
   	public List<AssuntoCampo> obterAssuntoCampoPorAssunto(Long idAssunto){
   		
   		StringBuilder hql = new StringBuilder(); 
   		
   		hql.append(" SELECT assuntoCampo ");
   		hql.append(" FROM AssuntoCampo assuntoCampo ");
   		hql.append(" INNER JOIN assuntoCampo.assunto assunto ");
   		hql.append(" WHERE assunto.id = :idAssunto ");
   		

		TypedQuery<AssuntoCampo> query = this.getEntityManager().createQuery(hql.toString(), AssuntoCampo.class);
		query.setParameter("idAssunto", idAssunto);

		return query.getResultList();
   		
   	}
    
    
    @Override   
   	public List<AssuntoCampo> obterAssuntoCampoPorAssuntoCampoObrigatorio(Long idAssunto, Long idCampoObrigatorio){
   		
   		StringBuilder hql = new StringBuilder(); 
   		
   		hql.append(" SELECT assuntoCampo ");
   		hql.append(" FROM AssuntoCampo assuntoCampo ");
   		hql.append(" INNER JOIN assuntoCampo.assunto assunto ");
   		hql.append(" INNER JOIN assuntoCampo.camposObrigatorios campo ");
   		
   		hql.append(" WHERE 1=1 ");
   		
   		hql.append(" AND assunto.id = :idAssunto ");
   		
   		hql.append(" AND campo.id = :idCampoObrigatorio ");
   		

		TypedQuery<AssuntoCampo> query = this.getEntityManager().createQuery(hql.toString(), AssuntoCampo.class);
		if(idAssunto != null){
			query.setParameter("idAssunto", idAssunto);
		}
		
		if(idAssunto != null){
			query.setParameter("idCampoObrigatorio", idCampoObrigatorio);
		}
		

		return query.getResultList();
   		
   	}
    
	
}
