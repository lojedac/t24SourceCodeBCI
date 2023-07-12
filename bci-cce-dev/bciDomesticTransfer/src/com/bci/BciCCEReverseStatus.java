package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TBoolean;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to check the status of credited
 *         transaction.
 * 
 *         Developed By : Andrea Vaca H.
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : BATCH>MNEMONC/BciCCEINRtnBat
 *
 *         Attached As : Batch routine
 */
public class BciCCEReverseStatus extends ServiceLifecycle {

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        DataAccess da = new DataAccess(this);
        List<String> recIds = new ArrayList<String>();
        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

        String returnFile = "";

        returnFile = serviceData.getJobData(1);
        String fileName = paramrec.getInPath().getValue();
        fileName = fileName + "/" + returnFile;
        System.out.println("Filepath: " + fileName);
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            String line = itr.next();
            if ((line.substring(0, 3).equals("799"))) {
                String idUnivoco = line.substring(73, 80);
                System.out.println("IdUvivoco: " + idUnivoco);
                idUnivoco = StringUtils.leftPad(idUnivoco, 15, "0");
                recIds.add(idUnivoco);
            }
        }
        return recIds;
    }

    @Override
    public void inputRecord(String id, ServiceData serviceData, String controlItem, TBoolean setZeroAuth,
            List<String> versionNames, List<String> recordIds, List<TStructure> records) {

        // Variables store information
        String poReveId = "";
        String poOriginal = "";
        String bnkOriginal = "";
        String status = "";
        String preFix = "";
        // Variable to read the BNK of reverse transaction from
        // EB.BCI.CCE.MAPPING.FIELD.VALUES

        DataAccess daMapRec = new DataAccess(this);
        BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                daMapRec.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
        poReveId = mapRec.getInRef().getValue();

        // Variables to read POR.TRANSACTION or PAYMENT.ORDER and read the
        // status
        DataAccess daPo = new DataAccess(this);
        preFix = mapRec.getInRef().getValue().substring(0, 2);
        if (preFix.startsWith("BN")) {
            PorTransactionRecord porTransactionRec = new PorTransactionRecord(
                    daPo.getRecord("POR.TRANSACTION", poReveId));
            status = porTransactionRec.getStatuscode().getValue();
        } else {
            PaymentOrderRecord poaRec = new PaymentOrderRecord(daPo.getRecord("PAYMENT.ORDER", poReveId));
            status = poaRec.getPaymentStatusAddInfo().getValue().substring(0, 3);
        }

        try {
            // Variable to read Payment order and take the BNK original
            poOriginal = mapRec.getPoId().getValue();
            DataAccess daPa = new DataAccess(this);
            PaymentOrderRecord paymentRec = new PaymentOrderRecord(daPa.getRecord("PAYMENT.ORDER", poOriginal));
            bnkOriginal = paymentRec.getPaymentSystemId().getValue();
            if (bnkOriginal.isEmpty()) {
                PaymentOrderRecord paymentRecHist = new PaymentOrderRecord(
                        daPa.getHistoryRecord("PAYMENT.ORDER", poOriginal));
                bnkOriginal = paymentRecHist.getPaymentSystemId().getValue();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        BciCceMappingFieldValuesTable mapTable = new BciCceMappingFieldValuesTable(this);
        if (!status.equals("999")) {
            mapRec.setInRef(bnkOriginal);
            mapRec.setStatus("SENT_OUT");
            try {
                mapTable.write(id, mapRec);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            mapRec.setPoId(poOriginal);
            mapRec.setInRef(bnkOriginal);
            try {
                mapTable.write(id, mapRec);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}