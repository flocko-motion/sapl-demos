package org.demo;

import org.demo.domain.PrinterUser;
import org.demo.helper.EthConnect;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CrowdfundingForm extends VerticalLayout {

	private static final long serialVersionUID = 8878184008835129794L;

	public CrowdfundingForm(PrinterUser user, EthConnect ethConnect) {

		H2 crowdfunding = new H2("Crowdfunding");
		Paragraph explanation = new Paragraph(
				"We also need your help to maintain our printer service. Once our donation goal has been reached, we will unlock a "
						+ "new template for the community.");

		Input donationField = new Input();
		Label unit = new Label("ETH");
		unit.getStyle().set("margin-top", "10px");

		Button donate = new Button("Donate");
		donate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		donate.addClickListener(event -> ethConnect.makeDonation(user, donationField.getValue()));

		HorizontalLayout donation = new HorizontalLayout(donationField, unit, donate);

		VerticalLayout userShow = new VerticalLayout(crowdfunding, explanation, donation);

		add(userShow);

	}

}
