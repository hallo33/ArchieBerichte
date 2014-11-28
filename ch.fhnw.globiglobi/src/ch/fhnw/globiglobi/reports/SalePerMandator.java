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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.fhnw.globiglobi.widgets.SelectMandator;
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
	 * Shows only services for active mandator if true, for all mandators else.
	 */
	private boolean currentMandatorOnly;
	
	/**
	 * Initialize a double value.
	 */
	private double summe = 0;
	
	/**
	 * Shows only services of the specific mandator
	 */
	private String selectedMandatorID;
	
	/**
	 * Date format for data that comes from the database.
	 */
	private static final String DATE_DB_FORMAT = "yyyyMMdd";
	
	/**
	 * Defines the constructor for SalePerMandator.
	 */
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
		final ArrayList<String> headings = new ArrayList<String>(7);
		headings.add(Messages.SALEPERMANDATOR_HEADING_MANDATOR);
		headings.add(Messages.SALEPERMANDATOR_HEADING_CONSID);
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
		final Query<Kontakt> mandQuery = new Query<Kontakt>(Kontakt.class);
		
		/**
		 * Date format for data that comes from the database.
		 */
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);
		
		/**
		 * Check for results of the Query in the selected date space.
		 */
		consQuery.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		consQuery.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));
		
		// check if checkbox current mandator only is on or a mandator is selected
		if (!this.selectedMandatorID.equals("All")) {
			mandQuery.add("Bezeichnung3", "=", this.selectedMandatorID);
			// List<Kontakt> mandatorIDselect = "";
			consQuery.add("MandantID", "=", mandQuery.execute().get(0).getId());
		} else {
			if (this.currentMandatorOnly) {
				consQuery.add("MandantID", "=", CoreHub.actMandant.getId());
			}
		}
		
		monitor.subTask("Lade Konsultationen");
		// Execute Query
		List<Konsultation> consultations = consQuery.execute();
		// initialize list for content
		final ArrayList<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(7);
		
		summe = 0;
		
		// Eveything following is done for the consultations in the list consultations.
		for (Konsultation cons : consultations) {
			
			// Initialize the Strings to create data for the content.
			String erfasst = "";
			String verrechnet = "";
			String bezahlt = "";
			String umsatz = "";
			String totalerUmsatz = "";
			String consID = "";
			
			// check if cosultation is not deleted.
			if (!cons.delete()) {
				// check if consultation has a mandator, get his name and the consultation's ID.
				if (cons.getMandant() != null) {
					String mandant = cons.getMandant().getName();
					consID = cons.getId();
					
					// Create new list with Verrechnet in it from the Leistungen of a consultation.
					List<Verrechnet> Verrechenbar = cons.getLeistungen();
					Iterator<Verrechnet> itr = Verrechenbar.iterator();
					// Check if the List Verrechenabr has next with an iterator and get it's text.
					while (itr.hasNext()) {
						Verrechnet v = itr.next();
						if (erfasst.equals("")) {
							erfasst = v.getText();
						} else {
							erfasst += "\n" + v.getText();
						}
						
						// check if a consultation has a bill.
						if (cons.getRechnung() != null) {
							verrechnet = "verrechnet";
							
							// check if the bill is payed allready.
							if (cons.getRechnung().getOffenerBetrag().getAmountAsString()
								.equals("0.00")) {
								bezahlt = "bezahlt";
								
								// calculate the sale and the total sale of mandator's calculations.
								double geld = (v.getNettoPreis().getAmount() * v.getZahl());
								double geldRund = Math.round(100.0 * geld) / 100.0;
								
								if (umsatz.equals("")) {
									umsatz = String.valueOf(geldRund);
								}
								
								else {
									umsatz += "\n" + String.valueOf(geldRund);
								}
								
								summe += geldRund;

								totalerUmsatz = String.valueOf(Math.round(100.0 * summe) / 100.0);
							}
							
							// check if a bill has to be payed still.
							else if (!cons.getRechnung().getOffenerBetrag().getAmountAsString()
								.equals("0.00")) {
								bezahlt = "offen";
								umsatz = "0";
								totalerUmsatz = "0";
							}
						}
						// if a consultation has no bill.
						else {
							verrechnet = "nicht verrechnet";
							bezahlt = "offen";
							umsatz = "0";
							totalerUmsatz = "0";
						}
					}
					
					// add all the date from the content list to the final table rows.
					Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
					int index = 0;
					
					row[index++] = mandant;
					row[index++] = consID;
					row[index++] = erfasst;
					row[index++] = verrechnet;
					row[index++] = bezahlt;
					row[index++] = umsatz;
					row[index++] = totalerUmsatz;
					
					content.add(row);
					
					// check for cancelation.
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
