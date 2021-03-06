package br.gov.caixa.gitecsa.siarg.ws.serializer;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;

public class CustomDateTimeSerializer extends StdSerializer<Date>{
  
  public CustomDateTimeSerializer() {
    this(null);
  }
  public CustomDateTimeSerializer(Class t) {
    super(t);
  }
  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider prov) throws IOException, JsonGenerationException {
    gen.writeString(DateUtil.formatData(value, DateUtil.FORMATO_DATA_HORA_COMPLETO));
  }

}
