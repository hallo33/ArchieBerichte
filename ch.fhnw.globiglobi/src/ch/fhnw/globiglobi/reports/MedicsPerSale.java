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

import ch.elexis.data.Artikel;
import ch.elexis.data.Query;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

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
	
	/**
	 * Shows only patients for active mandator if true, all patients in the system else.
	 */
	private boolean currentMandatorOnly;

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
		final ArrayList<String> headings = new ArrayList<String>(6);
		headings.add(Messages.MEDICSPERSALE_HEADING_PRODUCER);
		headings.add(Messages.MEDICSPERSALE_HEADING_MEDINAME);
		headings.add(Messages.MEDICSPERSALE_HEADING_EAN);
		headings.add(Messages.MEDICSPERSALE_HEADING_PHARMACODE);
		headings.add(Messages.MEDICSPERSALE_HEADING_QUANTITY);
		headings.add(Messages.MEDICSPERSALE_HEADING_SALE);
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		// initialize list
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(6);
		
		// Create Queries
		final Query<Artikel> articleQuery = new Query<Artikel>(Artikel.class);
		
		// Execute Queries
		final List<Artikel> art = articleQuery.execute();
		
		for (final Artikel article : art) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			// definition of the variables for the result
			String producer = "";
			String mediname = article.getName();
			String ean = article.getEAN();
			String pharmacode = article.getPharmaCode();
			String quantity = "";
			String sale = "";
			
			// fill the rows with content
			final Comparable<?>[] row = {
				producer, mediname, ean, pharmacode, quantity, sale
			};
			
			// add the row to the list
			content.add(row);
		}
		
		// set content in the dataSet
		this.dataSet.setContent(content);
		
		// job finished successfully
		monitor.done();
		return Status.OK_STATUS;
	}
	
	// test
	
	/**
	 * @return True if statistic should be created for current mandator only, false else.
	 */
	@GetProperty(name = "Active Mandator Only", index = 1, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Compute statistics only for the current mandator. If unchecked, the statistic will be computed for all mandators.")
	public boolean getCurrentMandatorOnly(){
		return this.currentMandatorOnly;
	}
	
	/**
	 * @param currentMandatorOnly
	 */
	@SetProperty(name = "Active Mandator Only")
	public void setCurrentMandatorOnly(final boolean currentMandatorOnly){
		this.currentMandatorOnly = currentMandatorOnly;
	}
}
