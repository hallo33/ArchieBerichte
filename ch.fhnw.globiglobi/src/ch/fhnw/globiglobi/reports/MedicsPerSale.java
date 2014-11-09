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

public class MedicsPerSale extends AbstractTimeSeries {
	
	public MedicsPerSale(String name){
		super(Messages.MEDICSPERSALE_TITLE);
	}
	
	@Override
	public String getDescription(){
		return Messages.MEDICSPERSALE_DESCRIPTION;
	}
	
	@Override
	protected List<String> createHeadings(){
		final ArrayList<String> headings = new ArrayList<String>(1);
		headings.add("TEST");
		return headings;
	}
	
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		
		// job finished successfully
		monitor.done();
		return Status.OK_STATUS;
	}
	
}