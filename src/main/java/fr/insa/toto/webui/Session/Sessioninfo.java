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

import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.model.Utilisateur;
import java.io.Serializable;
import java.util.Optional;

/**
 *
 * @author vicbl
 */
public class Sessioninfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Utilisateur curUser;
    
    public static Sessioninfo getOrCreate(){
    VaadinSession cureSession = VaadinSession.getCurrent(); // map qui associe une clé à une valeur
    Sessioninfo curInfo = cureSession.getAttribute(Sessioninfo.class);
    if (curInfo == null){
        curInfo = new Sessioninfo();
        cureSession.setAttribute(Sessioninfo.class,curInfo);
        
    }
    return curInfo;
    }
    
    public static void login(Utilisateur u){
        Sessioninfo curInfo = getOrCreate();
        curInfo.curUser = u;
    }

    public static void logout(){
        Sessioninfo curInfo = getOrCreate();
        curInfo.curUser = null;
    }
    //pour dire sil y a ou non utilisateur :
    public static Optional<Utilisateur> curUser(){
        Utilisateur u = getOrCreate().curUser;
        if (u==null) {
            return Optional.empty();
            
        } else{
            return Optional.of(u);
        }
    
}
    public static boolean userConnected(){
        return curUser().isPresent();
        
    }
    public static boolean adminConnected(){
        Optional<Utilisateur> curUser = curUser();
        if (curUser.isEmpty()){
            return false;
        } else {
            return curUser.get().getRole() == 1; //1 c'ets admin et 2 utilisateur simple
        }
        
    }
}
