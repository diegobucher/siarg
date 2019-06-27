/**
 * DemandaDAOImpl.java
 * Versão: 1.0.0.00
 * 05/12/2017
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.dao.impl;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAOImpl;
import br.gov.caixa.gitecsa.siarg.dao.DemandaDAO;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeDemandaPorUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioIndicadorReaberturaDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.EnvolvimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

/**
 * Classe: Implementação dos DAOs de Demandas.
 * 
 * @author f520296
 */
public class DemandaDAOImpl extends BaseDAOImpl<Demanda> implements DemandaDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.caixa.gitecsa.siarg.dao.DemandaDAO#obterListaDemandasPrioridades(java.
	 * lang.Long)
	 */
	@Override
	public List<Demanda> obterListaDemandasPrioridades(Long idCaixaPostal, SituacaoEnum situacaoPriori) {

		Date dezDiasAtras = DateUtils.truncate(DateUtils.addDays(Calendar.getInstance().getTime(), -10),
				Calendar.DAY_OF_MONTH);
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai demandaPai ");
		hql.append(" LEFT JOIN FETCH demanda.assunto assunto ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" LEFT JOIN FETCH caixaPostalResponsavel.unidade unidade ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" LEFT JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" WHERE 1 = 1 ");
		if (situacaoPriori != null) {
			hql.append("  AND (caixaPostalResponsavel.id = :idCaixaPostal ");
			if (SituacaoEnum.FECHADA.equals(situacaoPriori)) {
				hql.append(" AND demanda.situacao = :fechada AND demanda.dataHoraEncerramento >= :dezDiasAtras) ");
			} else if (SituacaoEnum.RASCUNHO.equals(situacaoPriori)) {
				hql.append(" AND demanda.situacao = :rascunho) ");
			} else if (SituacaoEnum.MINUTA.equals(situacaoPriori)) {
				hql.append(" AND demanda.situacao = :minuta) ");
			} else if (SituacaoEnum.ABERTA.equals(situacaoPriori)) {
				hql.append(" AND demanda.situacao = :aberta) ");
			}
		} else {
			hql.append(" AND caixaPostalResponsavel.id = :idCaixaPostal  ");
			hql.append(" AND ( ");
			hql.append(" (demanda.situacao = :fechada AND demanda.dataHoraEncerramento >= :dezDiasAtras) ");
			hql.append(" OR demanda.situacao = :rascunho ");
			hql.append(" OR demanda.situacao = :aberta ");
			hql.append(" OR demanda.situacao = :minuta ");
			hql.append(" )");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("idCaixaPostal", idCaixaPostal);

		if (situacaoPriori != null) {
			if (SituacaoEnum.FECHADA.equals(situacaoPriori)) {
				query.setParameter("fechada", SituacaoEnum.FECHADA);
				query.setParameter("dezDiasAtras", dezDiasAtras);
			} else if (SituacaoEnum.ABERTA.equals(situacaoPriori)) {
				query.setParameter("aberta", SituacaoEnum.ABERTA);
			} else if (SituacaoEnum.RASCUNHO.equals(situacaoPriori)) {
				query.setParameter("rascunho", SituacaoEnum.RASCUNHO);
			} else if (SituacaoEnum.MINUTA.equals(situacaoPriori)) {
				query.setParameter("minuta", SituacaoEnum.MINUTA);
			}
		} else {
			query.setParameter("fechada", SituacaoEnum.FECHADA);
			query.setParameter("dezDiasAtras", dezDiasAtras);
			query.setParameter("rascunho", SituacaoEnum.RASCUNHO);
			query.setParameter("aberta", SituacaoEnum.ABERTA);
			query.setParameter("minuta", SituacaoEnum.MINUTA);
		}

		return query.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.caixa.gitecsa.siarg.dao.DemandaDAO#obterListaDemaisDemandas(java.lang.
	 * Long, java.lang.String, br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum)
	 */
	@Override
	public List<Demanda> obterListaDemaisDemandas(Long idCaixaPostal, EnvolvimentoEnum envolvimentoFiltro,
			SituacaoEnum situacaoDemaisFiltro) {

		Date sessentaDiasAtras = DateUtils.truncate(DateUtils.addDays(Calendar.getInstance().getTime(), -60),
				Calendar.DAY_OF_MONTH);

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai demandaPai ");
		hql.append(" LEFT JOIN FETCH demanda.assunto assunto ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" LEFT JOIN FETCH caixaPostalResponsavel.unidade unidade ");
		hql.append(" LEFT JOIN demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" LEFT JOIN FETCH demanda.fluxosDemandasList fluxosDemandasListFetch ");
		hql.append(" LEFT JOIN demanda.caixasPostaisObservadorList caixasPostaisObservadorList ");
		hql.append(" LEFT JOIN demanda.assunto assunto ");
		hql.append(" LEFT JOIN demanda.atendimentosList atendimentosList ");
		hql.append(" LEFT JOIN assunto.caixasPostaisObservadorList assuntoCaixasPostaisObservadorList ");
		hql.append(" WHERE 1 = 1 ");
		hql.append(
				" AND ((atendimentosList.dataHoraAtendimento > :sessentaDiasAtras OR atendimentosList.dataHoraRecebimento > :sessentaDiasAtras) ");
		hql.append(
				"        OR ( (demanda.situacao = :fechada OR demanda.situacao = :cancelada) AND COALESCE(demanda.dataHoraEncerramento, current_date()) > :sessentaDiasAtras) ");
		hql.append("     )");

		if (situacaoDemaisFiltro != null) {
			if (SituacaoEnum.FECHADA.equals(situacaoDemaisFiltro)) {
				hql.append(" AND (demanda.situacao = :fechada ) ");
			} else if (SituacaoEnum.ABERTA.equals(situacaoDemaisFiltro)) {
				hql.append(" AND (demanda.situacao = :aberta) ");
			} else if (SituacaoEnum.CANCELADA.equals(situacaoDemaisFiltro)) {
				hql.append(" AND (demanda.situacao = :cancelada) ");
			}
		} else {
			hql.append(
					" AND (demanda.situacao = :aberta OR demanda.situacao = :fechada OR demanda.situacao = :cancelada) ");
		}

		if (envolvimentoFiltro != null) {
			if (EnvolvimentoEnum.DEMANDANTE.equals(envolvimentoFiltro)) {
				hql.append(" AND caixaPostalDemandante.id = :idCaixaPostal ");
			} else if (EnvolvimentoEnum.RESPONSAVEL.equals(envolvimentoFiltro)) {
				hql.append(" AND caixaPostalResponsavel.id = :idCaixaPostal ");
			} else if (EnvolvimentoEnum.ENVOLVIDO.equals(envolvimentoFiltro)) {
				hql.append(" AND (fluxosDemandasList.caixaPostal.id = :idCaixaPostal ");
				hql.append(" OR caixaPostalDemandante.id = :idCaixaPostal) ");
			} else if (EnvolvimentoEnum.COM_COPIA.equals(envolvimentoFiltro)) {
				hql.append(" AND (caixasPostaisObservadorList.id = :idCaixaPostal ");
				hql.append(" OR assuntoCaixasPostaisObservadorList.id = :idCaixaPostal) ");
			}
		} else {
			hql.append(" AND ( ");
			hql.append(" caixaPostalDemandante.id = :idCaixaPostal ");
			hql.append(" OR caixaPostalResponsavel.id = :idCaixaPostal ");
			hql.append(" OR fluxosDemandasList.caixaPostal.id = :idCaixaPostal  ");
			hql.append(" OR caixasPostaisObservadorList.id = :idCaixaPostal ");
			hql.append(" OR assuntoCaixasPostaisObservadorList.id = :idCaixaPostal ");
			hql.append(" ) ");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("idCaixaPostal", idCaixaPostal);
		query.setParameter("sessentaDiasAtras", sessentaDiasAtras);

		// Setando esses parametros que SÃO necessarios para o teste de Fechadas,
		// Canceladas
		query.setParameter("fechada", SituacaoEnum.FECHADA);
		query.setParameter("cancelada", SituacaoEnum.CANCELADA);

		if (situacaoDemaisFiltro != null) {
			if (SituacaoEnum.FECHADA.equals(situacaoDemaisFiltro)) {
				query.setParameter("fechada", SituacaoEnum.FECHADA);
			} else if (SituacaoEnum.ABERTA.equals(situacaoDemaisFiltro)) {
				query.setParameter("aberta", SituacaoEnum.ABERTA);
			} else if (SituacaoEnum.CANCELADA.equals(situacaoDemaisFiltro)) {
				query.setParameter("cancelada", SituacaoEnum.CANCELADA);
			}
		} else {
			query.setParameter("aberta", SituacaoEnum.ABERTA);
			query.setParameter("fechada", SituacaoEnum.FECHADA);
			query.setParameter("cancelada", SituacaoEnum.CANCELADA);
		}

		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.caixa.gitecsa.siarg.dao.DemandaDAO#
	 * obterListaDemandasExternasPorAendimentoECaixaPostal
	 * (br.gov.caixa.gitecsa.siarg.model.CaixaPostal)
	 */
	@Override
	public List<Demanda> obterListaDemandasExternasPorAendimentoECaixaPostal(Long idCaixaPostalResponsavel,
			SituacaoEnum situacao) {
		Date dezDiasAtras = DateUtils.truncate(DateUtils.addDays(Calendar.getInstance().getTime(), -10),
				Calendar.DAY_OF_MONTH);

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM FluxoDemanda fluxoDemanda ");
		hql.append(" INNER JOIN fluxoDemanda.demanda demanda ");
		hql.append(" INNER JOIN demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai demandaPaiFetch ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandanteFetch ");
		hql.append(" LEFT JOIN FETCH demanda.assunto assuntoFetch ");
		hql.append(" LEFT JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavelFetch ");
		hql.append(" LEFT JOIN FETCH demanda.fluxosDemandasList fluxosDemandasListFetch ");
		hql.append(" WHERE demanda.id IN ( ");
		hql.append("    SELECT demandaInt.id  ");
		hql.append("    FROM Demanda demandaInt  ");
		hql.append("    INNER JOIN demandaInt.caixaPostalResponsavel caixaPostalDemandaInt ");
		hql.append("    INNER JOIN caixaPostalDemandaInt.unidade unidade ");
		hql.append(
				"    WHERE (unidade.tipoUnidade = :tipoUnidadeExterna0 OR unidade.tipoUnidade = :tipoUnidadeExterna3) ");
		hql.append(" ) AND ");
		hql.append(" fluxoDemanda.caixaPostal.id = :idCaixaPostalResponsavel ");

		hql.append(" AND fluxoDemanda.ativo = TRUE ");

		if (situacao != null) {
			if (SituacaoEnum.FECHADA.equals(situacao)) {
				hql.append(" AND (demanda.situacao = :fechada AND demanda.dataHoraEncerramento >= :dezDiasAtras) ");
			} else if (SituacaoEnum.ABERTA.equals(situacao)) {
				hql.append(" AND (demanda.situacao = :aberta) ");
			} else if (SituacaoEnum.RASCUNHO.equals(situacao)) {
				hql.append(" AND (demanda.situacao = :rascunho) ");
			} else if (SituacaoEnum.MINUTA.equals(situacao)) {
				hql.append(" AND (demanda.situacao = :minuta) ");
			}
		} else {
			hql.append(" AND (( demanda.situacao = :fechada AND demanda.dataHoraEncerramento >= :dezDiasAtras) ");
			hql.append(" OR ");
			hql.append(" (demanda.situacao = :rascunho OR demanda.situacao = :aberta OR demanda.situacao = :minuta ) ");
			hql.append(" )");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("idCaixaPostalResponsavel", idCaixaPostalResponsavel);
		query.setParameter("tipoUnidadeExterna0", TipoUnidadeEnum.ARROBA_EXTERNA);
		query.setParameter("tipoUnidadeExterna3", TipoUnidadeEnum.EXTERNA);

		if (situacao != null) {
			if (SituacaoEnum.FECHADA.equals(situacao)) {
				query.setParameter("fechada", SituacaoEnum.FECHADA);
				query.setParameter("dezDiasAtras", dezDiasAtras);
			} else if (SituacaoEnum.ABERTA.equals(situacao)) {
				query.setParameter("aberta", SituacaoEnum.ABERTA);
			} else if (SituacaoEnum.RASCUNHO.equals(situacao)) {
				query.setParameter("rascunho", SituacaoEnum.RASCUNHO);
			} else if (SituacaoEnum.MINUTA.equals(situacao)) {
				query.setParameter("minuta", SituacaoEnum.MINUTA);
			}
		} else {
			query.setParameter("fechada", SituacaoEnum.FECHADA);
			query.setParameter("dezDiasAtras", dezDiasAtras);
			query.setParameter("rascunho", SituacaoEnum.RASCUNHO);
			query.setParameter("aberta", SituacaoEnum.ABERTA);
			query.setParameter("minuta", SituacaoEnum.MINUTA);
		}

		return query.getResultList();
	}

	@Override
	public Demanda findByIdFetch(Long id) {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai pai");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel");
		hql.append(" INNER JOIN FETCH responsavel.unidade uniResp");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto");
		hql.append(" LEFT JOIN FETCH demanda.contratosList contratosList");
		hql.append(" WHERE demanda.id = :idDemanda ");
		hql.append(" ORDER BY demanda.id, contratosList.numeroContrato ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemanda", id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Demanda findByIdAndAbrangenciaFetch(Long idDemanda, Long idAbrangencia) {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai pai ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante ");
		hql.append(" INNER JOIN FETCH demandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN FETCH unidadeDemandante.abrangencia abrangencia ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel ");
		hql.append(" INNER JOIN FETCH responsavel.unidade uniResp ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" LEFT JOIN FETCH demanda.contratosList contratosList ");
		hql.append(" WHERE demanda.id = :idDemanda ");
		hql.append(" AND abrangencia.id = :idAbrangencia ");
		hql.append(" ORDER BY demanda.id, contratosList.numeroContrato ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemanda", idDemanda);
		query.setParameter("idAbrangencia", idAbrangencia);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Demanda> findByIdFetchList(List<Demanda> demandasList) {
		List<Long> idLongList = new ArrayList<Long>();
		for (Demanda demanda : demandasList) {
			idLongList.add(demanda.getId());
		}

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai pai");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel");
		hql.append(" INNER JOIN FETCH responsavel.unidade uniResp");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto");
		hql.append(" LEFT JOIN FETCH demanda.contratosList contratosList");
		hql.append(" WHERE demanda.id in :idDemandaList ");
		hql.append(" ORDER BY demanda.id, contratosList.numeroContrato ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemandaList", idLongList);

		return query.getResultList();
	}

	@Override
	public List<Demanda> findFilhosByIdPaiFetch(Long id) {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.demandaPai pai");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxos");
		hql.append(" INNER JOIN FETCH fluxos.caixaPostal caixa");
		hql.append(" WHERE pai.id = :idDemandaPai ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemandaPai", id);

		return query.getResultList();
	}

	@Override
	public List<Demanda> findFilhosConsultaByIdPaiECaixaPostalFetch(Long id, CaixaPostal caixaDemandante) {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.demandaPai pai");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxos");
		hql.append(" INNER JOIN FETCH fluxos.caixaPostal caixa");
		hql.append(" WHERE pai.id = :idDemandaPai ");
		hql.append(" AND demanda.caixaPostalDemandante.id = :idCaixaDemandante");
		hql.append(" AND demanda.tipoDemanda = :tipoDemanda");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemandaPai", id);
		query.setParameter("idCaixaDemandante", caixaDemandante.getId());
		query.setParameter("tipoDemanda", TipoDemandaEnum.CONSULTA);

		return query.getResultList();
	}

	@Override
	public List<Demanda> findFilhosIniciaisByIdPaiPostalFetch(Long id) {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.demandaPai pai");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante demandante");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel responsavel");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxos");
		hql.append(" INNER JOIN FETCH fluxos.caixaPostal caixa");
		hql.append(" WHERE pai.id = :idDemandaPai ");
		hql.append(" AND demanda.tipoDemanda != :tipoDemanda");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idDemandaPai", id);
		query.setParameter("tipoDemanda", TipoDemandaEnum.CONSULTA);

		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.caixa.gitecsa.siarg.dao.CaixaPostalDAO#
	 * obterQuantidadeDemandasDaCaixaPostal(br.gov.caixa
	 * .gitecsa.siarg.model.CaixaPostal)
	 */
	@Override
	public Long obterQuantidadeDemandasAbertasDaCaixaPostal(Long idCaixaPostal) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT count(demanda) ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" LEFT JOIN demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" WHERE caixaPostalResponsavel.id = :idCaixaPostal AND demanda.situacao = :aberta ");

		Query query = this.getEntityManager().createQuery(hql.toString());

		query.setParameter("idCaixaPostal", idCaixaPostal);
		query.setParameter("aberta", SituacaoEnum.ABERTA);

		return (Long) query.getSingleResult();
	}

	@Override
	public Long obterQuantidadeDemandasPorAssunto(Assunto assunto) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT count(demanda) ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.assunto assunto ");
		hql.append(" WHERE assunto.id = :idAssunto");
		hql.append(" AND demanda.situacao = :aberta ");

		Query query = this.getEntityManager().createQuery(hql.toString());

		query.setParameter("idAssunto", assunto.getId());
		query.setParameter("aberta", SituacaoEnum.ABERTA);

		return (Long) query.getSingleResult();
	}

	@Override
	public List<Demanda> obterDemandasEncaminhadosExternasNoPeriodo(Abrangencia abrangenciaSelecionada,
			Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH caixaPostalResponsavel.unidade unidadeCaixaPostalResponsavel  ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostalAssunto ");

		hql.append(" INNER JOIN demanda.atendimentosList atendimentos ");
		hql.append(" INNER JOIN atendimentos.fluxoDemanda fluxoDemanda ");
		hql.append(" INNER JOIN fluxoDemanda.caixaPostal caixaFluxoDemanda ");
		hql.append(" INNER JOIN caixaFluxoDemanda.unidade unidadeFluxoDemanda ");
		hql.append(" INNER JOIN unidadeFluxoDemanda.abrangencia abrangencia ");

		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosFetch ");
		hql.append(" LEFT JOIN FETCH atendimentosFetch.unidadeExterna unidadeExternaFetch ");

		hql.append(" WHERE 1=1 ");
		hql.append(
				" AND (unidadeFluxoDemanda.tipoUnidade = :externa OR unidadeFluxoDemanda.tipoUnidade = :arrobaexterna)");
		hql.append(" AND abrangencia = :abrangenciaSelecionada ");
		hql.append(" AND atendimentos.dataHoraRecebimento BETWEEN :dataInicial AND :dataFinal ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
		query.setParameter("dataInicial", dataInicial);
		query.setParameter("dataFinal", dataFinal);
		query.setParameter("externa", TipoUnidadeEnum.EXTERNA);
		query.setParameter("arrobaexterna", TipoUnidadeEnum.ARROBA_EXTERNA);

		return query.getResultList();
	}

	@Override
	public List<Demanda> obterListaDemandasAbertasParaExtrato() {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH caixaPostalResponsavel.unidade unidade ");
		hql.append(" INNER JOIN FETCH unidade.abrangencia abran ");
		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosList ");
		hql.append(" WHERE 1=1 ");
		hql.append(" AND demanda.situacao = :situacaoAberta ");
		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("situacaoAberta", SituacaoEnum.ABERTA);

		return query.getResultList();

	}

	@Override
	public List<Demanda> obterListaDemandasQuestionadasDemandante() {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosList ");
		hql.append(" INNER JOIN demanda.atendimentosList atendimentosFiltroList ");
		hql.append(" WHERE 1=1 ");
		hql.append(" AND demanda.situacao = :situacaoAberta ");
		hql.append(" AND caixaPostalDemandante.id = caixaPostalResponsavel.id ");
		hql.append(" AND atendimentosFiltroList.acaoEnum = :acaoQuestionar ");
		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("situacaoAberta", SituacaoEnum.ABERTA);
		query.setParameter("acaoQuestionar", AcaoAtendimentoEnum.QUESTIONAR);

		return query.getResultList();
	}

	@Override
	public List<Demanda> relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(Date dataInicio, Date datatFim,
			Unidade unidade) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostalAssunto");
		hql.append(" INNER JOIN FETCH caixaPostalAssunto.unidade unidadeAssunto ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante  ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH caixaPostalResponsavel.unidade unidadeResponsavel ");

		hql.append(" WHERE 1=1 ");
		hql.append(" AND (demanda.situacao = :aberta OR demanda.situacao = :fechada) ");
		hql.append(" AND unidadeDemandante.isRelConsolidadoAssunto = :ativo ");
		hql.append(" AND demanda.dataHoraAbertura BETWEEN :dataInicial AND :dataFinal ");
		if (unidade != null) {
			hql.append(" AND unidadeDemandante.id = :idUnidade");
		}

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("aberta", SituacaoEnum.ABERTA);
		query.setParameter("fechada", SituacaoEnum.FECHADA);
		query.setParameter("dataInicial", dataInicio);
		query.setParameter("dataFinal", datatFim);
		query.setParameter("ativo", Boolean.TRUE);
		if (unidade != null) {
			query.setParameter("idUnidade", unidade.getId());
		}

		return query.getResultList();

	}

	@Override
	public List<Demanda> relatorioUnidadesDemandantesPorSubordinacao(Date dataInicio, Date datatFim, Unidade unidade,
			List<Unidade> listaUnidades, Abrangencia abrangenciaSelecionada) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostalAssunto ");
		hql.append(" INNER JOIN FETCH caixaPostalAssunto.unidade unidadeAssunto ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");
		hql.append(" INNER JOIN FETCH unidadeDemandante.unidadeSubordinacao unidadeSubordinacao ");
		hql.append(" WHERE 1=1 ");
		hql.append(" AND (demanda.situacao = :aberta OR demanda.situacao = :fechada) ");
		hql.append(" AND demanda.dataHoraAbertura BETWEEN :dataInicial AND :dataFinal ");
		hql.append(" AND abrangencia.id = :idAbrangencia ");

		if (unidade != null) {
			hql.append(" AND unidadeSubordinacao.id = :idUnidadeSubordinacao");
		} else {
			hql.append(" AND unidadeSubordinacao.id IN (:unidades)");
		}

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("aberta", SituacaoEnum.ABERTA);
		query.setParameter("fechada", SituacaoEnum.FECHADA);
		query.setParameter("dataInicial", dataInicio);
		query.setParameter("dataFinal", datatFim);
		query.setParameter("idAbrangencia", abrangenciaSelecionada.getId());

		if (unidade != null) {
			query.setParameter("idUnidadeSubordinacao", unidade.getId());
		} else {
			List<Long> listaIdsUnidades = new ArrayList<>();
			for (Unidade temp : listaUnidades) {
				listaIdsUnidades.add(temp.getId());
			}
			query.setParameter("unidades", listaIdsUnidades);
		}
		return query.getResultList();
	}

	@Override
	public List<Demanda> obterListaDemandasAbertasPorSuegPeriodoRealizadas(Abrangencia abrangencia, Unidade unidade,
			Date dataInicio, Date datatFim, List<Long> listaIdsUnidades) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");
		hql.append(" LEFT JOIN FETCH unidadeDemandante.unidadeSubordinacao unidadeSubordinacao ");

		hql.append(" WHERE 1=1 ");
		hql.append(" AND (demanda.situacao = :aberta OR demanda.situacao = :fechada) ");
		hql.append(" AND demanda.dataHoraAbertura BETWEEN :dataInicial AND :dataFinal ");
		hql.append(" AND (abrangencia.id = :idAbrangencia) ");

		if (unidade != null) {
			hql.append(" AND (unidadeSubordinacao.id = :idUnidadeSubordinacao) ");
		} else {
			hql.append(" AND (unidadeSubordinacao.id IN (:unidades)) ");
		}

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("aberta", SituacaoEnum.ABERTA);
		query.setParameter("fechada", SituacaoEnum.FECHADA);
		query.setParameter("dataInicial", dataInicio);
		query.setParameter("dataFinal", datatFim);
		query.setParameter("idAbrangencia", abrangencia.getId());

		if (unidade != null) {
			query.setParameter("idUnidadeSubordinacao", unidade.getId());
		} else {
			query.setParameter("unidades", listaIdsUnidades);
		}
		return query.getResultList();
	}

	@Override
	public List<Demanda> obterListaDemandasAbertasPorSuegPeriodoATratar(Abrangencia abrangencia, Unidade unidade,
			Date dataInicio, Date datatFim, List<Long> listaIdsUnidades) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" INNER JOIN FETCH fluxosDemandasList.caixaPostal caixaPostalFD ");
		hql.append(" INNER JOIN FETCH caixaPostalFD.unidade unidadeFD ");
		hql.append(" INNER JOIN FETCH unidadeFD.abrangencia abrangencia ");
		hql.append(" INNER JOIN FETCH unidadeFD.unidadeSubordinacao unidadeSubordinacaFD ");

		hql.append(" WHERE 1=1 ");
		hql.append(" AND (demanda.situacao = :aberta OR demanda.situacao = :fechada) ");
		hql.append(" AND (abrangencia.id = :idAbrangencia) ");
		hql.append(" AND demanda.dataHoraAbertura BETWEEN :dataInicial AND :dataFinal ");

		if (unidade != null) {
			hql.append(" AND unidadeSubordinacaFD.id = :idUnidadeSubordinacao ");
		} else {
			hql.append(" AND unidadeSubordinacaFD.id IN (:unidades) ");
		}

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataInicial", dataInicio);
		query.setParameter("dataFinal", datatFim);
		query.setParameter("idAbrangencia", abrangencia.getId());

		query.setParameter("fechada", SituacaoEnum.FECHADA);
		query.setParameter("aberta", SituacaoEnum.ABERTA);

		if (unidade != null) {
			query.setParameter("idUnidadeSubordinacao", unidade.getId());
		} else {
			query.setParameter("unidades", listaIdsUnidades);
		}
		return query.getResultList();
	}

	@Override
	public List<Demanda> obterListaDemandasPorUnidadesResponsavelPeloAssunto(List<Long> idUnidadeList,
			List<SituacaoEnum> situacaoEnumList) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN assunto.caixaPostal cxAssunto ");
		hql.append(" INNER JOIN cxAssunto.unidade unidAssunto ");

		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" INNER JOIN FETCH fluxosDemandasList.caixaPostal caixaPostalFD ");
		hql.append(" INNER JOIN FETCH caixaPostalFD.unidade unidadeFD ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel cxResponsavel");
		hql.append(" INNER JOIN FETCH cxResponsavel.unidade unidadeResponsavel ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante cxDemandante");

		hql.append(" WHERE unidAssunto.id IN (:idUnidadeList) ");
		hql.append(" AND fluxosDemandasList.ativo = TRUE ");
		hql.append(" AND demanda.situacao IN (:situacaoEnumList) ");

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idUnidadeList", idUnidadeList);
		query.setParameter("situacaoEnumList", situacaoEnumList);

		return query.getResultList();
	}

	@Override
	public Boolean existeDemandasPorUnidadesResponsavelPeloAssunto(List<Long> idUnidadeList,
			List<SituacaoEnum> situacaoEnumList) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT COUNT (demanda) ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN demanda.assunto assunto ");
		hql.append(" INNER JOIN assunto.caixaPostal cxAssunto ");
		hql.append(" INNER JOIN cxAssunto.unidade unidAssunto ");

		hql.append(" WHERE unidAssunto.id IN (:idUnidadeList) ");
		hql.append(" AND demanda.situacao IN (:situacaoEnumList) ");

		TypedQuery<Long> query = this.getEntityManager().createQuery(hql.toString(), Long.class);
		query.setParameter("idUnidadeList", idUnidadeList);
		query.setParameter("situacaoEnumList", situacaoEnumList);
		Long valor = query.getSingleResult();

		if (valor > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Demanda> findByNumeroDemanda(List<Long> listaDemandaArquivo) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" WHERE demanda.id IN (:listaDemandaArquivo) ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("listaDemandaArquivo", listaDemandaArquivo);
		return query.getResultList();
	}

	@Override
	public List<Demanda> obterListaDemandasRelatorioAssuntoPeriodo(Long idAssunto, Date periodoDtInicial,
			Date periodoDtFinal, SituacaoEnum... situacao) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");

		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN assunto.caixaPostal cxAssunto ");
		hql.append(" INNER JOIN cxAssunto.unidade unidAssunto ");

		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" INNER JOIN FETCH fluxosDemandasList.caixaPostal caixaPostalFD ");
		hql.append(" INNER JOIN FETCH caixaPostalFD.unidade unidadeFD ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel cxResponsavel");
		hql.append(" INNER JOIN FETCH cxResponsavel.unidade unidadeResponsavel ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante cxDemandante");

		hql.append(" WHERE assunto.id = :idAssunto ");
		hql.append(" AND fluxosDemandasList.ativo = TRUE ");

		// TODO Ajustes de filtro por situação
		hql.append(" AND demanda.situacao IN (:situacaoEnumList) ");

		hql.append(
				" AND ((demanda.situacao = :situacaoAberta AND demanda.dataHoraAbertura BETWEEN :periodoDtInicial AND :periodoDtFinal) ");
		hql.append(
				" OR (demanda.situacao = :situacaoFechada AND demanda.dataHoraEncerramento BETWEEN :periodoDtInicial AND :periodoDtFinal ))");

		// TODO Ajustes de filtro por situação

		hql.append(" ORDER BY demanda.id ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);
		query.setParameter("idAssunto", idAssunto);
		query.setParameter("situacaoAberta", SituacaoEnum.ABERTA);
		query.setParameter("situacaoFechada", SituacaoEnum.FECHADA);
		query.setParameter("periodoDtInicial", periodoDtInicial, TemporalType.DATE);
		query.setParameter("periodoDtFinal", periodoDtFinal, TemporalType.DATE);
		query.setParameter("situacaoEnumList", Arrays.asList(situacao));

		return query.getResultList();

	}

	@Override
	public List<Demanda> obterDemandasEmAbertoPorAssunto(UnidadeDTO unidade, Assunto assunto, Date dataAberturaInicio,
			Date dataEncerramento, Abrangencia abrangenciaSelecionada) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM FluxoDemanda fluxoDemanda ");
		hql.append(" INNER JOIN fluxoDemanda.demanda demanda ");
		hql.append(" LEFT JOIN FETCH demanda.demandaPai demandaPai ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH caixaPostalResponsavel.unidade unidade ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosDemandasList ");
		hql.append(" INNER JOIN FETCH assunto.caixaPostal caixaPostalAssunto ");
		hql.append(" INNER JOIN caixaPostalAssunto.unidade unidadeAssunto ");
		hql.append(" INNER JOIN unidadeAssunto.abrangencia abrangencia ");

		hql.append(" WHERE demanda.id IN ( ");
		hql.append("    SELECT demandaInt.id  ");
		hql.append("    FROM Demanda demandaInt  ");
		hql.append("    INNER JOIN demandaInt.caixaPostalResponsavel caixaPostalDemandaInt ");
		hql.append("    INNER JOIN caixaPostalDemandaInt.unidade unidade ");
		hql.append("    WHERE (unidade.tipoUnidade != 0) ");
		hql.append(" )  ");

		hql.append(" AND fluxoDemanda.ativo = TRUE ");

		hql.append(" and demanda.situacao = 1 ");
		hql.append(" AND DATE(demanda.dataHoraAbertura) BETWEEN :dataHoraAbertura AND :dataEncerramento ");

		hql.append(" AND abrangencia = :abrangenciaSelecionada ");

		if (unidade != null) {
			hql.append(" and fluxoDemanda.caixaPostal.id in (  ");
			hql.append("    SELECT cxResp.id  ");
			hql.append("    FROM Demanda demandaCx  ");
			hql.append("    INNER JOIN demandaCx.caixaPostalResponsavel cxResp  ");
			hql.append("    INNER JOIN cxResp.unidade unCx");
			hql.append("    WHERE unCx.id = :unidade  ) ");
		}

		if (assunto != null) {
			hql.append(" AND demanda.assunto.id = :assunto ");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataHoraAbertura", dataAberturaInicio, TemporalType.DATE);
		query.setParameter("dataEncerramento", dataEncerramento, TemporalType.DATE);
		query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);

		if (unidade != null) {
			query.setParameter("unidade", unidade.getId());
		}

		if (assunto != null) {
			query.setParameter("assunto", assunto.getId());
		}

		return query.getResultList();
	}

	public List<Demanda> obterDemandasAguardandoUnidade(Date dataInici, Date dataFim,
			Abrangencia abrangenciaSelecionada, Long idUnidade) {
		StringBuilder hql = new StringBuilder();

		hql.append(" Select Distinct demanda ");
		hql.append(" From Demanda demanda");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");
		hql.append(" Join Fetch demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" Join Fetch caixaPostalResponsavel.unidade unidade ");
		hql.append(" Where demanda.situacao = 1 ");
		hql.append(" And unidade.ativo = TRUE ");
		hql.append(" And caixaPostalResponsavel.ativo = TRUE ");
		hql.append(" And DATE(demanda.dataHoraAbertura) BETWEEN :dataHoraAbertura And :dataEncerramento ");

		if (idUnidade != null) {
			hql.append(" And unidade.id = :idUnidade");
		}

		if (idUnidade == null) {
			hql.append(" AND abrangencia = :abrangenciaSelecionada ");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataHoraAbertura", dataInici, TemporalType.DATE);
		query.setParameter("dataEncerramento", dataFim, TemporalType.DATE);

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		if (idUnidade == null) {
			query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
		}

		return query.getResultList();
	}

	@Override
	public List<Demanda> obterDemandasNaExternaAguardandoUnidade(Date dataInici, Date dataFim, Long idUnidade) {
		StringBuilder hql = new StringBuilder();

		hql.append(" Select Distinct demanda ");
		hql.append(" From Demanda demanda");

		hql.append(" Join Fetch demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" Join Fetch caixaPostalResponsavel.unidade unidade ");
		hql.append(" JOIN FETCH demanda.fluxosDemandasList fluxoDemandaFetch ");
		hql.append(" JOIN FETCH fluxoDemandaFetch.caixaPostal cxFluxoFetch ");

		hql.append(" JOIN demanda.fluxosDemandasList fluxoDemanda ");
		hql.append(" JOIN fluxoDemanda.caixaPostal cxFluxo ");

		hql.append(" Where demanda.situacao = 1 ");
		hql.append(" And DATE(demanda.dataHoraAbertura) BETWEEN :dataHoraAbertura And :dataEncerramento ");
		hql.append(" AND (unidade.tipoUnidade = :tipoUnidadeExterna0 OR unidade.tipoUnidade = :tipoUnidadeExterna3) ");
		hql.append(" AND fluxoDemanda.ativo = TRUE ");
		hql.append(" AND cxFluxo.unidade.id = :idUnidade ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataHoraAbertura", dataInici, TemporalType.DATE);
		query.setParameter("dataEncerramento", dataFim, TemporalType.DATE);

		query.setParameter("tipoUnidadeExterna0", TipoUnidadeEnum.ARROBA_EXTERNA);
		query.setParameter("tipoUnidadeExterna3", TipoUnidadeEnum.EXTERNA);

		query.setParameter("idUnidade", idUnidade);

		return query.getResultList();
	}

	@Override
	public List<Demanda> obterDemandasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada,
			Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostaResponsavelJoin ");
		hql.append(" INNER JOIN FETCH caixaPostaResponsavelJoin.unidade unidadeJoin ");

		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN FETCH caixaPostalDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");

		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosFetch ");
		hql.append(" INNER JOIN FETCH fluxosFetch.caixaPostal cxFluxoFetch ");

		hql.append(" WHERE unidadeDemandante.ativo = TRUE ");
		hql.append(" AND caixaPostalDemandante.ativo = TRUE ");
		hql.append(" AND (DATE(demanda.dataHoraAbertura) BETWEEN :dataInicial AND :dataFinal ");
		hql.append(" OR ");
		hql.append(" DATE(demanda.dataHoraEncerramento) BETWEEN :dataInicial AND :dataFinal) ");

		if (idUnidade != null) {
			hql.append(" AND unidadeJoin.id = :idUnidade ");
		}
		if (idUnidade == null) {
			hql.append(" AND abrangencia = :abrangenciaSelecionada ");
		}

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}
		if (idUnidade == null) {
			query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
		}

		// long ini = System.currentTimeMillis();
		List<Demanda> demandas = query.getResultList();
		// long fim = System.currentTimeMillis();
		// System.out.println("Tempo total getResultList: "+(fim-ini)/1000 + " s ");

		// ini = System.currentTimeMillis();
		// for (Demanda demanda : demandas) {
		// if(demanda.getSituacao().equals(SituacaoEnum.FECHADA)) {
		// Hibernate.initialize(demanda.getFluxosDemandasList());
		// for (FluxoDemanda fluxo : demanda.getFluxosDemandasList()) {
		// Hibernate.initialize(fluxo.getCaixaPostal());
		// }
		// }
		// }
		// fim = System.currentTimeMillis();
		// System.out.println("Tempo total Hibernate.initialize() "+(fim-ini)/1000 + " s
		// ");

		return demandas;
	}

	@Override
	public List<Demanda> obterDemandasPorAcaoAtendimento(Date datainicial, Date dataFinal, String caixaPostal,
			String situacao) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosList ");
		hql.append(" INNER JOIN FETCH atendimentosList.fluxoDemanda fluxoDemanda ");
		hql.append(" INNER JOIN FETCH fluxoDemanda.caixaPostal caixaPostal ");
		hql.append(" WHERE DATE(atendimentosList.dataHoraAtendimento) BETWEEN :dataInicial AND :dataFinal ");
		hql.append(" AND caixaPostal.sigla = :caixaPostal");
		hql.append(" AND atendimentosList.acaoEnum = :situacao");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataInicial", datainicial, TemporalType.DATE);
		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);
		query.setParameter("caixaPostal", caixaPostal);

		if (AcaoAtendimentoEnum.QUESTIONAR.getDescricao().equals(situacao)) {
			query.setParameter("situacao", AcaoAtendimentoEnum.QUESTIONAR);
		} else if (AcaoAtendimentoEnum.CONSULTAR.getDescricao().equals(situacao)) {
			query.setParameter("situacao", AcaoAtendimentoEnum.CONSULTAR);
		} else if (AcaoAtendimentoEnum.ENCAMINHAR.getDescricao().equals(situacao)) {
			query.setParameter("situacao", AcaoAtendimentoEnum.ENCAMINHAR);
		} else if (AcaoAtendimentoEnum.RESPONDER.getDescricao().equals(situacao)) {
			query.setParameter("situacao", AcaoAtendimentoEnum.RESPONDER);
		}

		return query.getResultList();

	}

	@Override
	public List<Demanda> obterDemandasWSConsulta(FiltrosConsultaDemandas filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.assunto assunto ");
		hql.append(" INNER JOIN assunto.caixaPostal caixaRespAssunto ");
		hql.append(" INNER JOIN caixaRespAssunto.unidade unidadeRespAssunto ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalDemandante caixaDemandante ");
		hql.append(" INNER JOIN FETCH caixaDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN FETCH demanda.caixaPostalResponsavel caixaResponsavel ");
		hql.append(" INNER JOIN FETCH caixaResponsavel.unidade unidadeResponsavel ");
		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosListFetch ");
		hql.append(" LEFT JOIN FETCH atendimentosListFetch.fluxoDemanda fluxoDemandaAtendimento ");
		hql.append(" LEFT JOIN FETCH fluxoDemandaAtendimento.caixaPostal cxFluxoDemAten ");
		hql.append(" LEFT JOIN demanda.contratosList contratoList ");

		hql.append(" INNER JOIN caixaDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");

		hql.append(" WHERE abrangencia.id = :abrangencia ");
		hql.append(" AND DATE(demanda.dataHoraAbertura) BETWEEN :dataInicial AND :dataFinal ");

		/* INICIO - Campos Obrigatórios */

		// Só abertas (1) ou só fechadas (0)
		if (filtro.getAberta().equals(0) || filtro.getAberta().equals(1)) {
			hql.append(" AND demanda.situacao = :situacao ");
		}

		// Demanda em Externa
		if (filtro.getExterna().equals(1)) {
			hql.append(" AND unidadeResponsavel.tipoUnidade IN (:tipoUnidadeExterna) ");
		}

		if (filtro.getExterna().equals(0)) {
			hql.append(" AND unidadeResponsavel.tipoUnidade NOT IN (:tipoUnidadeExterna) ");
		}

		// Tipo Consulta
		if (filtro.getTipoConsulta().equals(1)) {
			hql.append(" AND demanda.tipoDemanda = :tipoDemanda ");
		}
		if (filtro.getTipoConsulta().equals(0)) {
			hql.append(" AND demanda.tipoDemanda != :tipoDemanda ");
		}

		// Demanda Reaberta
		if (filtro.getReaberta().equals(0)) {
			hql.append(
					" AND NOT EXISTS ( From Atendimento a WHERE a.demanda.id = demanda.id AND a.acaoEnum = :acaoReabrir) ");
		}
		if (filtro.getReaberta().equals(1)) {
			hql.append(
					" AND EXISTS ( From Atendimento a WHERE a.demanda.id = demanda.id AND a.acaoEnum = :acaoReabrir) ");
		}
		/* FIM - Campos Obrigatórios */

		/* INICIO - Campos Opcionais */
		if (filtro.getCoAssunto() != null) {
			hql.append(" AND assunto.id = :idAssunto  ");
		}

		if (StringUtils.isNotBlank(filtro.getCpDemandante())) {
			hql.append(" AND caixaDemandante.sigla = :cpDemandante  ");
		}

		if (StringUtils.isNotBlank(filtro.getCpResponsavelAssunto())) {
			hql.append(" AND caixaRespAssunto.sigla = :cpRespAssunto  ");
		}

		if (StringUtils.isNotBlank(filtro.getCpResponsavelAtual())) {
			hql.append(" AND caixaResponsavel.sigla = :cpRespAtual  ");
		}

		if (filtro.getCoUnidadeDemandante() != null) {
			hql.append(" AND unidadeDemandante.cgcUnidade = :coUnidadeDemandate  ");
		}

		if (filtro.getCoUnidadeRespAssunto() != null) {
			hql.append(" AND unidadeRespAssunto.cgcUnidade = :coUnidadeRespAssunto  ");
		}

		if (filtro.getCoUnidadeRespAtual() != null) {
			hql.append(" AND unidadeResponsavel.cgcUnidade = :coUnidadeRespAtual  ");
		}

		if (StringUtils.isNotBlank(filtro.getNuContrato())) {
			hql.append(" AND contratoList.numeroContrato = :numeroContrato ");
		}

		if (filtro.getTipoIteracao() != null) {
			hql.append(
					" AND EXISTS ( From Atendimento a WHERE a.demanda.id = demanda.id AND a.acaoEnum = :acaoParam)  ");
		}

		/* FIM - Campos Opcionais */

		hql.append(" ORDER BY demanda.id ASC");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		/* INICIO - Campos Obrigatórios */
		query.setParameter("abrangencia", filtro.getIdAbrangencia());
		query.setParameter("dataInicial", filtro.getDataInicial(), TemporalType.DATE);
		query.setParameter("dataFinal", filtro.getDataFinal(), TemporalType.DATE);

		// Só fechadas (0) ou só abertas (1)
		if (filtro.getAberta().equals(0)) {
			query.setParameter("situacao", SituacaoEnum.FECHADA);
		}
		if (filtro.getAberta().equals(1)) {
			query.setParameter("situacao", SituacaoEnum.ABERTA);
		}

		// Tipo Demanda
		if (filtro.getTipoConsulta().equals(0) || filtro.getTipoConsulta().equals(1)) {
			query.setParameter("tipoDemanda", TipoDemandaEnum.CONSULTA);
		}

		// Demanda em Externa
		if (filtro.getExterna().equals(0) || filtro.getExterna().equals(1)) {
			List<TipoUnidadeEnum> tiposExterna = new ArrayList<>();
			tiposExterna.add(TipoUnidadeEnum.EXTERNA);
			tiposExterna.add(TipoUnidadeEnum.ARROBA_EXTERNA);

			query.setParameter("tipoUnidadeExterna", tiposExterna);
		}

		if (filtro.getReaberta().equals(0) || filtro.getReaberta().equals(1)) {
			query.setParameter("acaoReabrir", AcaoAtendimentoEnum.REABRIR);
		}
		/* FIM - Campos Obrigatórios */

		/* INICIO - Campos Opcionais */
		if (filtro.getCoAssunto() != null) {
			query.setParameter("idAssunto", filtro.getCoAssunto());
		}

		if (StringUtils.isNotBlank(filtro.getCpDemandante())) {
			query.setParameter("cpDemandante", filtro.getCpDemandante());
		}

		if (StringUtils.isNotBlank(filtro.getCpResponsavelAssunto())) {
			query.setParameter("cpRespAssunto", filtro.getCpResponsavelAssunto());
		}

		if (StringUtils.isNotBlank(filtro.getCpResponsavelAtual())) {
			query.setParameter("cpRespAtual", filtro.getCpResponsavelAtual());
		}
		if (filtro.getCoUnidadeDemandante() != null) {
			query.setParameter("coUnidadeDemandate", filtro.getCoUnidadeDemandante());
		}

		if (filtro.getCoUnidadeRespAssunto() != null) {
			query.setParameter("coUnidadeRespAssunto", filtro.getCoUnidadeRespAssunto());
		}

		if (filtro.getCoUnidadeRespAtual() != null) {
			query.setParameter("coUnidadeRespAtual", filtro.getCoUnidadeRespAtual());
		}

		if (filtro.getNuContrato() != null) {
			query.setParameter("numeroContrato", filtro.getNuContrato());
		}

		if (filtro.getTipoIteracao() != null) {
			query.setParameter("acaoParam", AcaoAtendimentoEnum.valueOf(filtro.getTipoIteracao()));
		}
		/* FIM - Campos Opcionais */

		return query.getResultList();
	}

	@Override
	public List<Demanda> obterDemandasReabertasPorUnidadePeriodo(Long idUnidade, Abrangencia abrangenciaSelecionada,
			Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.caixaPostalDemandante caixaPostalDemandante ");
		hql.append(" INNER JOIN caixaPostalDemandante.unidade unidadeDemandante ");
		hql.append(" INNER JOIN unidadeDemandante.abrangencia abrangencia ");
		hql.append(" INNER JOIN demanda.caixaPostalResponsavel caixaPostalResponsavel ");

		hql.append(" INNER JOIN demanda.fluxosDemandasList fluxos ");
		hql.append(" INNER JOIN fluxos.caixaPostal cxFluxo ");
		hql.append(" INNER JOIN cxFluxo.unidade unidadeFluxo ");

		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosFetch ");

		hql.append(" WHERE unidadeDemandante.ativo = TRUE ");
		hql.append(" AND caixaPostalDemandante.ativo = TRUE ");
		hql.append(" AND (DATE(demanda.dataHoraAbertura) BETWEEN :dataInicial AND :dataFinal ");
		hql.append(" OR ");
		hql.append(" DATE(demanda.dataHoraEncerramento) BETWEEN :dataInicial AND :dataFinal) ");

		if (idUnidade == null) {
			hql.append(" AND abrangencia = :abrangenciaSelecionada ");
		} else {
			hql.append(" AND unidadeFluxo.id = :idUnidade ");
		}

		hql.append(" AND EXISTS ( From Atendimento a WHERE a.demanda.id = demanda.id AND a.acaoEnum = :reabrir) ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		query.setParameter("reabrir", AcaoAtendimentoEnum.REABRIR);

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}
		if (idUnidade == null) {
			query.setParameter("abrangenciaSelecionada", abrangenciaSelecionada);
		}

		// long ini = System.currentTimeMillis();
		List<Demanda> demandas = query.getResultList();
		// long fim = System.currentTimeMillis();
		// System.out.println("Tempo total obterDemandasReabertasPorUnidadePeriodo:
		// "+(fim-ini)/1000 + " s ");

		return demandas;
	}

	/*
	 * -------------------------------------------------INICIO Metodos para Compor
	 * GRID Relatorios por situação
	 * --------------------------------------------------
	 */

	/**
	 * Compoe GRID na tela de relatorios com as quantidades FECHADA
	 */
	@Override
	public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasFechadas(Long idUnidade,
			Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct  ");
		hql.append(" cx.sg_caixa_postal, count( distinct dem.nu_demanda )    ");
		hql.append(" from argsm001.argtb13_demanda dem  ");
		hql.append(" INNER JOIN argsm001.argtb15_atendimento atd ON atd.nu_demanda =  dem.nu_demanda  ");
		hql.append(" inner join argsm001.argtb16_fluxo_demanda fd on atd.nu_fluxo_demanda = fd.nu_fluxo_demanda  ");
		hql.append(" inner join argsm001.argtb08_caixa_postal cx on fd.nu_caixa_postal = cx.nu_caixa_postal  ");
		hql.append(" inner join argsm001.argtb02_unidade un on un.nu_unidade = cx.nu_unidade  ");
		hql.append(" where 1 = 1 ");
		hql.append(" and dem. ic_situacao = 2 and fd.nu_ordem = 1 and fd.ic_ativo = true ");
		hql.append(" and ((DATE(dem.dh_fechamento) between :dataInicial and :dataFinal)) ");
		if (idUnidade != null) {
			hql.append(" and un.nu_unidade = :idUnidade ");
		}
		hql.append(" group by (cx.sg_caixa_postal)  ");
		hql.append(" ORDER BY CX.SG_CAIXA_POSTAL ");

		Query query = this.getEntityManager().createNativeQuery(hql.toString());

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Object[]> list = query.getResultList();

		List<QuantidadeDemandaPorUnidadeDTO> demandas = new ArrayList<QuantidadeDemandaPorUnidadeDTO>();
		if (list != null) {
			for (Object[] objects : list) {
				if (objects[0] != null && objects[1] != null) {
					QuantidadeDemandaPorUnidadeDTO qtd = new QuantidadeDemandaPorUnidadeDTO();
					qtd.setSiglaCaixaPostal((String) objects[0]);
					qtd.setQuantidadeDemandas(Long.parseLong(objects[1].toString()));
					qtd.setSituacao(SituacaoEnum.FECHADA.toString());
					demandas.add(qtd);
				}
			}
		}

		return demandas;
	}

	@Override
	public List<Demanda> obterDemandasFechadasTotal(Long idUnidade, Long idAbrangencia, Date dataInicial, Date dataFinal) {

		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxosFetch ");
		hql.append(" INNER JOIN FETCH fluxosFetch.caixaPostal cx ");
		hql.append(" INNER JOIN demanda.atendimentosList atendimentosFetch ");
		hql.append(" INNER JOIN atendimentosFetch.fluxoDemanda fluxo ");
		// hql.append(" INNER JOIN fluxo.caixaPostal cxPostal ");
		hql.append(" INNER JOIN cx.unidade unidade ");
		hql.append(" INNER JOIN unidade.abrangencia abrangencia ");

		hql.append(" WHERE  1 = 1  ");
		if (idUnidade != null) {
			hql.append(" and unidade.id = :idUnidade ");
		}else {
			hql.append(" AND abrangencia.id = :idAbrangencia ");
		}
		hql.append(" and demanda.situacao = 2 and fluxosFetch.ordem = 1 and fluxosFetch.ativo = true ");
		hql.append(" and (DATE(demanda.dataHoraEncerramento) BETWEEN :dataInicial AND :dataFinal) ");
		hql.append(" order by cx.sigla asc ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}else {
			query.setParameter("idAbrangencia", idAbrangencia);
		}

		List<Demanda> listaTotal = query.getResultList();

		for (Demanda demanda : listaTotal) {
			demanda.setSituacao(SituacaoEnum.FECHADA);
		}

		return listaTotal;
	}

	/**
	 * Lista de demandas que compoe o relatorio analitico
	 */
	@Override
	public List<Demanda> obterListaDemandasFechadas(String siglaUnidade, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.atendimentosList atendimentosFetch ");
		hql.append(" INNER JOIN atendimentosFetch.fluxoDemanda fluxo ");
		hql.append(" INNER JOIN fluxo.caixaPostal cxPostal ");
		hql.append(" INNER JOIN cxPostal.unidade unidade ");
		hql.append(" where 1=1 ");
		hql.append(" and demanda.situacao = 2 and fluxo.ordem = 1 and fluxo.ativo = true ");
		hql.append(" and ((DATE(demanda.dataHoraEncerramento) BETWEEN :dataInicial AND :dataFinal)) ");
		hql.append(" and cxPostal.sigla = :siglaUnidade ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		if (siglaUnidade != null) {
			query.setParameter("siglaUnidade", siglaUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Demanda> demandas = query.getResultList();

		for (Demanda demanda : demandas) {
			demanda.setSituacaoDemandaRelatorio(SituacaoEnum.FECHADA.toString());
		}

		return demandas;
	}

	@Override
	public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasAbertas(Long idUnidade, Abrangencia abrangenciaSelecionada,
			Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct  ");
		hql.append(" cx.sg_caixa_postal, count( distinct dem.nu_demanda )    ");
		hql.append(" from argsm001.argtb13_demanda dem  ");
		hql.append(
				" inner join argsm001.argtb08_caixa_postal cx on dem.nu_caixa_postal_responsavel = cx.nu_caixa_postal  ");
		hql.append(" inner join argsm001.argtb02_unidade un on un.nu_unidade = cx.nu_unidade  ");
		hql.append(" where 1 = 1 ");
		hql.append(" and dem.ic_situacao = 1 ");
		hql.append(" and ((DATE(dem.dh_abertura) between :dataInicial and :dataFinal)) ");
		if (idUnidade != null) {
			hql.append(" and un.nu_unidade = :idUnidade ");
		}
		hql.append(" group by (cx.sg_caixa_postal)  ");
		hql.append(" ORDER BY CX.SG_CAIXA_POSTAL ");

		Query query = this.getEntityManager().createNativeQuery(hql.toString());

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Object[]> list = query.getResultList();

		List<QuantidadeDemandaPorUnidadeDTO> demandas = new ArrayList<QuantidadeDemandaPorUnidadeDTO>();
		if (list != null) {
			for (Object[] objects : list) {
				if (objects[0] != null && objects[1] != null) {
					QuantidadeDemandaPorUnidadeDTO qtd = new QuantidadeDemandaPorUnidadeDTO();
					qtd.setSiglaCaixaPostal((String) objects[0]);
					qtd.setQuantidadeDemandas(Long.parseLong(objects[1].toString()));
					qtd.setSituacao(SituacaoEnum.ABERTA.toString());
					demandas.add(qtd);
				}
			}
		}

		return demandas;
	}

	/**
	 * Lista de demandas que compoe o relatorio analitico
	 */
	@Override
	public List<Demanda> obterListaDemandasAbertas(String siglaUnidade, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN caixaPostalResponsavel.unidade unidade ");
		hql.append(" where 1=1 ");
		hql.append(" and demanda.situacao = 1 ");
		hql.append(" and ((DATE(demanda.dataHoraAbertura) BETWEEN :dataInicial AND :dataFinal)) ");
		hql.append(" and caixaPostalResponsavel.sigla = :siglaUnidade ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		if (siglaUnidade != null) {
			query.setParameter("siglaUnidade", siglaUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Demanda> demandas = query.getResultList();

		for (Demanda demanda : demandas) {
			demanda.setSituacaoDemandaRelatorio(SituacaoEnum.ABERTA.toString());
		}

		return demandas;
	}

	@Override
	public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasCanceladas(Long idUnidade,
			Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct  ");
		hql.append(" cx.sg_caixa_postal, count( distinct dem.nu_demanda )    ");
		hql.append(" from argsm001.argtb13_demanda dem  ");
		hql.append(
				" inner join argsm001.argtb08_caixa_postal cx on dem.nu_caixa_postal_responsavel = cx.nu_caixa_postal  ");
		hql.append(" inner join argsm001.argtb02_unidade un on un.nu_unidade = cx.nu_unidade  ");
		hql.append(" where 1 = 1 ");
		hql.append(" and dem.ic_situacao = 3 ");
		hql.append(" and ((DATE(dem.dh_fechamento) between :dataInicial and :dataFinal)) ");
		if (idUnidade != null) {
			hql.append(" and un.nu_unidade = :idUnidade ");
		}
		hql.append(" group by (cx.sg_caixa_postal)  ");
		hql.append(" ORDER BY CX.SG_CAIXA_POSTAL ");

		Query query = this.getEntityManager().createNativeQuery(hql.toString());

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Object[]> list = query.getResultList();

		List<QuantidadeDemandaPorUnidadeDTO> demandas = new ArrayList<QuantidadeDemandaPorUnidadeDTO>();
		if (list != null) {
			for (Object[] objects : list) {
				if (objects[0] != null && objects[1] != null) {
					QuantidadeDemandaPorUnidadeDTO qtd = new QuantidadeDemandaPorUnidadeDTO();
					qtd.setSiglaCaixaPostal((String) objects[0]);
					qtd.setQuantidadeDemandas(Long.parseLong(objects[1].toString()));
					qtd.setSituacao(SituacaoEnum.CANCELADA.toString());
					demandas.add(qtd);
				}
			}
		}

		return demandas;
	}

	@Override
	public List<Demanda> obterListaDemandasCanceladas(String siglaUnidade, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN demanda.caixaPostalResponsavel caixaPostalResponsavel ");
		hql.append(" INNER JOIN caixaPostalResponsavel.unidade unidade ");
		hql.append(" where 1=1 ");
		hql.append(" and demanda.situacao = 3 ");
		hql.append(" and ((DATE(demanda.dataHoraEncerramento) BETWEEN :dataInicial AND :dataFinal)) ");
		hql.append(" and caixaPostalResponsavel.sigla = :siglaUnidade ");

		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		if (siglaUnidade != null) {
			query.setParameter("siglaUnidade", siglaUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Demanda> demandas = query.getResultList();

		for (Demanda demanda : demandas) {
			demanda.setSituacaoDemandaRelatorio(SituacaoEnum.CANCELADA.toString());
		}

		return demandas;
	}

	@Override
	public List<QuantidadeDemandaPorUnidadeDTO> obterDemandasReabertas(Long idUnidade,
			Abrangencia abrangenciaSelecionada, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct  ");
		hql.append(" cx.sg_caixa_postal, count(distinct dem.nu_demanda) as qtd  ");
		hql.append(" from argsm001.argtb13_demanda dem  ");
		hql.append(" INNER JOIN argsm001.argtb15_atendimento atd ON atd.nu_demanda =  dem.nu_demanda  ");
		hql.append(
				" inner join argsm001.argtb08_caixa_postal cx on dem.nu_caixa_postal_responsavel = cx.nu_caixa_postal  ");
		hql.append(" inner join argsm001.argtb02_unidade un on un.nu_unidade = cx.nu_unidade  ");
		hql.append(" where 1 = 1 ");
		hql.append(" dem.ic_situacao = 1 and atd.nu_acao = 6  ");
		hql.append(" and ((DATE(atd.dh_atendimento) between :dataInicial and :dataFinal)) ");
		if (idUnidade != null) {
			hql.append(" and un.nu_unidade = :idUnidade ");
		}
		hql.append(" group by (cx.sg_caixa_postal)  ");
		hql.append(" ORDER BY CX.SG_CAIXA_POSTAL ");

		Query query = this.getEntityManager().createNativeQuery(hql.toString());

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Object[]> list = query.getResultList();

		List<QuantidadeDemandaPorUnidadeDTO> demandas = new ArrayList<QuantidadeDemandaPorUnidadeDTO>();
		if (list != null) {
			for (Object[] objects : list) {
				if (objects[0] != null && objects[1] != null) {
					QuantidadeDemandaPorUnidadeDTO qtd = new QuantidadeDemandaPorUnidadeDTO();
					qtd.setSiglaCaixaPostal((String) objects[0]);
					qtd.setQuantidadeDemandas(Long.parseLong(objects[1].toString()));
					// qtd.setSituacao("");
					demandas.add(qtd);
				}
			}
		}

		return demandas;
	}

	/**
	 * Lista de demandas que compoe o relatorio analitico
	 */
	@Override
	public List<Demanda> obterListaDemandasReabertasTotal(Long idUnidade, Date dataInicial, Date dataFinal) {
		StringBuilder hql = new StringBuilder();

		hql.append(" SELECT DISTINCT demanda ");
		hql.append(" FROM Demanda demanda ");
		hql.append(" INNER JOIN FETCH demanda.fluxosDemandasList fluxo ");
		hql.append(" INNER JOIN FETCH fluxo.caixaPostal caixaPostal ");
		hql.append(" INNER JOIN FETCH caixaPostal.unidade unidade ");
		hql.append(" where fluxo.ordem = 1 ");
		hql.append(" and fluxo.ativo = true ");
		hql.append(" and demanda.id IN (  ");
		hql.append(" 	select distinct demandaInt ");
		hql.append(" 	FROM Demanda demandaInt ");
		hql.append(" 	INNER JOIN demandaInt.atendimentosList atendimento ");
		hql.append("    where atendimento.acaoEnum = 6 ");
		hql.append("    and ((DATE(atendimento.dataHoraAtendimento) between :dataInicial and :dataFinal)) ");
		hql.append("  ) ");

		if (idUnidade != null) {
			hql.append(" and unidade.id = :idUnidade ");
		}
		TypedQuery<Demanda> query = this.getEntityManager().createQuery(hql.toString(), Demanda.class);

		if (idUnidade != null) {
			query.setParameter("idUnidade", idUnidade);
		}

		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);

		query.setParameter("dataFinal", dataFinal, TemporalType.DATE);

		List<Demanda> demandas = query.getResultList();

		for (Demanda demanda : demandas) {
			demanda.setSituacaoDemandaRelatorio("REABERTA");
		}

		return demandas;
	}

	public Atendimento consultaAtendimento(Long nuDemanda) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct atd.nu_atendimento, atd.dh_recebimento, atd.dh_atendimento, fd.pz_fluxo_demanda   ");
		hql.append(" from argsm001.argtb15_atendimento atd ");
		hql.append(" inner join argsm001.argtb16_fluxo_demanda fd on fd.nu_demanda = atd.nu_demanda ");
		hql.append(" where 1=1 ");
		hql.append(" and atd.nu_demanda = :nuDemanda ");
		hql.append(" and (atd.nu_acao = 5 or atd.nu_acao = 10)");
		hql.append(" and fd.nu_ordem = 1 ");
		hql.append(" and fd.ic_ativo = true ");
		hql.append(" order by 1 desc limit 1 ");

		Query query = this.getEntityManager().createNativeQuery(hql.toString());

		if (nuDemanda != null) {
			query.setParameter("nuDemanda", nuDemanda);
		}

		List<Object[]> list = query.getResultList();

		List<Atendimento> atendimentos = new ArrayList<Atendimento>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (list != null) {
			for (Object[] objects : list) {
				if (objects[0] != null && objects[1] != null) {
					Atendimento atendimento = new Atendimento();
					FluxoDemanda fd = new FluxoDemanda();
					atendimento.setId(new Long(objects[0].toString()).longValue());
					try {
						atendimento.setDataHoraRecebimento((sdf.parse((objects[1]).toString())));
						atendimento.setDataHoraAtendimento((sdf.parse((objects[2]).toString())));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fd.setPrazo(Integer.valueOf(objects[3].toString()));
					atendimento.setFluxoDemanda(fd);
					// qtd.setSituacao("");
					atendimentos.add(atendimento);
				}
			}
		}

		Atendimento atd = new Atendimento();

		atd = atendimentos.get(0);

		return atd;

	}
	
	@Override
	public List<RelatorioIndicadorReaberturaDTO> obterIndicadorReaberturaPorUnidadePeriodo(Long idUnidade, Long idAbrangencia, Date dataInicial, Date dataFinal) {
		StringBuffer  hql = new StringBuffer();
		
		hql.append("SELECT ");
		hql.append("	caixaPostal.sigla AS caixaPostalEnvolvida, ");
		hql.append("	( ");
		hql.append("		SELECT ");
		hql.append("			COUNT(DISTINCT demanda.id) ");
		hql.append("		FROM ");
		hql.append("			Demanda demanda ");
		hql.append("		WHERE demanda.caixaPostalDemandante.id = caixaPostal.id ");	
		hql.append("			AND DATE(demanda.dataHoraAbertura) BETWEEN :dataInicial AND :dataFinal ");	
		hql.append("	) AS qtdDemandasAbertas, ");
		hql.append("	( ");
		hql.append("		SELECT ");
		hql.append("			COUNT(DISTINCT demanda.id) ");
		hql.append("		FROM ");
		hql.append("			Atendimento atendimento ");
		hql.append("		INNER JOIN atendimento.demanda demanda ");
		hql.append("		WHERE demanda.caixaPostalDemandante.id = caixaPostal.id ");	
		hql.append("			AND atendimento.acaoEnum = :nuAcaoReabrir ");	
		hql.append("			AND DATE(atendimento.dataHoraAtendimento) BETWEEN :dataInicial AND :dataFinal ");	
		hql.append("	) AS qtdDemandasReabertas ");
		hql.append("FROM CaixaPostal caixaPostal ");
		hql.append("	INNER JOIN caixaPostal.unidade unidade ");
		hql.append("	INNER JOIN unidade.abrangencia abrangencia ");
		hql.append("WHERE 1 = 1 ");
		
		if (idUnidade != null && idUnidade > 0L) {
			hql.append(" AND unidade.id = :idUnidade ");
		}else {
			hql.append(" AND abrangencia.id = :idAbrangencia ");
		}
		
		TypedQuery<Tuple> query = this.getEntityManager().createQuery(hql.toString(), Tuple.class);
		
		query.setParameter("dataInicial", dataInicial, TemporalType.DATE);
	    query.setParameter("dataFinal", dataFinal, TemporalType.DATE);
	    query.setParameter("nuAcaoReabrir", AcaoAtendimentoEnum.REABRIR);
		
		if (idUnidade != null && idUnidade > 0L) {
			query.setParameter("idUnidade", idUnidade);
		}else {
			query.setParameter("idAbrangencia", idAbrangencia);
		}
		
		List<Tuple> lista  = query.getResultList();
		if (lista != null && lista.size() > 0) {
			List<RelatorioIndicadorReaberturaDTO> listaRetorno = new ArrayList<>();
			for (Tuple linha : lista) {
				String siglaCaixaPostal = linha.get("caixaPostalEnvolvida", String.class);
				Long qtdDemandasAbertas = linha.get("qtdDemandasAbertas", Long.class);
				Long qtdDemandasReabertas = linha.get("qtdDemandasReabertas", Long.class);
				if (qtdDemandasAbertas > 0L || qtdDemandasReabertas > 0L) {
					listaRetorno.add(new RelatorioIndicadorReaberturaDTO(siglaCaixaPostal, qtdDemandasAbertas, qtdDemandasReabertas));
				}
			}
			return listaRetorno;
		}
		return null;
	}

	/*
	 * -------------------------------------------------FIM Metodos para Compor GRID
	 * Relatorios por situação --------------------------------------------------
	 */
}
