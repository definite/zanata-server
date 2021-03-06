/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.action;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.zanata.ApplicationConfiguration;
import org.zanata.action.validator.EmailList;
import org.zanata.dao.ApplicationConfigurationDAO;
import org.zanata.model.HApplicationConfiguration;
import org.zanata.model.validator.Url;

@Name("serverConfigurationBean")
@Scope(ScopeType.PAGE)
@Restrict("#{s:hasRole('admin')}")
public class ServerConfigurationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @In
    private ApplicationConfigurationDAO applicationConfigurationDAO;

    @In
    private ApplicationConfiguration applicationConfiguration;

    @Url(canEndInSlash = true)
    @Getter
    @Setter
    private String registerUrl;

    @Url(canEndInSlash = false)
    @Getter
    @Setter
    private String serverUrl;

    @Getter
    @Setter
    private String emailDomain;

    @Getter
    @Setter
    @EmailList
    private String adminEmail;

    @Email
    @Getter
    @Setter
    private String fromEmailAddr;

    @Setter
    private String homeContent;

    @Setter
    private String helpContent;

    @Getter
    @Setter
    private boolean enableLogEmail;

    @Getter
    @Setter
    private String logDestinationEmails;

    @Getter
    @Setter
    private String logEmailLevel;

    @Url(canEndInSlash = true)
    @Getter
    @Setter
    private String piwikUrl;

    @Getter
    @Setter
    private String piwikIdSite;

    @Url(canEndInSlash = true)
    @Getter
    @Setter
    private String termsOfUseUrl;

    public String getHomeContent() {
        HApplicationConfiguration var =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HOME_CONTENT);
        return var != null ? var.getValue() : "";
    }

    public String getHelpContent() {
        HApplicationConfiguration var =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HELP_CONTENT);
        return var != null ? var.getValue() : "";
    }

    public String updateHomeContent() {
        HApplicationConfiguration var =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HOME_CONTENT);
        persistApplicationConfig(HApplicationConfiguration.KEY_HOME_CONTENT,
                var, homeContent);
        applicationConfigurationDAO.flush();

        FacesMessages.instance().add("Home content was successfully updated.");
        return "/home.xhtml";
    }

    public String updateHelpContent() {
        HApplicationConfiguration var =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HELP_CONTENT);
        persistApplicationConfig(HApplicationConfiguration.KEY_HELP_CONTENT,
                var, helpContent);
        applicationConfigurationDAO.flush();

        FacesMessages.instance().add(
                "Help page content was successfully updated.");
        return "/help/view.xhtml";
    }

    @Create
    public void onCreate() {
        HApplicationConfiguration registerUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_REGISTER);
        if (registerUrlValue != null) {
            this.registerUrl = registerUrlValue.getValue();
        }
        HApplicationConfiguration serverUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HOST);
        if (serverUrlValue != null) {
            this.serverUrl = serverUrlValue.getValue();
        }
        HApplicationConfiguration emailDomainValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_DOMAIN);
        if (emailDomainValue != null) {
            this.emailDomain = emailDomainValue.getValue();
        }
        HApplicationConfiguration adminEmailValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_ADMIN_EMAIL);
        if (adminEmailValue != null) {
            this.adminEmail = adminEmailValue.getValue();
        }

        this.fromEmailAddr = applicationConfiguration.getFromEmailAddr();

        HApplicationConfiguration emailLogEventsValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS);
        if (emailLogEventsValue != null) {
            this.enableLogEmail =
                    Boolean.parseBoolean(emailLogEventsValue.getValue());
        }
        HApplicationConfiguration logDestinationValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL);
        if (logDestinationValue != null) {
            this.logDestinationEmails = logDestinationValue.getValue();
        }
        HApplicationConfiguration logEmailLevelValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL);
        if (logEmailLevelValue != null) {
            this.logEmailLevel = logEmailLevelValue.getValue();
        }
        HApplicationConfiguration piwikUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_PIWIK_URL);
        if (piwikUrlValue != null) {
            this.piwikUrl = piwikUrlValue.getValue();
        }
        HApplicationConfiguration piwikIdSiteValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_PIWIK_IDSITE);
        if (piwikIdSiteValue != null) {
            this.piwikIdSite = piwikIdSiteValue.getValue();
        }

        HApplicationConfiguration termsOfUseUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_TERMS_CONDITIONS_URL);
        if (termsOfUseUrlValue != null) {
            this.termsOfUseUrl = termsOfUseUrlValue.getValue();
        }
    }

    @Transactional
    public void update() {
        HApplicationConfiguration registerUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_REGISTER);
        persistApplicationConfig(HApplicationConfiguration.KEY_REGISTER,
                registerUrlValue, registerUrl);

        HApplicationConfiguration serverUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_HOST);
        persistApplicationConfig(HApplicationConfiguration.KEY_HOST,
                serverUrlValue, serverUrl);

        HApplicationConfiguration emailDomainValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_DOMAIN);
        persistApplicationConfig(HApplicationConfiguration.KEY_DOMAIN,
                emailDomainValue, emailDomain);

        HApplicationConfiguration adminEmailValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_ADMIN_EMAIL);
        persistApplicationConfig(HApplicationConfiguration.KEY_ADMIN_EMAIL,
                adminEmailValue, adminEmail);

        HApplicationConfiguration fromEmailAddrValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_EMAIL_FROM_ADDRESS);
        persistApplicationConfig(
                HApplicationConfiguration.KEY_EMAIL_FROM_ADDRESS,
                fromEmailAddrValue, fromEmailAddr);

        HApplicationConfiguration emailLogEventsValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS);
        if (emailLogEventsValue == null) {
            emailLogEventsValue =
                    new HApplicationConfiguration(
                            HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS,
                            Boolean.toString(enableLogEmail));
        } else {
            emailLogEventsValue.setValue(Boolean.toString(enableLogEmail));
        }
        applicationConfigurationDAO.makePersistent(emailLogEventsValue);

        HApplicationConfiguration logDestEmailValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL);
        persistApplicationConfig(
                HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL,
                logDestEmailValue, logDestinationEmails);

        HApplicationConfiguration logEmailLevelValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL);
        persistApplicationConfig(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL,
                logEmailLevelValue, logEmailLevel);

        HApplicationConfiguration piwikUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_PIWIK_URL);
        persistApplicationConfig(HApplicationConfiguration.KEY_PIWIK_URL,
                piwikUrlValue, piwikUrl);

        HApplicationConfiguration piwikIdSiteValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_PIWIK_IDSITE);
        persistApplicationConfig(HApplicationConfiguration.KEY_PIWIK_IDSITE,
                piwikIdSiteValue, piwikIdSite);

        HApplicationConfiguration termsOfUseUrlValue =
                applicationConfigurationDAO
                        .findByKey(HApplicationConfiguration.KEY_TERMS_CONDITIONS_URL);
        persistApplicationConfig(
                HApplicationConfiguration.KEY_TERMS_CONDITIONS_URL,
                termsOfUseUrlValue, termsOfUseUrl);

        applicationConfigurationDAO.flush();
        FacesMessages.instance().add("Configuration was successfully updated.");
    }

    private void persistApplicationConfig(String key,
            HApplicationConfiguration appConfig, String newValue) {
        if (appConfig != null) {
            if (newValue == null || newValue.isEmpty()) {
                applicationConfigurationDAO.makeTransient(appConfig);
            } else {
                appConfig.setValue(newValue);
            }
        } else if (newValue != null && !newValue.isEmpty()) {
            appConfig = new HApplicationConfiguration(key, newValue);
            applicationConfigurationDAO.makePersistent(appConfig);
        }

        if (Events.exists()) {
            Events.instance().raiseTransactionSuccessEvent(
                    ApplicationConfiguration.EVENT_CONFIGURATION_CHANGED, key);
        }
    }

    public String cancel() {
        return "cancel";
    }
}
