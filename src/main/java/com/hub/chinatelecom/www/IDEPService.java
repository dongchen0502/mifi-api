

/**
 * IDEPService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package com.hub.chinatelecom.www;

    /*
     *  IDEPService java interface
     */

    public interface IDEPService {
          

        /**
          * Auto generated method signature
          * 
                    * @param exchange0
                
         */

         
                     public ExchangeResponse exchange(

                             Exchange exchange0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param exchange0
            
          */
        public void startexchange(

                Exchange exchange0,

                final com.hub.chinatelecom.www.IDEPServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    