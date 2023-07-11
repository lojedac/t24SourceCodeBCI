package com.bci;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

import com.techmill.integration.AuthenticationLogon;
import com.techmill.integration.auth.ResponseAuth;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsTable;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcibcrpparam.EbBciBcrpParamRecord;

/**
 *
 * @author David Barahona
 * @mail david.barahona@nagarro.com
 *
 *       ----------------------------------------------------------------------------------------------------------------
 *       Description : This routine is used to logon with BCRP
 * 
 *       Developed By : David Barahona
 *
 *       Development Reference : BCI_G3_IDD030_Interface_Authenticacion_Logon_ES
 *
 *       Attached To : BATCH > BNK/BCI.BCRP.AUTH
 *
 *       Attached As : Before Auth Routine / Input Routine
 *
 *
 *       -----------------------------------------------------------------------------------------------------------------
 *       M O D I F I C A T I O N S ***************************
 *       -----------------------------------------------------------------------------------------------------------------
 *       Defect Reference Modified By Date of Change Change Details
 *       (RTC/TUT/PACS) (YYYY-MM-DD)
 *       -----------------------------------------------------------------------------------------------------------------
 *       XXXX <<name of modifier>> <<modification details goes here>>
 *
 *       -----------------------------------------------------------------------------------------------------------------
 */
public class LBTRAutenticationLogOn extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // Session ss = new Session(this);
        // String userId = ss.getUserId();
        // DatesRecord dt = new DatesRecord(this);
        // com.temenos.t24.api.system.Date date = new
        // com.temenos.t24.api.system.Date(this);
        // String fechaActual = new
        // SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        // TDate tdate = new TDate(fechaActual);
        // String tipeDay = date.getDayType(tdate);
        DataAccess da = new DataAccess(this);
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        EbBciBcrpCredentialsRecord bciCredentRec = new EbBciBcrpCredentialsRecord(currentRecord);
        String initHour = String.format("%4s", bciCredentRec.getInitHour().getValue()).replace(' ', '0');
        String endHour = String.format("%4s", bciCredentRec.getEndHour().getValue()).replace(' ', '0');
        String horaActual = new SimpleDateFormat("HHmm").format(Calendar.getInstance().getTime());
        Date date1, date2, dateActual;
        String password = bciCredentRec.getPassword().getValue();
        String codigo = bciCredentRec.getCodigo().getValue();
        // if (tipeDay.equals("HOLIDAY")) {
        // bciCredentRec.getEndHour().setError("Today is NOT WORKING_DAY!");
        // bciCredentRec.getInitHour().setError("Today is NOT WORKING_DAY!");
        // return bciCredentRec.getValidationResponse();
        // }
        Session session = new Session(this);
        String userId = session.getUserId();

        SimpleDateFormat dateformtfch = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformthora = new SimpleDateFormat("HHmmss");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String myfecha = dateformtfch.format(date);
        String myHora = dateformthora.format(date);
        String myHora1 = dateformthora1.format(date);

        String mydate = myfecha + "-" + myHora;
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);

        try {
            DateFormat dateFormat = new SimpleDateFormat("HHmm");
            date1 = dateFormat.parse(initHour);
            date2 = dateFormat.parse(endHour);
            dateActual = dateFormat.parse(horaActual);

            if ((date1.compareTo(dateActual) <= 0) && (date2.compareTo(dateActual) >= 0)) {
                ResponseAuth bcrpResponse = new ResponseAuth();
                EbBciBcrpCredentialsRecord bciCredentRecNew = new EbBciBcrpCredentialsRecord(
                        da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
                String statusActual = bciCredentRecNew.getSidStatus().getValue();
                if (statusActual.equals("Activo")) {
                    bciCredentRec.getSidStatus().setError("EB-LBTR.ACTIVE.SESSION");
                    return bciCredentRec.getValidationResponse();
                }

                integrationLogsRecord.setAppName("AuthenticationLogon");
                integrationLogsRecord.setOut("codigo=" + codigo + " password=" + password);
                integrationLogsRecord.setTxId(userId);

                try {
                    AuthenticationLogon authenticationLogon = new AuthenticationLogon();
                    bcrpResponse = authenticationLogon.processRequest(codigo, password);
                    integrationLogsRecord.setIn(bcrpResponse.toString());
                    if (bcrpResponse.isOk()) {
                        EbBciBcrpCredentialsTable bciCredentTable = new EbBciBcrpCredentialsTable(this);
                        try {
                            bciCredentRecNew.setSid(bcrpResponse.getResult().getSid());
                            bciCredentRecNew.setSidStatus("Activo");
                            bciCredentRec.setSidStatus("Activo");
                            bciCredentTable.write("SYSTEM", bciCredentRecNew);
                            integrationLogsTable.write("LOGON-" + mydate, integrationLogsRecord);
                        } catch (Exception e) {
                            integrationLogsRecord.setFlagErr("YES");
                            bciCredentRec.getSid().setError("EB-LBTR.LOGIN.FAIL");
                        }
                    } else {
                        if (!bcrpResponse.getResult().getCodError().equals("-195")) {
                            integrationLogsRecord.setFlagErr("YES");
                            String msg = bcrpResponse.getDescription() + "::"
                                    + bcrpResponse.getResult().getMensajeError();
                            bciCredentRec.getSid().setError(msg);
                        }
                    }
                } catch (Exception e) {
                    integrationLogsRecord.setFlagErr("YES");
                    bciCredentRec.getSid().setError(e.getMessage());
                }

            } else {
                bciCredentRec.getEndHour().setError("EB-LBTR.OFF.HOUR");
                bciCredentRec.getInitHour().setError("EB-LBTR.OFF.HOUR");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            integrationLogsTable.write("LOGON-" + mydate, integrationLogsRecord);
            password = RandomStringUtils.randomAlphanumeric(3) + password + RandomStringUtils.randomAlphanumeric(5);
            bciCredentRec.setPassword(password);
            currentRecord.set(bciCredentRec.toStructure());
        } catch (T24IOException e) {
        }
        return bciCredentRec.getValidationResponse();

    }

    // ID.RTN
    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        Session ss = new Session(this);
        String userId = ss.getUserId();
        return userId;
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        DataAccess da = new DataAccess(this);
        EbBciBcrpParamRecord bciBcrpParamRec = new EbBciBcrpParamRecord(this);
        String initHour = "0000";
        String endHour = "0000";
        String status = "";
        try {
            bciBcrpParamRec = new EbBciBcrpParamRecord(da.getRecord("EB.BCI.BCRP.PARAM", "SYSTEM"));
            initHour = bciBcrpParamRec.getInitHour().getValue();
            endHour = bciBcrpParamRec.getEndHour().getValue();
            EbBciBcrpCredentialsRecord bciCredentRecSys = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
            status = bciCredentRecSys.getSidStatus().getValue();
            EbBciBcrpCredentialsRecord bciCredentRec = new EbBciBcrpCredentialsRecord(currentRecord);
            bciCredentRec.setInitHour(initHour);
            bciCredentRec.setEndHour(endHour);
            bciCredentRec.setSidStatus(status);
            currentRecord.set(bciCredentRec.toStructure());
        } catch (Exception e) {
        }

    }
}
