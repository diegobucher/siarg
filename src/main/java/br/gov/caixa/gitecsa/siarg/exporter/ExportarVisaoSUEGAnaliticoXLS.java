package br.gov.caixa.gitecsa.siarg.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import br.gov.caixa.gitecsa.arquitetura.exception.AppException;
import br.gov.caixa.gitecsa.arquitetura.exporter.AbstractExcelDataExporter;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.arquitetura.util.ObjectUtils;
import br.gov.caixa.gitecsa.siarg.dto.DemandantePorSubrodinacaoDTO;

public class ExportarVisaoSUEGAnaliticoXLS extends AbstractExcelDataExporter<DemandantePorSubrodinacaoDTO> {

  private static final String TITULO_RELATORIO = "Visão SUEG - Analítico das Unidades Demandantes x Unidades Demandadas";

  private static final String FONT_FAMILY = "Arial";

  private int maxNumberOfColumns = 0;
  
  private Date dataInicial;
  
  private Date dataFinal;

  public static final Logger LOGGER = Logger.getLogger(ExportarVisaoSUEGAnaliticoXLS.class);

  @Override
  public void createHeader() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    try {
      HSSFRow rowAppName = this.getSheet().createRow(0);
      rowAppName.setHeight((short) 700);
      this.getBook().setSheetName(0, "VisãoSUEGAnalitico");
      
      HSSFCell cellAppName = rowAppName.createCell(0);
      cellAppName.setCellValue(new HSSFRichTextString(MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema")));
      CellRangeAddress regionAppName = new CellRangeAddress(0, 0, 0, (this.maxNumberOfColumns - 1));
      this.addStyleToRegion(regionAppName, this.getHeaderStyle());
      rowAppName.getSheet().addMergedRegion(regionAppName);
      
      HSSFRow rowReportName = this.addRowAt(1);
      HSSFCell cellReportName = rowReportName.createCell(0);
      cellReportName.setCellValue(new HSSFRichTextString(TITULO_RELATORIO));
      cellReportName.setCellStyle(this.getReportNameStyle());
      CellRangeAddress regionReportName = new CellRangeAddress(1, 1, 0, (this.maxNumberOfColumns - 1));
      this.addStyleToRegion(regionReportName, this.getHeaderStyle());
      rowReportName.getSheet().addMergedRegion(regionReportName);

      HSSFRow rowReportName2 = this.addRowAt(2);
      HSSFCell cellReportName2 = rowReportName2.createCell(0);
      cellReportName2.setCellValue(new HSSFRichTextString("Período: " + sdf.format(dataInicial) + " até " + sdf.format(dataFinal)));
      cellReportName2.setCellStyle(this.getReportNameStyle());
      CellRangeAddress regionReportName2 = new CellRangeAddress(2, 2, 0, (this.maxNumberOfColumns - 1));
      this.addStyleToRegion(regionReportName2, this.getHeaderStyle());
      rowReportName2.getSheet().addMergedRegion(regionReportName2);
    
    } catch (Exception e) {
      LOGGER.error(e);
    } 
    
  }

  @Override
  public void createDetail() throws AppException {

    HSSFCellStyle headerStyle = this.getHeaderStyle();
    int rowNumber = 3;
    DemandantePorSubrodinacaoDTO dtoHeader = this.getData().get(0);

    rowNumber++;
    int ultimaColunaUtilizadaHeader = 0;
    HSSFRow rowHeader = this.addRowAt((rowNumber - 1));
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("SUEG");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("CGC");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("Unidade");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue("Demandas Abertas");
    rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
    ultimaColunaUtilizadaHeader++;
    for (String cellHeader : dtoHeader.getSiglaUnidadesDemandasList()) {
      rowHeader.createCell(ultimaColunaUtilizadaHeader).setCellValue(cellHeader);
      rowHeader.getCell(ultimaColunaUtilizadaHeader).setCellStyle(headerStyle);
      ultimaColunaUtilizadaHeader++;
    }

    HSSFCellStyle detailStyleCinza = this.getDetailStyle(true);
    HSSFCellStyle detailStyleBranco = this.getDetailStyle(false);

    for (int index = 0; index < this.getData().size(); index++) {

      int ultimaColunaUtilizada = 0;
      DemandantePorSubrodinacaoDTO dto = this.getData().get(index);
      HSSFRow rowDetail = this.addRowAt(rowNumber);

      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getLetraSueg());
      if (index % 2 == 0) {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleCinza);
      } else {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleBranco);
      }
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(String.format("%04d", dto.getUnidadeDemandante().getCgcUnidade()));
      if (index % 2 == 0) {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleCinza);
      } else {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleBranco);
      }
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getUnidadeDemandante().getSigla());
      if (index % 2 == 0) {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleCinza);
      } else {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleBranco);
      }
      ultimaColunaUtilizada++;
      rowDetail.createCell(ultimaColunaUtilizada).setCellValue(dto.getQtdDemandas());
      if (index % 2 == 0) {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleCinza);
      } else {
        rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleBranco);
      }
      ultimaColunaUtilizada++;
      for (Integer cellValue : dto.getValorUnidadesList()) {
        rowDetail.createCell(ultimaColunaUtilizada).setCellValue(cellValue);
        if (index % 2 == 0) {
          rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleCinza);
        } else {
          rowDetail.getCell(ultimaColunaUtilizada).setCellStyle(detailStyleBranco);
        }
        ultimaColunaUtilizada++;
      }
      rowNumber++;
    }

    this.maxNumberOfColumns = Math.max(this.maxNumberOfColumns, ultimaColunaUtilizadaHeader);
  }

  @Override
  public void createFooter() {
    throw new NotImplementedException();
  }

  
  public File exportComParametros(String filename, Date dataInicio, Date dataFim ) throws FileNotFoundException, IOException, AppException {
    
    this.dataInicial = dataInicio;
    this.dataFinal = dataFim;
    
    File file = export(filename);
    
    return file; 
  }
  
  
  @Override
  public File export(String filename) throws FileNotFoundException, IOException, AppException {

    this.createDetail();
    this.createHeader();

    if (!ObjectUtils.isNullOrEmpty(this.maxNumberOfColumns)) {
      for (int column = 0; column < this.maxNumberOfColumns; column++) {
        this.getSheet().autoSizeColumn(column);
      }
    }

    File file = new File(filename);
    OutputStream out = new FileOutputStream(file);
    this.getBook().write(out);

    return file; 
  }

  private HSSFCellStyle getHeaderStyle() {

    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte) 211, (byte) 211, (byte) 211);
    style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
    style.setLeftBorderColor(HSSFColor.BLACK.index);
    style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
    style.setTopBorderColor(HSSFColor.BLACK.index);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    HSSFFont font = this.getBook().createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeightInPoints((short) 10);
    font.setFontName(FONT_FAMILY);
    style.setFont(font);

    return style;
  }

  private HSSFCellStyle getReportNameStyle() {

    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    palette.setColorAtIndex(HSSFColor.GREY_50_PERCENT.index, (byte) 217, (byte) 217, (byte) 217);
    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
    style.setLeftBorderColor(HSSFColor.BLACK.index);
    style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
    style.setTopBorderColor(HSSFColor.BLACK.index);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    HSSFFont font = this.getBook().createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeightInPoints((short) 11);
    font.setFontName(FONT_FAMILY);
    style.setFont(font);

    return style;
  }

  private HSSFCellStyle getDetailStyle(Boolean cinza) {

    HSSFCellStyle style = this.getBook().createCellStyle();
    HSSFPalette palette = this.getBook().getCustomPalette();
    if (cinza) {
      palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte) 242, (byte) 242, (byte) 242);
      style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
    } else {
      palette.setColorAtIndex(HSSFColor.WHITE.index, (byte) 255, (byte) 255, (byte) 255);
      style.setFillForegroundColor(HSSFColor.WHITE.index);
    }
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    style.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
    style.setBottomBorderColor(HSSFColor.BLACK.index);
    style.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
    style.setRightBorderColor(HSSFColor.BLACK.index);
    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);

    return style;
  }

}
