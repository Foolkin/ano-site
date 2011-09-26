package net.anotheria.anosite.action;

import net.anotheria.anosite.util.AnositeConstants;
import net.java.dev.moskito.core.predefined.ActionStats;
import net.java.dev.moskito.core.predefined.Constants;
import net.java.dev.moskito.core.producers.IStats;
import net.java.dev.moskito.core.producers.IStatsProducer;
import net.java.dev.moskito.core.registry.ProducerRegistryFactory;
import net.java.dev.moskito.core.usecase.running.ExistingRunningUseCase;
import net.java.dev.moskito.core.usecase.running.PathElement;
import net.java.dev.moskito.core.usecase.running.RunningUseCase;
import net.java.dev.moskito.core.usecase.running.RunningUseCaseContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Moskito producer for Actions.
 *
 * @author h3ll
 */
public class ActionProducer implements IStatsProducer {

	/**
	 * ActionProducer producerId.
	 */
	private String producerId;
	/**
	 * Cached list with stats.
	 */
	private final List<IStats> actionStats;
	/**
	 * Stats for execute method.
	 */
	private ActionStats executeStats;

	/**
	 * Constructor.
	 *
	 * @param id producer id
	 */
	public ActionProducer(String id) {
		this.producerId = id;
		this.actionStats = new ArrayList<IStats>();
		this.executeStats = new ActionStats("execute", Constants.getDefaultIntervals());
		actionStats.add(executeStats);

		ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(this);
	}


	@Override
	public List<IStats> getStats() {
		return actionStats;
	}

	@Override
	public String getProducerId() {
		return producerId;
	}

	@Override
	public String getCategory() {
		return "action";
	}

	@Override
	public String getSubsystem() {
		return AnositeConstants.AS_MOSKITO_SUBSYSTEM;
	}

	/**
	 * Execute method - which simply delegates call to real Action clazz, and manages Moskito-Stats!
	 *
	 * @param req	 {@link HttpServletRequest}
	 * @param resp	{@link HttpServletResponse}
	 * @param mapping {@link ActionMapping}
	 * @param action  {@link Action}
	 * @return {@link ActionCommand}
	 * @throws Exception on errors from original action
	 */
	protected ActionCommand execute(HttpServletRequest req, HttpServletResponse resp, ActionMapping mapping, Action action) throws Exception {

		executeStats.addRequest();
		long startTime = System.nanoTime();
		RunningUseCase aRunningUseCase = RunningUseCaseContainer.getCurrentRunningUseCase();
		PathElement currentElement = null;
		ExistingRunningUseCase runningUseCase = aRunningUseCase.useCaseRunning() ?
				(ExistingRunningUseCase) aRunningUseCase : null;
		if (runningUseCase != null)
			currentElement = runningUseCase.startPathElement(new StringBuilder(getProducerId()).append('.').append("execute").toString());
		try {
			return action.execute(req, resp, mapping);
		} catch (Exception e) {
			executeStats.notifyError();
			throw e;
		} finally {
			long duration = System.nanoTime() - startTime;
			executeStats.addExecutionTime(duration);
			executeStats.notifyRequestFinished();
			if (currentElement != null)
				currentElement.setDuration(duration);
			if (runningUseCase != null)
				runningUseCase.endPathElement();
		}
	}
}