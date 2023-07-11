package com.bci;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

import com.techmill.integration.AuthenticationLogoff;
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

/**
 *
 * @author David Barahona
 * @mail david.barahona@nagarro.com
 *
 *       ----------------------------------------------------------------------------------------------------------------
 *       Description : This routine is used to log off session with BCRP
 * 
 *       Developed By : David Barahona
 *
 *       Development Reference : BCI_G3_IDD030_Interface_Authenticacion_Logon_ES
 *
 *       Attached To : EB.BCI.BCRP.CREDENTIALS,LOGOFF
 *
 *       Attached As : Before Auth Routine
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
public class LBTRAutenticationLogOff extends RecordLifecycle {
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        EbBciBcrpCredentialsRecord bciCredentRec = new EbBciBcrpCredentialsRecord(currentRecord);

        ResponseAuth bcrpResponse = new ResponseAuth();
        String password = bciCredentRec.getPassword().getValue();
        String codigo = bciCredentRec.getCodigo().getValue();

        EbBciBcrpCredentialsRecord bciCredentRecNew = new EbBciBcrpCredentialsRecord(
                da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        EbBciBcrpCredentialsTable bciCredentTable = new EbBciBcrpCredentialsTable(this);
        String statusActual = bciCredentRecNew.getSidStatus().getValue();
        if (statusActual.equals("NoActivo")) {
            bciCredentRec.getSidStatus().setError("EB-LBTR.NOACTIVE.SESSION");
            return bciCredentRec.getValidationResponse();
        }
        AuthenticationLogoff authenticationLogoff = new AuthenticationLogoff();
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
        integrationLogsRecord.setAppName("AuthenticationLogOff");
        integrationLogsRecord.setOut("codigo=" + codigo + " password=" + password);
        integrationLogsRecord.setTxId(userId);

        try {
            bcrpResponse = authenticationLogoff.processRequest(codigo, password);
            integrationLogsRecord.setIn(bcrpResponse.toString());
            if (bcrpResponse.isOk()) {
                password = RandomStringUtils.randomAlphanumeric(3) + password + RandomStringUtils.randomAlphanumeric(5);
                bciCredentRecNew.setSidStatus("NoActivo");
                bciCredentRec.setSidStatus("NoActivo");
                bciCredentRec.setPassword(password);
                currentRecord.set(bciCredentRec.toStructure());
            } else {
                if (bcrpResponse.getResult().getCodError().equals("-203")) {
                    integrationLogsRecord.setFlagErr("YES");
                    bciCredentRecNew.setSidStatus("NoActivo");
                } else {
                    String msg = bcrpResponse.getDescription() + "::" + bcrpResponse.getResult().getMensajeError();
                    bciCredentRec.getSid().setError(msg);
                }

            }
        } catch (Exception e1) {
            integrationLogsRecord.setIn(e1.getMessage());
        }

        try {
            bciCredentTable.write("SYSTEM", bciCredentRecNew);
            integrationLogsTable.write("LOGOFF-" + mydate, integrationLogsRecord);
        } catch (T24IOException e) {
        }
        return bciCredentRec.getValidationResponse();
    }
}
