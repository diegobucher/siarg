package br.gov.caixa.gitecsa.siarg.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.siarg.dto.RelacaoAssuntosDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioPeriodoPorAssuntoDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.MotivoReaberturaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.SituacaoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoDemandaEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoFluxoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.Auditoria;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.CamposObrigatorios;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.DemandaContrato;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.Parametro;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.model.UsuarioUnidade;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

public class Auxiliar {

	public Demanda criarDemanda(Long id) {
		Demanda demanda = new Demanda();
		demanda.setId(id);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2018, 07, 01, 01, 01);
		demanda.setDataHoraAbertura(calendar.getTime());
		demanda.setSituacao(SituacaoEnum.ABERTA);
		demanda.setCor("#000000");

		List<FluxoDemanda> fluxoDemandas = new ArrayList<FluxoDemanda>();
		fluxoDemandas.add(criarFluxoDemanda(1L));
		fluxoDemandas.add(criarFluxoDemanda(2L));
		demanda.setFluxosDemandasList(fluxoDemandas);

		demanda.setCaixaPostalDemandante(criarCaixaPostal(2L));
		demanda.setAssunto(criarAssunto(1L));
		demanda.setTitulo("Titulo demanda");
		demanda.setCaixaPostalResponsavel(criarCaixaPostal(3L));
		demanda.setTipoDemanda(TipoDemandaEnum.CONSULTA);
		demanda.setFlagDemandaPai(Boolean.FALSE);

		List<Atendimento> atendimentos = new ArrayList<Atendimento>();
		atendimentos.add(criarAtendimento(1L));
		atendimentos.add(criarAtendimento(2L));
		demanda.setAtendimentosList(atendimentos);

		List<CaixaPostal> caixaPostals = new ArrayList<CaixaPostal>();
		caixaPostals.add(criarCaixaPostal(1L));
		caixaPostals.add(criarCaixaPostal(2L));
		demanda.setCaixasPostaisObservadorList(caixaPostals);

		demanda.setTextoDemanda("Texto demanda");
		demanda.setMatriculaDemandante("MATR_01");

		List<Demanda> demandas = new ArrayList<Demanda>();
		demandas.add(criarDemandaFilho(4L));
		demandas.add(criarDemandaFilho(5L));
		demanda.setDemandaFilhosList(demandas);

		List<DemandaContrato> demandaContratos = new ArrayList<DemandaContrato>();
		demandaContratos.add(criarDemandaContrato(1L));
		demandaContratos.add(criarDemandaContrato(2L));
		demanda.setContratosList(demandaContratos);

