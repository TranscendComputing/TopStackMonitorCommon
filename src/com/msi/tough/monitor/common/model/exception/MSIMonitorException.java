package com.msi.tough.monitor.common.model.exception;

/**
 * Generic Monitor Exception
 * 
 * @author heathm
 */
public class MSIMonitorException extends Exception
{
    private static final long serialVersionUID = 1164697264717218782L;

    public MSIMonitorException(Exception e)
    {
        super(e);
    }

    public MSIMonitorException(String err)
    {
        super(err);
    }

    public MSIMonitorException(String err, Exception e)
    {
        super(err, e);
    }
}
