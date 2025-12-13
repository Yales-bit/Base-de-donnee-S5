/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.webui.Session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 *
 * @author vicbl
 */
public class Logout extends HorizontalLayout{
    private Button logout;
    
    public Logout(){
        this.logout = new Button("Déconnexion");
        this.logout.addClickListener((t)->{
            this.doLogout();
        }); 
        this.add("Bonjour"+Sessioninfo.curUser().get().getIdentifiant());
        this.add(this.logout);
    }

    public void doLogout() {
        Sessioninfo.logout();
        UI.getCurrent().refreshCurrentRoute(true); // rafraichir page      
    }
    
    
}