		return demanda;
	}

	public Demanda criarDemanda(Long id, String sigla) {
		Demanda demanda = criarDemanda(id);
		demanda.setCaixaPostalDemandante(criarCaixaPostal(1L, sigla));
		for (FluxoDemanda fluxoDemanda : demanda.getFluxosDemandasList()) {
			fluxoDemanda.setOrdem(5);
		}
		return demanda;
	}

	private DemandaContrato criarDemandaContrato(Long id) {
		DemandaContrato demandaContrato = new DemandaContrato();
		demandaContrato.setId(id);
		demandaContrato.setNumeroContrato("123ABC");
		return demandaContrato;
	}

	private Demanda criarDemandaFilho(Long id) {
		Demanda demanda = new Demanda();
		demanda.setId(id);
		return demanda;
	}

	public Assunto criarAssunto(Long id) {
		Assunto assunto = new Assunto();
		assunto.setId(id);
		assunto.setNome("Assunto teste");
		assunto.setArvoreCompleta("Assunto 001 > Assunto 002 > Assunto 003");
		assunto.setCaixaPostal(criarCaixaPostal(1L));
		assunto.setContrato(Boolean.TRUE);
		assunto.setAtivo(Boolean.TRUE);
		assunto.setFlagAssunto(Boolean.FALSE);

		List<FluxoAssunto> fluxoAssuntos = new ArrayList<FluxoAssunto>();
		fluxoAssuntos.add(criarFluxoAssunto(1L));
		fluxoAssuntos.add(criarFluxoAssunto(2L));
		assunto.setFluxosAssuntosList(fluxoAssuntos);

		List<Unidade> unidades = new ArrayList<Unidade>();
		unidades.add(criarUnidade(1L));
		assunto.setAssuntoUnidadeDemandanteList(unidades);
		assunto.setAbrangencia(criarAbrangencia(1L));
		assunto.setPrazo(5);
		assunto.setPermissaoAbertura(Boolean.TRUE);
		assunto.setTextoPreDefinido("Texto pré definido");
		assunto.setExcluido(Boolean.FALSE);

		List<CaixaPostal> caixaPostals = new ArrayList<CaixaPostal>();
		caixaPostals.add(criarCaixaPostal(1L));
		caixaPostals.add(criarCaixaPostal(2L));
		assunto.setCaixasPostaisObservadorList(caixaPostals);
		return assunto;
	}

	public Assunto criarAssunto(Long id, String nome) {
		Assunto assunto = criarAssunto(id);
		assunto.setAssuntoPai(criarAssuntoPai(1L, nome));
		return assunto;
	}

	private Assunto criarAssuntoPai(Long id, String nome) {
		Assunto assuntoPai = new Assunto();
		assuntoPai.setId(id);
		assuntoPai.setNome(nome);

		List<Assunto> assuntos = new ArrayList<Assunto>();
		assuntos.add(criarAssunto(5L));
		assuntos.add(criarAssunto(6L));
		assuntoPai.setAssuntosList(assuntos);

		return assuntoPai;
	}

	protected FluxoAssunto criarFluxoAssunto(Long id) {
		FluxoAssunto fluxoAssunto = new FluxoAssunto();
		fluxoAssunto.setId(id);
		fluxoAssunto.setTipoFluxo(TipoFluxoEnum.OUTROS_DEMANDANTES);
		fluxoAssunto.setOrdem(3);
		fluxoAssunto.setCaixaPostal(criarCaixaPostal(1L));
		fluxoAssunto.setPrazo(5);

		Assunto assunto = new Assunto();
		assunto.setId(1L);
		assunto.setNome("Nome assunto");
		fluxoAssunto.setAssunto(assunto);
		return fluxoAssunto;
	}

	public FluxoDemanda criarFluxoDemanda(Long id) {
		FluxoDemanda fluxoDemanda = new FluxoDemanda();
		fluxoDemanda.setId(id);
		fluxoDemanda.setCaixaPostal(criarCaixaPostal(1L));
		fluxoDemanda.setOrdem(0);
		fluxoDemanda.setPrazo(15);
		fluxoDemanda.setAtivo(true);
		return fluxoDemanda;
	}

	public CaixaPostal criarCaixaPostal(Long id) {
		CaixaPostal caixaPostal = new CaixaPostal();
		caixaPostal.setId(id);
		caixaPostal.setSigla("SIGLA");
		caixaPostal.setUnidade(criarUnidade(1L));

		List<Demanda> demandas = new ArrayList<Demanda>();
		demandas.add(criarDemandaFilho(6L));
		demandas.add(criarDemandaFilho(7L));
		caixaPostal.setDemandasObservadasList(demandas);
		return caixaPostal;
	}

	public CaixaPostal criarCaixaPostal(Long id, String sigla) {
		CaixaPostal caixaPostal = criarCaixaPostal(id);
		caixaPostal.setSigla(sigla);
		return caixaPostal;
	}

	public Unidade criarUnidade(Long id) {
		Unidade unidade = new Unidade();
		unidade.setId(id);
		unidade.setTipoUnidade(TipoUnidadeEnum.EXTERNA);

		List<CaixaPostal> caixaPostals = new ArrayList<CaixaPostal>();
		caixaPostals.add(criarCaixaPostalUnidade(3L));
		caixaPostals.add(criarCaixaPostalUnidade(4L));
		unidade.setCaixasPostaisList(caixaPostals);

		unidade.setUnidadeSubordinacao(criarUnidadeFilho(1L));
		unidade.setSigla("SIGLA_UNIDADE");
		unidade.setCgcUnidade(1496513600);
		return unidade;
	}

	private Unidade criarUnidadeFilho(Long id) {
		Unidade unidade = new Unidade();
		unidade.setId(id);
		unidade.setSigla("SIG_UNID");
		return unidade;
	}

	public Unidade criarUnidade(Long id, String sigla) {
		Unidade unidade = criarUnidade(id);
		unidade.setSigla(sigla);
		return unidade;
	}

	private CaixaPostal criarCaixaPostalUnidade(Long id) {
		CaixaPostal caixaPostal = new CaixaPostal();
		caixaPostal.setId(id);
		return caixaPostal;
	}

	public Atendimento criarAtendimento(Long id) {
		Atendimento atendimento = new Atendimento();
		atendimento.setId(id);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -2);
		atendimento.setDataHoraRecebimento(c.getTime());
		c.add(Calendar.DATE, +1);
		atendimento.setDataHoraAtendimento(c.getTime());
		atendimento.setFluxoDemanda(criarFluxoDemanda(3L));
		atendimento.setMatricula("MATR_01");
		atendimento.setDescricao("Texto atendimento");
		atendimento.setNomeUsuarioAtendimento("usuario_atendimento");
		atendimento.setUnidadeExterna(criarUnidade(3L));
		atendimento.setMotivoReabertura(MotivoReaberturaEnum.PRECISA_COMPLEMENTACAO);
		atendimento.setAcaoEnum(AcaoAtendimentoEnum.MIGRAR);
		return atendimento;
	}

	public Abrangencia criarAbrangencia(Long id) {
		Abrangencia abrangencia = new Abrangencia();
		abrangencia.setId(id);

		return abrangencia;
	}

	public UnidadeDTO criarUnidadeDTO(Long id) {
		UnidadeDTO unidadeDTO = new UnidadeDTO();
		unidadeDTO.setId(id);
		return unidadeDTO;
	}

	public Parametro criarParametro(Long id) {
		Parametro parametro = new Parametro();
		parametro.setId(id);
		parametro.setNome("NomeParametro");
		parametro.setValor("ValorParametro");
		return parametro;
	}

	public FiltrosConsultaDemandas criarFiltrosConsultaDemandas() {
		FiltrosConsultaDemandas filtrosConsultaDemandas = new FiltrosConsultaDemandas();
		filtrosConsultaDemandas.setCoUnidadeDemandada(1496513600);
		filtrosConsultaDemandas.setCpDemandada("SIGLA");
		filtrosConsultaDemandas.setPrazoDemanda(30);
		filtrosConsultaDemandas.setPrazoResponsavel(15);
		filtrosConsultaDemandas.setConteudo(1);
		return filtrosConsultaDemandas;
	}

	public Auditoria criarAuditoria(Long id) {
		Auditoria auditoria = new Auditoria();
		auditoria.setId(id);
		auditoria.setMatricula("MATR_AUDITORIA");
		return auditoria;
	}

	protected UsuarioUnidade criarUsuarioUnidade(Long id) {
		UsuarioUnidade usuarioUnidade = new UsuarioUnidade();
		usuarioUnidade.setId(id);
		usuarioUnidade.setUnidade(criarUnidade(1L));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -10);
		usuarioUnidade.setDataInicio(calendar.getTime());
		return usuarioUnidade;
	}

	public CamposObrigatorios criarCamposObrigatorios(Long id) {
		CamposObrigatorios camposObrigatorios = new CamposObrigatorios();
		camposObrigatorios.setId(id);
		return camposObrigatorios;
	}

	public InputStream criarStreamAssunto() throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream("src\\test\\resources\\files\\MigracaoAssuntoTeste.xls"));
	}

	public InputStream criarStreamDemanda() throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream("src\\test\\resources\\files\\MigracaoDemandaTeste.xls"));
	}

	protected RelacaoAssuntosDTO criarRelacaoAssuntosDTO(Long nrAssunto) {
		RelacaoAssuntosDTO relacaoAssuntosDTO = new RelacaoAssuntosDTO();
		relacaoAssuntosDTO.setNumeroAssunto(nrAssunto);
		relacaoAssuntosDTO.setNomeAssunto("Nome assunto 01 > Nome assunto 02 > Nome assunto 03 > Nome assunto 04");
		return relacaoAssuntosDTO;
	}

	protected RelatorioPeriodoPorAssuntoDTO criarRelatorioPeriodoPorAssuntoDTO(Long nrAssunto) {
		RelatorioPeriodoPorAssuntoDTO relatorioPeriodoPorAssuntoDTO = new RelatorioPeriodoPorAssuntoDTO();
		relatorioPeriodoPorAssuntoDTO.setNumeroAssunto(nrAssunto);
		relatorioPeriodoPorAssuntoDTO.setAssunto(criarAssunto(1L, "Nome assunto pai"));
		return relatorioPeriodoPorAssuntoDTO;
	}

}
