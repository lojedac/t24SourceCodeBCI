package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.ebbciccegroundreturn.EbBciCceGroundReturnRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardTable;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to update the status of the
 *         template for the confirmed transaction.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : BATCH>BciinCnfBat
 *
 *         Attached As : Batch routine
 *         -----------------------------------------------------------------------------------------------------------------
 *         Input Parameter: ---------------* Argument#1 : N/A Argument#2 : N/A
 *         Argument#3 : N/A -----------------* Output Parameter:
 *         ----------------* Argument#4 : N/A Argument#5 : N/A Argument#6 : N/A
 *         -----------------------------------------------------------------------------------------------------------------
 *         M O D I F I C A T I O N S ***************************
 *         -----------------------------------------------------------------------------------------------------------------
 *         Defect Reference Modified By Date of Change Change Details
 *         (RTC/TUT/PACS) (YYYY-MM-DD)
 *         -----------------------------------------------------------------------------------------------------------------
 *         XXXX <<name of modifier>> <<modification details goes here>>
 *
 *         -----------------------------------------------------------------------------------------------------------------
 */
public class BciinCnfBat extends ServiceLifecycle {

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<String> list = null;
        list = da.selectRecords("", "EB.BCI.CCE.CLEARING.PARAM", "", "WITH @ID EQ SYSTEM");
        return list;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub

        try {
            DataAccess da = new DataAccess(this);
            Date date = new Date(this);
            DatesRecord datesRec = date.getDates();
            String today = datesRec.getToday().getValue();
            String lastWorkingDay = datesRec.getLastWorkingDay().getValue();
            // String transRef = "";
            String confirmFile = "";
            String regCntVal = "";
            String transref = "";
            String idUnivoco = "";
            String appcode = serviceData.getJobData(0);// Data field
            BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                    da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

            EbBciCceGroundReturnRecord ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(this);
            try {
                ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(
                        da.getRecord("EB.BCI.CCE.GROUND.RETURN", "SYSTEM"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            confirmFile = serviceData.getJobData(1);
            String fileName = paramrec.getInPath().getValue();
            fileName = fileName + "/" + confirmFile;

            // Validaciones de los archivos Inward
            List<List<String>> listaArchivos = BciCCEInValidaciones.listaArchivos(fileName);
            Calendar calendario = Calendar.getInstance();
            String hora, minutos, segundos, msegundo;
            hora = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.HOUR_OF_DAY)), 2, "0");
            minutos = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.MINUTE)), 2, "0");
            segundos = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.SECOND)), 2, "0");
            msegundo = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.MILLISECOND)), 3, "0");
            String idLogInward = today + "-" + hora + "" + minutos + "" + segundos + "." + msegundo;
            for (List<String> list : listaArchivos) {

                String msg = BciCCEInValidaciones.validaciones(list, today, lastWorkingDay, ebBciCceGroundReturnRecord,
                        da);
                System.out.println(msg);
                if (!msg.equals("")) {
                    EbBciLCceLogsInwardRecord logInwrdRec = new EbBciLCceLogsInwardRecord(this);
                    logInwrdRec.setDate(today);
                    logInwrdRec.setHour(hora + ":" + minutos + ":" + segundos);
                    logInwrdRec.setFile(confirmFile);
                    logInwrdRec.setDescription(msg);
                    try {
                        EbBciLCceLogsInwardTable logInwrdtable = new EbBciLCceLogsInwardTable(this);
                        logInwrdtable.write(idLogInward, logInwrdRec);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
            }
            List<String> lines = Collections.emptyList();

            try {
                lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(this);
            Iterator<String> itr = lines.iterator();
            while (itr.hasNext()) {
                MapFieldTypeClass mapclass = new MapFieldTypeClass();
                MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
                String line1 = itr.next();
                String line = BciCCEInValidaciones.removeSpecialChar(line1);
                int flgadd = 0;
                int flgind = 0;
                if (line.substring(0, 1).equals("6")) {
                    /*
                     * transRef = line.substring(152, 177); transRef =
                     * transRef.trim();
                     */

                    int regCntLen = line.length();
                    regCntVal = line.substring(regCntLen - 15, regCntLen);
                    // transRef = mapVal.getInRef().getValue();

                    String first = line.substring(0, 99);
                    String second = line.substring(99);
                    mapclass.setMapFieldType("INDIVIDUAL");
                    mapclass.setMapFieldVal(first, 0);
                    mapclass.setMapFieldVal(second, 1);
                    mapRec.setMapFieldType(mapclass, 0);
                }
                if (line.substring(0, 1).equals("7")) {
                    String first = line.substring(0, 99);
                    String second = line.substring(99);
                    mapclass1.setMapFieldType("ADDITIONAL");
                    mapclass1.setMapFieldVal(first, 0);
                    mapclass1.setMapFieldVal(second, 1);
                    mapRec.setMapFieldType(mapclass1, 1);
                    flgadd = 1;
                    flgind = 1;
                }
                // to fetch additional string
                if ((line.substring(0, 1).equals("7")) && line.substring(1, 3).equals("99")) {
                    idUnivoco = line.substring(73, 80);
                    System.out.println("IdUvivoco: " + idUnivoco);
                    idUnivoco = StringUtils.leftPad(idUnivoco, 15, "0");
                }

                if (flgadd == 1 && flgind == 1) {
                    String poId = "";
                    try {
                        BciCceMappingFieldValuesRecord mapRecord = new BciCceMappingFieldValuesRecord(
                                da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", regCntVal));
                        poId = mapRecord.getPoId().getValue();
                        transref = mapRecord.getInRef().getValue();
                    } catch (Exception e) {

                    }
                    mapRec.setStatus("CONFIRMED_OUT");
                    mapRec.setDate(today);
                    mapRec.setPoId(poId);
                    mapRec.setTxnType(appcode);
                    mapRec.setInRef(transref);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    mapRec.setTime(dtf.format(now));
                    BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                    try {
                        mapTbl.write(idUnivoco, mapRec);

                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
