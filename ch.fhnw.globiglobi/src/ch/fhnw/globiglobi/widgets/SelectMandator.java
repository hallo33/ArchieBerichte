package ch.fhnw.globiglobi.widgets;

import org.eclipse.swt.widgets.Composite;

import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;

public class SelectMandator extends ComboWidget {
	
	public static final String DEFAULT_SELECTED = "Twenty";
	
	public SelectMandator(Composite parent, int style, final String labelText, RegexValidation regex){
		super(parent, style, labelText, regex);
		
		// Populate combo items in a custom fashion. This can come out of a
		// file, database or wherever you like most.
		String[] items = new String[] {
			"Twenty", "Thirty", "Fourty"
		};
		this.setItems(items);
	}

}
