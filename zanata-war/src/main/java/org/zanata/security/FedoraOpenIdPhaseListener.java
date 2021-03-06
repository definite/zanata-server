/*
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.security;

import java.io.IOException;
import java.io.PrintWriter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.navigation.Pages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FedoraOpenIdPhaseListener implements PhaseListener {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FedoraOpenIdPhaseListener.class);
    private static final long serialVersionUID = 1L;

    public void beforePhase(PhaseEvent event) {
        String viewId = Pages.getCurrentViewId();
        event.getFacesContext().getExternalContext().getRequestParameterMap();

        if (viewId == null || !viewId.startsWith("/openid.")) {
            return;
        }

        ZanataOpenId openid =
                (ZanataOpenId) Component.getInstance(ZanataOpenId.class);
        if (openid.getId() == null) {
            try {
                sendXRDS();
            } catch (IOException e) {
                LOGGER.warn("exception", e);
            }
            return;
        }

        openid.verify();

        Pages.handleOutcome(event.getFacesContext(), null, "/openid.xhtml");
    }

    private void sendXRDS() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext extContext = context.getExternalContext();
        HttpServletResponse response =
                (HttpServletResponse) extContext.getResponse();

        response.setContentType("application/xrds+xml");
        PrintWriter out = response.getWriter();

        // XXX ENCODE THE URL!
        ZanataOpenId open =
                (ZanataOpenId) Component.getInstance(ZanataOpenId.class);

        out.println("<XRDS xmlns=\"xri://$xrd*($v*2.0)\"><XRD><Service>"
                + "<Type>http://specs.openid.net/auth/2.0/return_to</Type><URI>"
                + open.returnToUrl() + "</URI></Service></XRD></XRDS>");

        context.responseComplete();
    }

    public void afterPhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
