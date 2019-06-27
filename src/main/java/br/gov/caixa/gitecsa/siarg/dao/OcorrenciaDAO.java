package br.gov.caixa.gitecsa.siarg.dao;

import java.util.Date;
import java.util.List;

import br.gov.caixa.gitecsa.arquitetura.dao.BaseDAO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Ocorrencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;

public interface OcorrenciaDAO extends BaseDAO<Ocorrencia> {

  List<Ocorrencia> obterListaOcorrenciasPorUnidade(UnidadeDTO unidadeSelecionadaDTO);

  List<Ocorrencia> obterListaOcorrenciasPor(Abrangencia abrangencia, Date dataPublicacao);

}
