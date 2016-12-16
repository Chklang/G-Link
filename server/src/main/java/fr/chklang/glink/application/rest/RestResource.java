package fr.chklang.glink.application.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import fr.chklang.glink.application.DB;
import fr.chklang.glink.application.WrapperObject;
import fr.chklang.glink.application.dto.ConfigurationDTO;
import fr.chklang.glink.application.dto.LinkDTO;
import fr.chklang.glink.application.model.Configuration;
import fr.chklang.glink.application.model.Link;

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
	
	@Path("/links")
	@GET
	public Response getLinks() {
		final List<LinkDTO> lConf = new ArrayList<>();
		DB.getInstance().getServer().execute(() -> {
			Link.finder.all().forEach((pLink) -> {
				lConf.add(new LinkDTO(pLink.getName(), pLink.getDescription(), pLink.getCommand()));
			});
		});
		return Response.ok(lConf).build();
	}
	
	@Path("/links/{link: .*}")
	@PUT
	public Response addOrSetLink(@PathParam("link") String pLinkName, LinkDTO pLink) {
		DB.getInstance().getServer().execute(() -> {
			Link lLink = Link.finder.getByName(pLinkName);
			if (lLink == null) {
				//Creation
				lLink = new Link(pLink.command, pLinkName, pLink.description, null);
				lLink.save();
			} else {
				lLink.setCommand(pLink.command);
				lLink.setDescription(pLink.description);
				lLink.update();
			}
		});
		return Response.status(204).build();
	}
	
	@Path("/links/{link: .*}")
	@DELETE
	public Response deleteLink(@PathParam("link") String pLinkName) {
		WrapperObject<Boolean> lWrapper = new WrapperObject<>();
		DB.getInstance().getServer().execute(() -> {
			boolean lIsDeleted = Link.finder.deleteByName(pLinkName);
			lWrapper.object = lIsDeleted;
		});
		if (lWrapper.object == Boolean.FALSE) {
			return Response.status(404).build();
		}
		return Response.status(204).build();
	}
}
