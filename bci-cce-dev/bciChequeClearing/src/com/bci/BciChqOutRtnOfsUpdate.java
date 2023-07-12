package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.messagehook.MessageContext;
import com.temenos.t24.api.hook.system.MessageLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
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
public class BciChqOutRtnOfsUpdate extends MessageLifecycle {

    @Override
    public void postProcess(OfsRequestDetailRecord requestDetailRecord, MessageContext messageContext) {
        // TODO Auto-generated method stub
        /////////////////////////////
      /*  String outwardFile = "/project/BCI/bnk/UD/CCEFile/sample2.txt";
        List<String> finalList = new ArrayList<String>();

        // System.out.println(outPath);
        FileWriter myWriter = null;
        File myObj = new File(outwardFile);
        try {
            myObj.createNewFile();
            myWriter = new FileWriter(outwardFile);
            try { */
                //////////////////////////////
              //  myWriter.write("Triggered OFS routine" + System.getProperty("line.separator"));
                Date dat = new Date(this);
                DatesRecord datesRec = dat.getDates();
                String today = datesRec.getToday().getValue();
                DataAccess da = new DataAccess(this);
                String msgIn = requestDetailRecord.getMsgIn().getValue();
                String msgOut = requestDetailRecord.getMsgOut().getValue();
                String transactionNumber = requestDetailRecord.getTransReference().getValue();
                String verName = requestDetailRecord.getVersion().getValue();
               /* myWriter.write("msgIn" +msgIn + System.getProperty("line.separator"));
                myWriter.write("msgOut" +msgOut + System.getProperty("line.separator"));
                myWriter.write("requestDetailRecord" +requestDetailRecord + System.getProperty("line.separator"));*/

                String status = requestDetailRecord.getStatus().getValue();
                
                int outlen = StringUtils.ordinalIndexOf(msgOut, "/", 2);
                status = msgOut.substring(outlen + 1, outlen + 2);
               // myWriter.write("status: " + status + System.getProperty("line.separator"));
                if (status.equals("1")) {
                    int pos = msgIn.indexOf("ORDERING.REFERENCE");
                  //  myWriter.write("pos" + pos + System.getProperty("line.separator"));
                    int len = pos + 23;
                    int endlen = len + 15;
                    String id = msgIn.substring(len, endlen);

                 //   myWriter.write("id" + id + System.getProperty("line.separator"));
                    int idlen = id.indexOf(",");
                 //   myWriter.write("successful trans" + System.getProperty("line.separator"));
                    /*
                     * int bnkPos =
                     * msgOut.indexOf("TransactionReferenceNumber"); int bnkLen
                     * = bnkPos + 31; int bnkEndLen = bnkPos + 31 + 16; String
                     * bnkid = msgIn.substring(bnkLen, bnkEndLen); int bnkIdLen
                     * = bnkid.indexOf(",");
                     */
                  //  myWriter.write("R17 version: " + verName + System.getProperty("line.separator"));
                    if (verName.contains("R17")) {
                     //   myWriter.write("R17 version" + System.getProperty("line.separator"));
                        BciCceInChqRtnAdjBalanceRecord mapRec = new BciCceInChqRtnAdjBalanceRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.ADJ.BALANCE", id));
                        BciCceInChqRtnAdjBalanceRecord mapRecwrite = new BciCceInChqRtnAdjBalanceRecord(this);
                    //    myWriter.write("id:" + id + System.getProperty("line.separator"));
                        List<AdjMapFieldTypeClass> fieldListmap = mapRec.getAdjMapFieldType();
                        String recStat = mapRec.getStatus().getValue();
                        AdjMapFieldTypeClass mapclass = new AdjMapFieldTypeClass();
                        AdjMapFieldTypeClass mapclass2 = new AdjMapFieldTypeClass();

                      //  myWriter.write("id:" + id + System.getProperty("line.separator"));
                        //for (AdjMapFieldTypeClass mapId : fieldListmap) {
                            String type = mapRec.getAdjMapFieldType().get(0).getAdjMapFieldType().getValue();
                           // String individualRegistrationfisrt = "";
                        //    myWriter.write("type: " + type + System.getProperty("line.separator"));
                            
                            String individualRegistrationfisrt = mapRec.getAdjMapFieldType(0).getAdjMapFieldVal(0).getValue();
                            String additionalRegistrationfisrt = mapRec.getAdjMapFieldType(1).getAdjMapFieldVal(0).getValue();
                            
                            /*if (type.equals("INDIVIDUAL-B")) {
                                mapclass.setAdjMapFieldType(type);
                                individualRegistrationfisrt = mapRec.getAdjMapFieldType(0).getAdjMapFieldVal(0).getValue();
                                
                            }

                            String additionalRegistrationfisrt = "";
                            if (type.equals("ADDITIONAL-B")) {
                                mapclass.setAdjMapFieldType(type);
                                additionalRegistrationfisrt = mapRec.getAdjMapFieldType(1).getAdjMapFieldVal(0).getValue();
                            }*/
                      //      myWriter.write("additionalRegistrationfisrt:" + additionalRegistrationfisrt   + System.getProperty("line.separator"));

                            mapclass.setAdjMapFieldType("INDIVIDUAL-B");
                            mapclass.setAdjMapFieldVal(individualRegistrationfisrt, 0);
                            mapRecwrite.setAdjMapFieldType(mapclass, 0);

                            mapclass2.setAdjMapFieldType("ADDITIONAL-B");
                            mapclass2.setAdjMapFieldVal(additionalRegistrationfisrt, 0);
                            mapRecwrite.setAdjMapFieldType(mapclass2, 1);

                      //  }
                        // mapRecwrite.setAdjMapFieldType(mapclass, 0);
                       //     myWriter.write("mapRecwrite:" + mapRecwrite + System.getProperty("line.separator"));
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                        mapRecwrite.setDate(today);
                        mapRecwrite.setPoId(transactionNumber);
                        // mapRecwrite.setInRef(bnkid);
                        mapRecwrite.setTime(dtf.format(now));
                        if (recStat.contains("R17")) {
                            mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R17");
                        }
                        if (recStat.contains("R18")) {
                            mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R18");
                        }
                        BciCceInChqRtnAdjBalanceTable mapTbl = new BciCceInChqRtnAdjBalanceTable(this);
                        try {
                            mapTbl.write(id, mapRecwrite);
                        } catch (T24IOException e) {

                        }
                    }

                    if (verName.contains("R16")) {

                        BciCceInChqRtnBalFavourableRecord mapRec = new BciCceInChqRtnBalFavourableRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.BAL.FAVOURABLE", id));
                        BciCceInChqRtnBalFavourableRecord mapRecwrite = new BciCceInChqRtnBalFavourableRecord(this);
                        List<BalMapFieldTypeClass> fieldListmap = mapRec.getBalMapFieldType();

                        // myWriter.write("mapRec:" +mapRec +
                        // System.getProperty("line.separator"));

                        BalMapFieldTypeClass mapclass = new BalMapFieldTypeClass();
                        BalMapFieldTypeClass mapclass2 = new BalMapFieldTypeClass();
                        // for (BalMapFieldTypeClass mapId : fieldListmap) {
                        String type = mapRec.getBalMapFieldType().get(0).getBalMapFieldType().getValue();
                       // String individualRegistrationfisrt = "";

                        
                        String individualRegistrationfisrt = mapRec.getBalMapFieldType(0).getBalMapFieldVal(0).getValue();
                        String additionalRegistrationfisrt = mapRec.getBalMapFieldType(1).getBalMapFieldVal(0).getValue();
                        
                       /* if (type.equals("INDIVIDUAL-C")) {
                            mapclass.setBalMapFieldType(type);
                            // individualRegistrationfisrt =
                            // mapId.getBalMapFieldVal().get(0).getValue();
                            individualRegistrationfisrt = mapRec.getBalMapFieldType(0).getBalMapFieldVal(0).getValue();
                            // bciRec.getMapFieldType(0).getMapFieldVal(0).getValue();
                            //myWriter.write("individualRegistrationfisrt:" + individualRegistrationfisrt  + System.getProperty("line.separator"));
                            /*
                             * mapclass.setBalMapFieldVal(
                             * individualRegistrationfisrt, 0);
                             * mapclass.setBalMapFieldType("INDIVIDUAL-C");
                             * mapclass.setBalMapFieldVal(
                             * individualRegistrationfisrt, 0);
                             * mapRecwrite.setBalMapFieldType(mapclass, 0);
                             
                        }

                        String additionalRegistrationfisrt = "";
                        if (type.equals("ADDITIONAL-C")) {
                            mapclass.setBalMapFieldType(type);
                            additionalRegistrationfisrt = mapRec.getBalMapFieldType(1).getBalMapFieldVal(0).getValue();
                            /*
                             * mapclass.setBalMapFieldVal(
                             * individualRegistrationfisrt, 0);
                             * mapclass.setBalMapFieldType("ADDITIONAL-C");
                             * mapclass.setBalMapFieldVal(
                             * additionalRegistrationfisrt, 0);
                             * mapRecwrite.setBalMapFieldType(mapclass, 0);
                             
                        } */
                        mapclass.setBalMapFieldType("INDIVIDUAL-C");
                        mapclass.setBalMapFieldVal(individualRegistrationfisrt, 0);
                        mapRecwrite.setBalMapFieldType(mapclass, 0);

                        mapclass2.setBalMapFieldType("ADDITIONAL-C");
                        mapclass2.setBalMapFieldVal(additionalRegistrationfisrt, 0);
                        mapRecwrite.setBalMapFieldType(mapclass2, 1);
                        // myWriter.write("mapRecwrite: " + mapRecwrite +
                        // System.getProperty("line.separator"));
                        // }
                       // myWriter.write("mapRecwrite: " + mapRecwrite + System.getProperty("line.separator"));
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                      //  myWriter.write("mapRecwrite 1: " + mapRecwrite + System.getProperty("line.separator"));
                        mapRecwrite.setDate(today);
                        // mapRecwrite.setInRef(bnkid);
                        mapRecwrite.setTime(dtf.format(now));
                        mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R16");
                    //    myWriter.write("mapRecwrite 2: " + mapRecwrite + System.getProperty("line.separator"));
                        mapRecwrite.setPoId(transactionNumber);
                     //   myWriter.write("mapRecwrite 3: " + mapRecwrite + System.getProperty("line.separator"));
                        BciCceInChqRtnBalFavourableTable mapTbl = new BciCceInChqRtnBalFavourableTable(this);
                        // myWriter.write("mapRecwrite 4: " + mapRecwrite +
                        // System.getProperty("line.separator"));
                        try {
                            mapTbl.write(id, mapRecwrite);
                     //       myWriter.write("mapTbl: " + mapRecwrite + System.getProperty("line.separator"));
                        } catch (T24IOException e) {

                        }

                    }

                } else {
                    PaymentOrderRecord payRec = new PaymentOrderRecord(
                            da.getRecord("", "PAYMENT.ORDER", "$NAU", transactionNumber));
                    String recid = payRec.getOrderingReference().getValue();
                  //  myWriter.write("recid: " + recid + System.getProperty("line.separator"));
                    if (verName.contains("R17")) {
                        BciCceInChqRtnAdjBalanceRecord mapRec = new BciCceInChqRtnAdjBalanceRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.ADJ.BALANCE", recid));
                     //   myWriter.write("mapRec: " + mapRec + System.getProperty("line.separator"));
                        String recStat = mapRec.getStatus().getValue();
                        BciCceInChqRtnAdjBalanceRecord mapRecwrite = new BciCceInChqRtnAdjBalanceRecord(this);
                        List<AdjMapFieldTypeClass> fieldListmap = mapRec.getAdjMapFieldType();
                        AdjMapFieldTypeClass mapclass = new AdjMapFieldTypeClass();
                        AdjMapFieldTypeClass mapclass2 = new AdjMapFieldTypeClass();
                        //for (AdjMapFieldTypeClass mapId : fieldListmap) {
                            String type = mapRec.getAdjMapFieldType().get(0).getAdjMapFieldType().getValue();
                           // String individualRegistrationfisrt = "";
                            String individualRegistrationfisrt = mapRec.getAdjMapFieldType(0).getAdjMapFieldVal(0).getValue();
                            String additionalRegistrationfisrt = mapRec.getAdjMapFieldType(1).getAdjMapFieldVal(0).getValue();
                          
                            mapclass.setAdjMapFieldType("INDIVIDUAL-B");
                            mapclass.setAdjMapFieldVal(individualRegistrationfisrt, 0);
                            mapRecwrite.setAdjMapFieldType(mapclass, 0);

                            mapclass2.setAdjMapFieldType("ADDITIONAL-B");
                            mapclass2.setAdjMapFieldVal(additionalRegistrationfisrt, 0);
                            mapRecwrite.setAdjMapFieldType(mapclass2, 1);
                         //   myWriter.write("Written till ind and add: " + mapRecwrite + System.getProperty("line.separator"));

                       // }

                        // mapRecwrite.setAdjMapFieldType(mapclass, 0);
                     //   myWriter.write("REC WRITTEN " + System.getProperty("line.separator"));
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                        mapRecwrite.setDate(today);
                        // mapRecwrite.setInRef(bnkid);
                        // mapRecwrite.setTime(value);
                        if (recStat.contains("R17")) {
                            mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R17_IHLD");
                        }
                        if (recStat.contains("R18")) {
                            mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R18_IHLD");
                        }

                        mapRecwrite.setPoId(transactionNumber);
                        BciCceInChqRtnAdjBalanceTable mapTbl = new BciCceInChqRtnAdjBalanceTable(this);
                    //    myWriter.write("mapRecwrite: " + mapRecwrite + System.getProperty("line.separator"));
                        
                        try {
                            mapTbl.write(recid, mapRecwrite);
                        } catch (T24IOException e) {

                        }
                    }

                    if (verName.contains("R16")) {
                        BciCceInChqRtnBalFavourableRecord mapRec = new BciCceInChqRtnBalFavourableRecord(
                                da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.BAL.FAVOURABLE", recid));
                        BciCceInChqRtnBalFavourableRecord mapRecwrite = new BciCceInChqRtnBalFavourableRecord(this);
                        List<BalMapFieldTypeClass> fieldListmap = mapRec.getBalMapFieldType();
                        BalMapFieldTypeClass mapclass = new BalMapFieldTypeClass();
                        BalMapFieldTypeClass mapclass2 = new BalMapFieldTypeClass();
                       // for (BalMapFieldTypeClass mapId : fieldListmap) {
                          //  String type = mapId.getBalMapFieldType().getValue();
                            
                            String individualRegistrationfisrt = mapRec.getBalMapFieldType(0).getBalMapFieldVal(0).getValue();
                            String additionalRegistrationfisrt = mapRec.getBalMapFieldType(1).getBalMapFieldVal(0).getValue();
                           // String individualRegistrationfisrt = "";
                            /*if (type.equals("INDIVIDUAL-C")) {
                                mapclass.setBalMapFieldType(type);
                                individualRegistrationfisrt = mapId.getBalMapFieldVal().get(0).getValue();
                                
                                /*
                                 * mapclass.setBalMapFieldVal(
                                 * individualRegistrationfisrt, 0);
                                 * mapclass.setBalMapFieldType("INDIVIDUAL-C");
                                 * mapclass.setBalMapFieldVal(
                                 * individualRegistrationfisrt, 0);
                                 * mapRecwrite.setBalMapFieldType(mapclass, 0);
                                 
                            }

                            String additionalRegistrationfisrt = "";
                            if (type.equals("ADDITIONAL-C")) {
                                mapclass.setBalMapFieldType(type);
                                additionalRegistrationfisrt = mapId.getBalMapFieldVal().get(1).getValue();

                                /*
                                 * mapclass.setBalMapFieldVal(
                                 * individualRegistrationfisrt, 0);
                                 * mapclass.setBalMapFieldType("ADDITIONAL-C");
                                 * mapclass.setBalMapFieldVal(
                                 * additionalRegistrationfisrt, 0);
                                 * mapRecwrite.setBalMapFieldType(mapclass, 0);
                                 
                            } */
                            mapclass.setBalMapFieldType("INDIVIDUAL-C");
                            mapclass.setBalMapFieldVal(individualRegistrationfisrt, 0);
                            mapRecwrite.setBalMapFieldType(mapclass, 0);

                            mapclass2.setBalMapFieldType("ADDITIONAL-C");
                            mapclass2.setBalMapFieldVal(additionalRegistrationfisrt, 0);
                            mapRecwrite.setBalMapFieldType(mapclass2, 1);

                      //  }
                        mapRecwrite.setBalMapFieldType(mapclass, 0);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        mapRecwrite.setTime(dtf.format(now));
                        mapRecwrite.setDate(today);
                        // mapRecwrite.setInRef(bnkid);
                        // mapRecwrite.setTime(value);
                        mapRecwrite.setStatus("TO_BE_ADJUST_OUT_R16_IHLD");
                        mapRecwrite.setPoId(transactionNumber);
                        BciCceInChqRtnBalFavourableTable mapTbl = new BciCceInChqRtnBalFavourableTable(this);
                        try {
                            mapTbl.write(recid, mapRecwrite);
                        } catch (T24IOException e) {

                        }
                    }

                }

           /* } catch (Exception e) {

            }
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                myWriter.close();
            } catch (IOException e) {

            }

        }
        /////////////////// */

    }

}
