package br.gov.caixa.gitecsa.siarg.ws.exception;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

@Provider
public class SiargWSExceptionHandler implements ExceptionMapper<Exception> {

  @Inject
  protected Logger logger;
  
  @Override
  public Response toResponse(Exception exception) {
    logger.info(ExceptionUtils.getStackTrace(exception));
    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
  }

}
