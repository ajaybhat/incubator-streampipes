package de.fzi.cep.sepa.sources.samples.main;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.osgi.init.OsgiSubmitter;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedEventProducer;
import de.fzi.cep.sepa.sources.samples.hella.EnvironmentalDataProducer;
import de.fzi.cep.sepa.sources.samples.hella.MontracProducer;
import de.fzi.cep.sepa.sources.samples.hella.MouldingMachineProducer;
import de.fzi.cep.sepa.sources.samples.hella.VisualInspectionProducer;
import de.fzi.cep.sepa.sources.samples.proveit.ProveITEventProducer;
import de.fzi.cep.sepa.sources.samples.ram.RamProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;
import de.fzi.cep.sepa.sources.samples.wunderbar.WunderbarProducer;
import de.fzi.cep.sepa.sources.samples.wunderbar.WunderbarProducer2;

public class SourcesSamplesInit extends OsgiSubmitter {

	
	@Override
	public void init() {
		DeclarersSingleton.getInstance().setRoute("sources-samples");
		DeclarersSingleton.getInstance().setPort(ClientConfiguration.INSTANCE.getPodPort());
		ClientConfiguration config = ClientConfiguration.INSTANCE;
		
		if (config.isTwitterActive()) DeclarersSingleton.getInstance().add(new TwitterStreamProducer());
		if (config.isMhwirthReplayActive())
		{
			DeclarersSingleton.getInstance().add(new DDMProducer())
			.add(new DrillBitProducer())
			.add(new EnrichedEventProducer())
			.add(new RamProducer());
		}
		if (config.isRandomNumberActive()) DeclarersSingleton.getInstance().add(new RandomDataProducer());
		if (config.isTaxiActive()) DeclarersSingleton.getInstance().add(new NYCTaxiProducer());
		if (config.isProveItActive()) DeclarersSingleton.getInstance().add(new ProveITEventProducer());
		if (config.isHellaReplayActive())
		{
			DeclarersSingleton.getInstance().add(new VisualInspectionProducer())
			.add(new MontracProducer())
			.add(new MouldingMachineProducer())
			.add(new EnvironmentalDataProducer());

		}
		DeclarersSingleton.getInstance().add(new WunderbarProducer())
		.add(new WunderbarProducer2());
	}

	@Override
	public String getContextPath() {
		return "/sources-samples";
	}
}
