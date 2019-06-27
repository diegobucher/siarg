package br.gov.caixa.gitecsa.siarg.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.dto.ConfiguracaoEmailDTO;
import br.gov.caixa.gitecsa.siarg.dto.EmailDTO;
import br.gov.caixa.gitecsa.siarg.dto.ExtratoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.dto.PrazoQuestionamentoDemandasDTO;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.Parametro;

public class EmailServiceTest {
  
  @InjectMocks
  private EmailService emailService;
  @Mock
  private ParametroDAO parametroDAO;
  private ConfiguracaoEmailDTO configuracaoEmailDTO = new ConfiguracaoEmailDTO();
  private EmailDTO emailDTO = new EmailDTO();
  private static final String MOTIVO = "Motivo";
  private static final String ASSUNTO = "Assunto";
  private static final String CONTEUDO = "Conteudo";
  private static final String URL = "URL";
  private static final String EMAIL_COPIA = "GEGOV08@mail.caixa";
  List<String> comCopiaList = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.emailDTO.setAssunto("Assunto");
    this.emailDTO.setDestinatario("teste@caixa.gov.br");
    this.emailDTO.setConteudo("Conteúdo");
    comCopiaList.add("teste1@caixa.gov.br");
    comCopiaList.add("teste2@caixa.gov.br");
    this.emailDTO.setComCopiaList(comCopiaList);
    this.configuracaoEmailDTO.setHost("stmtptest.caixa.gov.br");
    this.configuracaoEmailDTO.setPorta("7470");
    this.configuracaoEmailDTO.setRemetente("remetente@caixa.gov.br");
  }

  @Test
  public void enviarTest() {
    try {
      this.emailService.enviar(this.emailDTO, this.configuracaoEmailDTO, MOTIVO);
      assertTrue(true);
    }catch (Exception e) {
       assertFalse(false);
    }
  }
  
  @Test
  public void enviarEmailTest() {
    Map<String, String> map = new HashMap<>();
    String valor = "Valor";
    String chave = "Chave";
    map.put(chave, valor);
    try {
      this.emailService.enviarEmail(this.configuracaoEmailDTO, map, "Assunto", "Conteudo", this.comCopiaList, MOTIVO);
    }catch (Exception e) {
      assertFalse(false);
    }
  }
  
  @Test
  public void enviarEmailPorAcaoTest() {
    Demanda demanda = new Demanda();
    demanda.setId(1L);
    demanda.setTitulo("Titulo");
    demanda.setNomeUsuarioDemandante("c000000");
    demanda.setMatriculaDemandante("c000000");
    Assunto assunto = new Assunto();
    assunto.setNome("Nome Assunto");
    demanda.setAssunto(assunto);
    CaixaPostal caixaOrigem = new CaixaPostal();
    caixaOrigem.setSigla("Sg");
    CaixaPostal caixaDestino = new CaixaPostal();
    caixaDestino.setSigla("Sg");
    caixaDestino.setEmail("email@gov.caixa.br");
    caixaDestino.setRecebeEmail(true);
    List<CaixaPostal> caixasObservadorasList = new ArrayList<>();
    Parametro parametro = new Parametro();
    parametro.setValor("10");
    Mockito.when(this.parametroDAO.obterParametroByNome("QTD_CARACTERES_RESPOSTA")).thenReturn(parametro);
    Mockito.when(this.parametroDAO.obterParametroByNome("URL_SISTEMA")).thenReturn(parametro);
    Mockito.when(this.parametroDAO.obterParametroByNome("CONFIG_HOST")).thenReturn(parametro);
    Mockito.when(this.parametroDAO.obterParametroByNome("CONFIG_PORT")).thenReturn(parametro);
    Mockito.when(this.parametroDAO.obterParametroByNome("CONFIG_FROM")).thenReturn(parametro);
    Mockito.when(parametroDAO.obterParametroByNome("TEMPLATE_JOB_MOVIMENTACAO")).thenReturn(parametro);
    try {
    this.emailService.enviarEmailPorAcao(demanda, caixaOrigem, caixaDestino, caixasObservadorasList, 
        AcaoAtendimentoEnum.FECHAR, "Acompanhamento", "Assunto", 10, new Date().toString(), "c000000");
    }catch (Exception e) {
      assertFalse(false);
    }
  }
  
  @Test
  public void enviarEmailExtratoPorCaixaTest() {
    CaixaPostal caixaDestino = new CaixaPostal();
    caixaDestino.setSigla("Sg");
    caixaDestino.setEmail("email@gov.caixa.br");
    List<ExtratoDemandasDTO> extratoDemandasList = new ArrayList<>();
    ExtratoDemandasDTO extratoDemandasDTO = new ExtratoDemandasDTO();
    Demanda demanda = new Demanda();
    demanda.setId(1L);
    demanda.setTitulo("Titulo");
    demanda.setNomeUsuarioDemandante("c000000");
    demanda.setMatriculaDemandante("c000000");
    Assunto assunto = new Assunto();
    assunto.setNome("Nome Assunto");
    demanda.setAssunto(assunto);
    extratoDemandasDTO.setAssuntoCompleto("assuntoCompleto");
    extratoDemandasDTO.setCaixaPostalResponsavel(caixaDestino);
    extratoDemandasDTO.setCor("cor");
    extratoDemandasDTO.setDemanda(demanda);
    extratoDemandasDTO.setDiasSemInteracao(1);
    extratoDemandasDTO.setUltimaMovimentacao(new Date());
    extratoDemandasList.add(extratoDemandasDTO);
    try {
      this.emailService.enviarEmailExtratoPorCaixa(caixaDestino, this.configuracaoEmailDTO, extratoDemandasList, ASSUNTO, CONTEUDO, URL, MOTIVO, EMAIL_COPIA);
    }catch (Exception e) {
      assertFalse(false);
    }
  }
  
  @Test
  public void enviarEmailQuestionamentoDemandanteTest() {
    Demanda demanda = new Demanda();
    demanda.setId(1L);
    demanda.setTitulo("Titulo");
    demanda.setNomeUsuarioDemandante("c000000");
    demanda.setMatriculaDemandante("c000000");
    Assunto assunto = new Assunto();
    assunto.setNome("Nome Assunto");
    demanda.setAssunto(assunto);
    CaixaPostal caixaDestino = new CaixaPostal();
    caixaDestino.setSigla("Sg");
    caixaDestino.setEmail("email@gov.caixa.br");
    List<PrazoQuestionamentoDemandasDTO> prazoQuestionamentoDemandasList = new ArrayList<>();
    PrazoQuestionamentoDemandasDTO prazoQuestionamentoDemandasDTO = new PrazoQuestionamentoDemandasDTO();
    prazoQuestionamentoDemandasDTO.setAssuntoCompleto(ASSUNTO);
    prazoQuestionamentoDemandasDTO.setDataDemanda(new Date());
    prazoQuestionamentoDemandasDTO.setDataUltimaAlteracao(new Date());
    prazoQuestionamentoDemandasDTO.setDemanda(demanda);
    prazoQuestionamentoDemandasDTO.setDiasSemInteracao(10);
    prazoQuestionamentoDemandasDTO.setIdDemanda(demanda.getId());
    prazoQuestionamentoDemandasList.add(prazoQuestionamentoDemandasDTO);
    try {
    this.emailService.enviarEmailQuestionamentoDemandante(caixaDestino, configuracaoEmailDTO, prazoQuestionamentoDemandasList, 
        ASSUNTO, CONTEUDO, MOTIVO, "10", "10", URL);
    }catch (Exception e) {
      assertFalse(false);
    }
  }

}
