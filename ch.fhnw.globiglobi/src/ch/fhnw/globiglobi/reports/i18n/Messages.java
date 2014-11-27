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
package ch.fhnw.globiglobi.reports.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * <p>
 * Message class. Used for i18n.
 * </p>
 * 
 * 
 * @author Lukas Eisenhut
 * @author Stefan Waldenmaier
 */

public final class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "ch.fhnw.globiglobi.reports.i18n.messages"; //$NON-NLS-1$
	
	// General
	public static String CALCULATING;

	// Patients Per Mandator
	public static String PATIENTPERMAN_TITLE;
	public static String PATIENTPERMAN_DESCRIPTION;
	public static String PATIENTPERMAN_HEADING_STAMMARZT;
	public static String PATIENTPERMAN_HEADING_PATNAME;
	public static String PATIENTPERMAN_HEADING_PATPRENAME;
	public static String PATIENTPERMAN_HEADING_GENDER;
	public static String PATIENTPERMAN_HEADING_BIRTHDY;
	public static String PATIENTPERMAN_HEADING_STREET;
	public static String PATIENTPERMAN_HEADING_PLZ;
	public static String PATIENTPERMAN_HEADING_CITY;
	public static String PATIENTPERMAN_HEADING_PHONE1;
	public static String PATIENTPERMAN_HEADING_PHONE2;
	public static String PATIENTPERMAN_HEADING_FAX;
	public static String PATIENTPERMAN_HEADING_MAIL;
	public static String PATIENTPERMAN_HEADING_FALL;
	
	// SalePerMandator
	public static String SALEPERMANDATOR_TITLE;
	public static String SALEPERMANDATOR_DESCRIPTION;
	public static String SALEPERMANDATOR_HEADING_MANDATOR;
	public static String SALEPERMANDATOR_HEADING_CONSID;
	public static String SALEPERMANDATOR_HEADING_DETECTEDSERVICE;
	public static String SALEPERMANDATOR_HEADING_CHARGEDSERVICE;
	public static String SALEPERMANDATOR_HEADING_PAYEDSERVICE;
	public static String SALEPERMANDATOR_HEADING_SALE;
	public static String SALEPERMANDATOR_HEADING_TOTALSALE;
	static {
		// Load message values from bundle file.
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
