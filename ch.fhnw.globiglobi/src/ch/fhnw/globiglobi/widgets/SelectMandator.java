package ch.fhnw.globiglobi.widgets;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;

public class SelectMandator extends ComboWidget {
	
	public static final String DEFAULT_SELECTED = "All";
	
	public SelectMandator(Composite parent, int style, final String labelText, RegexValidation regex){
		super(parent, style, labelText, regex);
		
		final Query<Kontakt> mandQuery = new Query<Kontakt>(Kontakt.class);
		mandQuery.add("istMandant", "=", "1");
		
		final List<Kontakt> mand = mandQuery.execute();
		String[] items = new String[mand.size() + 1];
		
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
