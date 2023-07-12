package com.bci;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.temenos.api.TField;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AprTypeClass;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.accountstatement.AccountStatementRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.deptacctofficer.DeptAcctOfficerRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.records.limit.LimitRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.records.transaction.TransactionRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.customer.Phone1Class;
import com.temenos.t24.api.records.customer.AddressClass;
import com.temenos.t24.api.records.customer.ContactTypeClass;
import com.temenos.t24.api.tables.ebbciengageoneintrepparam.EbBciEngageoneIntRepParamRecord;
import com.temenos.t24.api.tables.ebbciengageonegenfilepath.EbBciEngageoneGenFilePathRecord;
import com.temenos.t24.api.records.ebcontractbalances.FromDateClass;
import com.temenos.t24.api.tables.ebbciacctstmtentrydays.EbBciAcctStmtEntryDaysRecord;
import com.temenos.t24.api.records.aclockedevents.AcLockedEventsRecord;
import com.temenos.t24.api.records.aaproduct.AaProductRecord;

/**
*
*----------------------------------------------------------------------------------------------------------------
* Description           : Create a file with Account Statement frequency during a COB
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

public class BciCobCurrentAccountStmtFileGen extends ServiceLifecycle {

    @Override
    public void processSingleThreaded(ServiceData serviceData) {

        DataAccess da = new DataAccess(this);             
        Session session = new Session(this);
        Contract contract = new Contract(this);   
        
        String today = "";
        String currTime = "";
        String companyIdCob = session.getCompanyId()+"-COB";
        String localCcy = session.getLocalCurrency();

        List<String> aaArrangementRecList = new ArrayList<String>();      
        String selCmd = "WITH PRODUCT.LINE EQ 'ACCOUNTS'";        //Select AA.ARRANGEMENT record which are having ACCOUNTS product line
        
        try            
        {
            aaArrangementRecList = da.selectRecords("","AA.ARRANGEMENT","",selCmd); 
        } 
        catch (Exception aaArr) {   
            aaArrangementRecList.clear();        
        } 
       
        if(!aaArrangementRecList.isEmpty())
        {
          //Get TODAY value
            try               
            {      
                DatesRecord datesrec = new DatesRecord(da.getRecord("DATES",companyIdCob));
                today = datesrec.getToday().getValue();                 
            } 
            catch (Exception dateErr) {                               
                dateErr.getMessage();  
            } 
             
            for(String aaArrRecId : aaArrangementRecList)
            {
                String operationNo = "";
                String accountNo = "";
                String startDate = "";
                String endDate = "";
                String lastFquDate = "";
                int continueflag = 0;
                String hdrCode = "";
                String detCode = "";
                String eventCode = "";
                String currency = "";
                String aaProduct = "";
                String productDes = "";
                String customerNo1 = "";
                String customerNo2 = "";
                String customerNo3 = "";
                String customerNo4 = "";
                String customerNo5 = "";
                String lastBal = "";
                int cusFlag = 0;
                String legalID1 = "";
                String legalID2 = "";
                String legalID3 = "";
                String legalID4 = "";
                String legalID5 = "";
                String customerName1 = "";
                String customerName2 = "";
                String customerName3 = "";
                String customerName4 = "";
                String customerName5 = "";
                String address = "";
                String email = "";
                String phoneNo = "";
                String customerCommunication = "";
                String branchShortName = "";
                String branchCode = "";
                String codeExecutive = "";
                String executiveName = "";
                String executiveTelephone = "";
                String executiveEmail = "";
                String drTREA = "";
                String crTREA = "";
                String drTEA = "";
                String crTEA = "";
                int aaArrAcctBalFlg = 0;
                String stmtFqu = "";
                String periodMonth = "";
                String periodFromState = "";
                String periodUntilState = "";
                String LimitRef = "";
                String internalAmt = "";
                String countAvailBal = "";
                String previousBal = "";
                String amtUse = "";
                String amtAvailable = "";
                String totWithHoldAmt = "0";
                String dailyBal = "";
                String stmtTransAmt = "";
                String bookDate = "";
                String narrative = "";
                String transRef = "";
                int stmtRecCntInt = 0;
                String stmtRecCnt = "";
                String amtDepMovement = "";
                String amtChrgMovement = "";
                String totChrgAmt = "";
                String totDepAmt = "";
                double totDepAmtDbl = 0;
                double totChrgAmtDbl = 0;
                double dailyBalDbl = 0;
                String stmtEntDets = "";
                String stmtEntDetails = "";
                String hdrNoticeMsg = "";
                String hdrDetNoticeMsg = "";
                String filePath = "";
                double internalAmtDbl = 0;
                String workBalance = "0";
                double workBalanceDbl = 0;
                double amtAvailableDbl = 0;
                long diffDaysCnt = 0;
                String fileEventCode = "";
                String stmtFquDate = "";
                String lockedAmt = "";
                double lockedAmtDbl = 0;
                double totWithHoldAmtDbl = 0;
                String availableBal = "";
                double availableBalDbl = 0;
                
                try               
                {      
                    AaArrangementRecord aaArrRec = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT",aaArrRecId));
                    accountNo = aaArrRec.getLinkedAppl(0).getLinkedApplId().getValue();
                    currency = aaArrRec.getCurrency().getValue();
                    customerNo1 = aaArrRec.getCustomer(0).getCustomer().getValue();
                    
                  //Get PRODUCT description (Tipo de cuenta) 
                    try
                    {
                        aaProduct = aaArrRec.getProduct(0).getProduct().getValue();
                        AaProductRecord aaProductRec = new AaProductRecord(da.getRecord("AA.PRODUCT", aaProduct));
                        productDes = aaProductRec.getDescription().get(1).getValue();
                    }
                    catch(Exception productDesErr)
                    {
                        productDes = "";
                    }
                    
                    if(productDes.length() >= 5)
                    {
                        productDes = productDes.substring(0, 5);
                    }

                    try
                    {
                        AccountStatementRecord acctStmtRec = new AccountStatementRecord(da.getRecord("ACCOUNT.STATEMENT", accountNo));
                        lastBal = acctStmtRec.getFqu1LastBalance().getValue();

                        try
                        {
                            stmtFquDate = acctStmtRec.getStmtFqu1(1).getValue().substring(0, 8); 
                            periodMonth = stmtFquDate.substring(0,6)+"00";     //period Month
                        }
                        catch(Exception stmtFquDateErr)
                        {
                            periodMonth = "";
                        }
                                
                        if(stmtFquDate.equals(today))
                        {                                 
                            stmtFqu = acctStmtRec.getLastStatementNo().getValue();
                            String lastFqu = acctStmtRec.getFqu1LastDate().getValue();                          
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            Calendar calendar = Calendar.getInstance();
                            
                            if(!lastFqu.equals(""))
                            {
                                try
                                {
                                    calendar.setTime(sdf.parse(lastFqu));
                                    
                                  //Incrementing the date by 1 day
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);  
                                    lastFquDate = sdf.format(calendar.getTime());
                                  
                                }
                                catch(ParseException calendarErr)
                                {
                                    calendarErr.printStackTrace();
                                }
                            }  
                            else
                            {
                                try
                                {
                                    AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", accountNo));   //Read ACCOUNT record for getting OPENING.DATE field value
                                    lastFquDate = accRec.getOpeningDate().getValue();
                                    lastBal = "0";
                                }
                                catch(Exception accErr)
                                {
                                    accErr.getMessage(); 
                                }
                            }
                            
                            startDate = lastFquDate;
                            endDate = stmtFquDate;
                            operationNo = aaArrRecId;
                         
                            if(!startDate.equals("") && !endDate.equals(""))
                            {
                                continueflag = 1;
                            }
                        }
                    }
                    catch(Exception acctStmtErr)
                    {
                        continueflag = 0;      
                    }
                    
                    if(continueflag == 1)    
                    {
                        
                    //Get the Header Code and Detail Code from EB.BCI.ENGAGEONE.INT.REP.PARAM table 
                        EbBciEngageoneIntRepParamRecord bciEngageoneIntRepParamRec = new EbBciEngageoneIntRepParamRecord();  
                        try
                        {        
                            bciEngageoneIntRepParamRec = new EbBciEngageoneIntRepParamRecord(da.getRecord("EB.BCI.ENGAGEONE.INT.REP.PARAM", "CURRENT.ACCT.STMT"));
                           
                            hdrCode = bciEngageoneIntRepParamRec.getHeaderCode(0).getValue();
                            detCode = bciEngageoneIntRepParamRec.getDetailCode(0).getValue();         
                            eventCode = bciEngageoneIntRepParamRec.getEventCode().get(0).getEventCode().getValue();
                            fileEventCode = eventCode;
                        }
                        catch(Exception engageRecErr)
                        {
                            engageRecErr.getMessage();
                        }
                       
                        
                     //Read CUSTOMER table
                        CustomerRecord cusRec = new CustomerRecord();
                        try
                        {  
                            cusRec = new CustomerRecord(da.getRecord("CUSTOMER", customerNo1));
                        }
                        catch(Exception e2)
                        {
                            cusFlag = 1;
                        }
                                                
                        if(cusFlag == 0)
                        {
                            customerName1 = cusRec.getShortName(0).getValue();
                            
                     //Get the field LEGAL.ID from customer table       
                            List<LegalIdClass> legalIdlist = cusRec.getLegalId();            
                            if(!legalIdlist.isEmpty()){
                                legalID1 = legalIdlist.get(0).getLegalId().getValue();      
                            }                
                                                        
                      //Get the fields PHONE.1 and EMAIL.1 from customer table 
                            List<Phone1Class> phoneList = cusRec.getPhone1();             
                            if(!phoneList.isEmpty())
                            {
                            phoneNo = phoneList.get(0).getPhone1().getValue();
                            email = phoneList.get(0).getEmail1().getValue();
                            }
                           
                      //Get the field ADDRESS from customer table 
                            List<AddressClass> addressList = cusRec.getAddress();
                            if(!addressList.isEmpty())
                            {
                                address = cusRec.getAddress(0).get(0).getValue();
                            }                 
                            
                      //Get the field CONTACT.DATA from customer table 
                            List<ContactTypeClass> contactTypecList = cusRec.getContactType();            
                            if(!contactTypecList.isEmpty()){
                               customerCommunication =  contactTypecList.get(0).getContactData().getValue();
                            }
                        }         
                     
                    //Get Company ID and Company Name
                        branchCode = "BCI";
                        try
                        {
                            CompanyRecord companyRec = new CompanyRecord(da.getRecord("COMPANY", session.getCompanyId()));   //Read COMPANY table and Get the value of NAME field 
                            branchShortName = companyRec.getCompanyName(0).getValue();
                                
                            int branchShortNameLen = branchShortName.length();
                            if(branchShortNameLen >= 30)
                            {
                                branchShortName = branchShortName.substring(0, 30);
                            }                     
                        }
                        catch(Exception companyErr)
                        {
                            companyErr.getMessage();
                        }     
                                                 
                    //Get Code Executive           
                        contract.setContractId(operationNo);
                        AaPrdDesOfficersRecord aaPrdDesOfficerRec = new AaPrdDesOfficersRecord();
                        try
                        {
                            aaPrdDesOfficerRec = new AaPrdDesOfficersRecord(contract.getConditionForProperty("OFFICERS"));
                            codeExecutive = aaPrdDesOfficerRec.getPrimaryOfficer().getValue(); 
                        }
                        catch(Exception offErr)
                        {
                            offErr.getMessage();
                        }
                             
                    //Get Executive Name and Executive Telephone
                          if(!codeExecutive.equals(""))
                          {
                              try
                              {
                                  DeptAcctOfficerRecord deptAcctOffRec = new DeptAcctOfficerRecord(da.getRecord("DEPT.ACCT.OFFICER", codeExecutive));   //Read DEPT.ACCT.OFFICER table and Get the fields NAME & TELERPHONE.NO 
                                  executiveName = deptAcctOffRec.getName().getValue();
                                  executiveTelephone = deptAcctOffRec.getTelephoneNo().getValue();
                              }
                              catch(Exception deptOffErr)
                              {
                                  deptOffErr.getMessage();
                              }                  
                          } 
                          
                          
                     //Get TEA (Debtor) and TREA (Debtor) from AA.ARR.INTEREST table
                          AaPrdDesInterestRecord aaPrdDesDRInterestRec = new AaPrdDesInterestRecord();
                          try
                          {
                              aaPrdDesDRInterestRec = new AaPrdDesInterestRecord(contract.getConditionForProperty("DRINTEREST"));
                              drTEA = aaPrdDesDRInterestRec.getFixedRate(0).getEffectiveRate().getValue();       //Get EFFECTIVE.RATE field Value         
                              drTREA = "";
                          }
                          catch(Exception drIntErr)
                          {
                              drIntErr.getMessage();
                          }
                             
                          
                     //Get TEA (Creditor) from AA.ARR.INTEREST table
                          AaPrdDesInterestRecord aaPrdDesCRInterestRec = new AaPrdDesInterestRecord();
                          try
                          {
                              aaPrdDesCRInterestRec = new AaPrdDesInterestRecord(contract.getConditionForProperty("CRINTEREST"));
                              crTEA = aaPrdDesCRInterestRec.getFixedRate(0).getEffectiveRate().getValue();       //Get EFFECTIVE.RATE field Value         
                          }
                          catch(Exception crIntErr)
                          {
                              crIntErr.getMessage();
                          }
                        
                          
                     //Get TREA (Creditor) from AA.ARR.ACCOUNT table
                          AaPrdDesAccountRecord aaArrAccRec = new AaPrdDesAccountRecord();
                          try
                          {
                              aaArrAccRec = new AaPrdDesAccountRecord(contract.getConditionForProperty("BALANCE"));
                              
                              List<AprTypeClass> aprTypeClsList = aaArrAccRec.getAprType();
                              for(AprTypeClass aprTypeCls : aprTypeClsList)
                              {
                                  String aprType = "";
                                  aprType = aprTypeCls.getAprType().getValue();
                                  if(aprType.equals("PEACCT.TREA.ACCOUNTS"))
                                  {
                                      crTREA = aprTypeCls.getAprRate().getValue();
                                      break;
                                  }
                              }
                          }
                          catch(Exception aaArrAccBalRecErr)
                          {
                              aaArrAcctBalFlg = 1;
                          }
                          
                          if(aaArrAcctBalFlg == 1)
                          {
                              try
                              {  
                                  aaArrAccRec = new AaPrdDesAccountRecord(contract.getConditionForProperty("ACCOUNT"));
                                  
                                  List<AprTypeClass> aprTypeClsList = aaArrAccRec.getAprType();
                                  for(AprTypeClass aprTypeCls : aprTypeClsList)
                                  {
                                      String aprType = "";
                                      aprType = aprTypeCls.getAprType().getValue();
                                      if(aprType.equals("PEACCT.TREA.ACCOUNTS"))
                                      {
                                          crTREA = aprTypeCls.getAprRate().getValue();
                                          break;
                                      }
                                  }
                              }
                              catch(Exception aaArrAccRecErr)
                              {
                                  aaArrAccRecErr.getMessage();
                              }
                          }
                  
                      //Get Period from the state of acta, Period until the state of acta                            
                          periodFromState = startDate;
                          periodUntilState = endDate;
                          
                          
                      //Get WORKING.BALANCE and LIMIT.KEY from ACCOUNT table  
                          try
                          {
                              AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", accountNo));   
                              LimitRef = accRec.getLimitKey().getValue();      //Get Limit ID
                              
                              workBalance = accRec.getWorkingBalance().getValue();
                              try
                              {
                                  workBalanceDbl = Double.parseDouble(workBalance);
                              }
                              catch(Exception workBalErr)
                              {
                                  workBalanceDbl = 0; 
                              }
                          }
                          catch(Exception accErr)
                          {
                              accErr.getMessage();
                          }
                          
                          
                      //Get Monto Utilizado (Amount used)                             
                          if(workBalance.substring(0, 1).equals("-"))   //If Working Balance is negative, then only take ACCOUNT > WORKING.BALANCE
                          {
                              amtUse = workBalance;
                          }
                          
                          
                      //Get INTERNAL.AMOUNT (Monto linea de sobregiro - Overdraft Line amount) from LIMIT details                      
                          try
                          {
                              LimitRecord limitRec = new LimitRecord(da.getRecord("LIMIT", LimitRef));                     
                              internalAmt = limitRec.getInternalAmount().getValue();
                              internalAmtDbl = Double.parseDouble(internalAmt);
                          }
                          catch(Exception limitErr)
                          {
                              internalAmtDbl = 0;
                          }      
                         
                          
                      //Get Countable Balance and LOCKED.AMT from ECB table
                          try
                          {
                              EbContractBalancesRecord ecbRec = new EbContractBalancesRecord(da.getRecord("EB.CONTRACT.BALANCES", accountNo));
                              countAvailBal = ecbRec.getTradeDatedGlBal().getValue();
                              try
                              {
                                  List<FromDateClass> fromDatecls = ecbRec.getFromDate();
                                  lockedAmt = fromDatecls.get(fromDatecls.size()-1).getLockedAmt().getValue();
                                  lockedAmtDbl = Double.parseDouble(lockedAmt);
                              }
                              catch(Exception lockAmtErr)
                              {
                                  lockAmtErr.getMessage();
                              }
                          }
                          catch(Exception ecbErr)
                          {
                              ecbErr.getMessage();
                          }
                          
                       
                     //Get Monto disponiple (Amount available) 
                          try
                          {
                              amtAvailableDbl = workBalanceDbl + internalAmtDbl;
                              amtAvailable = String.format("%.2f", amtAvailableDbl);
                          }
                          catch(Exception amtAvailBalErr)
                          {
                              amtAvailBalErr.getMessage();
                          }
                          
                          
                     //Get Saldo disponiple (Available Balance)   
                          try
                          {
                              availableBalDbl = workBalanceDbl + internalAmtDbl - lockedAmtDbl;
                              availableBal = String.format("%.2f", availableBalDbl);
                          }
                          catch(Exception availBalErr)
                          {
                              availBalErr.getMessage();
                          }
                          
                          
                     //Get Total Withhold Amount (Total Retenciones)
                          
                          String selAcLockEventCmd = "WITH (ACCOUNT.NUMBER EQ "+accountNo+") AND ((TO.DATE GT "+today+") OR (TO.DATE EQ ''))";                        
                          List<String> acLockEventIdList = new ArrayList<String>(); 
                          try            
                          {
                              acLockEventIdList = da.selectRecords("","AC.LOCKED.EVENTS","",selAcLockEventCmd); 
                              for(String acLockEventId : acLockEventIdList)
                              {
                                  AcLockedEventsRecord acLockEventRec = new AcLockedEventsRecord(da.getRecord("AC.LOCKED.EVENTS", acLockEventId));                                 
                                  lockedAmt = ""; 
                                  lockedAmtDbl = 0;
                                  
                                  lockedAmt = acLockEventRec.getLockedAmount().getValue();
                                  try
                                  {
                                      lockedAmtDbl = Double.parseDouble(lockedAmt);
                                  }
                                  catch(Exception lockedAmtErr)
                                  {
                                      lockedAmtDbl = 0;
                                  }
                                  totWithHoldAmtDbl = totWithHoldAmtDbl + lockedAmtDbl;
                              }
                              totWithHoldAmt = String.format("%.2f", totWithHoldAmtDbl);     //Convert into 2 decimal value
                          }                          
                          catch(Exception acLockEventErr)
                          {
                              acLockEventErr.getMessage();
                          }
                          
                             
                     //Get STMT.ENTRY details of Account        
                         
                          int startDateInt = Integer.parseInt(startDate);
                          int endDateInt = Integer.parseInt(endDate);
                          long diff = 0;
                          SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
                          try 
                          {
                              Date date1 = myFormat.parse(startDate);
                              Date date2 = myFormat.parse(endDate);
                              diff = date2.getTime() - date1.getTime();
                              diffDaysCnt = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
                          }
                          catch (ParseException daysCntErr)
                          {
                              daysCntErr.getMessage();
                          }
                          
                          for(int i=1; i<=diffDaysCnt; i++)
                          {
                              if(startDateInt <= endDateInt)
                              {
                                  String bciAcctStmtEntryDaysId = "";
                                  bciAcctStmtEntryDaysId = accountNo+"-"+startDate;
                                  List<TField> stmtEntryIdList = new ArrayList<TField>();
                                  int bciAcctStmtEntryFlg = 0;
                                  
                                  try
                                  {
                                      EbBciAcctStmtEntryDaysRecord bciAcctStmtEntryDaysRec = new EbBciAcctStmtEntryDaysRecord(da.getRecord("EB.BCI.ACCT.STMT.ENTRY.DAYS", bciAcctStmtEntryDaysId));
                                      stmtEntryIdList = bciAcctStmtEntryDaysRec.getStmtEntryId();
                                  }
                                  catch(Exception bciAcctStmtEntryDaysErr)
                                  {
                                      bciAcctStmtEntryFlg = 1;
                                  }
                               
                                  
        //***----------------Header Notice - Get Amount of Detail records, Total Charges and Checks and Total Deposits and Payments---------------------***
        //***-----------Detail Notice - Get Movement Date, Movement Description, Document number corresponding to the movement, Amount in Checks and other Movement Charges, Amount in Deposits and Subscriptions of the Movement and Daily Balance corresponding to the movement----------------***
                        
                           
                               //If EB.BCI.ACCT.STMT.ENTRY.DAYS record is exist
                                  if(bciAcctStmtEntryFlg == 0)
                                  {
                                      previousBal = lastBal;
                                      dailyBalDbl = Double.parseDouble(lastBal);
                                      
                                      for(TField stmtEntryIdFld : stmtEntryIdList)
                                      {    
                                          String stmtEntryId = stmtEntryIdFld.toString();
                                          int smtEntryFlag = 0;                                          
                                          String stmtEntryRecStatus = "";
                                          String maskPrint = "";
                                          String transactionCode = "";
                                          
                                          StmtEntryRecord stmtEntryRec = new StmtEntryRecord();
                                          try
                                          {
                                              stmtEntryRec = new StmtEntryRecord(da.getRecord("STMT.ENTRY", stmtEntryId));
                                          }
                                          catch(Exception stmtEntryRecErr)
                                          {
                                              smtEntryFlag = 1;
                                          }
                                          
                                          if(smtEntryFlag == 0)
                                          {
                                              stmtEntryRecStatus = stmtEntryRec.getRecordStatusRec().getValue();
                                              maskPrint = stmtEntryRec.getMaskPrint().getValue();
                                              transactionCode = stmtEntryRec.getTransactionCode().getValue();
                                              
                                              if((!stmtEntryRecStatus.equals("REVE")) && (maskPrint.equals("")))
                                              {
                                                  stmtRecCntInt = stmtRecCntInt + 1;
                                                  stmtTransAmt = "";
                                                  double stmtTransAmtDbl = 0;
                                                  bookDate = "";
                                                  narrative = "";
                                                  transRef = "";
                                                  amtChrgMovement = "";
                                                  amtDepMovement = "";
                                                  String stmtCcy = "";
                                                  stmtEntDets = "";
                                                  
                                              //Get NARRATIVE value
                                                  try
                                                  {
                                                      narrative = stmtEntryRec.getNarrative(0).getValue();
                                                  }
                                                  catch(Exception narrtiveErr)
                                                  {
                                                      narrative = "";
                                                  }
                                                  
                                                  if(narrative.equals(""))
                                                  {
                                                      try
                                                      {
                                                          TransactionRecord transRec = new TransactionRecord(da.getRecord("TRANSACTION", transactionCode));
                                                          narrative = transRec.getNarrative(0).getValue();
                                                      }
                                                      catch(Exception transRecErr)
                                                      {
                                                          narrative = "";
                                                      } 
                                                  }
                                                  
                                                  if(narrative.length() >= 30)
                                                  {
                                                      narrative = narrative.substring(0, 30);
                                                  }  
                                                  
                                              //Get TRANS.REFERENCE value
                                                  transRef = stmtEntryRec.getTransReference().getValue();
                                                  if(transRef.length() >= 30)
                                                  {
                                                      transRef = transRef.substring(0, 30);
                                                  } 
                                                  
                                                  bookDate = stmtEntryRec.getBookingDate().getValue();
                                                  stmtCcy = stmtEntryRec.getCurrency().getValue();
                                                  
                                                  if(stmtCcy.equals(localCcy))
                                                  {
                                                      stmtTransAmt = stmtEntryRec.getAmountLcy().getValue();
                                                  }
                                                  else
                                                  {
                                                      stmtTransAmt = stmtEntryRec.getAmountFcy().getValue();
                                                  }
                                                  
                                                  try
                                                  {
                                                      stmtTransAmtDbl = Double.parseDouble(stmtTransAmt);   //Convert into double value
                                                  }
                                                  catch(Exception stmtTransAmtErr)
                                                  {
                                                      stmtTransAmtErr.getMessage();
                                                  }
                                                  
                                                  dailyBalDbl = dailyBalDbl + stmtTransAmtDbl;
                                                  try
                                                  {
                                                      dailyBal = String.format("%.2f", dailyBalDbl);   //Convert into 2 decimal value
                                                  }
                                                  catch(Exception dailyBalErr)
                                                  {
                                                      dailyBalErr.getMessage();
                                                  }

                                                  if(stmtTransAmt.contains("-")) 
                                                  {
                                                      amtChrgMovement = stmtTransAmt;
                                                      totChrgAmtDbl = totChrgAmtDbl + stmtTransAmtDbl;
                                                  }
                                                  else
                                                  {
                                                      amtDepMovement = stmtTransAmt;
                                                      totDepAmtDbl = totDepAmtDbl + stmtTransAmtDbl; 
                                                  }
                                                  
                                                //Format the Stmt Entry string values based on the length
                                                  detCode = StringUtils.rightPad(detCode, 20, " ");
                                                  eventCode = StringUtils.rightPad(eventCode, 5, " ");
                                                  bookDate = StringUtils.rightPad(bookDate, 8, "0");
                                                  branchCode = StringUtils.rightPad(branchCode, 5, " ");
                                                  branchShortName = StringUtils.rightPad(branchShortName, 30, " ");
                                                  narrative = StringUtils.rightPad(narrative, 30, " ");
                                                  transRef = StringUtils.rightPad(transRef, 30, " ");

                                                  amtChrgMovement = formatAmt(amtChrgMovement);     //Calling formatAmt Class for getting Amount Format                                 
                                                  amtDepMovement = formatAmt(amtDepMovement);                                  
                                                  dailyBal = formatMinusAmt(dailyBal);      //Mantis-312

                                               //Form Stmt Entry Message
                                                  stmtEntDets = detCode+eventCode+bookDate+branchCode+branchShortName+narrative+transRef+amtChrgMovement+amtDepMovement+dailyBal;

                                                  if(stmtRecCntInt == 1)
                                                  {
                                                      stmtEntDetails = stmtEntDets;
                                                  }else
                                                  {
                                                      stmtEntDetails = stmtEntDetails+'\n'+stmtEntDets;
                                                  }   
                                              }
                                          }
                                      }
                                  }
                                  
                              //Incrementing the Start Date
                                  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                  Calendar calendar = Calendar.getInstance();
                                  try
                                  {
                                      calendar.setTime(sdf.parse(startDate));
                                    
                                      calendar.add(Calendar.DAY_OF_MONTH, 1);    //Incrementing the date by 1 day
                                      startDate = sdf.format(calendar.getTime());
                                  }
                                  catch(ParseException calendarErr)
                                  {
                                      calendarErr.getMessage();
                                  }
                                  startDateInt = Integer.parseInt(startDate);
                              }
                          }
                                                    
                          stmtRecCnt = Integer.toString(stmtRecCntInt);  
                          try
                          {
                              totChrgAmt = String.format("%.2f", totChrgAmtDbl);    //Convert into 2 decimal value                      
                          }
                          catch(Exception totChrgAmtErr)
                          {
                              totChrgAmt = "0";
                          }
               
                          try
                          {
                              totDepAmt = String.format("%.2f", totDepAmtDbl);    //Convert into 2 decimal value
                          }
                          catch(Exception totDepAmtErr)
                          {
                              totDepAmt = "0";
                          }
                          
                          
                      //Format the Header notice string values based on the length    
                          hdrCode = StringUtils.rightPad(hdrCode, 20, " ");
                          eventCode = StringUtils.rightPad(eventCode, 5, " ");
                          stmtRecCnt = StringUtils.leftPad(stmtRecCnt, 5, "0");
                          stmtFqu = StringUtils.leftPad(stmtFqu, 30, "0");                        
                          customerNo1 = StringUtils.rightPad(customerNo1, 20, " ");
                          customerName1 = StringUtils.rightPad(customerName1, 120, " ");
                          legalID1 = StringUtils.rightPad(legalID1, 20, " ");
                          customerNo2 = StringUtils.rightPad(customerNo2, 20, " ");
                          customerName2 = StringUtils.rightPad(customerName2, 120, " ");
                          legalID2 = StringUtils.rightPad(legalID2, 20, " ");
                          customerNo3 = StringUtils.rightPad(customerNo3, 20, " "); 
                          customerName3 = StringUtils.rightPad(customerName3, 120, " ");
                          legalID3 = StringUtils.rightPad(legalID3, 20, " ");
                          customerNo4 = StringUtils.rightPad(customerNo4, 20, " ");
                          customerName4 = StringUtils.rightPad(customerName4, 120, " ");
                          legalID4 = StringUtils.rightPad(legalID4, 20, " ");
                          customerNo5 = StringUtils.rightPad(customerNo5, 20, " ");
                          customerName5 = StringUtils.rightPad(customerName5, 120, " ");
                          legalID5 = StringUtils.rightPad(legalID5, 20, " ");
                          accountNo = StringUtils.rightPad(accountNo, 30, " ");
                          currency = StringUtils.rightPad(currency, 5, " ");
                          address = StringUtils.rightPad(address, 120, " ");
                          email = StringUtils.rightPad(email, 50, " ");
                          phoneNo = StringUtils.rightPad(phoneNo, 20, " ");   
                          branchCode = StringUtils.rightPad(branchCode, 5, " "); 
                          branchShortName = StringUtils.rightPad(branchShortName, 30, " ");        
                          codeExecutive = StringUtils.rightPad(codeExecutive, 5, " ");        
                          executiveName = StringUtils.rightPad(executiveName, 100, " ");        
                          executiveTelephone = StringUtils.rightPad(executiveTelephone, 20, " ");        
                          executiveEmail = StringUtils.rightPad(executiveEmail, 50, " "); 
                          productDes = StringUtils.rightPad(productDes, 5, " ");
                          periodFromState = StringUtils.rightPad(periodFromState, 8, "0");
                          periodUntilState = StringUtils.rightPad(periodUntilState, 8, "0");
                          periodMonth = StringUtils.rightPad(periodMonth, 8, "0");
                          customerCommunication = StringUtils.rightPad(customerCommunication, 150, " "); 
                         
                          crTEA = formatRate(crTEA);       //Calling formatRate Class for getting Rate Format                         
                          drTEA = formatRate(drTEA);
                          crTREA = formatRate(crTREA);
                          drTREA = formatRate(drTREA);
                          
                          internalAmt = formatAmt(internalAmt);       //Calling formatAmt Class for getting Amount Format
                          totChrgAmt = formatAmt(totChrgAmt);
                          totDepAmt = formatAmt(totDepAmt);                         
                          totWithHoldAmt = formatAmt(totWithHoldAmt);                       
                          
						  amtUse = formatMinusAmt(amtUse);            //Calling formatMinusAmt Class for getting Amount Format
                          amtAvailable = formatMinusAmt(amtAvailable);
                          previousBal = formatMinusAmt(previousBal);
                          availableBal = formatMinusAmt(availableBal);
                          countAvailBal = formatMinusAmt(countAvailBal);
                            
                      //Form Header Notice Message
                          hdrNoticeMsg = hdrCode+eventCode+stmtRecCnt+stmtFqu+customerNo1+customerName1+legalID1+customerNo2+customerName2+legalID2+customerNo3+customerName3+legalID3+customerNo4+customerName4+legalID4+customerNo5+customerName5+legalID5+accountNo+currency+address+email+phoneNo+branchCode+branchShortName+codeExecutive+executiveName+executiveTelephone+executiveEmail+productDes+periodFromState+periodUntilState+internalAmt+amtUse+amtAvailable+periodMonth+previousBal+totChrgAmt+totDepAmt+countAvailBal+totWithHoldAmt+availableBal+drTEA+crTEA+drTREA+crTREA+customerCommunication;
                          
                      //Form Header Notice and Detail Notice Message
                          if(stmtEntDetails.equals(""))
                          {
                              hdrDetNoticeMsg = hdrNoticeMsg;
                          }
                          else
                          {
                              hdrDetNoticeMsg = hdrNoticeMsg+'\n'+stmtEntDetails;
                              hdrDetNoticeMsg = hdrDetNoticeMsg.replaceAll("\n", "\r\n");
                          }
                          
                      //Get file Generated path from local table 
                          try
                          {
                              EbBciEngageoneGenFilePathRecord engageGenFilePathrec = new EbBciEngageoneGenFilePathRecord(da.getRecord("EB.BCI.ENGAGEONE.GEN.FILE.PATH","CURRENT.ACCT.STMT"));
                              filePath = engageGenFilePathrec.getPath().getValue();                               
                          }
                          catch(Exception pathErr)
                          {
                              pathErr.getMessage(); 
                          }
                       
                      //Get Current time
                          if(currTime.equals(""))
                          {
                              try
                              {
                                  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss");  
                                  LocalDateTime now = LocalDateTime.now();
                                  currTime = dtf.format(now);
                              }
                              catch(Exception currTimeErr)
                              {
                                  currTimeErr.getMessage();
                              }
                          }
                        
                          
                     //Create a text file under the given path with update the Account statement details based on frequency                          
                          String fileName = fileEventCode+"-"+today+"-"+currTime;
                          String filePathName = filePath+"/"+fileName+".txt";
                          try 
                          {
                                
                             File OutputFile = new File(filePathName);
                             FileWriter writer;
                             if(OutputFile.exists())
                             {
                                 writer = new FileWriter(OutputFile, true);
                             } 
                             else
                             {
                                 OutputFile.createNewFile();
                                 writer = new FileWriter(OutputFile);
                             }
                             
                            BufferedWriter bufferedWriter = new BufferedWriter(writer);
                            BufferedReader br = new BufferedReader(new FileReader(filePathName));
                            try 
                            {
                                if (br.readLine()!= null) 
                                {
                                    hdrDetNoticeMsg = "\r\n" + hdrDetNoticeMsg;   
                                }
                             }
                             catch(Exception buffReadErr)
                             {
                                 buffReadErr.getMessage();
                             }
                               
                             bufferedWriter.write((new StringBuilder(hdrDetNoticeMsg).toString()));                                   
                             bufferedWriter.close();
                          } 
                          catch(IOException ioe)
                          {
                              ioe.getMessage();
                          }        
                     } 
                } 
                catch (Exception aaArrErr)
                {                               
                    aaArrErr.getMessage();  
                }                               
            }
        }      
    }

//*----------------------Format the Rate field values (15 fixed Length)-----------------------------*//
    
    public String formatRate(String rateVal){
        
        String c1 = "";
        if(rateVal.equals(""))
        {
            c1 = StringUtils.leftPad(rateVal, 15, "0");
        }else
        {        
            if((rateVal.indexOf("-") != -1))
            {
                rateVal = rateVal.substring(1);
            }
            
            if((rateVal.indexOf(".") == -1))
            {
                rateVal = rateVal+".0";
            }
            
            String a1 = rateVal.split("\\.")[0];
            a1 = StringUtils.leftPad(a1, 9, "0");
    
            String b1 = rateVal.split("\\.")[1];
            b1 = StringUtils.rightPad(b1, 6, "0");
    
            c1 = a1+b1;
            if(c1.length() > 15)
            {
                c1 = c1.substring(0, 15);
            }
        } 
        return c1;
    }
    
 //*-----------------------Format the Amount field values (22 fixed Length)-----------------------------*//
    
    public String formatAmt(String amtVal){
        
        String c2 = "";

        if(amtVal.equals("") || amtVal.equals("0000000000000000000000"))
        {
            c2 = StringUtils.leftPad(amtVal, 22, "0");
        }
        else
        {         
            if((amtVal.indexOf("-") != -1))
            {
                amtVal = amtVal.substring(1);
            }
            
            if((amtVal.indexOf(".") == -1))
            {
                amtVal = amtVal+".0";
            }
            
            String a2 = amtVal.split("\\.")[0]; 
            a2 = StringUtils.leftPad(a2, 20, "0");
    
            String b2 = amtVal.split("\\.")[1];
            b2 = StringUtils.rightPad(b2, 2, "0");

            c2 = a2+b2;   
            if(c2.length() > 22)
            {
                c2 = c2.substring(0, 22);
            }
        }
        return c2;
     } 
    
//*----------Format the minus Amount for the following fields Previous Balance, Account Balance, Available Balance, Available Amount, Daily Balance and Count Available Balance-----------*//
    
    public String formatMinusAmt(String amtVal){
        
        String a2 = "";
        String b2 = "";
        String c2 = "";
        
        if(amtVal.equals("") || amtVal.equals("0000000000000000000000"))
        {
            c2 = StringUtils.leftPad(amtVal, 22, "0");
        }
        else
        {     
            if((amtVal.indexOf(".") == -1))
            {
                amtVal = amtVal+".0";
            }
            
            if((amtVal.indexOf("-") != -1))
            {
                amtVal = amtVal.substring(1);
                a2 = amtVal.split("\\.")[0]; 
                a2 = StringUtils.leftPad(a2, 19, "0");
                a2 = "-"+a2;
            }
            else
            {
                a2 = amtVal.split("\\.")[0]; 
                a2 = StringUtils.leftPad(a2, 20, "0");
            }
          
            b2 = amtVal.split("\\.")[1];
            b2 = StringUtils.rightPad(b2, 2, "0");
            
            c2 = a2+b2;  
            if(c2.length() > 22)
            {
                c2 = c2.substring(0, 22);
            }
        }
        return c2;
     } 
}
