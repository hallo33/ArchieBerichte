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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
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
		final ArrayList<String> headings = new ArrayList<String>(6);
		headings.add(Messages.SALEPERMANDATOR_HEADING_MANDATOR);
		headings.add(Messages.SALEPERMANDATOR_HEADING_DETECTEDSERVICE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_CHARGEDSERVICE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_PAYEDSERVICE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_SALE);
		headings.add(Messages.SALEPERMANDATOR_HEADING_TOTALSALE);
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
			
			String erfasst = "";
			String verrechnet = "";
			String bezahlt = "";
			String umsatz = "";
			String totalerUmsatz = "";

			if (cons.getMandant() != null) {
				String mandant = cons.getMandant().getName();
				
				List<Verrechnet> Verrechenbar = cons.getLeistungen();
				Iterator<Verrechnet> itr = Verrechenbar.iterator();

				while (itr.hasNext()) {
					Verrechnet v = itr.next();
					erfasst = v.getText();
					
					if (cons.getRechnung() != null) {
						verrechnet = "verrechnet";
						
						if (verrechnet.equals("verrechnet")
							&& cons.getRechnung().getOffenerBetrag().getAmountAsString()
								.equals("0.00")) {
							bezahlt = "bezahlt";
							
							double geld = (v.getNettoPreis().getAmount());
							double geldRund = Math.round(100.0 * geld) / 100.0;
							umsatz = String.valueOf(geldRund);

							List<Double> SummeUmsatz = new ArrayList<Double>();
							SummeUmsatz.add(geldRund);
							Iterator<Double> itr3 = SummeUmsatz.iterator();
							double d = 0;
							while (itr3.hasNext()) {
								d += itr3.next().doubleValue();
							}
							totalerUmsatz = String.valueOf(d);
						}
						
						else if (verrechnet.equals("verrechnet")
							&& !cons.getRechnung().getOffenerBetrag().getAmountAsString()
								.equals("0.00")) {
							bezahlt = "offen";
							umsatz = "0";
							totalerUmsatz = "0";
						}
					}

					else {
						verrechnet = "nicht verrechnet";
						bezahlt = "offen";
						umsatz = "0";
						totalerUmsatz = "0";
					}
					
					// Collections.sort(consultations);
					
					Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
					int index = 0;
					
					row[index++] = mandant;
					row[index++] = erfasst;
					row[index++] = verrechnet;
					row[index++] = bezahlt;
					row[index++] = umsatz;
					row[index++] = totalerUmsatz;
					
					content.add(row);
					
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
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
	
	/**
	 * @return Gives back the selected Mandator Kuerzel.
	 */
	@GetProperty(name = "Select Mandator", index = 10, widgetType = WidgetTypes.VENDOR, description = "Select a Mandator", vendorClass = SelectMandator.class)
	public String getSelectedMandator(){
		return this.selectedMandatorID;
	}
	
	/**
	 * @param Sets
	 *            the selected Mandator.
	 */
	@SetProperty(name = "Select Mandator")
	public void setSelectedMandator(final String mandID){
		this.selectedMandatorID = mandID;
	}
}
