package fr.chklang.glink.application.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

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
		try {
			final List<ConfigurationDTO> lConf = new ArrayList<>();
			System.out.println("Get config");
			DB.getInstance().getServer().execute(() -> {
				System.out.println("Connexion OK");
				Configuration.finder.all().forEach((pConfiguration) -> {
					System.out.println("Object : " + pConfiguration);
					lConf.add(new ConfigurationDTO(pConfiguration.getKey(), pConfiguration.getValue()));
				});
				System.out.println("Fin connexion");
			});
			System.out.println("Fin : " + lConf);
			return Response.ok(lConf).build();
		} catch (Exception e) {
			return Response.status(500).entity(e).build();
		}
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
				lConf.add(new LinkDTO(pLink.getName(), pLink.getDescription(), pLink.getCommand(), pLink.getParameters()));
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
				lLink = new Link(pLink.command, pLinkName, pLink.description, null, pLink.parameters);
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
	
	@Path("/start/{link: .*}")
	@GET
	public Response startLink(@PathParam("link") String pLinkName) throws IOException {
		WrapperObject<Link> lWrapper = new WrapperObject<>();
		DB.getInstance().getServer().execute(() -> {
			Link lLink = Link.finder.getByName(pLinkName);
			lWrapper.object = lLink;
		});
		if (lWrapper.object == null) {
			return Response.status(404).build();
		}
		Link lLink = lWrapper.object;
		try {
			String lCommand = lLink.getCommand();
			if (!StringUtils.isEmpty(lLink.getParameters())) {
				lCommand += " " + lLink.getParameters();
			}
			Runtime.getRuntime().exec(lCommand);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		return Response.ok().build();
	}
}
