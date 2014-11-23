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

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.fhnw.globiglobi.widgets.SelectMandator;
import ch.rgw.tools.TimeTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Provides statistics about the mandators services ordered by the Mandators name. Resulting dataset
 * contains information about mandators services.
 * </p>
 * 
 * 
 * @author Lukas Eisenhut
 * @author Stefan Waldenmaier
 */
public class SalePerMandator extends AbstractTimeSeries {
	
	/**
	 * Shows only patients for active mandator if true, all patients in the system else.
	 */
	private boolean currentMandatorOnly;
	
	/**
	 * Shows only patients of the specific mandator
	 */
	private String selectedMandatorID;
	
	/**
	 * Date format for data that comes from the database.
	 */
	// private static final String DATE_DB_FORMAT = "yyyyMMdd";
	
	public SalePerMandator(){
		super(Messages.SALEPERMANDATOR_TITLE);
		this.selectedMandatorID = SelectMandator.DEFAULT_SELECTED;
	}
	
	/**
	 * Return an appropriate description
	 */
	public String getDescription(){
		return Messages.SALEPERMANDATOR_DESCRIPTION;
	}
	
	/**
	 * Create dataset headings in this method
	 */

	@Override
	protected List<String> createHeadings(){
		final ArrayList<String> headings = new ArrayList<String>(4);
		headings.add(Messages.SALEPERMANDATOR_HEADING_MANDATOR);
		headings.add(Messages.SALEPERMANDATOR_HEADING_DETECTEDSERVICE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_CHARGEDSERVICE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_PAYEDSERVICE);
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		monitor.beginTask("Umsätze pro Mandant", IProgressMonitor.UNKNOWN);
		Query consQuery = new Query(Konsultation.class);
		TimeTool ttStart = new TimeTool(this.getStartDate().getTimeInMillis());
		TimeTool ttEnd = new TimeTool(this.getEndDate().getTimeInMillis());
		consQuery.add(Konsultation.FLD_DATE, Query.GREATER_OR_EQUAL,
			ttStart.toString(TimeTool.DATE_COMPACT));
		consQuery.add(Konsultation.FLD_DATE, Query.LESS_OR_EQUAL,
			ttEnd.toString(TimeTool.DATE_COMPACT));
		
		// final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);
		
		// consQuery.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		// consQuery.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));
		
		// check if checkbox current mandator only is on
		if (this.currentMandatorOnly) {
			consQuery.add("MandantID", "=", CoreHub.actMandant.getId());
		}
		
		monitor.subTask("Lade Konsultationen");
		// Execute Queries
		List<Konsultation> consultations = consQuery.execute();
		// initialize list
		final ArrayList<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(6);
		
		for (Konsultation cons : consultations) {

			// List<Verrechnet> Verrechenbar = cons.getLeistungen();
			// Iterator<Verrechnet> itr = Verrechenbar.iterator();
			// while (itr.hasNext()) {
			// Verrechnet v = itr.next();
			if (cons.getMandant() != null) {
				String mandant = cons.getMandant().getName();
				
				Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
				int index = 0;

				row[index++] = mandant;
				row[index++] = "";
				row[index++] = "";
				row[index++] = "";
				row[index++] = "";
				row[index++] = "";
				content.add(row);
				
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		
		// set content in the dataSet
		this.dataSet.setContent(content);
		
		// job finished successfully
		
		monitor.done();
		return Status.OK_STATUS;

	}
	
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
