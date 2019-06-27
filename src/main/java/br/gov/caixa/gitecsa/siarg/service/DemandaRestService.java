package br.gov.caixa.gitecsa.siarg.service;

import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;

import br.gov.caixa.gitecsa.siarg.ws.model.DemandaAbertaDTO;
import br.gov.caixa.gitecsa.siarg.ws.model.FiltrosConsultaDemandas;

//@Stateless
@Singleton
public class DemandaRestService {
  
  @Inject
  private DemandaService demandaService;
  
  public List<DemandaAbertaDTO> obterDemandasWSConsulta(FiltrosConsultaDemandas filtro) {
    return demandaService.obterDemandasWSConsulta(filtro);
  }

}
