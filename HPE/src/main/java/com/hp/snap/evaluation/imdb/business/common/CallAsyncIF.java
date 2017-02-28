/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common
** Date: 5/22/12				Time: 8:36 AM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

/**
 * Call Interface.
 * IMDB venders must implement this interface.
 * The implementation class name is specified in configuration file with key: CallImplClass
 */
public interface CallAsyncIF extends CallIF
{
	/**
	 * Prepare call data before begining.
	 * E.g. generating call data in memory.
	 * @param resultData		Result Data
	 * @param resultCode 0-success, otherwise fail
	 * @return
	 */
	public <T>CallAsyncIF callback(int resultCode, T resultData );

}
