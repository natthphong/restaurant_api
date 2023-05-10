package com.prior.restaurant.service;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.prior.restaurant.entity.BillEntity;
import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.BillModel;
import com.prior.restaurant.models.IncomeModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.repository.BillNativeRepository;
import com.prior.restaurant.repository.BillRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


@Service
@Slf4j
public class BillService {

    @Value("${project.path.bill}")
    private String pathBill;
    private final BillRepository billRepository;
    private final BillNativeRepository billNativeRepository;

    public BillService(BillRepository billRepository, BillNativeRepository billNativeRepository) {
        this.billRepository = billRepository;
        this.billNativeRepository = billNativeRepository;
    }

    public boolean validateBill(BillModel billModel){
        return StringUtils.isNotEmpty(billModel.getUuid())&&
                StringUtils.isNotEmpty(billModel.getMenuList())&&
                null!= billModel.getNumTable()&&
                null!= billModel.getOrderId();
    }


    public void generateBill(String message) {
        try {
            log.info("message {}" , message);
            ObjectMapper objectMapper = new ObjectMapper();
           BillModel billModel = objectMapper.readValue(message,BillModel.class);
            billModel.setTimeStamp(LocalDate.now());
                if (validateBill(billModel)){
                    String data = "date : "+billModel.getTimeStamp()+"\n"+
                            "order : " + billModel.getOrderId() + "\n" + "bill : " + billModel.getUuid()
                            + "\n" + "table: " + billModel.getNumTable()+ "\n \n";
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(pathBill + File.separator
                                + billModel.getUuid() + ".pdf"));
                    document.open();
                    setRectangleInPdf(document);
                    Paragraph chunk = new Paragraph("Restaurant", getFont("Header"));
                    chunk.setAlignment(Element.ALIGN_CENTER);
                    document.add(chunk);
                    Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                    document.add(paragraph);
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    addTableHeader(table);
                    JSONArray jsonArray = getJsonArrayFromString(billModel.getMenuList());
                    int sum = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String, Object> jsonStr = new ObjectMapper().readValue(jsonArray.get(i).toString(), HashMap.class);
                        sum +=addRows(table,jsonStr);
                    }
                    table.setHorizontalAlignment(Element.ALIGN_CENTER);
                    document.add(table);
                    Paragraph footer = new Paragraph("\n\n\n\n\n\n\nTotal: " + sum + "\n\n"
                            + "Thank you for visiting. Please visit again!!", getFont("Data")
                    );
                    billModel.setTotal(sum);
                    insertBill(billModel);
                    footer.setAlignment(Element.ALIGN_CENTER);
                    document.add(footer);
                    document.close();
                }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private int  addRows(PdfPTable table, Map<String, Object> data) {
        //log.info("Inside addRows {}", data);
        String id = "menu : "+ String.valueOf(data.get("id"));
        table.addCell(id);
        table.addCell(String.valueOf(data.get("name")));
        table.addCell(String.valueOf(data.get("price")));
        table.addCell(String.valueOf(data.get("type")));
        table.addCell(String.valueOf(data.get("status")));
        return Integer.parseInt(String.valueOf(data.get("price")));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Id", "Name", "Price", "Type", "Status")
                .forEach(title -> {
                    PdfPCell     header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(title));
                    header.setBackgroundColor(BaseColor.WHITE);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }


    private Font getFont(String type) {
        switch (type) {
            case "Header":
                Font header = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                header.setStyle(Font.BOLD);
                return header;
            case "Data":
                Font data = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                data.setStyle(Font.BOLD);
                return data;
            default:
                return new Font();
        }

    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside RECTANGLE PDF");
       Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(BillModel billModel) {
        log.info("inside insert bill");
        BillEntity bill = new BillEntity();
        bill.setUuid(billModel.getUuid());
        bill.setOrderId(billModel.getOrderId());
        bill.setNumTable(billModel.getNumTable());
        bill.setTotal(billModel.getTotal());
        bill.setTimeStamp(billModel.getTimeStamp());
        this.billRepository.save(bill);
    }


    public  JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }



    public ResponseModel<java.util.List<BillModel>> getBills() throws BaseException {

        ResponseModel<java.util.List<BillModel>> response = new ResponseModel<>();
        response.setTimestamp(LocalDate.now());
        response.setStatus(200);
        response.setDescription("ok");
        try {
            List<BillEntity> billEntities =  billRepository.findAll();
            log.info("inside getBills {}" ,billEntities);
            List<BillModel> billModels = listBillEntityToListBillModel(billEntities);
                response.setData(billModels);
        }catch (Exception ex){
            ex.printStackTrace();
            throw  new BaseException("error in find bills");
        }

        return  response;


    }

    private  BillModel BillEntityToBillModel(BillEntity billEntity){
        BillModel billModel =new BillModel();
        billModel.setUuid(billEntity.getUuid());
        billModel.setOrderId(billEntity.getOrderId());
        billModel.setNumTable(billEntity.getNumTable());
        billModel.setTimeStamp(billEntity.getTimeStamp());
        billModel.setTotal(billEntity.getTotal());
        return  billModel;
    }

    private List<BillModel> listBillEntityToListBillModel(List<BillEntity> billEntities) {
            List<BillModel> billModels = new ArrayList<>();
            for (BillEntity billEntity : billEntities){
                billModels.add(BillEntityToBillModel(billEntity));
            }
            return billModels;

    }

    public ResponseEntity<byte[]> getPdf(String uuid) throws  BaseException{
        log.info("Inside getPdf {}", uuid);
        try {
            byte[] byteArray = new byte[0];
            String filePath = pathBill + File.separator + uuid+ ".pdf";
            log.info("filepath {}", filePath);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
        } catch (Exception ex) {
           throw new BaseException("uuid invalidate");
        }
    }
    private byte[] getByteArray(String filePath) throws IOException {
        log.info("Inside getByteArray");
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(filePath);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    public ResponseModel<IncomeModel> getIncome(IncomeModel incomeModel) throws BaseException {
            log.info("inside income{}" , incomeModel);
        ResponseModel<IncomeModel> response = new ResponseModel<>();
        response.setTimestamp(LocalDate.now());
        response.setStatus(200);
        response.setDescription("ok");
        try {
            Integer income = billNativeRepository.getIncome(incomeModel);

            incomeModel.setTotal(income);

            response.setData(incomeModel);

        }catch (Exception ex){
            ex.printStackTrace();
            throw  new BaseException("error in find bills");
        }

        return  response;
    }
}
