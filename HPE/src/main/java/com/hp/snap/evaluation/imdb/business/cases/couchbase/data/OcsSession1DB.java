/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.data;

/**
 * @author Yang, Lin
 */
public interface OcsSession1DB {

    public long getLASTUPDATETIME();

    public void setLASTUPDATETIME(long LASTUPDATETIME);

    public long getCREATEDTIME();

    public void setCREATEDTIME(long CREATEDTIME);

    public long getTIMEOUTPERIOD();

    public void setTIMEOUTPERIOD(long TIMEOUTPERIOD);

    public long getTIMEOUTLINE();

    public void setTIMEOUTLINE(long TIMEOUTLINE);

    public int getSTATE();

    public void setSTATE(int STATE);

    public short getPARTITIONID();

    public void setPARTITIONID(short PARTITIONID);

    public short getSESSIONSTATE();

    public void setSESSIONSTATE(short SESSIONSTATE);

    public String getSERVERIDENTIFIER();

    public void setSERVERIDENTIFIER(String SERVERIDENTIFIER);

    public String getNODEHOST();

    public void setNODEHOST(String NODEHOST);

    public String getNODEREALM();

    public void setNODEREALM(String NODEREALM);

    public String getORIGINALHOST();

    public void setORIGINALHOST(String ORIGINALHOST);

    public String getORIGINALREALM();

    public void setORIGINALREALM(String ORIGINALREALM);

    public short getLASTCCREQUESTNUMBER();

    public void setLASTCCREQUESTNUMBER(short LASTCCREQUESTNUMBER);

    public int getLASTRESULTCODE();

    public void setLASTRESULTCODE(int LASTRESULTCODE);

    public byte[] getSERVICEINFOS();

    public void setSERVICEINFOS(byte[] SERVICEINFOS);

    public long getMULTIPLESERVICESSUPPORTED();

    public void setMULTIPLESERVICESSUPPORTED(long MULTIPLESERVICESSUPPORTED);

    public long getSUBSCRIBERID();

    public void setSUBSCRIBERID(long SUBSCRIBERID);

    public long getDEVICEID();

    public void setDEVICEID(long DEVICEID);

    public int getIDENTIFIERTYPE();

    public void setIDENTIFIERTYPE(int IDENTIFIERTYPE);

    public String getIDENTIFIER();

    public void setIDENTIFIER(String IDENTIFIER);

    public byte getUSEREQUIPMENTINFOTYPE();

    public void setUSEREQUIPMENTINFOTYPE(byte USEREQUIPMENTINFOTYPE);

    public byte[] getUSEREQUIPMENTINFOVALUE();

    public void setUSEREQUIPMENTINFOVALUE(byte[] USEREQUIPMENTINFOVALUE);

    public byte[] getSERVICEPARAMETERS();

    public void setSERVICEPARAMETERS(byte[] SERVICEPARAMETERS);

    public byte[] getRATINGSESSIONS();

    public void setRATINGSESSIONS(byte[] RATINGSESSIONS);

    public byte[] getABMCLIENTSESSION();

    public void setABMCLIENTSESSION(byte[] ABMCLIENTSESSION);

    public byte[] getEXTENDED();

    public void setEXTENDED(byte[] EXTENDED);

}
