package fr.chklang.glink.application.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import fr.chklang.glink.application.DB;
import fr.chklang.glink.application.dto.ConfigurationDTO;
import fr.chklang.glink.application.model.Configuration;

@Path("/rest")
public class RestResource {

	@Path("/config")
	@GET
	public Response getConfig() {
		final List<ConfigurationDTO> lConf = new ArrayList<>();
		DB.getInstance().getServer().execute(() -> {
			Configuration.finder.all().forEach((pConfiguration) -> {
				lConf.add(new ConfigurationDTO(pConfiguration.getKey(), pConfiguration.getValue()));
			});
		});
		return Response.ok(lConf).build();
	}

	@Path("/config")
	@POST
	public Response setConfig(final List<ConfigurationDTO> pConf) {
		DB.getInstance().getServer().execute(() -> {
			Configuration.finder.deleteAll();
			pConf.forEach((pConfigurationDTO) -> {
				Configuration lConfiguration = new Configuration(pConfigurationDTO.key, pConfigurationDTO.value);
				lConfiguration.save();
			});
		});
		return Response.status(204).build();
	}
}
