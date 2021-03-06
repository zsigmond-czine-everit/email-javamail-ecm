/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.email.javamail.ecm.sender.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.mail.Session;

import org.everit.email.javamail.ecm.sender.JavamailEmailSenderComponentConstants;
import org.everit.email.javamail.sender.JavaMailEmailSender;
import org.everit.email.sender.EmailSender;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM component that wraps a {@link JavaMailEmailSender} and registers it as an OSGi service.
 */
@Component(componentId = JavamailEmailSenderComponentConstants.SERVICE_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit JavaMail Email Sender",
    description = "JSR 919 implementation of Everit Email Sender")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "" + "=${@class}")
@ManualService({ EmailSender.class })
public class JavamailEmailSenderComponent {

  private ServiceRegistration<EmailSender> serviceRegistration;

  private Session session;

  /**
   * Component activator that registers the {@link EmailSender} service.
   *
   * @param componentContext
   *          The context of the component.
   */
  @Activate
  public void activate(final ComponentContext<JavamailEmailSenderComponent> componentContext) {
    EmailSender emailSender = new JavaMailEmailSender(session);

    Dictionary<String, Object> properties = new Hashtable<>(componentContext.getProperties());
    serviceRegistration =
        componentContext.registerService(EmailSender.class, emailSender, properties);
  }

  @Deactivate
  public void deactivate() {
    serviceRegistration.unregister();
  }

  @ServiceRef(referenceId = JavamailEmailSenderComponentConstants.References.SERVICE_REF_SESSION,
      defaultValue = "", label = "Session",
      description = "Java Mail Session that is used to create Transport instances.")
  public void setSession(final Session session) {
    this.session = session;
  }
}
