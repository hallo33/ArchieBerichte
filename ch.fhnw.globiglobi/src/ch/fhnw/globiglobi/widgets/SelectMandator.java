package ch.fhnw.globiglobi.widgets;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;

/**
 * 
 * @author Lukas Eisenhut
 * 
 *         This class is used for the dropdown menu to get a list of all mandators in the system
 * 
 */
public class SelectMandator extends ComboWidget {
	
	public static final String DEFAULT_SELECTED = "All";
	
	/**
	 * 
	 * Return a String array containing all Names of mandators. First field in the Array is the
	 * DEFAULT_SELECTED value.
	 * 
	 * @param parent
	 * @param style
	 * @param labelText
	 * @param regex
	 */
	public SelectMandator(Composite parent, int style, final String labelText, RegexValidation regex){
		super(parent, style, labelText, regex);
		
		// load all mandators (where istMandant = 1) from the database
		final Query<Kontakt> mandQuery = new Query<Kontakt>(Kontakt.class);
		mandQuery.add("istMandant", "=", "1");
		
		final List<Kontakt> mand = mandQuery.execute();
		// create the final String array
		String[] items = new String[mand.size() + 1];
		
		// set the first value
		items[0] = "All";
		int i = 1;
		// Fill the List with the mandators
		for (Kontakt mandant : mand) {
			items[i] = mandant.getKuerzel();
			i++;
		}
		this.setItems(items);
	}

}
