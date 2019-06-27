package br.gov.caixa.gitecsa.arquitetura.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

public class JasperReportUtils {

  /**
   * Executa a geração do relatório.
   */
  public static StreamedContent run(final InputStream jasper, final String filename, final Map<String, Object> parameters,
      List<?> dataSource) throws JRException {
    try {
      JRDataSource ds = null;
      if (dataSource != null && !dataSource.isEmpty()) {
        ds = new JRBeanCollectionDataSource(dataSource);
      } else {
        ds = new JREmptyDataSource();
      }

      File report = new File(filename);
      byte[] out = JasperRunManager.runReportToPdf(jasper, parameters, ds);

      FileOutputStream outputStream = new FileOutputStream(report);
      outputStream.write(out);
      outputStream.close();

      return RequestUtils.download(report, filename);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Exportar para Excel.
   * @param  jasper jasper
   * @param nomeArquivo nomeArquivo
   * @param params params
   * @param dataSource dataSource
   * @return xls
   * @throws JRException JRException
   * @throws IOException IOException
   */
  public static StreamedContent runXLS(final InputStream jasper, final String nomeArquivo, final HashMap<String, Object> params,
      final List<?> dataSource) throws JRException, IOException {
    JRDataSource ds = null;
    if (dataSource != null && !dataSource.isEmpty()) {
      ds = new JRBeanCollectionDataSource(dataSource);
    } else {
      ds = new JREmptyDataSource();
    }

    JasperReport jasperReport = JasperCompileManager.compileReport(jasper);
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, ds);

    JRXlsExporter exporter = new JRXlsExporter();
    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsReport);
    exporter.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    exporter.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
    exporter.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporter.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
    exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
    exporter.setParameter(JExcelApiExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
    exporter.setParameter(JExcelApiExporterParameter.IS_IGNORE_CELL_BACKGROUND, Boolean.FALSE);
    exporter.exportReport();

    InputStream inputStream = new ByteArrayInputStream(xlsReport.toByteArray());
    StreamedContent streamedContent = new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", nomeArquivo + ".xls");
    xlsReport.close();
    return streamedContent;
  }
}
