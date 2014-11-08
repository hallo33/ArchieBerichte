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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.fhnw.globiglobi.widgets.SelectMandator;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Provides statistics about patients per mandator. Resulting dataset contains personal information
 * about the patients grouped by every mandator.
 * </p>
 * 
 * 
 * @author Lukas Eisenhut
 * @author Stefan Waldenmaier
 */
public class PatientsPerMandator extends AbstractTimeSeries {
	
	/**
	 * Shows only patients for active mandator if true, all patients in the system else.
	 */
	private boolean currentMandatorOnly;
	
	/**
	 * Shows only patients of the specific mandator
	 */
	private String selectedMandatorID;
	
	/**
	 * Is used for selecting uniques mandators to a Fall
	 */
	private boolean check;
	
	/**
	 * Initiate all the required variables
	 */
	private String fallID;
	
	/**
	 * Date format for data that comes from the database.
	 */
	private static final String DATE_DB_FORMAT = "yyyyMMdd";
	
	public PatientsPerMandator(){
		super(Messages.PATIENTPERMAN_TITLE);
		
		this.selectedMandatorID = SelectMandator.DEFAULT_SELECTED;
	}
	
	/**
	 * Return an appropriate description
	 */
	public String getDescription(){
		return Messages.PATIENTPERMAN_DESCRIPTION;
	}
	
	/**
	 * Create dataset headings in this method
	 */
	protected List<String> createHeadings(){
		final ArrayList<String> headings = new ArrayList<String>(13);
		headings.add(Messages.PATIENTPERMAN_HEADING_STAMMARZT);
		headings.add(Messages.PATIENTPERMAN_HEADING_PATNAME);
		headings.add(Messages.PATIENTPERMAN_HEADING_PATPRENAME);
		headings.add(Messages.PATIENTPERMAN_HEADING_GENDER);
		headings.add(Messages.PATIENTPERMAN_HEADING_BIRTHDY);
		headings.add(Messages.PATIENTPERMAN_HEADING_STREET);
		headings.add(Messages.PATIENTPERMAN_HEADING_PLZ);
		headings.add(Messages.PATIENTPERMAN_HEADING_CITY);
		headings.add(Messages.PATIENTPERMAN_HEADING_PHONE1);
		headings.add(Messages.PATIENTPERMAN_HEADING_PHONE2);
		headings.add(Messages.PATIENTPERMAN_HEADING_FAX);
		headings.add(Messages.PATIENTPERMAN_HEADING_MAIL);
		headings.add(Messages.PATIENTPERMAN_HEADING_FALL);
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		
		final SimpleDateFormat databaseFormat = new SimpleDateFormat(DATE_DB_FORMAT);

		// initialize list
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(13);
		final HashMap<String, Konsultation> allCons = new HashMap<String, Konsultation>();
		
		// Create Queries
		final Query<Konsultation> behandlungQuery = new Query<Konsultation>(Konsultation.class);
		final Query<Patient> patQuery = new Query<Patient>(Patient.class);
		// behandlungQuery.add("Datum", ">=", databaseFormat.format(this.getStartDate().getTime()));
		// behandlungQuery.add("Datum", "<=", databaseFormat.format(this.getEndDate().getTime()));
		if (this.currentMandatorOnly) {
			behandlungQuery.add("MandantID", "=", CoreHub.actMandant.getId());
		}

		// Execute Queries
		final List<Konsultation> cons = behandlungQuery.execute();
		final List<Patient> patients = patQuery.execute();

		// get the filtered consultations and put it in the consList
		for (final Konsultation kons : cons) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

				// fill the consList with all the consultations resulting from the query
				allCons.put(kons.getId(), kons);
		}
		
		for (final Patient patient : patients) {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			if (patient.isValid()) {
				// initiating the list with the mandators per patient
				List<Konsultation> konsList = new ArrayList<Konsultation>();
				
				// get the patients address through the Anschrift Object
				Anschrift patanschrift = patient.getAnschrift();

				Fall[] faelle = patient.getFaelle();
				for (final Fall fall : faelle) {
				// if(fall.isValid()){
						
						Konsultation[] consultations = fall.getBehandlungen(false);
						
						// get every mandator that worked on the Fall and add them to the kons list
						
						for (final Konsultation konsultation : consultations) {
							
							// check if the selected consultation is in the consList
							if (allCons.containsKey(konsultation.getId())) {
								if (konsultation.getMandant().isValid()) {
									check = false;
									// for test uses
									fallID = konsultation.getFall().getId();
									
									// iterating through kons to check if mandator has already been
									// added to the list
									Iterator<Konsultation> itr = konsList.iterator();
									while (itr.hasNext()) {
										Konsultation k = itr.next();
										// set check true if mandator already exists in kons list.
										if (konsultation.getMandant().getId()
											.equals(k.getMandant().getId())) {
											check = true;
										}
									}
									
									// Add consultation to kons List if mandator does not exist yet.
									if (check == false) {
										konsList.add(konsultation);
									}
								}
							}
						}
				// }
				}
				// get the unique mandator of each entry in kons list and add them to the
				// dataset.
				Iterator<Konsultation> itr2 = konsList.iterator();
				while (itr2.hasNext()) {
					Konsultation k2 = itr2.next();
					// fill the rows with content
					final Comparable<?>[] row =
						{
							k2.getMandant().getId(), patient.getName(), patient.getVorname(),
							patient.getGeschlecht(), patient.getGeburtsdatum(),
							patanschrift.getStrasse(), patanschrift.getPlz(),
							patanschrift.getOrt(), patient.get("Telefon1"),
							patient.get("Telefon2"), patient.get("Fax"), patient.getMailAddress(),
							fallID
						};
					
					// add the row to the list
					content.add(row);
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
	@GetProperty(name = "Active Mandator Only", index = 2, widgetType = WidgetTypes.BUTTON_CHECKBOX, description = "Compute statistics only for the current mandator. If unchecked, the statistic will be computed for all mandators.")
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