/*******************************************************************************
 * Copyright (c) 2014 Lukas Eisenhut, Stefan Waldenmaier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Lukas Eisenhut
 *     Stefan Waldenmaier
 *******************************************************************************/
package ch.fhnw.globiglobi.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;

/**
 * <p>
 * Provides statistics about the sold medics ordered by the sales. Resulting dataset contains
 * information about the medics and the solds.
 * </p>
 * 
 * 
 * @author Lukas Eisenhut
 * @author Stefan Waldenmaier
 */
public class MedicsPerSale extends AbstractTimeSeries {
	
	public MedicsPerSale(){
		super(Messages.MEDICSPERSALE_TITLE);
	}
	
	/**
	 * Return an appropriate description
	 */
	public String getDescription(){
		return Messages.MEDICSPERSALE_DESCRIPTION;
	}
	
	/**
	 * Create dataset headings in this method
	 */
	@Override
	protected List<String> createHeadings(){
		final ArrayList<String> headings = new ArrayList<String>(3);
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		return Status.OK_STATUS;
	}
}
