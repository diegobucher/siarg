package br.gov.caixa.gitecsa.siarg.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import br.gov.caixa.gitecsa.arquitetura.exception.DataBaseException;
import br.gov.caixa.gitecsa.arquitetura.schedule.AbstractSchedule;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.siarg.dao.ParametroDAO;
import br.gov.caixa.gitecsa.siarg.email.EmailService;
import br.gov.caixa.gitecsa.siarg.service.FeriadoService;
import br.gov.caixa.gitecsa.siarg.service.RotinaEmailService;
import br.gov.caixa.gitecsa.siarg.util.ParametroConstantes;

@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class JobQuestionamentoDemandanteSchedule extends AbstractSchedule {

    private static final String TRUE = "true";
    
    private static final String REGEX_HORARIO = "\\d{2}:\\d{2}";

    @Resource
    private UserTransaction transaction;

    private Date dhExecucao;
    
    @EJB
    private EmailService emailService;
    
    @EJB
    private RotinaEmailService rotinaEmailService;

    @EJB
    private FeriadoService feriadoService;

    @Inject
    private ParametroDAO parametroDAO;
    

    @Override
    public void scheduleInitialTask() {
      /**
       * Obter Parâmetro do Jboss - Balanceamento de carga.
       */
      Boolean jobAtivo = Boolean.FALSE;
      if (TRUE.equals(System.getProperty(Constantes.JOB_QUESTIONAMENTO_SCHEDULE))) {
        jobAtivo = Boolean.TRUE;
      }
      if (jobAtivo) {
          this.scheduleTask();
      }
    }

    @Override
    public void scheduleTask() {
      
      SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      String horaAgendamento = parametroDAO.obterParametroByNome(ParametroConstantes.HORARIO_JOB_QUESTIONAMENTO).getValor();

        if (Pattern.matches(REGEX_HORARIO, horaAgendamento)) {
  
          javax.ejb.ScheduleExpression expression = this.getScheduleExpressionForHours(horaAgendamento);
  
          javax.ejb.TimerConfig config = new javax.ejb.TimerConfig();
          config.setInfo(this.getClass());
          config.setPersistent(false);
  
          javax.ejb.Timer timer = this.timerService.createCalendarTimer(expression, config);
          this.dhExecucao = timer.getNextTimeout();
  
          String agendamento = sdf.format(this.dhExecucao);
          logger.info("JOB QUESTIONAMENTO DEMANDAS AGENDADO COM SUCESSO PARA: " + agendamento);
          
      } else {
          logger.info("PARAMETRO INVALIDO. O HORARIO DE ATUALIZACAO DEVE ESTAR NO FORMATO \"HH:mm\"");
      }
    }

    @Override
    public void doWork() {

      try {
        
        if(feriadoService.isDiaUtil(new Date())) {
          SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
          String msgInfo = "=================================================\r\n";
          msgInfo += "Início da Rotina de Alerta - " + sdf.format(new Date()) + "\r\n";
          logger.info(msgInfo);
          
          rotinaEmailService.enviarEmailMonitorarPrazoQuestionamentoDemandante();
          
          msgInfo = "";
          msgInfo += "Fim da Rotina de Alerta - " + sdf.format(new Date()) + "\r\n";
          msgInfo += "=================================================";
          logger.info(msgInfo);
        }
        
      } catch (DataBaseException e) {
        logger.error(e);
      }
      
      
    }
}
