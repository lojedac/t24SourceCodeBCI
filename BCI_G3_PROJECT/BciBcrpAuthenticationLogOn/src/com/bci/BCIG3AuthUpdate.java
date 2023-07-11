package com.bci;

import java.util.List;

import com.temenos.api.TBoolean;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsTable;

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
 *       Attached As : Batch Routine
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
public class BCIG3AuthUpdate extends ServiceLifecycle {
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {

        DataAccess da = new DataAccess(this);
        List<String> ids = da.selectRecords("", "EB.BCI.BCRP.CREDENTIALS", "", "WITH SID.STATUS EQ 'Activo'");
        // List<String> ids = new ArrayList<String>();
        // ids.add("SYSTEM");
        return ids;
    }

    @Override
    public void inputRecord(String id, ServiceData serviceData, String controlItem, TBoolean setZeroAuth,
            List<String> versionNames, List<String> recordIds, List<TStructure> records) {
        DataAccess da = new DataAccess(this);
        try {
            TStructure currentRecord = da.getRecord("EB.BCI.BCRP.CREDENTIALS", id);
            EbBciBcrpCredentialsTable bciCredentTable = new EbBciBcrpCredentialsTable(this);
            EbBciBcrpCredentialsRecord bciCredentRec = new EbBciBcrpCredentialsRecord(currentRecord);

            String sidStatus = bciCredentRec.getSidStatus().getValue();
            if (sidStatus.equals("Activo")) {
                // bciCredentRec.setSid("");
                bciCredentRec.setSidStatus("NoActivo");
                bciCredentTable.write(id, bciCredentRec);
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

}
