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
import fr.chklang.glink.application.HotKeysUtils;
import fr.chklang.glink.application.WrapperObject;
import fr.chklang.glink.application.dto.ConfigurationDTO;
import fr.chklang.glink.application.dto.HotkeyDTO;
import fr.chklang.glink.application.dto.LinkDTO;
import fr.chklang.glink.application.model.Configuration;
import fr.chklang.glink.application.model.Link;

@Path("/rest")
public class RestResource {

	@Path("/config")
	@GET
	public Response getConfig() {
		try {
			final ConfigurationDTO lConfigurationDTO = new ConfigurationDTO();
			DB.getInstance().getServer().execute(() -> {
				Configuration lConfNbSquaresX = Configuration.finder.byId("nbSquaresX");
				Configuration lConfNbSquaresY = Configuration.finder.byId("nbSquaresY");
				Configuration lConfHotkeyCtrl = Configuration.finder.byId("hotkey_ctrl");
				Configuration lConfHotkeyAlt = Configuration.finder.byId("hotkey_alt");
				Configuration lConfHotkeyShift = Configuration.finder.byId("hotkey_shift");
				Configuration lConfHotkey = Configuration.finder.byId("hotkey");
				
				lConfigurationDTO.nbSquaresX = Integer.parseInt(lConfNbSquaresX.getValue());
				lConfigurationDTO.nbSquaresY = Integer.parseInt(lConfNbSquaresY.getValue());
				HotkeyDTO lHotkeyDTO = new HotkeyDTO();
				lHotkeyDTO.hotkey_ctrl = Boolean.parseBoolean(lConfHotkeyCtrl.getValue());
				lHotkeyDTO.hotkey_alt = Boolean.parseBoolean(lConfHotkeyAlt.getValue());
				lHotkeyDTO.hotkey_shift = Boolean.parseBoolean(lConfHotkeyShift.getValue());
				lHotkeyDTO.hotkey = lConfHotkey.getValue();
				lConfigurationDTO.hotkey = lHotkeyDTO;
			});
			return Response.ok(lConfigurationDTO).build();
		} catch (Exception e) {
			return Response.status(500).entity(e).build();
		}
	}

	@Path("/config")
	@POST
	public Response setConfig(final ConfigurationDTO pConf) {
		DB.getInstance().getServer().execute(() -> {
			Configuration.finder.byId("nbSquaresX").setValue(Integer.toString(pConf.nbSquaresX)).update();
			Configuration.finder.byId("nbSquaresY").setValue(Integer.toString(pConf.nbSquaresY)).update();
			
			HotkeyDTO lHotkeyDTO = pConf.hotkey;
			Configuration.finder.byId("hotkey_ctrl").setValue(Boolean.toString(lHotkeyDTO.hotkey_ctrl)).update();
			Configuration.finder.byId("hotkey_alt").setValue(Boolean.toString(lHotkeyDTO.hotkey_alt)).update();
			Configuration.finder.byId("hotkey_shift").setValue(Boolean.toString(lHotkeyDTO.hotkey_shift)).update();
			Configuration.finder.byId("hotkey").setValue(lHotkeyDTO.hotkey).update();
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
	
	@Path("/links")
	@POST
	public Response setLinks(List<LinkDTO> pLinks) {
		DB.getInstance().getServer().execute(() -> {
			Link.finder.deleteAll();
			pLinks.forEach((pLink) -> {
				Link lLink = new Link(pLink.command, pLink.name, pLink.description, null, pLink.parameters);
				lLink.save();
			});
		});
		return Response.status(204).build();
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
	
	@Path("/hotkeys")
	@POST
	public Response testHotkey(HotkeyDTO pHotkeyDTO) {
		boolean lTest = HotKeysUtils.testKeys(pHotkeyDTO);
		if (lTest) {
			return Response.ok().build();
		} else {
			return Response.status(409).build();
		}
	}
}
