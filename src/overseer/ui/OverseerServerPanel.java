package overseer.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import overseer.view.OverseerServerViewModel;

public class OverseerServerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private OverseerServerViewModel viewModel;

	public OverseerServerPanel(OverseerServerViewModel viewModel) {
		super();
		this.viewModel = viewModel;

		// Create styled panel border with server name
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), viewModel.name));
	}

}
