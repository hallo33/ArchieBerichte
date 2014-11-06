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
import ch.elexis.data.Bestellung;
import ch.elexis.data.Query;
import ch.fhnw.globiglobi.reports.i18n.Messages;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;

/**
 * <p>
 * Provides a list of all articles with its current stock as well as the current orders.
 * </p>
 * 
 * 
 * @author Lukas Eisenhut
 * @author Stefan Waldenmaier
 */
public class CurrentStock extends AbstractDataProvider {
	
	public int anzahl;

	public CurrentStock(){
		super(Messages.CURRENTSTOCK_TITLE);
	}
	
	/**
	 * Return an appropriate description
	 */
	public String getDescription(){
		return Messages.CURRENTSTOCK_DESCRIPTION;
	}
	
	/**
	 * Create dataset headings in this method
	 */
	protected List<String> createHeadings(){
		final ArrayList<String> headings = new ArrayList<String>(8);
		headings.add(Messages.CURRENTSTOCK_HEADING_ARTICLE);
		headings.add(Messages.CURRENTSTOCK_HEADING_EAN);
		headings.add(Messages.CURRENTSTOCK_HEADING_PHARMACODE);
		headings.add(Messages.CURRENTSTOCK_HEADING_ISTBEST);
		headings.add(Messages.CURRENTSTOCK_HEADING_MINDBEST);
		headings.add(Messages.CURRENTSTOCK_HEADING_MAXBEST);
		headings.add(Messages.CURRENTSTOCK_HEADING_BESTELLUNGEN);
		headings.add(Messages.CURRENTSTOCK_HEADING_LIEFERANT);
		return headings;
	}
	
	/**
	 * Compose the contents of a dataset here
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		
		// initialize list
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(8);
		
		// Create Queries
		final Query<Artikel> artikelQuery = new Query<Artikel>(Artikel.class);
		final Query<Bestellung> bestellungQuery = new Query<Bestellung>(Bestellung.class);

		// Execute Queries
		final List<Artikel> art = artikelQuery.execute();
		final List<Bestellung> best = bestellungQuery.execute();
		

		// for (final Artikel artikel : art) {
		for (int i = 0; i < 10; i++) {
			Artikel artikel = art.get(i);
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			if (artikel.isValid()) {
				String lieferant = artikel.getLieferant().getId();
				
				final Comparable<?>[] row =
					{
						artikel.getName(), artikel.getEAN(), artikel.getPharmaCode(),
						artikel.getIstbestand(), artikel.getMinbestand(), artikel.getMaxbestand(),
						"", lieferant
					};
				
				// add the row to the list
				content.add(row);
			}
		}

		// set content in the dataSet
		this.dataSet.setContent(content);
		
		// job finished successfully
		monitor.done();
		return Status.OK_STATUS;
	}
}
