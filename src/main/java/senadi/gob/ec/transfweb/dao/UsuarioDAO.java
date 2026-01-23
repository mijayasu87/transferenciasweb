/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Usuario;

/**
 *
 * @author michael
 */
public class UsuarioDAO extends DAOAbstract<Usuario>{
    public UsuarioDAO(Usuario u){
        super(u);
    }

    @Override
    public List<Usuario> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select u from Usuario u");
        return query.getResultList();
    }
    
    public Usuario getUsuarioByUser(String user){
        Query query = this.getEntityManager().createQuery("Select u from Usuario u where u.nick = '"+user+"'");
        if(query.getResultList().isEmpty()){
            return new Usuario();
        }else{
            return (Usuario) query.getResultList().get(0);
        }
        
    }
}
