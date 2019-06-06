package gov.healthit.chpl.builder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

import gov.healthit.chpl.dto.surveillance.report.AnnualReportDTO;
import gov.healthit.chpl.dto.surveillance.report.QuarterlyReportDTO;

public class SurveillanceExperienceWorksheetBuilder extends XlsxWorksheetBuilder {
    private static final int MIN_COLUMN = 8;

    private XSSFColor tabColor;
    private PropertyTemplate pt;

    public SurveillanceExperienceWorksheetBuilder(final Workbook workbook) {
        super(workbook);
        pt = new PropertyTemplate();
    }

    /**
     * Creates a formatted Excel worksheet with the information in the report.
     * @param report
     * @return
     */
    public Sheet buildWorksheet(final AnnualReportDTO report) throws IOException {
        //create sheet
        Sheet sheet = workbook.createSheet("Surveillance Experience");
        if (sheet instanceof XSSFSheet) {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();
            tabColor = new XSSFColor(
                    new java.awt.Color(196, 215, 155), colorMap);
            xssfSheet.setTabColor(tabColor);

            //hide all the columns after E
            CTCol col = xssfSheet.getCTWorksheet().getColsArray(0).addNewCol();
            col.setMin(MIN_COLUMN);
            col.setMax(DEFAULT_MAX_COLUMN); // the last column (1-indexed)
            col.setHidden(true);

            //TODO: figure out how to hide rows after the content of the sheet ends
        }

        //set some styling that applies to the whole sheet
        sheet.setDisplayGridlines(false);
        sheet.setDisplayRowColHeadings(false);

        //columns B, C, and D need a certain width to match the document format
        int colWidth = getColumnWidth(35);
        sheet.setColumnWidth(1, colWidth);
        sheet.setColumnWidth(2,  colWidth);
        sheet.setColumnWidth(3, colWidth);

        //column F needs a certain width to match the document format
        sheet.setColumnWidth(5, getColumnWidth(71));

        //apply the borders after the sheet has been created
        pt.applyBorders(sheet);
        return sheet;
    }
}