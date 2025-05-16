package com.apiventures.timetrack.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.apiventures.timetrack.entity.SubmittedEntryEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class NotifyPayrollDeptService {

    final String excelFilePath = "src/main/resources/file/hqcentralpayrollrequestform.xlsx";
    @Autowired
    private com.apiventures.timetrack.service.EmailNotificationService emailService;
    //@Scheduled(cron = "0 24 15 * * WED,THU,FRI")
    public void notifyPayrollDept(List<SubmittedEntryEntity> submittedEntries) throws MessagingException, IOException {

        final String outputFilePath = "temp_submitted_hours_" + System.currentTimeMillis() + ".xlsx"; // Example: unique temp filename
        File generatedExcelFile = generateSimpleSubmittedHoursExcel(submittedEntries, outputFilePath);
        String[] ccRecipients = {"somasekhar.patil@marriott.com"};
        String recipient = "edison.nalluri@marriott.com";
        String subject = "Historical Correction Pay Period Week Ending Date 05/16/2025";
        String emailBodyText = "Hi HQ Payroll Admin,<br>Please find the weekly report attached.<br><br>Thank you."; // Example
        String attachmentFilePath = generatedExcelFile.getAbsolutePath(); // Path to your file
        String attachmentFileName = "SubmittedHours.xlsx";

        emailService.sendEmailWithAttachment(
                recipient,
                subject,
                emailBodyText,
                attachmentFilePath,
                attachmentFileName,
                ccRecipients
        );

    }




    public File generateSimpleSubmittedHoursExcel(List<SubmittedEntryEntity> submittedEntries, String outputFilePath) throws IOException {


        Workbook workbook = new XSSFWorkbook();


        Sheet sheet = workbook.createSheet("Submitted Hours");


        Row headerRow = sheet.createRow(0); // First row (index 0)


        String[] headers = {"Date", "Project ID", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};


        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCell.setCellStyle(headerCellStyle);
        }


        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        if (submittedEntries != null && !submittedEntries.isEmpty()) {
            for (SubmittedEntryEntity entry : submittedEntries) {
                Row dataRow = sheet.createRow(rowNum++);


                Cell dateCell = dataRow.createCell(0);
                if (entry.getWeekCloseDate() != null) {
                    dateCell.setCellValue(entry.getWeekCloseDate().format(dateFormatter));
                } else {
                    dateCell.setCellValue("");
                }


                Cell projectCell = dataRow.createCell(1);
                projectCell.setCellValue(entry.getProject());

                Cell monCell = dataRow.createCell(2);
                monCell.setCellValue(entry.getMon());

                Cell tueCell = dataRow.createCell(3);
                tueCell.setCellValue(entry.getTue());

                Cell wedCell = dataRow.createCell(4);
                wedCell.setCellValue(entry.getWed());

                Cell thuCell = dataRow.createCell(5);
                thuCell.setCellValue(entry.getThu());

                Cell friCell = dataRow.createCell(6);
                friCell.setCellValue(entry.getFri());

            }
        } else {

            Row dataRow = sheet.createRow(rowNum++);
            Cell noDataCell = dataRow.createCell(0);
            noDataCell.setCellValue("No submitted hours data available.");

        }


        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }



        File outputFile = new File(outputFilePath);
        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            workbook.write(fileOut);
        } finally {

            workbook.close();
        }


        return outputFile;
    }

}
