package com.bci;

import java.util.List;
import java.util.ArrayList;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbciacctstmtentrydays.EbBciAcctStmtEntryDaysRecord;
import com.temenos.t24.api.tables.ebbciacctstmtentrydays.EbBciAcctStmtEntryDaysTable;


/**
*
*----------------------------------------------------------------------------------------------------------------
* Description           : Create a record in EB.BCI.ACCT.STMT.ENTRY.DAYS table by replicate the records from ACCT.ENT.LWORK.DAY during COB
* Developed By          : Preethi I,Techmill Technologies
* Development Reference : BCI_G5_IDD054_Current Account Statement
* Attached To           : BATCH
* Attached As           : Batch Routine
*-----------------------------------------------------------------------------------------------------------------
*  M O D I F I C A T I O N S
* ***************************
*-----------------------------------------------------------------------------------------------------------------
* Defect Reference       Modified By                    Date of Change        Change Details
* (RTC/TUT/PACS)                                        (YYYY-MM-DD)       
*-----------------------------------------------------------------------------------------------------------------
* XXXX                   <<name of modifier>>                            <<modification details goes here>>
* 
* 
*-----------------------------------------------------------------------------------------------------------------
* Include files
*-----------------------------------------------------------------------------------------------------------------
*
*/

public class BciCobMoveAcctEntLworkDayRecs extends ServiceLifecycle {    
        
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {

        DataAccess da = new DataAccess(this);
        
    //Select ACCT.ENT.LWORK.DAY table   
        List<String> acctEntLworkDayRecList = new ArrayList<String>(); 
        try            
        {
            acctEntLworkDayRecList = da.selectRecords("","ACCT.ENT.LWORK.DAY","",""); 
        } 
        catch (Exception acctEntLworkDayErr) {   
            acctEntLworkDayRecList.clear();        
        }                         
        return acctEntLworkDayRecList; 
    }   
    
  //**---------------------------------------------------------------------------------------------------------**//
    
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        
    //Initialize the variable
        DataAccess da = new DataAccess(this);
        Session session = new Session(this);
        
        String arrId = "";
        String productLine = "";
        int acctEntLworkDayFlg = 0;
        String today = "";
        String bciAcctStmtEntryDaysId = "";
        int bciAcctStmtEntryDaysFlg = 0;
        String acctEntLworkDayRecId = id;
        String companyId = session.getCompanyId()+"-COB";
        
    //Get ARRANGEMENT.ID from Account record    
        try
        {
            AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", acctEntLworkDayRecId));   //Read ACCOUNT record for getting OPENING.DATE field value
            arrId = accRec.getArrangementId().getValue();
        }
        catch(Exception accErr)
        {
            arrId = ""; 
        }
        
        
    //Get PRODUCT.LINE from AA.ARRANGEMENT record   
        if(!arrId.equals(""))
        {
            try               
            {      
                AaArrangementRecord aaArrRec = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT",arrId));
                productLine = aaArrRec.getProductLine().getValue();
            }
            catch(Exception aaArrErr)
            {
                productLine = "";
            }
        }
        
        
        if(productLine.equals("ACCOUNTS"))     //Updated ACCT.ENT.LWORK.DAY record to local table, only if PRODUCT.LINE is ACCOUNTS
        {
            
        //Get TODAY value
            try               
            {      
                DatesRecord datesrec = new DatesRecord(da.getRecord("DATES",companyId));
                today = datesrec.getToday().getValue();                 
            } 
            catch (Exception dateErr) 
            {                               
                dateErr.getMessage();  
            }
                
                
       //Get STMT.ENTRY ID's for last working day ACCT.ENT.LWORK.DAY table  
            List<String> acctEntLworkDayRec =  new ArrayList<String>();                       
            try
            {
                acctEntLworkDayRec = da.getConcatValues("ACCT.ENT.LWORK.DAY", acctEntLworkDayRecId);   
            }
            catch(Exception acctEntLworkDayErr)
            {
                acctEntLworkDayFlg = 1;
            }
                
                
            bciAcctStmtEntryDaysId = acctEntLworkDayRecId+"-"+today;     //ID format for EB.BCI.ACCT.STMT.ENTRY.DAYS
                
            EbBciAcctStmtEntryDaysRecord bciAcctStmtEntryDaysRec = new EbBciAcctStmtEntryDaysRecord();
            try
            {
                bciAcctStmtEntryDaysRec = new EbBciAcctStmtEntryDaysRecord(da.getRecord("EB.BCI.ACCT.STMT.ENTRY.DAYS", bciAcctStmtEntryDaysId));
            }
            catch(Exception bciAcctStmtEntryDaysErr)
            {
                bciAcctStmtEntryDaysFlg = 1;
            }
                
                
        //If ACCT.ENT.LWORK.DAY record is exist and EB.BCI.ACCT.STMT.ENTRY.DAYS is not exist
                
            if((acctEntLworkDayFlg == 0) && (bciAcctStmtEntryDaysFlg == 1))
            {       
                EbBciAcctStmtEntryDaysTable bciAcctStmtEntryDaysTable = new EbBciAcctStmtEntryDaysTable(this);            
                bciAcctStmtEntryDaysRec = new EbBciAcctStmtEntryDaysRecord();
                        
                int i = 0;
                for(String stmtEntryId : acctEntLworkDayRec)
                {                                
                    bciAcctStmtEntryDaysRec.setStmtEntryId(stmtEntryId, i);     //Set STMT.ENTRY.ID in BCI.ACCT.ENT.STMT.DAYS table
                    i++;
                }

                    
            //Create a record in BCI.ACCT.ENT.STMT.DAYS table 
                try
                {
                    bciAcctStmtEntryDaysTable.write(bciAcctStmtEntryDaysId, bciAcctStmtEntryDaysRec);
                } 
                catch (T24IOException bciAcctEntStmtDaysErr)
                {
                    bciAcctEntStmtDaysErr.getMessage();
                }
            }
        }                                                       
    }        
}
