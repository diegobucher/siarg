package br.gov.caixa.gitecsa.siarg.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.dto.ConfiguracaoEmailDTO;
import br.gov.caixa.gitecsa.siarg.email.EmailService;
import br.gov.caixa.gitecsa.siarg.enumerator.AcaoAtendimentoEnum;
import br.gov.caixa.gitecsa.siarg.enumerator.TipoUnidadeEnum;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Atendimento;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.Parametro;
import br.gov.caixa.gitecsa.siarg.model.Unidade;

public class RotinaEmailServiceTest {
  
  @InjectMocks
  private RotinaEmailService rotinaEmailService;
  @Mock
  private EmailService emailService;
  @Mock
  private ParametroDAO parametroDAO;
  @Mock
  private AssuntoService assuntoService;
  @Mock
  private FeriadoService feriadoService;
  @Mock
  private DemandaService demandaService;
  private static final String ASSUNTO = "Assunto";
  private static final String TEMPLATE = "Template";
  private static final String URL_SISTEMA = "http://siarg.caixa/siarg/login";
  private ConfiguracaoEmailDTO configuracaoEmailDTO = new ConfiguracaoEmailDTO();
  private Assunto assunto = new Assunto();
  private Assunto assuntoPai = new Assunto();
  private Demanda demanda = new Demanda();
  private List<Assunto> assuntosList = new ArrayList<>();
  private List<Demanda> demandasAbertasList = new ArrayList<>();
  private CaixaPostal caixaPostalDemandante = new CaixaPostal();
  private List<Date> datasList = new ArrayList<>();
  private Atendimento penultimoAtendimento = new Atendimento();
  private Atendimento atendimento = new Atendimento();
  private List<Atendimento> ultimoAtendimento = new ArrayList<>();
  private Unidade unidade = new Unidade();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Calendar cal = Calendar.getInstance();
    cal.set(2018, Calendar.JANUARY, 10);
    this.datasList.add(cal.getTime());
    this.configuracaoEmailDTO.setHost("sistemas.correiolivre.caixa");
    this.configuracaoEmailDTO.setPorta("25");
    this.configuracaoEmailDTO.setRemetente("GEGOV18@mail.caixa");
    this.assunto.setId(1L);
    this.assunto.setNome("Nome Assunto");
    this.assuntoPai.setId(1L);
    this.assuntoPai.setNome("Nome Assunto Pai");
    this.assunto.setAssuntoPai(this.assuntoPai);
    this.demanda.setAssunto(this.assunto);
    this.caixaPostalDemandante.setId(1L);
    this.demanda.setCaixaPostalDemandante(this.caixaPostalDemandante);
    this.demanda.setCaixaPostalResponsavel(this.caixaPostalDemandante);
    this.assuntosList.add(this.assunto);
    this.penultimoAtendimento.setId(1L);
    this.penultimoAtendimento.setDataHoraAtendimento(cal.getTime());
    this.penultimoAtendimento.setDataHoraRecebimento(cal.getTime());
    this.penultimoAtendimento.setAcaoEnum(AcaoAtendimentoEnum.QUESTIONAR);
    this.atendimento.setId(2L);
    this.atendimento.setDataHoraAtendimento(cal.getTime());
    this.atendimento.setDataHoraRecebimento(cal.getTime());
    this.ultimoAtendimento.add(this.penultimoAtendimento);
    this.ultimoAtendimento.add(this.atendimento);
    this.demanda.setAtendimentosList(this.ultimoAtendimento);
    this.unidade.setTipoUnidade(TipoUnidadeEnum.FILIAL);
    this.caixaPostalDemandante.setUnidade(this.unidade);
    this.demandasAbertasList.add(this.demanda);
  }

  @Test
  public void testEnviarEmailExtratoDemandas() {
    Parametro parametroAssunto = new Parametro();
    parametroAssunto.setValor(ASSUNTO);
    Parametro parametroTemplate = new Parametro();
    parametroTemplate.setValor(TEMPLATE);
    Parametro parametroUrl = new Parametro();
    parametroUrl.setValor(URL_SISTEMA);
    Mockito.when(this.parametroDAO.obterParametroByNome("ASSUNTO_JOB_EXTRATO")).thenReturn(parametroAssunto);
    Mockito.when(this.parametroDAO.obterParametroByNome("TEMPLATE_JOB_EXTRATO")).thenReturn(parametroTemplate);
    Mockito.when(this.parametroDAO.obterParametroByNome("URL_SISTEMA")).thenReturn(parametroUrl);
    Mockito.when(this.emailService.getConfiguracaoEmail()).thenReturn(this.configuracaoEmailDTO);
    Mockito.when(this.assuntoService.obterAssuntosFetchPai()).thenReturn(this.assuntosList);
    Mockito.when(this.feriadoService.obterListaDeDatasDosFeriados()).thenReturn(this.datasList);
    Mockito.when(this.demandaService.obterListaDemandasAbertasParaExtrato()).thenReturn(this.demandasAbertasList);
    try {
      this.rotinaEmailService.enviarEmailExtratoDemandas();
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testEnviarEmailMonitorarPrazoQuestionamentoDemandante() throws DataBaseException {
    Parametro parametroAssunto = new Parametro();
    parametroAssunto.setValor(ASSUNTO);
    Parametro parametroTemplate = new Parametro();
    parametroTemplate.setValor(TEMPLATE);
    Parametro parametroUrl = new Parametro();
    parametroUrl.setValor(URL_SISTEMA);
    Parametro parametroPzLimiteCancelamento = new Parametro();
    parametroPzLimiteCancelamento.setValor("5");
    Parametro parametroPzLimiteAviso = new Parametro();
    parametroPzLimiteAviso.setValor("5");
    Mockito.when(this.parametroDAO.obterParametroByNome("ASSUNTO_JOB_QUESTIONAMENTO")).thenReturn(parametroAssunto);
    Mockito.when(this.parametroDAO.obterParametroByNome("TEMPLATE_JOB_QUESTIONAMENTO")).thenReturn(parametroTemplate);
    Mockito.when(this.parametroDAO.obterParametroByNome("URL_SISTEMA")).thenReturn(parametroUrl);
    Mockito.when(this.parametroDAO.obterParametroByNome("PRAZO_LIMITE_CANCELAMENTO")).thenReturn(parametroPzLimiteCancelamento);
    Mockito.when(this.parametroDAO.obterParametroByNome("PRAZO_LIMITE_AVISO")).thenReturn(parametroPzLimiteAviso);
    Mockito.when(this.emailService.getConfiguracaoEmail()).thenReturn(this.configuracaoEmailDTO);
    Mockito.when(this.demandaService.obterListaDemandasQuestionadasDemandante()).thenReturn(this.demandasAbertasList);
    Calendar cal = Calendar.getInstance();
    cal.set(2019, Calendar.JANUARY, 28, 10, 20);
    Mockito.when(this.feriadoService.calcularDataVencimentoPrazo(this.atendimento.getDataHoraRecebimento(), 5)).thenReturn(cal.getTime());
    Mockito.when(this.feriadoService.calcularDataVencimentoPrazo(this.atendimento.getDataHoraRecebimento(), 5)).thenReturn(cal.getTime());
    try {
      this.rotinaEmailService.enviarEmailMonitorarPrazoQuestionamentoDemandante();
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

}
