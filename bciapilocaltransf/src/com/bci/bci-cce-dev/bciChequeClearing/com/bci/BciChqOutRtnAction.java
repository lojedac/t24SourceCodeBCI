package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.CommonData;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.Flags;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentApplicationUpdate;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentContext;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.StatusAction;
import com.temenos.t24.api.hook.payments.PaymentLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ebqueriesanswers.EbQueriesAnswersRecord;
import com.temenos.t24.api.records.poragreementandadvice.PorAgreementAndAdviceRecord;
import com.temenos.t24.api.records.poraudittrail.PorAuditTrailRecord;
import com.temenos.t24.api.records.porpostingandconfirmation.PorPostingAndConfirmationRecord;
import com.temenos.t24.api.records.porsupplementaryinfo.PorSupplementaryInfoRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.records.ppcompanyproperties.PpCompanyPropertiesRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterTable;
import com.temenos.t24.api.tables.bcicceheadercounter.NameClass;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceTable;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.AdjMapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableTable;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BalMapFieldTypeClass;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciChqOutRtnAction extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        // TODO Auto-generated method stub
        String transtype = arg1.getIncomingmessagetype().getValue();

        // ADDING FILE WRITING
      /*  String outwardFile = "/project/BCI/bnk/UD/CCEFile/sample3.txt";
        List<String> finalList = new ArrayList<String>();

        // System.out.println(outPath);
        FileWriter myWriter = null;
        File myObj = new File(outwardFile);
        try {
            myObj.createNewFile();
            myWriter = new FileWriter(outwardFile);
            try { */

                ////////////////////
                // myWriter.write("ROUTINE TRIGGERED " +
                //////////////////// System.getProperty("line.separator"));
                String source = arg1.getOriginatingsource().getValue();
              //  myWriter.write("TRYING NEW COPY" + System.getProperty("line.separator"));
                if ((transtype.equals("FOVRBAL")) && (source.equals("POA"))) {
                    // if (source.equals("POA")) {
                    // myWriter.write("condition satisfied" +
                    // System.getProperty("line.separator"));
                    // myWriter.write("ENTERED LOOP 1 " +
                    // System.getProperty("line.separator"));
                    // if ((transtype.equals("FOVRBAL"))) {

                    // myWriter.write("ENTERED LOOP 1 " +
                    // System.getProperty("line.separator"));
                    try {

                        // myWriter.write("ENTERED LOOP " +
                        // System.getProperty("line.separator"));
                        DataAccess da = new DataAccess(this);
                        // String oeId = arg3.getOrderEntryId(0).getValue();
                        String paymentId = arg1.getFilereferenceincoming().getValue();
                        // PpOrderEntryRecord pporderRec = new
                        // PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY",
                        // oeId));
                        PaymentOrderRecord pporderRec = new PaymentOrderRecord(
                                da.getRecord("PAYMENT.ORDER", paymentId));
                        String bnkRef = pporderRec.getPaymentSystemId().getValue();

                        String regCnt = pporderRec.getLocalRefField("L.REGIST.CONTR").getValue();
                        BciCceInChqRtnBalFavourableRecord mapRec = new BciCceInChqRtnBalFavourableRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.BAL.FAVOURABLE", regCnt));
                        BciCceInChqRtnBalFavourableRecord mapRecwrite = new BciCceInChqRtnBalFavourableRecord(this);
                        String date = mapRec.getDate().getValue();
                        // String transactionNumber =
                        // mapRec.getOeId().getValue();
                        List<BalMapFieldTypeClass> fieldListmap = mapRec.getBalMapFieldType();
                        BalMapFieldTypeClass mapclass = new BalMapFieldTypeClass();
                        BalMapFieldTypeClass mapclass2 = new BalMapFieldTypeClass();
                        // String individualRegistrationfisrt = "";
                        String individualRegistrationsec = "";
                        // String additionalRegistrationfirst = "";
                        String additionalRegistrationSec = "";

                        String individualRegistrationfisrt = mapRec.getBalMapFieldType(0).getBalMapFieldVal(0)
                                .getValue();
                        String additionalRegistrationfisrt = mapRec.getBalMapFieldType(1).getBalMapFieldVal(0)
                                .getValue();

                        // myWriter.write("lINE 3 " +
                        // System.getProperty("line.separator"));
                        mapclass.setBalMapFieldType("INDIVIDUAL-C");
                        mapclass.setBalMapFieldVal(individualRegistrationfisrt, 0);
                        mapRecwrite.setBalMapFieldType(mapclass, 0);

                        mapclass2.setBalMapFieldType("ADDITIONAL-C");
                        mapclass2.setBalMapFieldVal(additionalRegistrationfisrt, 0);
                        mapRecwrite.setBalMapFieldType(mapclass2, 1);

                        mapRecwrite.setPoId(paymentId);
                        mapRecwrite.setDate(date);
                        mapRecwrite.setStatus("ADJUST_OUT_R16");
                        // mapRecwrite.setOeId(oeId);
                        mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                        BciCceInChqRtnBalFavourableTable mapTbl = new BciCceInChqRtnBalFavourableTable(this);
                        // myWriter.write("lINE 4 " +
                        // System.getProperty("line.separator"));
                        try {
                            mapTbl.write(regCnt, mapRecwrite);
                        } catch (T24IOException e) {

                        }
                    } catch (Exception e) {

                    }
                    // }
                }

                
                if ((transtype.equals("ADJSTMTS")) && (source.equals("POA"))) {                  
                  //  myWriter.write("condition satisfied FOR R17" + System.getProperty("line.separator"));
                    // myWriter.write("ENTERED LOOP 1 " +
                    // System.getProperty("line.separator"));
                    // if ((transtype.equals("FOVRBAL"))) {

                //    myWriter.write("ENTERED LOOP 1 " + System.getProperty("line.separator"));
                    try {

                     //   myWriter.write("ENTERED LOOP " + System.getProperty("line.separator"));
                        DataAccess da = new DataAccess(this);
                        // String oeId = arg3.getOrderEntryId(0).getValue();
                        String paymentId = arg1.getFilereferenceincoming().getValue();
                        // PpOrderEntryRecord pporderRec = new
                        // PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY",
                        // oeId));
                        PaymentOrderRecord pporderRec = new PaymentOrderRecord(
                                da.getRecord("PAYMENT.ORDER", paymentId));
                        String bnkRef = pporderRec.getPaymentSystemId().getValue();
                        // String id =
                        // pporderRec.getSendersreferencenumber().getValue();
                        // String id =
                        // arg1.getSendersreferenceincoming().getValue();
                        // String paymentOrderId =
                        // arg1.getFilereferenceincoming().getValue();
                      //  myWriter.write("lINE 1 " + System.getProperty("line.separator"));
                        String regCnt = pporderRec.getLocalRefField("L.REGIST.CONTR").getValue();
                        BciCceInChqRtnAdjBalanceRecord mapRec = new BciCceInChqRtnAdjBalanceRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.ADJ.BALANCE", regCnt));
                        BciCceInChqRtnAdjBalanceRecord mapRecwrite = new BciCceInChqRtnAdjBalanceRecord(this);
                        String date = mapRec.getDate().getValue();
                        // String transactionNumber =
                        // mapRec.getOeId().getValue();
                        //List<AdjMapFieldTypeClass> fieldListmap = mapRec.getAdjMapFieldType();
                        
                        String recStat = mapRec.getStatus().getValue();
                        AdjMapFieldTypeClass mapclass = new AdjMapFieldTypeClass();
                        AdjMapFieldTypeClass mapclass2 = new AdjMapFieldTypeClass();
                        String individualRegistrationfisrt = mapRec.getAdjMapFieldType(0).getAdjMapFieldVal(0)
                                .getValue();
                        String additionalRegistrationfisrt = mapRec.getAdjMapFieldType(1).getAdjMapFieldVal(0)
                                .getValue();

                        // myWriter.write("lINE 3 " +
                        // System.getProperty("line.separator"));
                        mapclass.setAdjMapFieldType("INDIVIDUAL-B");
                        mapclass.setAdjMapFieldVal(individualRegistrationfisrt, 0);
                        mapRecwrite.setAdjMapFieldType(mapclass, 0);

                        mapclass2.setAdjMapFieldType("ADDITIONAL-B");
                        mapclass2.setAdjMapFieldVal(additionalRegistrationfisrt, 0);
                        mapRecwrite.setAdjMapFieldType(mapclass2, 1);
                                                  
                        mapRecwrite.setPoId(paymentId);
                        mapRecwrite.setDate(date);
                        if (recStat.contains("R17")) {
                            mapRecwrite.setStatus("ADJUST_OUT_R17");
                        }
                        if (recStat.contains("R18")) {
                            mapRecwrite.setStatus("ADJUST_OUT_R18");
                        }
                        // mapRecwrite.setOeId(oeId);
                        mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                        BciCceInChqRtnAdjBalanceTable mapTbl = new BciCceInChqRtnAdjBalanceTable(this);
                    //    myWriter.write("lINE 4 " + System.getProperty("line.separator"));
                        try {
                            mapTbl.write(regCnt, mapRecwrite);
                        } catch (T24IOException e) {

                        }
                    } catch (Exception e) {

                    }
                    // }
                }
                //////////
          /*  } catch (Exception e) {

            }
            myWriter.close();
        } catch (

        Exception e) {
            e.printStackTrace();
        } finally {
            try {
                myWriter.close();
            } catch (IOException e) {

            } 

        } */
    }

}
