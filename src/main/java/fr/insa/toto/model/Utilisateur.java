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
package fr.insa.toto.model;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author vicbl
 */
public class Utilisateur extends ClasseMiroir implements Serializable{
    private static final long serialVersionUID = 1L; // version 1
    private String identifiant;
    private String mdp;
    private int role;
    
    // on fait un nouvel utilisateur 
public Utilisateur(String identifiant,String mdp, int role){
    super();
    this.identifiant = identifiant;
    this.mdp = mdp;
    this.role = role;
    
    
}
public Utilisateur(int id, String identifiant,String mdp, int role){
    super(id);
    this.identifiant = identifiant;
    this.mdp = mdp;
    this.role = role;
    
}
    
    
    

    @Override
    protected Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement( "insert into Utilisateur (identifiant,mdp,role) values (?,?,?)",
            PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setString(1,this.getIdentifiant());
        insert.setString(2,this.getMdp());
        insert.setInt(3,this.getRole());
        insert.executeUpdate();
        System.out.println("utilisateur créé sans id");
        return insert;
        
    }


    public int getRole() {
        return role ; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getIdentifiant() {
         return identifiant ;
    }

    private String getMdp() {
     return mdp ;
    }
    
    public static List<Utilisateur> TousLesUtilisateurs(Connection con)throws SQLException {
        List<Utilisateur> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement( "select id, identifiant, mdp, role from Utilisateur")){
            try (ResultSet allU = pst.executeQuery()){
                while(allU.next()){
                    res.add(new Utilisateur(allU.getInt("id"), allU.getString("identifiant"),
                    allU.getString("mdp"),allU.getInt("role")));
                }
            }
        }
        return res;
    }
    //supp utilisateur
   
  public static Optional<Utilisateur> findByIdentifiantMdp(Connection con, String identifiant, String mdp) throws SQLException{
      try (PreparedStatement pst = con.prepareStatement(
            "select id,role from Utilisateur where identifiant = ? and mdp = ?")){
          pst.setString(1, identifiant);
          pst.setString(2, mdp);
          ResultSet res = pst.executeQuery();
          if(res.next()){
              int id = res.getInt(1);
              int role = res.getInt(2);
              return Optional.of(new Utilisateur(id,identifiant,mdp,role));
          }else{
              return Optional.empty();
          }
        
          
      }
  }

    /**
     * @param role the role to set
     */
    public void setRole(int role) {
        this.role = role;
    }
}
