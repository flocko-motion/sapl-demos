package org.demo.view.blocking;

import org.demo.service.UIController;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.spring.PolicyEnforcementPoint;

@SpringView(name = "traditional")
@SpringComponent("traditionalPatientView")
public class PatientView extends AbstractPatientView {

	@Autowired
	public PatientView(PolicyEnforcementPoint pep, UIController controller) {
		super(pep, controller);
	}

	@Override
	protected AbstractPatientForm createForm(PolicyEnforcementPoint pep,
			UIController uiController,
			AbstractPatientForm.RefreshCallback refreshCallback) {
		return new PatientForm(pep, uiController, refreshCallback);
	}

}
