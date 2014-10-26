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

import ch.elexis.data.Anschrift;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

/**
 * <p>
 * Provides statistics about patients per mandator. Resulting dataset contains personal information
 * about the patients grouped by every mandator.
 * </p>
 * TESSST
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
	private String selectMandatorID;
	
	/**
	 * Date format for data that comes from the database.
	 */
	// private static final String DATE_DB_FORMAT = "yyyyMMdd";
	
	public PatientsPerMandator(){
		super(Messages.PATIENTPERMAN_TITLE);
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
		final ArrayList<String> headings = new ArrayList<String>(12);
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
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		// initialize list
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(12);
		
		// Create Queries
		final Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		
		// Execute Queries
		final List<Patient> pat = patientQuery.execute();

		for (final Patient patient : pat) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			// definition of the variables for the result
			String mandant = "";
			String fallID = "";
			String behID = "";
			String patname = patient.getName();
			String patvname = patient.getVorname();
			String gender = patient.getGeschlecht();
			String birthday = patient.getGeburtsdatum();
			// String phone1 = patient.g;
			String mail = patient.getMailAddress();

			// get the Address
			Anschrift anschrift = patient.getAnschrift();
			String street = patient.get("Strasse");
			String plz = anschrift.getPlz();
			String city = anschrift.getOrt();

			
			// get Faelle
			Fall[] faelle = patient.getFaelle();
			for (final Fall fall : faelle) {
				fallID = fall.getId();
				// get Consultations per Fall
				Konsultation[] kons = fall.getBehandlungen(false);
				
				for (final Konsultation konsultation : kons) {
					
					// if Konsultation has a Mandator and Fall
					if(konsultation.isValid() == true){
						behID = konsultation.getId();
						
						// get Mandator of the consultation
						Mandant mand = konsultation.getMandant();

						// checks if is Mandator (istMandant = 1)
						if (mand.isValid() == true) {
							mandant = mand.getKuerzel();
						}
						
						//
						Fall f1 = konsultation.getFall();
						
						//

						// fill the rows with content
						final Comparable<?>[] row =
							{
								mandant, fallID, behID, patname, patvname, gender, birthday, street,
								plz, city, "Fax", mail
							};
						
						// add the row to the list
						content.add(row);
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
}