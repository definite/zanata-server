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
package org.zanata.page.administration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.zanata.page.BasePage;

public class AdministrationPage extends BasePage
{
   @FindBy(id = "Admin_Manage_languages_home")
   private WebElement manageLanguagesLink;

   @FindBy(id = "Admin_Manage_users_home")
   private WebElement manageUsersLink;

   public AdministrationPage(WebDriver driver)
   {
      super(driver);
   }

   public ManageLanguagePage goToManageLanguagePage()
   {
      manageLanguagesLink.click();
      return new ManageLanguagePage(getDriver());
   }

   public ManageUserPage goToManageUserPage()
   {
      manageUsersLink.click();
      return new ManageUserPage(getDriver());
   }
}
